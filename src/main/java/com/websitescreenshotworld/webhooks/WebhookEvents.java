package com.websitescreenshotworld.webhooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.Capture;
import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import com.websitescreenshotworld.internal.Json;
import com.websitescreenshotworld.webhooks.WebhookEvent.Envelope;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Reads a verified body into the event class its product and type name. */
final class WebhookEvents {
  private WebhookEvents() {}

  static WebhookEvent parse(byte[] body) {
    try {
      JsonNode root = Json.readTree(body);
      if (root == null || !root.isObject()) {
        throw new IOException("The body is not a JSON object");
      }
      Envelope envelope = Json.convert(root, Envelope.class);
      JsonNode data = root.path("data");
      String type = envelope.type() == null ? "" : envelope.type();
      if (type.equals("ping")) {
        return new PingEvent(envelope, Json.convert(data, Ping.class));
      }
      if (Product.INSTANT_SNAP.equals(envelope.product())) {
        switch (type) {
          case "capture.succeeded":
            return new InstantSnapCaptureSucceededEvent(
                envelope, Json.convert(data, Capture.class));
          case "capture.failed":
            return new InstantSnapCaptureFailedEvent(envelope, Json.convert(data, Capture.class));
          default:
            break;
        }
      } else if (Product.AUTO_SCHEDULE.equals(envelope.product())) {
        switch (type) {
          case "capture.succeeded":
            return new AutoScheduleCaptureSucceededEvent(
                envelope, Json.convert(data, ScheduledCapture.class));
          case "capture.failed":
            return new AutoScheduleCaptureFailedEvent(
                envelope, Json.convert(data, ScheduledCapture.class));
          case "capture.changed":
            return new CaptureChangedEvent(envelope, Json.convert(data, ChangedCapture.class));
          case "delivery.completed":
            return new DeliveryCompletedEvent(envelope, Json.convert(data, DeliveredCapture.class));
          case "schedule.paused":
            return new SchedulePausedEvent(envelope, Json.convert(data, SchedulePause.class));
          case "sitemap_run.completed":
            return new SitemapRunCompletedEvent(envelope, Json.convert(data, SitemapRun.class));
          default:
            break;
        }
      }
      return new UnknownWebhookEvent(envelope, rawData(data));
    } catch (IOException e) {
      throw new WebsiteScreenshotWorldException("The webhook body is not a valid event", e);
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> rawData(JsonNode data) throws IOException {
    return data.isObject() ? Json.convert(data, LinkedHashMap.class) : Map.of();
  }
}
