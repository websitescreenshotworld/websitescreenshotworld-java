package com.websitescreenshotworld.errors;

/**
 * The service could not handle the request, such as {@code capture_busy} or {@code
 * service_unavailable}. The temporary ones are retried automatically before this is thrown.
 */
public class ServiceException extends WebsiteScreenshotWorldException {
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
  public ServiceException(
      String message,
      String errorCode,
      Integer httpStatus,
      Integer retryAfterSeconds,
      String rawBody) {
    super(message, errorCode, httpStatus, retryAfterSeconds, rawBody);
  }
}
