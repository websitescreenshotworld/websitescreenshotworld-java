package com.websitescreenshotworld.errors;

/** The API key is missing, malformed, revoked or for the other product: {@code api_key_invalid}. */
public class AuthenticationException extends WebsiteScreenshotWorldException {
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
  public AuthenticationException(
      String message,
      String errorCode,
      Integer httpStatus,
      Integer retryAfterSeconds,
      String rawBody) {
    super(message, errorCode, httpStatus, retryAfterSeconds, rawBody);
  }
}
