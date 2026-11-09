package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import com.websitescreenshotworld.webhooks.AutoScheduleCaptureFailedEvent;
import com.websitescreenshotworld.webhooks.AutoScheduleCaptureSucceededEvent;
import com.websitescreenshotworld.webhooks.CaptureChangedEvent;
import com.websitescreenshotworld.webhooks.DeliveryCompletedEvent;
import com.websitescreenshotworld.webhooks.Destination;
import com.websitescreenshotworld.webhooks.InstantSnapCaptureFailedEvent;
import com.websitescreenshotworld.webhooks.InstantSnapCaptureSucceededEvent;
import com.websitescreenshotworld.webhooks.PingEvent;
import com.websitescreenshotworld.webhooks.Product;
import com.websitescreenshotworld.webhooks.SchedulePausedEvent;
import com.websitescreenshotworld.webhooks.SitemapRunCompletedEvent;
import com.websitescreenshotworld.webhooks.UnknownWebhookEvent;
import com.websitescreenshotworld.webhooks.Webhook;
import com.websitescreenshotworld.webhooks.WebhookEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/** Reads every example in spec/events into its typed event. */
class WebhookEventsTest {
  private static final String SECRET = "whsec_test";
  private static final long NOW = 1790418605L;
  private static final UUID SCHEDULE_ID = UUID.fromString("7d4e2b1a-6c5f-4a3e-9d8c-2b1a0f9e8d7c");
  private static final Map<String, Class<? extends WebhookEvent>> TYPES =
      Map.of(
          "instant-snap.capture.succeeded.json", InstantSnapCaptureSucceededEvent.class,
          "instant-snap.capture.failed.json", InstantSnapCaptureFailedEvent.class,
          "auto-schedule.capture.succeeded.json", AutoScheduleCaptureSucceededEvent.class,
          "auto-schedule.capture.failed.json", AutoScheduleCaptureFailedEvent.class,
          "auto-schedule.capture.changed.json", CaptureChangedEvent.class,
          "auto-schedule.delivery.completed.json", DeliveryCompletedEvent.class,
          "auto-schedule.schedule.paused.json", SchedulePausedEvent.class,
          "auto-schedule.sitemap_run.completed.json", SitemapRunCompletedEvent.class,
          "auto-schedule.ping.json", PingEvent.class,
          "unknown.json", UnknownWebhookEvent.class);

  @TestFactory
  Stream<DynamicTest> everyExampleParsesToItsType() {
    return Spec.files("events").stream()
        .map(
            file ->
                DynamicTest.dynamicTest(
                    file.getFileName().toString(),
                    () -> {
                      String name = file.getFileName().toString();
                      assertTrue(TYPES.containsKey(name), "no expected type for " + name);
                      read(name, TYPES.get(name));
                    }));
  }

  @Test
  void instantSnapCaptureSucceeded() throws Exception {
    InstantSnapCaptureSucceededEvent event =
        read("instant-snap.capture.succeeded.json", InstantSnapCaptureSucceededEvent.class);
    assertEquals(UUID.fromString("d3b07384-d9a0-4c9b-8f6e-2a1b3c4d5e6f"), event.id());
    assertEquals("capture.succeeded", event.type());
    assertEquals(Instant.parse("2026-09-26T10:30:05Z"), event.createdAt());
    assertEquals(Product.INSTANT_SNAP, event.product());
    Capture capture = event.data();
    assertEquals(UUID.fromString("3f6c2a1e-9b8d-4c7e-a5f4-1d2e3c4b5a69"), capture.id());
    assertEquals(CaptureStatus.SUCCEEDED, capture.status());
    assertEquals(CaptureFormat.PNG, capture.format());
    assertEquals(482113L, capture.sizeBytes());
    assertEquals(Instant.parse("2026-09-26T10:30:04.512Z"), capture.capturedAt());
    assertNull(capture.downloadUrl());
  }

  @Test
  void instantSnapCaptureFailed() throws Exception {
    InstantSnapCaptureFailedEvent event =
        read("instant-snap.capture.failed.json", InstantSnapCaptureFailedEvent.class);
    assertEquals(CaptureStatus.FAILED, event.data().status());
    assertEquals("capture_failed", event.data().errorCode());
    assertEquals(403, event.data().pageHttpStatus());
    assertNull(event.data().width());
  }

  @Test
  void autoScheduleCaptureSucceeded() throws Exception {
    AutoScheduleCaptureSucceededEvent event =
        read("auto-schedule.capture.succeeded.json", AutoScheduleCaptureSucceededEvent.class);
    assertEquals(Product.AUTO_SCHEDULE, event.product());
    assertEquals(SCHEDULE_ID, event.data().scheduleId());
    assertEquals("Pricing page", event.data().scheduleName());
    assertEquals("succeeded", event.data().status());
    assertEquals(MediaCategory.IMAGE, event.data().mediaCategory());
    assertEquals(PageSourceFormat.HTML, event.data().pageSource().format());
    assertNull(event.data().sitemapRunId());
  }

