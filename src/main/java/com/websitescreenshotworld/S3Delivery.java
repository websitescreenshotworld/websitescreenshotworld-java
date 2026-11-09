package com.websitescreenshotworld;

/**
 * The bucket {@link InstantSnap#captureToBucket} uploads to: Amazon S3 or any S3-compatible store.
 * Needs a {@linkplain Builder#region(String) region} or an {@linkplain Builder#endpointUrl(String)
 * endpoint URL}.
 *
 * <p>The secret access key is sent to the API and never returned; it has no getter here.
 */
public final class S3Delivery {
  private String accessKeyId;
  private String secretAccessKey;
  private String bucket;
  private String region;
  private String endpointUrl;
  private Boolean forcePathStyle;
  private String keyPrefix;
  private Boolean waitForSync;

  private S3Delivery() {}

  private S3Delivery(Builder builder) {
    this.accessKeyId = builder.accessKeyId;
    this.secretAccessKey = builder.secretAccessKey;
    this.bucket = builder.bucket;
    this.region = builder.region;
    this.endpointUrl = builder.endpointUrl;
    this.forcePathStyle = builder.forcePathStyle;
    this.keyPrefix = builder.keyPrefix;
    this.waitForSync = builder.waitForSync;
  }

  /**
   * Starts a bucket destination.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the access key id.
   *
   * @return the access key id
   */
  public String accessKeyId() {
    return accessKeyId;
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
   * Returns the region.
   *
   * @return the region, or {@code null} if not set
   */
  public String region() {
    return region;
  }

  /**
   * Returns the endpoint URL of an S3-compatible store.
   *
   * @return the URL, or {@code null} if not set
   */
  public String endpointUrl() {
    return endpointUrl;
  }

  /**
   * Returns whether path-style addressing is used.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean forcePathStyle() {
    return forcePathStyle;
  }

  /**
   * Returns the prefix put before each object key.
   *
   * @return the prefix, or {@code null} if not set
   */
  public String keyPrefix() {
    return keyPrefix;
  }

  /**
   * Returns whether the API waits for the upload before answering.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean waitForSync() {
    return waitForSync;
  }

  /** Builds an {@link S3Delivery}. */
  public static final class Builder {
    private String accessKeyId;
    private String secretAccessKey;
    private String bucket;
    private String region;
    private String endpointUrl;
    private Boolean forcePathStyle;
    private String keyPrefix;
    private Boolean waitForSync;

    private Builder() {}

    /**
     * Sets the access key id. Required.
     *
     * @param accessKeyId the access key id
     * @return this builder
     */
    public Builder accessKeyId(String accessKeyId) {
      this.accessKeyId = accessKeyId;
      return this;
    }

    /**
     * Sets the secret access key. Required.
     *
     * @param secretAccessKey the secret access key
     * @return this builder
     */
    public Builder secretAccessKey(String secretAccessKey) {
      this.secretAccessKey = secretAccessKey;
      return this;
    }

    /**
     * Sets the bucket name. Required.
     *
     * @param bucket the bucket
     * @return this builder
     */
    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    /**
     * Sets the region, such as {@code eu-west-2}.
     *
     * @param region the region
     * @return this builder
     */
    public Builder region(String region) {
      this.region = region;
      return this;
    }

    /**
     * Sets the endpoint of an S3-compatible store. Must be https and not a private address.
     *
     * @param endpointUrl the endpoint URL
     * @return this builder
     */
    public Builder endpointUrl(String endpointUrl) {
      this.endpointUrl = endpointUrl;
      return this;
    }

    /**
     * Uses path-style addressing ({@code endpoint/bucket/key}). Defaults to off.
     *
     * @param forcePathStyle whether to use path-style addressing
     * @return this builder
     */
    public Builder forcePathStyle(boolean forcePathStyle) {
      this.forcePathStyle = forcePathStyle;
      return this;
    }

    /**
     * Sets a prefix for each object key, such as {@code "captures/"}.
     *
     * @param keyPrefix the prefix
     * @return this builder
     */
    public Builder keyPrefix(String keyPrefix) {
      this.keyPrefix = keyPrefix;
      return this;
    }

    /**
     * Waits up to 30 seconds for the upload before answering. Defaults to off.
     *
     * @param waitForSync whether to wait
     * @return this builder
     */
    public Builder waitForSync(boolean waitForSync) {
      this.waitForSync = waitForSync;
      return this;
    }

    /**
     * Builds the destination.
     *
     * @return the destination
     * @throws IllegalStateException if the access key id, secret access key or bucket is missing
     */
    public S3Delivery build() {
      if (accessKeyId == null || secretAccessKey == null || bucket == null) {
        throw new IllegalStateException("S3Delivery needs accessKeyId, secretAccessKey and bucket");
      }
      return new S3Delivery(this);
    }
  }
}
