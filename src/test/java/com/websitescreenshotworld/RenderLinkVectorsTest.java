package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/** Runs every case in spec/render-link-vectors.json. */
class RenderLinkVectorsTest {
  private static final JsonNode VECTORS = Spec.read(Spec.path("render-link-vectors.json"));

  @TestFactory
  Stream<DynamicTest> vectors() {
    return StreamSupport.stream(VECTORS.get("cases").spliterator(), false)
        .map(vector -> DynamicTest.dynamicTest(vector.get("name").asText(), () -> check(vector)));
  }

  private static void check(JsonNode vector) {
    JsonNode options = vector.get("options");
    RenderLinkOptions.Builder builder =
        RenderLinkOptions.builder(
            vector.get("render_secret").asText(),
            vector.get("key_prefix").asText(),
            options.get("url").asText());
    if (vector.has("format")) {
      builder.format(CaptureFormat.of(vector.get("format").asText()));
    }
    if (vector.has("base_url")) {
      builder.baseUrl(vector.get("base_url").asText());
    }
    for (Map.Entry<String, JsonNode> option : options.properties()) {
      apply(builder, option.getKey(), option.getValue());
    }

    assertEquals(vector.get("url").asText(), RenderLink.build(builder.build()));
  }

  private static void apply(RenderLinkOptions.Builder builder, String name, JsonNode value) {
    switch (name) {
      case "url":
        break;
      case "ttl":
        builder.ttlSeconds(value.asInt());
        break;
      case "width":
        builder.width(value.asInt());
        break;
      case "height":
        builder.height(value.asInt());
        break;
      case "device_scale_factor":
        builder.deviceScaleFactor(value.asDouble());
        break;
      case "device_category":
        builder.deviceCategory(DeviceCategory.of(value.asText()));
        break;
      case "emulated_device":
        builder.emulatedDevice(value.asText());
        break;
      case "scroll_mode":
        builder.scrollMode(ScrollMode.of(value.asText()));
        break;
      case "auto_height":
        builder.autoHeight(value.asBoolean());
        break;
      case "max_height":
        builder.maxHeight(value.asInt());
        break;
      case "zoom":
        builder.zoom(value.asInt());
        break;
      case "block_ads":
        builder.blockAds(value.asBoolean());
        break;
      case "block_pop_ups":
        builder.blockPopUps(value.asBoolean());
        break;
      case "press_escape":
        builder.pressEscape(value.asBoolean());
        break;
      case "page_timeout_ms":
        builder.pageTimeoutMs(value.asInt());
        break;
      case "scrolling_delay_ms":
        builder.scrollingDelayMs(value.asInt());
        break;
      case "locale":
        builder.locale(value.asText());
        break;
      case "time_zone":
        builder.timeZone(value.asText());
        break;
      case "hide":
        builder.hide(value.asText());
        break;
      case "xpath":
        builder.xpath(value.asText());
        break;
      case "click":
        builder.click(value.asText());
        break;
      case "hover":
        builder.hover(value.asText());
        break;
      case "country":
        builder.country(value.asText());
        break;
      case "thumb_width":
        builder.thumbWidth(value.asInt());
        break;
      default:
        throw new IllegalStateException(
            "The vector names an option this test does not know: " + name);
    }
  }

  @Test
  void refusesWhatCannotBeALink() {
    String link =
        RenderLink.build(
            RenderLinkOptions.builder("wsw_rsk_x", "0123456789ab", "https://example.com").build());
    assertTrue(link.matches(".*&sig=[0-9a-f]{64}$"));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            RenderLink.build(
                RenderLinkOptions.builder("", "0123456789ab", "https://example.com").build()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            RenderLink.build(
                RenderLinkOptions.builder("wsw_rsk_x", "0123456789AB", "https://example.com")
                    .build()));
    assertThrows(
        IllegalArgumentException.class,
        () -> RenderLink.build(RenderLinkOptions.builder("wsw_rsk_x", "0123456789ab", "").build()));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            RenderLink.build(
                RenderLinkOptions.builder("wsw_rsk_x", "0123456789ab", "https://example.com")
                    .format(CaptureFormat.IMAGE_PDF)
                    .build()));
  }

  @Test
  void leavesOutAnOptionThatIsNotSet() {
    String link =
        RenderLink.build(
            RenderLinkOptions.builder("wsw_rsk_x", "0123456789ab", "https://example.com")
                .blockAds(false)
                .build());
    assertTrue(link.contains("block_ads=false"));
    assertFalse(link.contains("width="));
  }
}
