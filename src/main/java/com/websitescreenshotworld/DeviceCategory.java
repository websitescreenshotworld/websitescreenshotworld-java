package com.websitescreenshotworld;

import java.util.Map;

/**
 * The kind of browser a capture emulates.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class DeviceCategory extends Token {
  /** A desktop browser. */
  public static final DeviceCategory DESKTOP = new DeviceCategory("desktop");

  /** A mobile device; see {@link CaptureOptions.Builder#emulatedDevice(String)}. */
  public static final DeviceCategory MOBILE = new DeviceCategory("mobile");

  private static final Map<String, DeviceCategory> KNOWN = index(DESKTOP, MOBILE);

  private DeviceCategory(String value) {
    super(value);
  }

  /**
   * Returns the DeviceCategory for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static DeviceCategory of(String value) {
    return lookup(KNOWN, value, DeviceCategory::new);
  }
}
