package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;
import java.util.Collections;
import java.util.List;

/** What {@link CaptureOptions.Builder#emulatedDevice(String)} and {@link Proxy} accept. */
public final class Catalog {
  private List<String> emulatedDevices;
  private List<String> proxyCountries;

  private Catalog() {}

  /**
   * Returns the devices a capture can emulate, such as {@code "iPhone 13"}.
   *
   * @return the device names
   */
  public List<String> emulatedDevices() {
    return emulatedDevices == null ? List.of() : Collections.unmodifiableList(emulatedDevices);
  }

  /**
   * Returns the countries a capture can use a proxy in, such as {@code "gb"}.
   *
   * @return the countries
   */
  public List<String> proxyCountries() {
    return proxyCountries == null ? List.of() : Collections.unmodifiableList(proxyCountries);
  }

  /** Returns the catalog as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
