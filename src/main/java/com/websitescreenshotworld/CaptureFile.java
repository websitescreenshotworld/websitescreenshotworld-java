package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Timestamps;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * A file returned by {@link InstantSnap#captureFile}, with its metadata.
 *
 * <p>The file is streamed from the connection, not held in memory: read {@link #body()} once, or
 * call {@link #saveTo(Path)}. Close it when done, ideally with try-with-resources, so the
 * connection is released.
 *
 * <pre>{@code
 * try (CaptureFile file = client.instantSnap().captureFile(options)) {
 *   file.saveTo(Path.of(file.filename()));
 * }
 * }</pre>
 */
public final class CaptureFile implements Closeable {
  private final InputStream body;
  private final String contentType;
  private final String filename;
  private final UUID id;
  private final CaptureStatus status;
  private final Integer width;
  private final Integer height;
  private final Long sizeBytes;
  private final Integer durationMs;
  private final Integer pageHttpStatus;
  private final String specVersion;
  private final Instant capturedAt;
  private final String finalUrl;
  private final String sha256;
  private final Integer partCount;
  private final boolean idempotentReplayed;

  CaptureFile(InputStream body, HttpHeaders headers) {
    this.body = body;
    this.contentType = headers.firstValue("Content-Type").orElse(null);
    this.filename =
        headers.firstValue("Content-Disposition").map(ContentDisposition::filename).orElse(null);
    this.id = uuid(meta(headers, "Id"));
    String statusValue = meta(headers, "Status");
    this.status = statusValue == null ? null : CaptureStatus.of(statusValue);
    this.width = integer(meta(headers, "Width"));
    this.height = integer(meta(headers, "Height"));
    this.sizeBytes = longValue(meta(headers, "Size-Bytes"));
    this.durationMs = integer(meta(headers, "Duration-Ms"));
    this.pageHttpStatus = integer(meta(headers, "Page-Http-Status"));
    this.specVersion = meta(headers, "Spec-Version");
    this.capturedAt = instant(meta(headers, "Captured-At"));
    this.finalUrl = meta(headers, "Final-Url");
    this.sha256 = meta(headers, "Sha256");
    this.partCount = integer(meta(headers, "Part-Count"));
    this.idempotentReplayed =
        headers.firstValue("Idempotent-Replayed").map("true"::equalsIgnoreCase).orElse(false);
  }

  /**
   * Returns the file's bytes as a stream that can be read once.
   *
   * @return the stream
   */
  public InputStream body() {
    return body;
  }

  /**
   * Writes the file to {@code target}, replacing any file there, and closes the stream.
   *
   * @param target where to write
   * @throws UncheckedIOException if the file cannot be read or written
   */
  public void saveTo(Path target) {
    try (InputStream in = body) {
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns the file's media type, such as {@code image/png}.
   *
   * @return the media type, or {@code null} if the answer had none
   */
  public String contentType() {
    return contentType;
  }

  /**
   * Returns the file name the API suggests, such as {@code <id>.png}.
   *
   * @return the name, or {@code null} if the answer had none
   */
  public String filename() {
    return filename;
  }

  /**
   * Returns the capture id.
   *
   * @return the id, or {@code null} if the answer had none
   */
  public UUID id() {
    return id;
  }

  /**
   * Returns the capture status, {@link CaptureStatus#DELIVERED} for a file answer.
   *
   * @return the status, or {@code null} if the answer had none
   */
  public CaptureStatus status() {
    return status;
  }

  /**
   * Returns the file's width.
   *
   * @return the width in pixels, or {@code null} if not known
   */
  public Integer width() {
    return width;
  }

  /**
   * Returns the file's height.
   *
   * @return the height in pixels, or {@code null} if not known
   */
  public Integer height() {
    return height;
  }

  /**
   * Returns the file size.
   *
   * @return the size in bytes, or {@code null} if not known
   */
  public Long sizeBytes() {
    return sizeBytes;
  }

  /**
   * Returns how long the capture took.
   *
   * @return the duration in milliseconds, or {@code null} if not known
   */
  public Integer durationMs() {
    return durationMs;
  }

  /**
   * Returns the status the website answered with.
   *
   * @return the HTTP status, or {@code null} if not known
   */
  public Integer pageHttpStatus() {
    return pageHttpStatus;
  }

  /**
   * Returns the version of the default set the capture was taken under.
   *
   * @return the version, or {@code null} if not known
   */
  public String specVersion() {
    return specVersion;
  }

  /**
   * Returns when the page was captured.
   *
   * @return the time, or {@code null} if not known
   */
  public Instant capturedAt() {
    return capturedAt;
  }

  /**
   * Returns where the browser ended up.
   *
   * @return the URL, or {@code null} if not known or not plain ASCII
   */
  public String finalUrl() {
    return finalUrl;
  }

  /**
   * Returns the lowercase hex SHA-256 digest of the file.
   *
   * @return the digest, or {@code null} if not known
   */
  public String sha256() {
    return sha256;
  }

  /**
   * Returns how many parts the answer has.
   *
   * @return the count, or {@code null} for a single-part answer
   */
  public Integer partCount() {
    return partCount;
  }

  /**
   * Returns whether this answer replayed an earlier request with the same idempotency key.
   *
   * @return {@code true} for a replay
   */
  public boolean idempotentReplayed() {
    return idempotentReplayed;
  }

  /**
   * Closes the stream and releases the connection.
   *
   * @throws UncheckedIOException if closing fails
   */
  @Override
  public void close() {
    try {
      body.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String meta(HttpHeaders headers, String name) {
    return headers.firstValue("X-InstantSnap-" + name).orElse(null);
  }

  // Metadata headers are informational, so a malformed one reads as absent rather than failing
  // a capture that has already been paid for.

  private static Integer integer(String value) {
    try {
      return value == null ? null : Integer.valueOf(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static Long longValue(String value) {
    try {
      return value == null ? null : Long.valueOf(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static UUID uuid(String value) {
    try {
      return value == null ? null : UUID.fromString(value.trim());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  private static Instant instant(String value) {
    try {
      return value == null ? null : Timestamps.parse(value);
    } catch (DateTimeParseException e) {
      return null;
    }
  }
}
