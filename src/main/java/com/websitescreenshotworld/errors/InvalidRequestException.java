package com.websitescreenshotworld.errors;

/**
 * The request was not valid, such as {@code validation_failed} or {@code idempotency_key_reused}.
 * Not retried.
 */
public class InvalidRequestException extends WebsiteScreenshotWorldException {
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
  public InvalidRequestException(
      String message,
      String errorCode,
      Integer httpStatus,
      Integer retryAfterSeconds,
      String rawBody) {
    super(message, errorCode, httpStatus, retryAfterSeconds, rawBody);
  }
}
