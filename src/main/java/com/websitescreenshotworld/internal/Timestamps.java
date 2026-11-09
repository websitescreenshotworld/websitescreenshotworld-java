package com.websitescreenshotworld.internal;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Parses the API's date-times. Not part of the public API. */
public final class Timestamps {
  private Timestamps() {}

  /**
   * Parses an ISO 8601 date-time with any fraction length and either {@code Z} or an offset.
   *
   * @param text the date-time
   * @return the instant
   * @throws DateTimeParseException if {@code text} is not an ISO 8601 date-time with an offset
   */
  public static Instant parse(String text) {
    return OffsetDateTime.parse(text.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
  }
}
