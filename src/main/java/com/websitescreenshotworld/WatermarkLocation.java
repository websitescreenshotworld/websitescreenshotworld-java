package com.websitescreenshotworld;

import java.util.Map;

/**
 * Where the timestamp watermark goes.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class WatermarkLocation extends Token {
  /** Above the capture. */
  public static final WatermarkLocation TOP = new WatermarkLocation("top");

  /** Below the capture. */
  public static final WatermarkLocation BOTTOM = new WatermarkLocation("bottom");

  private static final Map<String, WatermarkLocation> KNOWN = index(TOP, BOTTOM);

  private WatermarkLocation(String value) {
    super(value);
  }

  /**
   * Returns the WatermarkLocation for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static WatermarkLocation of(String value) {
    return lookup(KNOWN, value, WatermarkLocation::new);
  }
}
