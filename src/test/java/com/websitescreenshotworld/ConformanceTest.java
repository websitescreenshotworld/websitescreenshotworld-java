package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/** Runs every case in spec/fixtures against conformance/server.js. */
class ConformanceTest {
  private static final Map<String, Class<? extends WebsiteScreenshotWorldException>> KINDS =
      Map.of(
          "base", WebsiteScreenshotWorldException.class,
          "authentication", AuthenticationException.class,
          "invalid_request", InvalidRequestException.class,
          "plan_limit", PlanLimitException.class,
          "not_found", NotFoundException.class,
          "rate_limit", RateLimitException.class,
          "capture", CaptureException.class,
          "service", ServiceException.class);

  private static ConformanceServer server;
  private static WebsiteScreenshotWorld client;

  @BeforeAll
  static void start() throws IOException {
    server = ConformanceServer.start();
    JsonNode keys = Spec.read(Spec.path("fixtures", "keys.json"));
    client =
        WebsiteScreenshotWorld.builder()
            .instantSnapApiKey(keys.get("instant_snap").asText())
            .autoScheduleApiKey(keys.get("auto_schedule").asText())
            .instantSnapBaseUrl(server.baseUrl())
            .autoScheduleBaseUrl(server.baseUrl())
            .build();
  }

  @AfterAll
  static void stop() {
    server.close();
  }

  @TestFactory
  Stream<DynamicTest> fixtures() {
    return Spec.files("fixtures").stream()
        .filter(file -> !file.getFileName().toString().equals("keys.json"))
        .map(file -> DynamicTest.dynamicTest(file.getFileName().toString(), () -> run(file)));
  }

  private static void run(Path file) throws Exception {
    JsonNode fixture = Spec.read(file);
    server.load(fixture);
    JsonNode expect = fixture.get("expect");

    if (expect.has("error")) {
      WebsiteScreenshotWorldException error =
          assertThrows(WebsiteScreenshotWorldException.class, () -> call(fixture.get("call")));
      checkRequests(fixture.get("expect_requests"), server.requests());
      checkError(expect.get("error"), error);
    } else {
      JsonNode result = call(fixture.get("call"));
      checkRequests(fixture.get("expect_requests"), server.requests());
      checkSubset("result", expect.get("result"), result);
    }
  }

  /** Makes the fixture's call and returns the result with its wire field names. */
  private static JsonNode call(JsonNode call) throws Exception {
    RequestOptions requestOptions =
        call.has("idempotency_key")
            ? RequestOptions.builder().idempotencyKey(call.get("idempotency_key").asText()).build()
            : RequestOptions.none();
    InstantSnap instantSnap = client.instantSnap();
    String method = call.get("method").asText();
    switch (method) {
      case "capture":
        return Json.toTree(instantSnap.capture(options(call), requestOptions));
      case "capture_file":
        try (CaptureFile file = instantSnap.captureFile(options(call), requestOptions)) {
          return describe(file);
        }
      case "capture_to_bucket":
        return Json.toTree(instantSnap.captureToBucket(options(call), requestOptions));
      case "submit":
        return Json.toTree(instantSnap.submit(options(call), requestOptions));
      case "submit_to_bucket":
        return Json.toTree(instantSnap.submitToBucket(options(call), requestOptions));
      case "wait":
        return Json.toTree(
            instantSnap.waitFor(
                UUID.fromString(call.get("id").asText()),
                seconds(call, "timeout_seconds", 360),
                seconds(call, "poll_interval_seconds", 3),
                requestOptions));
      case "get":
        return Json.toTree(instantSnap.get(UUID.fromString(call.get("id").asText())));
      case "defaults":
        return Json.toTree(instantSnap.defaults());
      case "catalog":
        return Json.toTree(instantSnap.catalog());
      case "trigger_now":
        return Json.toTree(
            client
                .autoSchedule()
                .triggerNow(UUID.fromString(call.get("web_page_id").asText()), requestOptions));
      default:
        throw new AssertionError("Unknown fixture method " + method);
    }
  }

  private static Duration seconds(JsonNode call, String field, double fallback) {
    double seconds = call.has(field) ? call.get(field).asDouble() : fallback;
    return Duration.ofNanos(Math.round(seconds * 1_000_000_000L));
  }

  private static CaptureOptions options(JsonNode call) throws IOException {
    return Json.convert(call.get("options"), CaptureOptions.class);
  }

