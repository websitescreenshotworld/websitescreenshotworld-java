package com.websitescreenshotworld.errors;

/**
 * A webhook's {@code X-WSW-Signature} header is missing, malformed, too old or does not match the
 * body. Answer the delivery with a 400 and do not act on it.
 */
public class SignatureVerificationException extends WebsiteScreenshotWorldException {
  private static final long serialVersionUID = 1L;

  /**
   * Creates the error.
   *
   * @param message why the signature was refused
   */
  public SignatureVerificationException(String message) {
    super(message, null);
  }
}
