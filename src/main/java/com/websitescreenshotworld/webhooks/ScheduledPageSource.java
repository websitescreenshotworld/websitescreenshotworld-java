package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.PageSourceFormat;
import com.websitescreenshotworld.internal.Json;

/** The page source kept with a scheduled capture. */
public final class ScheduledPageSource {
  private PageSourceFormat format;
  private String sha256;

  private ScheduledPageSource() {}

  /**
   * Returns the form the source was kept in.
   *
   * @return the format
   */
  public PageSourceFormat format() {
    return format;
  }

  /**
   * Returns the lowercase hex SHA-256 digest.
   *
   * @return the digest, or {@code null} if not known
   */
  public String sha256() {
    return sha256;
  }

  /** Returns the page source as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
