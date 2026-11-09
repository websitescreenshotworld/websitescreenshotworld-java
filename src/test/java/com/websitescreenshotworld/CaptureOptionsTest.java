package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.internal.Json;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CaptureOptionsTest {
  @Test
  void onlyWhatIsSetIsSent() throws Exception {
    JsonNode wire = Json.toTree(CaptureOptions.builder().url("https://example.com").build());
    assertEquals(json("{\"url\":\"https://example.com\"}"), wire);
  }

  @Test
  void falseZeroAndEmptyAreValues() throws Exception {
    CaptureOptions options =
        CaptureOptions.builder()
            .url("https://example.com")
            .blockAds(false)
            .scrollingDelayMs(0)
            .userAgent("")
            .viewport(Viewport.builder().width(1440).build())
            .timestampWatermark(TimestampWatermark.builder().build())
            .build();
    assertEquals(
        json(
            "{\"url\":\"https://example.com\",\"block_ads\":false,\"scrolling_delay_ms\":0,"
                + "\"user_agent\":\"\",\"viewport\":{\"width\":1440},\"timestamp_watermark\":{}}"),
        Json.toTree(options));
  }

  @Test
  void headerAndCookieNamesAreSentAsGiven() {
    CaptureOptions options =
        CaptureOptions.builder()
            .url("https://example.com")
            .header("sessionId", "abc")
            .headers(Map.of("X-Trace", "1"))
            .cookie("Theme_Mode", "dark")
            .build();
    JsonNode wire = Json.toTree(options);
    assertEquals("1", wire.get("headers").get("X-Trace").asText());
    assertEquals(1, wire.get("headers").size());
    assertEquals("dark", wire.get("cookies").get("Theme_Mode").asText());
  }

  @Test
  void everyBlockUsesTheApiNames() {
    CaptureOptions options =
        CaptureOptions.builder()
            .url("https://example.com")
            .mediaCategory(MediaCategory.PDF)
            .format(CaptureFormat.DOCUMENT_PDF)
            .deviceCategory(DeviceCategory.MOBILE)
            .emulatedDevice("Pixel 7")
            .scrollMode(ScrollMode.SCROLL)
            .autoHeight(true)
            .maxHeight(4000)
            .zoom(90)
            .blockPopUps(true)
            .pageTimeoutMs(20000)
            .locale("en-GB")
            .timeZone("Europe/London")
            .geolocation(Geolocation.of(51.5, -0.12, 50))
            .selectors(Selectors.builder().xpath("//main").click("#accept").hover(".menu").build())
            .proxy(Proxy.of("gb"))
            .pageSource(PageSourceOptions.of(PageSourceFormat.TEXT))
            .s3Delivery(
                S3Delivery.builder()
                    .accessKeyId("AKIA")
                    .secretAccessKey("secret")
                    .bucket("b")
                    .endpointUrl("https://s3.example.com")
                    .forcePathStyle(true)
                    .waitForSync(true)
                    .build())
            .callbackUrl("https://hooks.example.com")
            .build();
    JsonNode wire = Json.toTree(options);
    for (String field :
        new String[] {
          "media_category",
          "format",
          "device_category",
          "emulated_device",
          "scroll_mode",
          "auto_height",
          "max_height",
          "zoom",
          "block_pop_ups",
          "page_timeout_ms",
          "locale",
          "time_zone",
          "geolocation",
          "selectors",
          "proxy",
          "page_source",
          "s3_delivery",
          "callback_url"
        }) {
      assertTrue(wire.has(field), field);
    }
    assertEquals("document_pdf", wire.get("format").asText());
    assertEquals(50, wire.get("geolocation").get("accuracy").asInt());
    assertEquals("secret", wire.get("s3_delivery").get("secret_access_key").asText());
    assertTrue(wire.get("s3_delivery").get("force_path_style").asBoolean());
  }

  @Test
  void timeoutSecondsIsSentAsTimeout() throws Exception {
    CaptureOptions options =
        CaptureOptions.builder().url("https://example.com").timeoutSeconds(240).build();
    assertEquals(240, options.timeoutSeconds());
    assertEquals(json("{\"url\":\"https://example.com\",\"timeout\":240}"), Json.toTree(options));
    assertEquals(
        240, Json.convert(Json.toTree(options), CaptureOptions.class).timeoutSeconds().intValue());
  }

  @Test
  void urlIsRequired() {
    assertThrows(IllegalStateException.class, () -> CaptureOptions.builder().build());
  }

  @Test
  void idempotencyKeyMustBePrintableAscii() {
    assertThrows(
        IllegalArgumentException.class, () -> RequestOptions.builder().idempotencyKey("has space"));
  }

  private static JsonNode json(String text) throws Exception {
    return Json.readTree(text.getBytes(StandardCharsets.UTF_8));
  }
}