  @Test
  void autoScheduleCaptureFailed() throws Exception {
    AutoScheduleCaptureFailedEvent event =
        read("auto-schedule.capture.failed.json", AutoScheduleCaptureFailedEvent.class);
    assertEquals("failed", event.data().status());
    assertEquals(503, event.data().pageHttpStatus());
    assertNull(event.data().pageSource());
  }

  @Test
  void captureChanged() throws Exception {
    CaptureChangedEvent event =
        read("auto-schedule.capture.changed.json", CaptureChangedEvent.class);
    assertEquals(12.5, event.data().changePercent());
    assertEquals(
        UUID.fromString("1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d"),
        event.data().comparedToCaptureId());
    assertTrue(event.data().changeImageAvailable());
    assertEquals(SCHEDULE_ID, event.data().scheduleId());
  }

  @Test
  void deliveryCompleted() throws Exception {
    DeliveryCompletedEvent event =
        read("auto-schedule.delivery.completed.json", DeliveryCompletedEvent.class);
    assertEquals("media_delivered", event.data().status());
    assertEquals(1, event.data().destinations().size());
    Destination destination = event.data().destinations().get(0);
    assertEquals(1, destination.slot());
    assertEquals("Marketing bucket", destination.name());
    assertEquals("aws_s3", destination.provider());
    assertEquals("succeeded", destination.status());
    assertEquals(Instant.parse("2026-09-26T10:30:09Z"), destination.completedAt());
  }

  @Test
  void schedulePaused() throws Exception {
    SchedulePausedEvent event =
        read("auto-schedule.schedule.paused.json", SchedulePausedEvent.class);
    assertEquals(SCHEDULE_ID, event.data().scheduleId());
    assertEquals("quota_exhausted", event.data().reason());
    assertEquals(Instant.parse("2026-09-26T10:30:00Z"), event.data().pausedAt());
  }

  @Test
  void sitemapRunCompleted() throws Exception {
    SitemapRunCompletedEvent event =
        read("auto-schedule.sitemap_run.completed.json", SitemapRunCompletedEvent.class);
    assertEquals("https://example.com/sitemap.xml", event.data().sitemapUrl());
    assertEquals("completed", event.data().status());
    assertEquals(120, event.data().pagesListed());
    assertEquals(80, event.data().pagesMatched());
    assertEquals(50, event.data().pagesQueued());
    assertEquals(48, event.data().pagesSucceeded());
    assertEquals(2, event.data().pagesFailed());
    assertEquals(30, event.data().pagesOverLimit());
    assertEquals(0, event.data().pagesOverQuota());
    assertNull(event.data().message());
  }

  @Test
  void ping() throws Exception {
    PingEvent event = read("auto-schedule.ping.json", PingEvent.class);
    assertEquals("Production", event.data().endpointName());
    assertEquals(Instant.parse("2026-09-26T10:30:00Z"), event.data().sentAt());
  }

  @Test
  void unknownType() throws Exception {
    UnknownWebhookEvent event = read("unknown.json", UnknownWebhookEvent.class);
    assertEquals("capture.teleported", event.type());
    assertEquals(1, event.data().get("anything"));
  }

  @Test
  void knownTypeFromAnUnknownProductIsUnknown() throws Exception {
    String body =
        "{\"id\":\"d3b07384-d9a0-4c9b-8f6e-2a1b3c4d5e6f\",\"type\":\"capture.succeeded\","
            + "\"created_at\":\"2026-09-26T10:30:05.000Z\",\"product\":\"future_product\","
            + "\"data\":{\"id\":\"x\"}}";
    WebhookEvent event = verify(body.getBytes(StandardCharsets.UTF_8));
    assertInstanceOf(UnknownWebhookEvent.class, event);
    assertEquals(Product.of("future_product"), event.product());
  }

  @Test
  void signedBodyThatIsNotAnEventIsRefused() {
    WebsiteScreenshotWorldException error =
        assertThrows(
            WebsiteScreenshotWorldException.class,
            () -> verify("[1, 2]".getBytes(StandardCharsets.UTF_8)));
    assertNotNull(error.getCause());
  }

  private static <T extends WebhookEvent> T read(String name, Class<T> type)
      throws IOException, GeneralSecurityException {
    byte[] body = Files.readAllBytes(Spec.path("events", name));
    return assertInstanceOf(type, verify(body));
  }

  private static WebhookEvent verify(byte[] body) throws GeneralSecurityException {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
    mac.update((NOW + ".").getBytes(StandardCharsets.UTF_8));
    StringBuilder hex = new StringBuilder();
    for (byte b : mac.doFinal(body)) {
      hex.append(String.format("%02x", b));
    }
    String header = "t=" + NOW + ",v1=" + hex;
    Clock clock = Clock.fixed(Instant.ofEpochSecond(NOW), ZoneOffset.UTC);
    return Webhook.verify(body, header, SECRET, Webhook.DEFAULT_TOLERANCE, clock);
  }
}
