package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The values a capture takes for options it does not send, and the limits it is checked against,
 * versioned by {@link #specVersion()}. Getters return {@code null} for anything the API did not
 * include.
 */
public final class Defaults {
  private String specVersion;
  private ResponseType responseType;
  private MediaCategory mediaCategory;
  private Map<String, CaptureFormat> format;
  private Map<String, List<CaptureFormat>> formats;
  private DeviceCategory deviceCategory;
  private String emulatedDevice;
  private Viewport viewport;
  private Integer zoom;
  private ScrollMode scrollMode;
  private Boolean autoHeight;
  private Integer autoHeightMaxHeight;
  private Integer autoHeightMaxHeightWebp;
  private Boolean blockAds;
  private Boolean blockPopUps;
  private Boolean pressEscape;
  private Integer pageTimeoutMs;
  private Integer scrollingDelayMs;
  private Boolean geolocation;
  private Double geolocationAccuracy;
  private Boolean waitForCloudSync;
  private Map<String, Object> timestampWatermark;
  private Map<String, Object> pageSource;
  private Map<String, Object> limits;
  private List<String> leftToTheBrowser;

  private Defaults() {}

  private static <K, V> Map<K, V> view(Map<K, V> map) {
    return map == null ? null : Collections.unmodifiableMap(map);
  }

  /**
   * Returns the version of this default set, such as {@code v1}.
   *
   * @return the version
   */
  public String specVersion() {
    return specVersion;
  }

  /**
   * Returns the default response type.
   *
   * @return the response type
   */
  public ResponseType responseType() {
    return responseType;
  }

  /**
   * Returns the default media category.
   *
   * @return the category
   */
  public MediaCategory mediaCategory() {
    return mediaCategory;
  }

  /**
   * Returns the default format of each media category, keyed by the category's value.
   *
   * @return the default formats
   */
  public Map<String, CaptureFormat> format() {
    return view(format);
  }

  /**
   * Returns the formats each media category accepts, keyed by the category's value.
   *
   * @return the accepted formats
   */
  public Map<String, List<CaptureFormat>> formats() {
    return view(formats);
  }

  /**
   * Returns the default device category.
   *
   * @return the category
   */
  public DeviceCategory deviceCategory() {
    return deviceCategory;
  }

  /**
   * Returns the device emulated by default on mobile.
   *
   * @return the device name
   */
  public String emulatedDevice() {
    return emulatedDevice;
  }

  /**
   * Returns the default viewport.
   *
   * @return the viewport
   */
  public Viewport viewport() {
    return viewport;
  }

  /**
   * Returns the default zoom in percent.
   *
   * @return the zoom
   */
  public Integer zoom() {
    return zoom;
  }

  /**
   * Returns the default scroll mode.
   *
   * @return the mode
   */
  public ScrollMode scrollMode() {
    return scrollMode;
  }

  /**
   * Returns whether captures grow to the page's height by default.
   *
   * @return the flag
   */
  public Boolean autoHeight() {
    return autoHeight;
  }

  /**
   * Returns the tallest automatic-height capture.
   *
   * @return the height in pixels, or {@code null} if there is no general ceiling
   */
  public Integer autoHeightMaxHeight() {
    return autoHeightMaxHeight;
  }

  /**
   * Returns the tallest automatic-height WebP capture.
   *
   * @return the height in pixels
   */
  public Integer autoHeightMaxHeightWebp() {
    return autoHeightMaxHeightWebp;
  }

  /**
   * Returns whether ads are blocked by default.
   *
   * @return the flag
   */
  public Boolean blockAds() {
    return blockAds;
  }

  /**
   * Returns whether pop-ups are blocked by default.
   *
   * @return the flag
   */
  public Boolean blockPopUps() {
    return blockPopUps;
  }

  /**
   * Returns whether Escape is pressed by default.
   *
   * @return the flag
   */
  public Boolean pressEscape() {
    return pressEscape;
  }

  /**
   * Returns the default page load timeout.
   *
   * @return the timeout in milliseconds
   */
  public Integer pageTimeoutMs() {
    return pageTimeoutMs;
  }

  /**
   * Returns the default pause between scroll steps.
   *
   * @return the delay in milliseconds
   */
  public Integer scrollingDelayMs() {
    return scrollingDelayMs;
  }

  /**
   * Returns whether a position is reported by default.
   *
   * @return the flag
   */
  public Boolean geolocation() {
    return geolocation;
  }

  /**
   * Returns the default geolocation accuracy.
   *
   * @return the accuracy in metres
   */
  public Double geolocationAccuracy() {
    return geolocationAccuracy;
  }

  /**
   * Returns whether bucket uploads are waited for by default.
   *
   * @return the flag
   */
  public Boolean waitForCloudSync() {
    return waitForCloudSync;
  }

  /**
   * Returns the timestamp watermark defaults, as the API sends them.
   *
   * @return the defaults
   */
  public Map<String, Object> timestampWatermark() {
    return view(timestampWatermark);
  }

  /**
   * Returns the page source defaults, as the API sends them.
   *
   * @return the defaults
   */
  public Map<String, Object> pageSource() {
    return view(pageSource);
  }

  /**
   * Returns the limits options are checked against, as the API sends them.
   *
   * @return the limits
   */
  public Map<String, Object> limits() {
    return view(limits);
  }

  /**
   * Returns the settings the API leaves to the browser's own defaults.
   *
   * @return the setting names
   */
  public List<String> leftToTheBrowser() {
    return leftToTheBrowser == null ? null : Collections.unmodifiableList(leftToTheBrowser);
  }

  /** Returns the default set as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
