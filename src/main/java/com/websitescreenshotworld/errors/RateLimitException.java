package com.websitescreenshotworld.errors;

/**
 * Too many requests: {@code rate_limited}, retried automatically before it is thrown, or {@code
 * queue_full}, too many async captures waiting or running, thrown at once with {@link
 * #retryAfterSeconds()}.
 */
public class RateLimitException extends WebsiteScreenshotWorldException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates the error.
   *
   * @param message the API's message, one sentence for a person
   * @param errorCode the API's error code
   * @param httpStatus the HTTP status of the answer
   * @param retryAfterSeconds how long the API asked to wait before retrying, or {@code null}
   * @param rawBody the body of the answer, or {@code null}
   */
  public RateLimitException(
      String message,
      String errorCode,
      Integer httpStatus,
      Integer retryAfterSeconds,
      String rawBody) {
    super(message, errorCode, httpStatus, retryAfterSeconds, rawBody);
  }
}
