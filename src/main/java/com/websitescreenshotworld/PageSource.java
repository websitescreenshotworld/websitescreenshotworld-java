package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;

/** The page source kept with a capture. */
public final class PageSource {
  private PageSourceFormat format;
  private String contentType;
  private Long sizeBytes;
  private String sha256;
  private String downloadUrl;

  private PageSource() {}

  /**
   * Returns the form the source was kept in.
   *
   * @return the format
   */
  public PageSourceFormat format() {
    return format;
  }

  /**
   * Returns the media type, such as {@code text/html}.
   *
   * @return the media type, or {@code null} if nothing was kept
   */
  public String contentType() {
    return contentType;
  }

  /**
   * Returns the size.
   *
   * @return the size in bytes, or {@code null} if nothing was kept
   */
  public Long sizeBytes() {
    return sizeBytes;
  }

  /**
   * Returns the lowercase hex SHA-256 digest.
   *
   * @return the digest, or {@code null} if nothing was kept
   */
  public String sha256() {
    return sha256;
  }

  /**
   * Returns a short-lived link to the source, reissued on every read.
   *
   * @return the URL, or {@code null} if there is none (always in webhooks)
   */
  public String downloadUrl() {
    return downloadUrl;
  }

  /** Returns the page source as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
