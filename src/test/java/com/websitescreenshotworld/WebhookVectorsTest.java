package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.errors.SignatureVerificationException;
import com.websitescreenshotworld.webhooks.PingEvent;
import com.websitescreenshotworld.webhooks.Webhook;
import com.websitescreenshotworld.webhooks.WebhookEvent;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/** Runs every case in spec/webhook-vectors.json. */
class WebhookVectorsTest {
  private static final JsonNode VECTORS = Spec.read(Spec.path("webhook-vectors.json"));
  private static final String SECRET = VECTORS.get("secret").asText();

  @TestFactory
  Stream<DynamicTest> vectors() {
    return StreamSupport.stream(VECTORS.get("cases").spliterator(), false)
        .map(vector -> DynamicTest.dynamicTest(vector.get("name").asText(), () -> check(vector)));
  }

  private static void check(JsonNode vector) {
    String body = vector.get("body").asText();
    String header = vector.get("header").asText();
    Duration tolerance = Duration.ofSeconds(vector.path("tolerance").asLong(300));
    Clock clock = Clock.fixed(Instant.ofEpochSecond(vector.get("now").asLong()), ZoneOffset.UTC);

    if (vector.get("valid").asBoolean()) {
      WebhookEvent event = Webhook.verify(body, header, SECRET, tolerance, clock);
      assertInstanceOf(PingEvent.class, event);
      WebhookEvent fromBytes =
          Webhook.verify(body.getBytes(StandardCharsets.UTF_8), header, SECRET, tolerance, clock);
      assertEquals(event.id(), fromBytes.id());
    } else {
      assertThrows(
          SignatureVerificationException.class,
          () -> Webhook.verify(body, header, SECRET, tolerance, clock));
    }
  }

  @Test
  void missingHeaderIsRefused() {
    assertThrows(SignatureVerificationException.class, () -> Webhook.verify("{}", null, SECRET));
  }

  @Test
  void defaultToleranceIsFiveMinutes() {
    assertEquals(Duration.ofSeconds(300), Webhook.DEFAULT_TOLERANCE);
  }
}
