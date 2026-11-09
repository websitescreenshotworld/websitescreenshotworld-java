package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.errors.ConnectionException;
import com.websitescreenshotworld.errors.NotFoundException;
import com.websitescreenshotworld.errors.PlanLimitException;
import com.websitescreenshotworld.errors.RateLimitException;
import com.websitescreenshotworld.internal.Json;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClientTest {
  private static final byte[] FILE = "file-bytes".getBytes(StandardCharsets.UTF_8);
  private static final UUID WEB_PAGE_ID = UUID.fromString("7d4e2b1a-6c5f-4a3e-9d8c-2b1a0f9e8d7c");
  private static final UUID CAPTURE_ID = UUID.fromString("3f6c2a1e-9b8d-4c7e-a5f4-1d2e3c4b5a69");

  private static ConformanceServer server;

  @BeforeAll
  static void start() throws IOException {
    server = ConformanceServer.start();
  }

  @AfterAll
  static void stop() {
    server.close();
  }

  @Test
  void missingKeyFailsBeforeSending() throws Exception {
    assumeTrue(System.getenv("WSW_INSTANT_SNAP_API_KEY") == null);
    assumeTrue(System.getenv("WSW_AUTO_SCHEDULE_API_KEY") == null);
    server.load(responses());
    WebsiteScreenshotWorld client =
        WebsiteScreenshotWorld.builder()
            .instantSnapBaseUrl(server.baseUrl())
            .autoScheduleBaseUrl(server.baseUrl())
            .build();

    IllegalStateException capture =
        assertThrows(
            IllegalStateException.class,
            () ->
                client
                    .instantSnap()
                    .capture(CaptureOptions.builder().url("https://example.com").build()));
    assertTrue(capture.getMessage().contains("WSW_INSTANT_SNAP_API_KEY"));
    IllegalStateException trigger =
        assertThrows(
            IllegalStateException.class, () -> client.autoSchedule().triggerNow(WEB_PAGE_ID));
    assertTrue(trigger.getMessage().contains("WSW_AUTO_SCHEDULE_API_KEY"));
    assertEquals(0, server.requests().size());
  }

  @Test
  void defaultsNeverSendTheKey() throws Exception {
    server.load(responses("{\"status\":200,\"body\":{\"success\":true,\"spec_version\":\"v1\"}}"));
    Defaults defaults = client().instantSnap().defaults();

    assertEquals("v1", defaults.specVersion());
    JsonNode headers = server.requests().get(0).get("headers");
    assertFalse(headers.has("x-api-key"));
    assertEquals("application/json", headers.get("accept").asText());
  }

  @Test
  void userAgentNamesLibraryAndRuntime() throws Exception {
    server.load(responses("{\"status\":200,\"body\":{\"success\":true}}"));
    client().instantSnap().defaults();

    String userAgent = server.requests().get(0).get("headers").get("user-agent").asText();
    assertEquals(
        "websitescreenshotworld-java/0.1.0 (Java " + System.getProperty("java.version") + ")",
        userAgent);
  }

  @Test
  void downloadWritesFileWithoutTheKey(@TempDir Path folder) throws Exception {
    server.load(responses(fileResponse(), fileResponse()));
    Capture capture = captureWithLink(server.baseUrl() + "/media/capture.png?s=abc");
    InstantSnap instantSnap = client().instantSnap();

    Path target = folder.resolve("capture.png");
    instantSnap.download(capture, target);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    instantSnap.download(capture, stream);

    assertArrayEquals(FILE, Files.readAllBytes(target));
    assertArrayEquals(FILE, stream.toByteArray());
    JsonNode requests = server.requests();
    assertEquals(2, requests.size());
    for (JsonNode request : requests) {
      assertEquals("GET", request.get("method").asText());
      assertEquals("/media/capture.png?s=abc", request.get("path").asText());
      assertFalse(request.get("headers").has("x-api-key"));
    }
  }

  @Test
  void downloadNeedsALink() {
    Capture capture = captureWithLink(null);
    assertThrows(
        IllegalArgumentException.class,
        () -> client().instantSnap().download(capture, new ByteArrayOutputStream()));
  }

  @Test
  void downloadErrorsAreTyped() throws Exception {
    server.load(responses("{\"status\":403,\"body_text\":\"<Error>AccessDenied</Error>\"}"));
    Capture capture = captureWithLink(server.baseUrl() + "/media/expired.png");

    PlanLimitException error =
        assertThrows(
            PlanLimitException.class,
            () -> client().instantSnap().download(capture, new ByteArrayOutputStream()));
    assertEquals("forbidden", error.errorCode());
    assertEquals("<Error>AccessDenied</Error>", error.rawBody());
  }

  @Test
  void captureFileSavesToPath(@TempDir Path folder) throws Exception {
    server.load(responses(fileResponse()));
    Path target = folder.resolve("saved.png");
    try (CaptureFile file =
        client()
            .instantSnap()
            .captureFile(CaptureOptions.builder().url("https://example.com").build())) {
      file.saveTo(target);
    }
    assertArrayEquals(FILE, Files.readAllBytes(target));
    assertEquals("*/*", server.requests().get(0).get("headers").get("accept").asText());
  }

  @Test
  void captureToBucketNeedsADestination() {
    CaptureOptions options = CaptureOptions.builder().url("https://example.com").build();
    assertThrows(
        IllegalArgumentException.class, () -> client().instantSnap().captureToBucket(options));
  }

  @Test
  void connectionFailuresAreRetried() throws Exception {
    try (FakeServer fake = FakeServer.closingEveryConnection()) {
      WebsiteScreenshotWorld client =
          WebsiteScreenshotWorld.builder()
              .autoScheduleApiKey("key")
              .autoScheduleBaseUrl(fake.baseUrl())
              .build();

      assertThrows(ConnectionException.class, () -> client.autoSchedule().triggerNow(WEB_PAGE_ID));
      assertEquals(3, fake.connections());
    }
  }

  @Test
  void timeoutStopsRetrying() throws Exception {
    try (FakeServer fake = FakeServer.neverAnswering()) {
      WebsiteScreenshotWorld client =
          WebsiteScreenshotWorld.builder()
              .autoScheduleApiKey("key")
              .autoScheduleBaseUrl(fake.baseUrl())
              .build();
      RequestOptions options = RequestOptions.builder().timeout(Duration.ofMillis(300)).build();

      long started = System.nanoTime();
      assertThrows(
          ConnectionException.class, () -> client.autoSchedule().triggerNow(WEB_PAGE_ID, options));
      assertTrue(
          Duration.ofNanos(System.nanoTime() - started).compareTo(Duration.ofSeconds(2)) < 0);
      assertEquals(1, fake.connections());
    }
  }

  @Test
  void waitForReturnsTheLastCaptureWhenTimeRunsOut() throws Exception {
    String queued = captureResponse("queued");
    server.load(responses(queued, queued, queued, queued, queued, queued, queued, queued));

    Capture capture =
        client().instantSnap().waitFor(CAPTURE_ID, Duration.ofMillis(150), Duration.ofMillis(50));

    assertEquals(CaptureStatus.QUEUED, capture.status());
    int reads = server.requests().size();
    assertTrue(reads >= 2 && reads < 8, "reads " + reads);
  }

  @Test
  void waitForReadsOnceWithNoTimeLeft() throws Exception {
    server.load(responses(captureResponse("running"), captureResponse("succeeded")));

    Capture capture = client().instantSnap().waitFor(CAPTURE_ID, Duration.ZERO, Duration.ZERO);

    assertEquals(CaptureStatus.RUNNING, capture.status());
    assertEquals(1, server.requests().size());
  }

  @Test
  void waitForThrowsWhatGetThrows() throws Exception {
    server.load(
        responses(
            "{\"status\":404,\"body\":{\"success\":false,\"error_code\":\"capture_not_found\","
                + "\"error_message\":\"No capture.\",\"retry_after_seconds\":null}}"));

    NotFoundException error =
        assertThrows(NotFoundException.class, () -> client().instantSnap().waitFor(CAPTURE_ID));
    assertEquals("capture_not_found", error.errorCode());
  }

  @Test
  void queueFullIsNotRetried() throws Exception {
    server.load(
        responses(
            "{\"status\":429,\"body\":{\"success\":false,\"error_code\":\"queue_full\","
                + "\"error_message\":\"Queue full.\",\"retry_after_seconds\":1}}",
            captureResponse("queued")));

    RateLimitException error =
        assertThrows(
            RateLimitException.class,
            () ->
                client()
                    .instantSnap()
                    .submit(CaptureOptions.builder().url("https://example.com").build()));
    assertEquals("queue_full", error.errorCode());
    assertEquals(1, error.retryAfterSeconds());
    assertEquals(1, server.requests().size());
  }

  @Test
  void submitToBucketNeedsADestination() {
    CaptureOptions options = CaptureOptions.builder().url("https://example.com").build();
    assertThrows(
        IllegalArgumentException.class, () -> client().instantSnap().submitToBucket(options));
  }

  private static WebsiteScreenshotWorld client() {
    return WebsiteScreenshotWorld.builder()
        .instantSnapApiKey("wsw_api_000000000000_AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
        .instantSnapBaseUrl(server.baseUrl())
        .build();
  }

  private static Capture captureWithLink(String downloadUrl) {
    try {
      JsonNode node =
          Json.readTree(
              ("{\"id\":\"3f6c2a1e-9b8d-4c7e-a5f4-1d2e3c4b5a69\",\"download_url\":"
                      + (downloadUrl == null ? "null" : "\"" + downloadUrl + "\"")
                      + "}")
                  .getBytes(StandardCharsets.UTF_8));
      return Json.convert(node, Capture.class);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  private static String captureResponse(String status) {
    return "{\"status\":200,\"body\":{\"success\":true,\"id\":\""
        + CAPTURE_ID
        + "\",\"status\":\""
        + status
        + "\",\"media_category\":null}}";
  }

  private static String fileResponse() {
    return "{\"status\":200,\"headers\":{\"Content-Type\":\"image/png\"},\"body_base64\":\""
        + Base64.getEncoder().encodeToString(FILE)
        + "\"}";
  }

  private static JsonNode responses(String... responses) throws IOException {
    String json = "{\"responses\":[" + String.join(",", responses) + "]}";
    return Json.readTree(json.getBytes(StandardCharsets.UTF_8));
  }

  /** A TCP server that misbehaves in one way, counting the connections it accepts. */
  private static final class FakeServer implements AutoCloseable {
    private final ServerSocket socket;
    private final AtomicInteger connections = new AtomicInteger();
    private final List<Socket> held = new ArrayList<>();
    private final Thread acceptor;

    private FakeServer(boolean hold) throws IOException {
      socket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
      acceptor =
          new Thread(
              () -> {
                while (!socket.isClosed()) {
                  try {
                    Socket connection = socket.accept();
                    connections.incrementAndGet();
                    if (hold) {
                      synchronized (held) {
                        held.add(connection);
                      }
                    } else {
                      connection.close();
                    }
                  } catch (IOException e) {
                    return;
                  }
                }
              });
      acceptor.setDaemon(true);
      acceptor.start();
    }

    static FakeServer closingEveryConnection() throws IOException {
      return new FakeServer(false);
    }

    static FakeServer neverAnswering() throws IOException {
      return new FakeServer(true);
    }

    String baseUrl() {
      return "http://127.0.0.1:" + socket.getLocalPort();
    }

    int connections() {
      return connections.get();
    }

    @Override
    public void close() throws IOException {
      socket.close();
      synchronized (held) {
        for (Socket connection : held) {
          connection.close();
        }
      }
    }
  }
}
