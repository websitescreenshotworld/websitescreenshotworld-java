package com.websitescreenshotworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.websitescreenshotworld.internal.Json;
import com.websitescreenshotworld.internal.Timestamps;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Dates, tokens, file names and unknown fields in answers. */
class ParsingTest {
  @ParameterizedTest
  @ValueSource(
      strings = {
        "2026-09-26T10:30:04.512Z",
        "2026-09-26T10:30:04.512000Z",
        "2026-09-26T10:30:04.512000000Z",
        "2026-09-26T16:00:04.512+05:30",
        "2026-09-26T10:30:04.512+00:00"
      })
  void datesAreReadLeniently(String text) {
    assertEquals(Instant.parse("2026-09-26T10:30:04.512Z"), Timestamps.parse(text));
  }

  @Test
  void datesWithoutFractionAreRead() {
    assertEquals(Instant.parse("2026-09-26T10:30:04Z"), Timestamps.parse("2026-09-26T10:30:04Z"));
    assertEquals(
        Instant.parse("2026-09-26T10:30:04.5Z"), Timestamps.parse("2026-09-26T10:30:04.5Z"));
  }

  @Test
  void knownTokensAreTheConstants() {
    assertSame(CaptureStatus.SUCCEEDED, CaptureStatus.of("succeeded"));
    assertSame(CaptureFormat.DOCUMENT_PDF, CaptureFormat.of("document_pdf"));
  }

  @Test
  void unknownTokensComeThrough() {
    CaptureStatus status = CaptureStatus.of("archived");
    assertEquals("archived", status.value());
    assertEquals(CaptureStatus.of("archived"), status);
    assertEquals("archived", status.toString());
  }

  @Test
  void unknownFieldsAndValuesDoNotBreakAnswers() throws Exception {
    String body =
        "{\"success\":true,\"id\":\"3f6c2a1e-9b8d-4c7e-a5f4-1d2e3c4b5a69\",\"status\":\"archived\","
            + "\"format\":\"avif\",\"brand_new_field\":{\"nested\":[1,2]},"
            + "\"captured_at\":\"2026-09-26T10:30:04.51234Z\"}";
    Capture capture = Json.read(body.getBytes(StandardCharsets.UTF_8), Capture.class);
    assertEquals("archived", capture.status().value());
    assertEquals(CaptureFormat.of("avif"), capture.format());
    assertEquals(Instant.parse("2026-09-26T10:30:04.51234Z"), capture.capturedAt());
    assertNull(capture.pageSource());
  }

  @Test
  void filenameComesFromContentDisposition() {
    assertEquals("a.png", ContentDisposition.filename("attachment; filename=\"a.png\""));
    assertEquals("a.png", ContentDisposition.filename("attachment; filename=a.png"));
    assertEquals(
        "naïve.png",
        ContentDisposition.filename(
            "attachment; filename=\"naive.png\"; filename*=UTF-8''na%C3%AFve.png"));
    assertEquals("x; y.png", ContentDisposition.filename("attachment; filename=\"x; y.png\""));
    assertEquals(
        "passwd", ContentDisposition.filename("attachment; filename=\"../../etc/passwd\""));
    assertNull(ContentDisposition.filename("attachment"));
  }
}
