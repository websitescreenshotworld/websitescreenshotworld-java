package com.websitescreenshotworld.errors;

/**
 * The base of every error this library throws.
 *
 * <p>When the API refused a request, {@link #errorCode()} holds its stable, lowercase code; switch
 * on that, never on the status or the message. This class itself is thrown for a code this library
 * does not know yet; each known kind has a subclass.
 */
public class WebsiteScreenshotWorldException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /** The API's error code, or {@code null}. */
  private final String errorCode;

  /** The HTTP status of the answer, or {@code null}. */
  private final Integer httpStatus;

  /** How long the API asked to wait before retrying, or {@code null}. */
  private final Integer retryAfterSeconds;

  /** The body of the answer, or {@code null}. */
  private final String rawBody;

  /**
   * Creates an error for a refusal from the API.
   *
   * @param message the API's message, one sentence for a person
   * @param errorCode the API's error code
   * @param httpStatus the HTTP status of the answer
   * @param retryAfterSeconds how long the API asked to wait before retrying, or {@code null}
   * @param rawBody the body of the answer, or {@code null}
   */
  public WebsiteScreenshotWorldException(
      String message,
      String errorCode,
      Integer httpStatus,
      Integer retryAfterSeconds,
      String rawBody) {
    super(message);
    this.errorCode = errorCode;
    this.httpStatus = httpStatus;
    this.retryAfterSeconds = retryAfterSeconds;
    this.rawBody = rawBody;
  }

  /**
   * Creates an error that did not come from an API refusal.
   *
   * @param message what went wrong
   * @param cause the underlying failure, or {@code null}
   */
  public WebsiteScreenshotWorldException(String message, Throwable cause) {
    super(message, cause);
    this.errorCode = null;
    this.httpStatus = null;
    this.retryAfterSeconds = null;
    this.rawBody = null;
  }

  /**
   * Returns the API's error code, such as {@code rate_limited}.
   *
   * @return the code, or {@code null} if the API did not refuse the request
   */
  public String errorCode() {
    return errorCode;
  }

  /**
   * Returns the HTTP status of the answer.
   *
   * @return the status, or {@code null} if no answer came back
   */
  public Integer httpStatus() {
    return httpStatus;
  }

  /**
   * Returns how long the API asked to wait before retrying, from the body or the {@code
   * Retry-After} header.
   *
   * @return the wait in seconds, or {@code null} if none was given
   */
  public Integer retryAfterSeconds() {
    return retryAfterSeconds;
  }

  /**
   * Returns the body of the answer as text.
   *
   * @return the body, or {@code null} if there was none
   */
  public String rawBody() {
    return rawBody;
  }
}
