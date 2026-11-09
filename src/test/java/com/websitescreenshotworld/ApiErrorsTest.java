package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.errors.AuthenticationException;
import com.websitescreenshotworld.errors.CaptureException;
import com.websitescreenshotworld.errors.InvalidRequestException;
import com.websitescreenshotworld.errors.NotFoundException;
import com.websitescreenshotworld.errors.PlanLimitException;
import com.websitescreenshotworld.errors.RateLimitException;
import com.websitescreenshotworld.errors.ServiceException;
import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Pins the error mapping to spec/errors.json. */
class ApiErrorsTest {
  private static final JsonNode ERRORS = Spec.read(Spec.path("errors.json"));
  private static final HttpHeaders NO_HEADERS = HttpHeaders.of(Map.of(), (name, value) -> true);
  private static final Map<String, Class<? extends WebsiteScreenshotWorldException>> KINDS =
      Map.of(
          "authentication", AuthenticationException.class,
          "invalid_request", InvalidRequestException.class,
          "plan_limit", PlanLimitException.class,
          "not_found", NotFoundException.class,
          "rate_limit", RateLimitException.class,
          "capture", CaptureException.class,
          "service", ServiceException.class);

  @Test
  void everyCodeRaisesItsKind() {
    ERRORS
        .get("kinds")
        .properties()
        .forEach(
            kind -> {
              for (JsonNode code : kind.getValue()) {
                WebsiteScreenshotWorldException error = refusal(code.asText());
                assertSame(KINDS.get(kind.getKey()), error.getClass(), code.asText());
                assertEquals(code.asText(), error.errorCode());
              }
            });
  }

  @Test
  void unknownCodeRaisesTheBase() {
    assertSame(WebsiteScreenshotWorldException.class, refusal("something_new").getClass());
  }

  @Test
  void onlyTheListedCodesAreRetried() {
    Set<String> retryable = new HashSet<>();
    ERRORS.get("retryable").forEach(code -> retryable.add(code.asText()));
    ERRORS
        .get("kinds")
        .forEach(
            codes ->
                codes.forEach(
                    code ->
                        assertEquals(
                            retryable.contains(code.asText()),
                            ApiErrors.isRetryable(code.asText()),
                            code.asText())));
  }

  @Test
  void statusFallbackMatchesTheSpec() {
    ERRORS
        .get("fallback_by_status")
        .properties()
        .forEach(
            entry -> {
              if (entry.getKey().equals("description")) {
                return;
              }
              int status = entry.getKey().equals("other") ? 418 : Integer.parseInt(entry.getKey());
              assertEquals(entry.getValue().asText(), ApiErrors.fallbackCode(status));
            });
  }

  @Test
  void retryAfterHeaderFillsInForTheBody() {
    HttpHeaders headers =
        HttpHeaders.of(Map.of("Retry-After", List.of("7")), (name, value) -> true);
    WebsiteScreenshotWorldException error =
        ApiErrors.fromResponse(429, headers, "Too many".getBytes(StandardCharsets.UTF_8));
    assertSame(RateLimitException.class, error.getClass());
    assertEquals(7, error.retryAfterSeconds());
    assertEquals("Too many", error.rawBody());
  }

  private static WebsiteScreenshotWorldException refusal(String code) {
    String body =
        "{\"success\":false,\"error_code\":\""
            + code
            + "\",\"error_message\":\"Refused.\",\"retry_after_seconds\":null}";
    return ApiErrors.fromResponse(400, NO_HEADERS, body.getBytes(StandardCharsets.UTF_8));
  }
}
