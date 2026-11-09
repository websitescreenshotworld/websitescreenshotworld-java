package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;
import java.time.Instant;
import java.util.UUID;

/**
 * An Instant Snap capture, as {@link InstantSnap#capture}, {@link InstantSnap#captureToBucket},
 * {@link InstantSnap#submit}, {@link InstantSnap#submitToBucket} and {@link InstantSnap#get} return
 * it. The file itself is fetched with {@link InstantSnap#download(Capture, java.nio.file.Path)}.
 */
public final class Capture {
  private UUID id;
  private CaptureStatus status;
  private ResponseType responseType;
  private MediaCategory mediaCategory;
  private CaptureFormat format;
  private String contentType;
  private Long sizeBytes;
  private Integer width;
  private Integer height;
  private Integer durationMs;
  private Integer pageHttpStatus;
  private Instant capturedAt;
  private String finalUrl;
  private String mediaSha256;
  private String downloadUrl;
  private Instant expiresAt;
  private PageSource pageSource;
  private S3DeliveryResult s3Delivery;
  private String callbackUrl;
  private String errorCode;
  private String specVersion;
  private transient boolean idempotentReplayed;

  private Capture() {}

  Capture replayed(boolean replayed) {
    this.idempotentReplayed = replayed;
    return this;
  }

  /**
   * Returns the capture id.
   *
   * @return the id
   */
  public UUID id() {
    return id;
  }

  /**
   * Returns where the capture is in its life. An async capture starts {@link CaptureStatus#QUEUED},
   * then is {@link CaptureStatus#RUNNING} until it ends.
   *
   * @return the status
   */
  public CaptureStatus status() {
    return status;
  }

  /**
   * Returns how the capture answered.
   *
   * @return the response type
   */
  public ResponseType responseType() {
    return responseType;
  }

  /**
   * Returns the kind of file produced.
   *
   * @return the category, or {@code null} while the capture is queued or running
   */
  public MediaCategory mediaCategory() {
    return mediaCategory;
  }

  /**
   * Returns the format written.
   *
   * @return the format, or {@code null} if nothing was written
   */
  public CaptureFormat format() {
    return format;
  }

  /**
   * Returns the file's media type, such as {@code image/png}.
   *
   * @return the media type, or {@code null} if nothing was written
   */
  public String contentType() {
    return contentType;
  }

  /**
   * Returns the file size.
   *
   * @return the size in bytes, or {@code null} if nothing was written
   */
  public Long sizeBytes() {
    return sizeBytes;
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
   * @return the HTTP status, or {@code null} if the website did not answer
   */
  public Integer pageHttpStatus() {
    return pageHttpStatus;
  }

  /**
   * Returns when the page was captured.
   *
   * @return the time, or {@code null} if not captured
   */
  public Instant capturedAt() {
    return capturedAt;
  }

  /**
   * Returns where the browser ended up, when the page redirected.
   *
   * @return the URL, or {@code null} if the page did not redirect
   */
  public String finalUrl() {
    return finalUrl;
  }

  /**
   * Returns the lowercase hex SHA-256 digest of the file.
   *
   * @return the digest, or {@code null} if nothing was written
   */
  public String mediaSha256() {
    return mediaSha256;
  }

  /**
   * Returns a link to the file, valid for 60 minutes. {@link InstantSnap#get} issues a fresh one.
   *
   * @return the URL, or {@code null} for {@link ResponseType#BY_FORMAT} and {@link
   *     ResponseType#CLOUD_STORAGE} captures and in webhooks
   */
  public String downloadUrl() {
    return downloadUrl;
  }

  /**
   * Returns when the file is deleted, 24 hours after the capture.
   *
   * @return the time, or {@code null} if nothing is kept
   */
  public Instant expiresAt() {
    return expiresAt;
  }

  /**
   * Returns the page source kept with the capture.
   *
   * @return the page source, or {@code null} if it was not asked for
   */
  public PageSource pageSource() {
    return pageSource;
  }

  /**
   * Returns the upload to your bucket.
   *
   * @return the upload, or {@code null} unless the capture used {@link InstantSnap#captureToBucket}
   */
  public S3DeliveryResult s3Delivery() {
    return s3Delivery;
  }

  /**
   * Returns the URL that also receives this capture's webhook.
   *
   * @return the URL, or {@code null} if none was set
   */
  public String callbackUrl() {
    return callbackUrl;
  }

  /**
   * Returns why a {@link CaptureStatus#FAILED} or {@link CaptureStatus#TIMED_OUT} capture ended, as
   * an error code such as {@code capture_failed}.
   *
   * @return the code, or {@code null} if the capture did not fail
   */
  public String errorCode() {
    return errorCode;
  }

  /**
   * Returns the version of the default set the capture was taken under, such as {@code v1}.
   *
   * @return the version
   */
  public String specVersion() {
    return specVersion;
  }

  /**
   * Returns whether this answer replayed an earlier request with the same idempotency key, so no
   * new capture was taken.
   *
   * @return {@code true} for a replay
   */
  public boolean idempotentReplayed() {
    return idempotentReplayed;
  }

  /** Returns the capture as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
