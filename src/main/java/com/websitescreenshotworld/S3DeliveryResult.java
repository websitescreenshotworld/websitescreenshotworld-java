package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;

/**
 * How the upload to your bucket went, for a capture taken with {@link InstantSnap#captureToBucket}.
 */
public final class S3DeliveryResult {
  private S3DeliveryStatus status;
  private String bucket;
  private String key;
  private String accessKeyId;
  private String error;

  private S3DeliveryResult() {}

  /**
   * Returns how far the upload has got.
   *
   * @return the status
   */
  public S3DeliveryStatus status() {
    return status;
  }

  /**
   * Returns the bucket name.
   *
   * @return the bucket
   */
  public String bucket() {
    return bucket;
  }

  /**
   * Returns the object key.
   *
   * @return the key, or {@code null} until the upload completes
   */
  public String key() {
    return key;
  }

  /**
   * Returns the access key id, masked to its last four characters.
   *
   * @return the masked id, or {@code null} (always in webhooks)
   */
  public String accessKeyId() {
    return accessKeyId;
  }

  /**
   * Returns why the upload failed.
   *
   * @return the reason, or {@code null} if it did not fail
   */
  public String error() {
    return error;
  }

  /** Returns the upload as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
