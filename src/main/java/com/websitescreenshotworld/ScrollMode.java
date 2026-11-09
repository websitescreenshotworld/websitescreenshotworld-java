package com.websitescreenshotworld;

import java.util.Map;

/**
 * How much of the page a capture covers.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class ScrollMode extends Token {
  /** Only what fits in the viewport. */
  public static final ScrollMode VIEWPORT = new ScrollMode("viewport");

  /** The full page, scrolled from top to bottom. */
  public static final ScrollMode SCROLL = new ScrollMode("scroll");

  private static final Map<String, ScrollMode> KNOWN = index(VIEWPORT, SCROLL);

  private ScrollMode(String value) {
    super(value);
  }

  /**
   * Returns the ScrollMode for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static ScrollMode of(String value) {
    return lookup(KNOWN, value, ScrollMode::new);
  }
}
