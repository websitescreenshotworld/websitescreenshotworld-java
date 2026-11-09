package com.websitescreenshotworld;

import java.util.Objects;

/**
 * Also keeps the page source, returned as {@link Capture#pageSource()}. Charged as a second
 * capture, capped at 10 MB, and not available with {@link InstantSnap#captureFile}.
 */
public final class PageSourceOptions {
  private PageSourceFormat format;

  private PageSourceOptions() {}

  private PageSourceOptions(PageSourceFormat format) {
    this.format = Objects.requireNonNull(format, "format");
  }

  /**
   * Keeps the page source in a format.
   *
   * @param format the format
   * @return the options
   */
  public static PageSourceOptions of(PageSourceFormat format) {
    return new PageSourceOptions(format);
  }

  /**
   * Returns the format.
   *
   * @return the format
   */
  public PageSourceFormat format() {
    return format;
  }
}
