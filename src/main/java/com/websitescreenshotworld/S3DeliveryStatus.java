package com.websitescreenshotworld;

import java.util.Map;

/**
 * How far the upload to your bucket has got.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class S3DeliveryStatus extends Token {
  /** Not started yet. */
  public static final S3DeliveryStatus NOT_STARTED = new S3DeliveryStatus("not_started");

  /** Uploading. */
  public static final S3DeliveryStatus IN_PROGRESS = new S3DeliveryStatus("in_progress");

  /** Uploaded. */
  public static final S3DeliveryStatus COMPLETED = new S3DeliveryStatus("completed");

  /** The upload failed; see {@link S3DeliveryResult#error()}. */
  public static final S3DeliveryStatus FAILED = new S3DeliveryStatus("failed");

  private static final Map<String, S3DeliveryStatus> KNOWN =
      index(NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED);

  private S3DeliveryStatus(String value) {
    super(value);
  }

  /**
   * Returns the S3DeliveryStatus for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static S3DeliveryStatus of(String value) {
    return lookup(KNOWN, value, S3DeliveryStatus::new);
  }
}
