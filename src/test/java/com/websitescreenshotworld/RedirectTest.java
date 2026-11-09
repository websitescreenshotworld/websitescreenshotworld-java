package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** A redirect must never carry the API key to another place. */
class RedirectTest {
  private static final UUID WEB_PAGE_ID = UUID.fromString("7d4e2b1a-6c5f-4a3e-9d8c-2b1a0f9e8d7c");

  private HttpServer server;
  private String baseUrl;
  private final AtomicInteger elsewhere = new AtomicInteger();
  private final AtomicReference<String> keySentElsewhere = new AtomicReference<>();

  @BeforeEach
  void start() throws IOException {
    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    server.createContext(
        "/elsewhere",
        exchange -> {
          elsewhere.incrementAndGet();
          keySentElsewhere.set(exchange.getRequestHeaders().getFirst("X-Api-Key"));
          byte[] body = "{}".getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().set("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, body.length);
          try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
          }
        });
    server.createContext(
        "/",
        exchange -> {
          exchange.getResponseHeaders().set("Location", baseUrl + "/elsewhere");
          exchange.sendResponseHeaders(302, -1);
          exchange.close();
        });
    server.start();
  }

  @AfterEach
  void stop() {
    server.stop(0);
  }

  @Test
  void ownHttpClientDoesNotFollowRedirects() {
    WebsiteScreenshotWorld client =
        WebsiteScreenshotWorld.builder()
            .autoScheduleApiKey("wsw_autoapi_secret")
            .autoScheduleBaseUrl(baseUrl)
            .build();

    WebsiteScreenshotWorldException error =
        assertThrows(
            WebsiteScreenshotWorldException.class,
            () -> client.autoSchedule().triggerNow(WEB_PAGE_ID));

    assertEquals(302, error.httpStatus());
    assertEquals(0, elsewhere.get());
  }

  @Test
  void answerThroughARedirectIsRefused() {
    WebsiteScreenshotWorld client =
        WebsiteScreenshotWorld.builder()
            .autoScheduleApiKey("wsw_autoapi_secret")
            .autoScheduleBaseUrl(baseUrl)
            .httpClient(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build())
            .build();

    WebsiteScreenshotWorldException error =
        assertThrows(
            WebsiteScreenshotWorldException.class,
            () -> client.autoSchedule().triggerNow(WEB_PAGE_ID));

    assertTrue(error.getMessage().contains("after a redirect"), error.getMessage());
    assertNull(error.errorCode());
  }
}
