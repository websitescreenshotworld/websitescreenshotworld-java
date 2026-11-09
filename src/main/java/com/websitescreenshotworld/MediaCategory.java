package com.websitescreenshotworld;

import java.util.Map;

/**
 * What kind of file a capture produces.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class MediaCategory extends Token {
  /** An image: PNG, JPEG, WebP or TIFF. */
  public static final MediaCategory IMAGE = new MediaCategory("image");

  /** A video of the page scrolling: MP4, WebM, MOV or GIF. */
  public static final MediaCategory VIDEO = new MediaCategory("video");

  /** A PDF. */
  public static final MediaCategory PDF = new MediaCategory("pdf");

  private static final Map<String, MediaCategory> KNOWN = index(IMAGE, VIDEO, PDF);

  private MediaCategory(String value) {
    super(value);
  }

  /**
   * Returns the MediaCategory for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static MediaCategory of(String value) {
    return lookup(KNOWN, value, MediaCategory::new);
  }
}
