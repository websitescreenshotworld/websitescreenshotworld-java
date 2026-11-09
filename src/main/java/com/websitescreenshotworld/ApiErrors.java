package com.websitescreenshotworld;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.errors.AuthenticationException;
import com.websitescreenshotworld.errors.CaptureException;
import com.websitescreenshotworld.errors.InvalidRequestException;
import com.websitescreenshotworld.errors.NotFoundException;
import com.websitescreenshotworld.errors.PlanLimitException;
import com.websitescreenshotworld.errors.RateLimitException;
import com.websitescreenshotworld.errors.ServiceException;
import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import com.websitescreenshotworld.internal.Json;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Turns a refusal into the right exception, as spec/errors.json describes. */
final class ApiErrors {
  private static final Set<String> RETRYABLE =
      Set.of("rate_limited", "capture_busy", "service_unavailable", "idempotency_key_in_use");

  private static final Map<String, Factory> KINDS = new HashMap<>();

  static {
    register(AuthenticationException::new, "api_key_invalid");
    register(
        InvalidRequestException::new,
        "validation_failed",
        "s3_destination_invalid",
        "web_page_id_invalid",
        "idempotency_key_reused",
        "sitemap_schedule_not_triggerable");
    register(
        PlanLimitException::new,
        "quota_exhausted",
        "feature_not_available",
        "no_trigger_allowance",
        "forbidden");
    register(NotFoundException::new, "capture_not_found", "web_page_not_found", "not_found");
    register(RateLimitException::new, "rate_limited", "queue_full");
    register(
        CaptureException::new,
        "capture_failed",
        "capture_timeout",
        "payload_too_large",
        "capture_expired");
    register(
        ServiceException::new,
        "capture_busy",
        "service_unavailable",
        "internal_error",
        "request_cancelled",
        "idempotency_key_in_use");
  }

  private ApiErrors() {}

  static boolean isRetryable(String errorCode) {
    return RETRYABLE.contains(errorCode);
  }

  static WebsiteScreenshotWorldException fromResponse(
      int status, HttpHeaders headers, byte[] body) {
    String rawBody = new String(body, StandardCharsets.UTF_8);
    Integer retryAfter = retryAfterHeader(headers);
    JsonNode json = parseObject(body);
    String code;
    String message;
    if (json != null && json.path("error_code").isTextual()) {
      code = json.get("error_code").asText();
      message = json.path("error_message").isTextual() ? json.get("error_message").asText() : code;
      JsonNode hint = json.path("retry_after_seconds");
      if (hint.canConvertToInt()) {
        retryAfter = hint.asInt();
      }
    } else {
      code = fallbackCode(status);
      message = "The API answered HTTP " + status + " without an error body.";
    }
    return KINDS
        .getOrDefault(code, WebsiteScreenshotWorldException::new)
        .create(message, code, status, retryAfter, rawBody);
  }

  static String fallbackCode(int status) {
    switch (status) {
      case 400:
        return "validation_failed";
      case 401:
        return "api_key_invalid";
      case 403:
        return "forbidden";
      case 404:
        return "not_found";
      case 409:
        return "idempotency_key_in_use";
      case 413:
        return "payload_too_large";
      case 429:
        return "rate_limited";
      case 500:
        return "internal_error";
      case 502:
      case 503:
      case 504:
        return "service_unavailable";
      default:
        return "unknown_error";
    }
  }

  private static JsonNode parseObject(byte[] body) {
    try {
      JsonNode node = Json.readTree(body);
      return node != null && node.isObject() ? node : null;
    } catch (IOException e) {
      return null;
    }
  }

  private static Integer retryAfterHeader(HttpHeaders headers) {
    try {
      return headers
          .firstValue("Retry-After")
          .map(value -> Integer.valueOf(value.trim()))
          .orElse(null);
    } catch (NumberFormatException e) {
      // An HTTP-date is allowed by the standard but never sent by the API; ignore it.
      return null;
    }
  }

  private static void register(Factory factory, String... codes) {
    for (String code : codes) {
      KINDS.put(code, factory);
    }
  }

  @FunctionalInterface
  private interface Factory {
    WebsiteScreenshotWorldException create(
        String message, String errorCode, Integer httpStatus, Integer retryAfter, String rawBody);
  }
}
