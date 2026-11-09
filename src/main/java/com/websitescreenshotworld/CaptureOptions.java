package com.websitescreenshotworld;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * What to capture and how. Only the URL is required; an option that is not set is not sent, so the
 * API applies its default (see {@link InstantSnap#defaults()}).
 *
 * <pre>{@code
 * CaptureOptions options = CaptureOptions.builder()
 *     .url("https://example.com")
 *     .format(CaptureFormat.WEBP)
 *     .viewport(Viewport.builder().width(1440).build())
 *     .build();
 * }</pre>
 */
public final class CaptureOptions {
  private String url;
  private MediaCategory mediaCategory;
  private CaptureFormat format;
  private Viewport viewport;
  private DeviceCategory deviceCategory;
  private String emulatedDevice;
  private ScrollMode scrollMode;
  private Boolean autoHeight;
  private Integer maxHeight;
  private Integer zoom;
  private Boolean blockAds;
  private Boolean blockPopUps;
  private Boolean pressEscape;
  private Integer pageTimeoutMs;
  private Integer scrollingDelayMs;
  private String userAgent;
  private String locale;
  private String timeZone;
  private Map<String, String> headers;
  private Map<String, String> cookies;
  private Geolocation geolocation;
  private Selectors selectors;
  private Proxy proxy;
  private TimestampWatermark timestampWatermark;
  private PageSourceOptions pageSource;
  private S3Delivery s3Delivery;
  private String callbackUrl;
  // Sent as "timeout"; its accessors are named timeoutSeconds so it is not confused with
  // RequestOptions#timeout, the time the client waits for an answer.
  private Integer timeout;

  private CaptureOptions() {}

  private CaptureOptions(Builder builder) {
    this.url = builder.url;
    this.mediaCategory = builder.mediaCategory;
    this.format = builder.format;
    this.viewport = builder.viewport;
    this.deviceCategory = builder.deviceCategory;
    this.emulatedDevice = builder.emulatedDevice;
    this.scrollMode = builder.scrollMode;
    this.autoHeight = builder.autoHeight;
    this.maxHeight = builder.maxHeight;
    this.zoom = builder.zoom;
    this.blockAds = builder.blockAds;
    this.blockPopUps = builder.blockPopUps;
    this.pressEscape = builder.pressEscape;
    this.pageTimeoutMs = builder.pageTimeoutMs;
    this.scrollingDelayMs = builder.scrollingDelayMs;
    this.userAgent = builder.userAgent;
    this.locale = builder.locale;
    this.timeZone = builder.timeZone;
    this.headers = copy(builder.headers);
    this.cookies = copy(builder.cookies);
    this.geolocation = builder.geolocation;
    this.selectors = builder.selectors;
    this.proxy = builder.proxy;
    this.timestampWatermark = builder.timestampWatermark;
    this.pageSource = builder.pageSource;
    this.s3Delivery = builder.s3Delivery;
    this.callbackUrl = builder.callbackUrl;
    this.timeout = builder.timeoutSeconds;
  }

  private static Map<String, String> copy(Map<String, String> map) {
    return map == null ? null : new LinkedHashMap<>(map);
  }

  /**
   * Starts a set of options.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the page to capture.
   *
   * @return the URL
   */
  public String url() {
    return url;
  }

  /**
   * Returns the kind of file to produce.
   *
   * @return the category, or {@code null} if not set
   */
  public MediaCategory mediaCategory() {
    return mediaCategory;
  }

  /**
   * Returns the file format.
   *
   * @return the format, or {@code null} if not set
   */
  public CaptureFormat format() {
    return format;
  }

  /**
   * Returns the browser window.
   *
   * @return the viewport, or {@code null} if not set
   */
  public Viewport viewport() {
    return viewport;
  }

  /**
   * Returns the kind of browser to emulate.
   *
   * @return the category, or {@code null} if not set
   */
  public DeviceCategory deviceCategory() {
    return deviceCategory;
  }

  /**
   * Returns the device to emulate.
   *
   * @return the device name, or {@code null} if not set
   */
  public String emulatedDevice() {
    return emulatedDevice;
  }

  /**
   * Returns how much of the page to cover.
   *
   * @return the mode, or {@code null} if not set
   */
  public ScrollMode scrollMode() {
    return scrollMode;
  }

  /**
   * Returns whether the capture grows to the page's height.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean autoHeight() {
    return autoHeight;
  }

  /**
   * Returns the tallest capture allowed, in pixels.
   *
   * @return the height, or {@code null} if not set
   */
  public Integer maxHeight() {
    return maxHeight;
  }

  /**
   * Returns the page zoom in percent.
   *
   * @return the zoom, or {@code null} if not set
   */
  public Integer zoom() {
    return zoom;
  }

  /**
   * Returns whether ads are blocked.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean blockAds() {
    return blockAds;
  }

  /**
   * Returns whether pop-ups are blocked.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean blockPopUps() {
    return blockPopUps;
  }

  /**
   * Returns whether Escape is pressed after the pop-up pass and after scrolling.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean pressEscape() {
    return pressEscape;
  }

  /**
   * Returns how long the page may take to load, in milliseconds.
   *
   * @return the timeout, or {@code null} if not set
   */
  public Integer pageTimeoutMs() {
    return pageTimeoutMs;
  }

  /**
   * Returns the pause between scroll steps, in milliseconds.
   *
   * @return the delay, or {@code null} if not set
   */
  public Integer scrollingDelayMs() {
    return scrollingDelayMs;
  }

  /**
   * Returns the browser's User-Agent.
   *
   * @return the User-Agent, or {@code null} if not set
   */
  public String userAgent() {
    return userAgent;
  }

  /**
   * Returns the browser's locale.
   *
   * @return the BCP 47 tag, or {@code null} if not set
   */
  public String locale() {
    return locale;
  }

  /**
   * Returns the browser's time zone.
   *
   * @return the IANA time zone, or {@code null} if not set
   */
  public String timeZone() {
    return timeZone;
  }

  /**
   * Returns the headers sent with every request the page makes.
   *
   * @return the headers, or {@code null} if not set
   */
  public Map<String, String> headers() {
    return headers == null ? null : Collections.unmodifiableMap(headers);
  }

  /**
   * Returns the cookies set for the URL's origin.
   *
   * @return the cookies, or {@code null} if not set
   */
  public Map<String, String> cookies() {
    return cookies == null ? null : Collections.unmodifiableMap(cookies);
  }

  /**
   * Returns the position reported to the page.
   *
   * @return the position, or {@code null} if not set
   */
  public Geolocation geolocation() {
    return geolocation;
  }

  /**
   * Returns the elements to act on before capturing.
   *
   * @return the selectors, or {@code null} if not set
   */
  public Selectors selectors() {
    return selectors;
  }

  /**
   * Returns the proxy to capture through.
   *
   * @return the proxy, or {@code null} if not set
   */
  public Proxy proxy() {
    return proxy;
  }

  /**
   * Returns the timestamp watermark.
   *
   * @return the watermark, or {@code null} if not set
   */
  public TimestampWatermark timestampWatermark() {
    return timestampWatermark;
  }

  /**
   * Returns how the page source is kept.
   *
   * @return the page source options, or {@code null} if not set
   */
  public PageSourceOptions pageSource() {
    return pageSource;
  }

  /**
   * Returns the bucket to upload to.
   *
   * @return the destination, or {@code null} if not set
   */
  public S3Delivery s3Delivery() {
    return s3Delivery;
  }

  /**
   * Returns the URL that also receives this capture's webhook.
   *
   * @return the URL, or {@code null} if not set
   */
  public String callbackUrl() {
    return callbackUrl;
  }

  /**
   * Returns the browser time an async capture may use, sent as {@code timeout}.
   *
   * @return the seconds, or {@code null} if not set
   */
  public Integer timeoutSeconds() {
    return timeout;
  }

  /** Builds {@link CaptureOptions}. */
  public static final class Builder {
    private String url;
    private MediaCategory mediaCategory;
    private CaptureFormat format;
    private Viewport viewport;
    private DeviceCategory deviceCategory;
    private String emulatedDevice;
    private ScrollMode scrollMode;
    private Boolean autoHeight;
    private Integer maxHeight;
    private Integer zoom;
    private Boolean blockAds;
    private Boolean blockPopUps;
    private Boolean pressEscape;
    private Integer pageTimeoutMs;
    private Integer scrollingDelayMs;
    private String userAgent;
    private String locale;
    private String timeZone;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private Geolocation geolocation;
    private Selectors selectors;
    private Proxy proxy;
    private TimestampWatermark timestampWatermark;
    private PageSourceOptions pageSource;
    private S3Delivery s3Delivery;
    private String callbackUrl;
    private Integer timeoutSeconds;

    private Builder() {}

    /**
     * Sets the page to capture: an absolute http or https URL of at most 2048 characters, with no
     * credentials in it. Required.
     *
     * @param url the URL
     * @return this builder
     */
    public Builder url(String url) {
      this.url = url;
      return this;
    }

    /**
     * Sets the kind of file to produce. Defaults to {@link MediaCategory#IMAGE}.
     *
     * @param mediaCategory the category
     * @return this builder
     */
    public Builder mediaCategory(MediaCategory mediaCategory) {
      this.mediaCategory = mediaCategory;
      return this;
    }

    /**
     * Sets the file format, which must belong to the media category.
     *
     * @param format the format
     * @return this builder
     */
    public Builder format(CaptureFormat format) {
      this.format = format;
      return this;
    }

    /**
     * Sets the browser window.
     *
     * @param viewport the viewport
     * @return this builder
     */
    public Builder viewport(Viewport viewport) {
      this.viewport = viewport;
      return this;
    }

    /**
     * Sets the kind of browser to emulate. Defaults to {@link DeviceCategory#DESKTOP}.
     *
     * @param deviceCategory the category
     * @return this builder
     */
    public Builder deviceCategory(DeviceCategory deviceCategory) {
      this.deviceCategory = deviceCategory;
      return this;
    }

    /**
     * Sets a device from {@link Catalog#emulatedDevices()}, matched case-insensitively. Defaults to
     * "iPhone 13" for {@link DeviceCategory#MOBILE}.
     *
     * @param emulatedDevice the device name
     * @return this builder
     */
    public Builder emulatedDevice(String emulatedDevice) {
      this.emulatedDevice = emulatedDevice;
      return this;
    }

    /**
     * Sets how much of the page to cover. Defaults to {@link ScrollMode#VIEWPORT}, or {@link
     * ScrollMode#SCROLL} for video.
     *
     * @param scrollMode the mode
     * @return this builder
     */
    public Builder scrollMode(ScrollMode scrollMode) {
      this.scrollMode = scrollMode;
      return this;
    }

    /**
     * Grows the capture to the page's height. Defaults to off.
     *
     * @param autoHeight whether to grow
     * @return this builder
     */
    public Builder autoHeight(boolean autoHeight) {
      this.autoHeight = autoHeight;
      return this;
    }

    /**
     * Sets the tallest capture allowed, from 250 to 100000 pixels. A value above the format's
     * ceiling is refused, not lowered.
     *
     * @param maxHeight the height
     * @return this builder
     */
    public Builder maxHeight(int maxHeight) {
      this.maxHeight = maxHeight;
      return this;
    }

    /**
     * Sets the page zoom, from 50 to 100 percent. Defaults to 100.
     *
     * @param zoom the zoom
     * @return this builder
     */
    public Builder zoom(int zoom) {
      this.zoom = zoom;
      return this;
    }

    /**
     * Blocks ads. Defaults to on.
     *
     * @param blockAds whether to block ads
     * @return this builder
     */
    public Builder blockAds(boolean blockAds) {
      this.blockAds = blockAds;
      return this;
    }

    /**
     * Blocks pop-ups. Defaults to on.
     *
     * @param blockPopUps whether to block pop-ups
     * @return this builder
     */
    public Builder blockPopUps(boolean blockPopUps) {
      this.blockPopUps = blockPopUps;
      return this;
    }

    /**
     * Presses Escape after the pop-up pass and again after scrolling (once in a video), for a modal
     * that closes on Escape. Defaults to off.
     *
     * @param pressEscape whether to press Escape
     * @return this builder
     */
    public Builder pressEscape(boolean pressEscape) {
      this.pressEscape = pressEscape;
      return this;
    }

    /**
     * Sets how long the page may take to load, from 1000 to 60000 milliseconds. Defaults to 30000.
     *
     * @param pageTimeoutMs the timeout
     * @return this builder
     */
    public Builder pageTimeoutMs(int pageTimeoutMs) {
      this.pageTimeoutMs = pageTimeoutMs;
      return this;
    }

    /**
     * Sets the pause after each scroll step of a full-page capture, from 0 to 10000 milliseconds.
     * Defaults to 500.
     *
     * @param scrollingDelayMs the delay
     * @return this builder
     */
    public Builder scrollingDelayMs(int scrollingDelayMs) {
      this.scrollingDelayMs = scrollingDelayMs;
      return this;
    }

    /**
     * Sets the browser's User-Agent, at most 512 characters.
     *
     * @param userAgent the User-Agent
     * @return this builder
     */
    public Builder userAgent(String userAgent) {
      this.userAgent = userAgent;
      return this;
    }

    /**
     * Sets the browser's locale, a BCP 47 tag such as {@code en-GB}.
     *
     * @param locale the locale
     * @return this builder
     */
    public Builder locale(String locale) {
      this.locale = locale;
      return this;
    }

    /**
     * Sets the browser's time zone, an IANA name such as {@code Europe/London}.
     *
     * @param timeZone the time zone
     * @return this builder
     */
    public Builder timeZone(String timeZone) {
      this.timeZone = timeZone;
      return this;
    }

    /**
     * Replaces the headers sent with every request the page makes. Names are sent exactly as given.
     * Needs the RequestHeadersCookies plan feature.
     *
     * @param headers the headers
     * @return this builder
     */
    public Builder headers(Map<String, String> headers) {
      this.headers = new LinkedHashMap<>(headers);
      return this;
    }

    /**
     * Adds one header sent with every request the page makes.
     *
     * @param name the name, sent exactly as given
     * @param value the value
     * @return this builder
     */
    public Builder header(String name, String value) {
      if (headers == null) {
        headers = new LinkedHashMap<>();
      }
      headers.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value"));
      return this;
    }

    /**
     * Replaces the cookies set for the URL's origin. Names are sent exactly as given. Needs the
     * RequestHeadersCookies plan feature.
     *
     * @param cookies the cookies
     * @return this builder
     */
    public Builder cookies(Map<String, String> cookies) {
      this.cookies = new LinkedHashMap<>(cookies);
      return this;
    }

    /**
     * Adds one cookie set for the URL's origin.
     *
     * @param name the name, sent exactly as given
     * @param value the value
     * @return this builder
     */
    public Builder cookie(String name, String value) {
      if (cookies == null) {
        cookies = new LinkedHashMap<>();
      }
      cookies.put(Objects.requireNonNull(name, "name"), Objects.requireNonNull(value, "value"));
      return this;
    }

    /**
     * Sets the position reported to pages that ask for it.
     *
     * @param geolocation the position
     * @return this builder
     */
    public Builder geolocation(Geolocation geolocation) {
      this.geolocation = geolocation;
      return this;
    }

    /**
     * Sets elements to act on before capturing.
     *
     * @param selectors the selectors
     * @return this builder
     */
    public Builder selectors(Selectors selectors) {
      this.selectors = selectors;
      return this;
    }

    /**
     * Captures through a proxy in another country.
     *
     * @param proxy the proxy
     * @return this builder
     */
    public Builder proxy(Proxy proxy) {
      this.proxy = proxy;
      return this;
    }

    /**
     * Adds a strip with the capture time and URL.
     *
     * @param timestampWatermark the watermark
     * @return this builder
     */
    public Builder timestampWatermark(TimestampWatermark timestampWatermark) {
      this.timestampWatermark = timestampWatermark;
      return this;
    }

    /**
     * Also keeps the page source.
     *
     * @param pageSource the page source options
     * @return this builder
     */
    public Builder pageSource(PageSourceOptions pageSource) {
      this.pageSource = pageSource;
      return this;
    }

    /**
     * Sets the bucket that {@link InstantSnap#captureToBucket} and {@link
     * InstantSnap#submitToBucket} upload to. Required there, and refused by the other capture
     * methods.
     *
     * @param s3Delivery the destination
     * @return this builder
     */
    public Builder s3Delivery(S3Delivery s3Delivery) {
      this.s3Delivery = s3Delivery;
      return this;
    }

    /**
     * Sets a URL that also receives this capture's {@code capture.succeeded} or {@code
     * capture.failed} webhook. Needs the Webhooks plan feature.
     *
     * @param callbackUrl the URL
     * @return this builder
     */
    public Builder callbackUrl(String callbackUrl) {
      this.callbackUrl = callbackUrl;
      return this;
    }

    /**
     * Sets the seconds of browser time the capture may use, every attempt included; time in the
     * queue does not count. Async only ({@link InstantSnap#submit} and {@link
     * InstantSnap#submitToBucket}), 30 to 300, server default 120; the server refuses it on the
     * other capture methods. Sent as {@code timeout}. Not the time the client waits for an answer,
     * which is {@link RequestOptions.Builder#timeout}.
     *
     * @param timeoutSeconds the seconds
     * @return this builder
     */
    public Builder timeoutSeconds(int timeoutSeconds) {
      this.timeoutSeconds = timeoutSeconds;
      return this;
    }

    /**
     * Builds the options.
     *
     * @return the options
     * @throws IllegalStateException if no URL was set
     */
    public CaptureOptions build() {
      if (url == null) {
        throw new IllegalStateException("CaptureOptions needs a url");
      }
      return new CaptureOptions(this);
    }
  }
}
