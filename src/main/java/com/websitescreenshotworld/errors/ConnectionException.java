package com.websitescreenshotworld.errors;

/**
 * The request could not be sent, or no answer came back in time. Retried automatically before it is
 * thrown, unless the call's timeout ran out.
 */
public class ConnectionException extends WebsiteScreenshotWorldException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates the error.
   *
   * @param message what went wrong
   * @param cause the underlying failure, or {@code null}
   */
  public ConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