  private static JsonNode describe(CaptureFile file) throws IOException, NoSuchAlgorithmException {
    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
    try (InputStream in = new DigestInputStream(file.body(), sha256)) {
      in.transferTo(OutputStream.nullOutputStream());
    }
    ObjectNode node = JsonNodeFactory.instance.objectNode();
    node.put("id", String.valueOf(file.id()));
    node.put("status", file.status().value());
    node.put("content_type", file.contentType());
    node.put("width", file.width());
    node.put("height", file.height());
    node.put("size_bytes", file.sizeBytes());
    node.put("duration_ms", file.durationMs());
    node.put("page_http_status", file.pageHttpStatus());
    node.put("spec_version", file.specVersion());
    node.put("captured_at", String.valueOf(file.capturedAt()));
    node.put("final_url", file.finalUrl());
    node.put("sha256", file.sha256());
    node.put("part_count", file.partCount());
    node.put("filename", file.filename());
    node.put("body_sha256", hex(sha256.digest()));
    return node;
  }

  private static void checkRequests(JsonNode expected, JsonNode actual) {
    assertEquals(expected.size(), actual.size(), "number of requests");
    Set<String> idempotencyKeys = new HashSet<>();
    for (int i = 0; i < expected.size(); i++) {
      JsonNode want = expected.get(i);
      JsonNode got = actual.get(i);
      String where = "request " + (i + 1);
      assertEquals(want.get("method").asText(), got.get("method").asText(), where + " method");
      assertEquals(want.get("path").asText(), got.get("path").asText(), where + " path");

      JsonNode headers = got.get("headers");
      for (Map.Entry<String, JsonNode> header : want.path("headers").properties()) {
        String name = header.getKey().toLowerCase(Locale.ROOT);
        assertEquals(
            header.getValue().asText(),
            headers.path(name).asText(null),
            where + " header " + header.getKey());
      }
      String userAgent = headers.path("user-agent").asText();
      assertTrue(
          userAgent.matches("websitescreenshotworld-java/\\d+\\.\\d+\\.\\d+ \\(Java [^)]+\\)"),
          where + " User-Agent " + userAgent);

      if (want.has("idempotency_key")) {
        String key = headers.path("idempotency-key").asText("");
        assertFalse(key.isEmpty(), where + " Idempotency-Key");
        idempotencyKeys.add(key);
      }

      if (want.has("body")) {
        JsonNode body = got.get("body");
        if (want.get("body").isNull()) {
          assertTrue(body.isNull(), where + " has no body");
        } else {
          JsonNode sent = readJson(body.asText());
          assertTrue(
              Spec.sameJson(want.get("body"), sent),
              where + " body: expected " + want.get("body") + " but was " + sent);
        }
      }
    }
    boolean same =
        expected.size() > 0 && "same".equals(expected.get(0).path("idempotency_key").asText());
    if (same) {
      assertEquals(1, idempotencyKeys.size(), "one Idempotency-Key shared by every request");
    }
  }

  private static void checkError(JsonNode expected, WebsiteScreenshotWorldException error) {
    Class<? extends WebsiteScreenshotWorldException> kind =
        KINDS.get(expected.get("kind").asText());
    assertNotNull(kind, "known kind " + expected.get("kind"));
    assertSame(kind, error.getClass(), "error class");
    assertEquals(expected.get("error_code").asText(), error.errorCode());
    assertEquals(expected.get("http_status").asInt(), error.httpStatus());
    if (expected.has("message")) {
      assertEquals(expected.get("message").asText(), error.getMessage());
    }
    if (expected.has("retry_after_seconds")) {
      JsonNode retryAfter = expected.get("retry_after_seconds");
      assertEquals(retryAfter.isNull() ? null : retryAfter.asInt(), error.retryAfterSeconds());
    }
  }

  /** Checks that every field in {@code expected} has the same value in {@code actual}. */
  private static void checkSubset(String where, JsonNode expected, JsonNode actual) {
    JsonNode value = actual == null ? NullNode.getInstance() : actual;
    if (expected.isObject()) {
      if (!value.isObject()) {
        fail(where + ": expected an object but was " + value);
      }
      expected
          .properties()
          .forEach(
              field ->
                  checkSubset(
                      where + "." + field.getKey(), field.getValue(), value.get(field.getKey())));
    } else {
      assertTrue(
          Spec.sameJson(expected, value), where + ": expected " + expected + " but was " + value);
    }
  }

  private static JsonNode readJson(String text) {
    try {
      return Json.readTree(text.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new AssertionError("Not JSON: " + text, e);
    }
  }

  private static String hex(byte[] bytes) {
    StringBuilder hex = new StringBuilder();
    for (byte b : bytes) {
      hex.append(String.format("%02x", b));
    }
    return hex.toString();
  }
}
