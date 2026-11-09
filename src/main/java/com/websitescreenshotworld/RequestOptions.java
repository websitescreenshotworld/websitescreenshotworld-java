package com.websitescreenshotworld;

import java.time.Duration;
import java.util.regex.Pattern;

/**
 * Settings for one call that override the client's.
 *
 * <pre>{@code
 * RequestOptions options = RequestOptions.builder()
 *     .idempotencyKey("order-1234-hero")
 *     .timeout(Duration.ofMinutes(4))
 *     .build();
 * }</pre>
 */
public final class RequestOptions {
  private static final Pattern IDEMPOTENCY_KEY = Pattern.compile("[\\x21-\\x7E]{1,255}");
  private static final RequestOptions NONE = new RequestOptions(builder());

  private final String idempotencyKey;
  private final Duration timeout;

  private RequestOptions(Builder builder) {
    this.idempotencyKey = builder.idempotencyKey;
    this.timeout = builder.timeout;
  }

  /**
   * Starts a set of request options.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns request options that change nothing.
   *
   * @return the empty options
   */
  public static RequestOptions none() {
    return NONE;
  }

  /**
   * Returns the idempotency key captures send.
   *
   * @return the key, or {@code null} to use a new random key for each call
   */
  public String idempotencyKey() {
    return idempotencyKey;
  }

  /**
   * Returns how long the call may take, retries included.
   *
   * @return the timeout, or {@code null} to use the client's
   */
  public Duration timeout() {
    return timeout;
  }

  /** Builds {@link RequestOptions}. */
  public static final class Builder {
    private String idempotencyKey;
    private Duration timeout;

    private Builder() {}

    /**
     * Sets the {@code Idempotency-Key} a capture sends, so a repeat of the call within 24 hours
     * gets the first capture back instead of taking a new one. Without it, each capture call uses a
     * new random key, shared by that call's retries. Ignored by calls other than captures.
     *
     * @param idempotencyKey 1 to 255 printable ASCII characters, no spaces
     * @return this builder
     * @throws IllegalArgumentException if the key has other characters or is too long
     */
    public Builder idempotencyKey(String idempotencyKey) {
      if (!IDEMPOTENCY_KEY.matcher(idempotencyKey).matches()) {
        throw new IllegalArgumentException(
            "An idempotency key is 1 to 255 printable ASCII characters without spaces");
      }
      this.idempotencyKey = idempotencyKey;
      return this;
    }

    /**
     * Sets how long the call may take, retries included. Once it runs out, the call stops with a
     * {@link com.websitescreenshotworld.errors.ConnectionException} and is not retried.
     *
     * @param timeout a positive duration
     * @return this builder
     * @throws IllegalArgumentException if the duration is zero or negative
     */
    public Builder timeout(Duration timeout) {
      if (timeout.isNegative() || timeout.isZero()) {
        throw new IllegalArgumentException("timeout must be positive");
      }
      this.timeout = timeout;
      return this;
    }

    /**
     * Builds the request options.
     *
     * @return the request options
     */
    public RequestOptions build() {
      return new RequestOptions(this);
    }
  }
}
