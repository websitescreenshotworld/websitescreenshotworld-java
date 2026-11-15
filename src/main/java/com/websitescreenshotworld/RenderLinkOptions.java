package com.websitescreenshotworld;

import java.util.Objects;

/**
 * What a render link captures, for {@link RenderLink#build(RenderLinkOptions)}. The render secret,
 * key prefix and page URL are required; every option that is not set is left out of the link, and
 * the API applies its default.
 */
public final class RenderLinkOptions {
  private final String renderSecret;
  private final String keyPrefix;
  private final String url;
  private final CaptureFormat format;
  private final String baseUrl;
  private final Integer ttlSeconds;
  private final Integer width;
  private final Integer height;
  private final Double deviceScaleFactor;
  private final DeviceCategory deviceCategory;
  private final String emulatedDevice;
  private final ScrollMode scrollMode;
  private final Boolean autoHeight;
  private final Integer maxHeight;
  private final Integer zoom;
  private final Boolean blockAds;
  private final Boolean blockPopUps;
  private final Boolean pressEscape;
  private final Integer pageTimeoutMs;
  private final Integer scrollingDelayMs;
  private final String locale;
  private final String timeZone;
  private final String hide;
  private final String xpath;
  private final String click;
  private final String hover;
  private final String country;
  private final Integer thumbWidth;

  private RenderLinkOptions(Builder builder) {
    this.renderSecret = builder.renderSecret;
    this.keyPrefix = builder.keyPrefix;
    this.url = builder.url;
    this.format = builder.format;
    this.baseUrl = builder.baseUrl;
    this.ttlSeconds = builder.ttlSeconds;
    this.width = builder.width;
    this.height = builder.height;
    this.deviceScaleFactor = builder.deviceScaleFactor;
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
    this.locale = builder.locale;
    this.timeZone = builder.timeZone;
    this.hide = builder.hide;
    this.xpath = builder.xpath;
    this.click = builder.click;
    this.hover = builder.hover;
    this.country = builder.country;
    this.thumbWidth = builder.thumbWidth;
  }

  /**
   * Starts the options of a render link.
   *
   * @param renderSecret the key's render secret ({@code wsw_rsk_...}), shown on the API Keys page
   *     once render links are on for the key
   * @param keyPrefix the key's 12-character prefix: the part after {@code wsw_api_} and before the
   *     next {@code _}
   * @param url the page to capture
   * @return a new builder
   */
  public static Builder builder(String renderSecret, String keyPrefix, String url) {
    return new Builder(renderSecret, keyPrefix, url);
  }

  /**
   * Returns the render secret.
   *
   * @return the render secret
   */
  public String renderSecret() {
    return renderSecret;
  }

  /**
   * Returns the key prefix.
   *
   * @return the key prefix
   */
  public String keyPrefix() {
    return keyPrefix;
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
   * Returns the image format.
   *
   * @return the format, or {@code null} for png
   */
  public CaptureFormat format() {
    return format;
  }

  /**
   * Returns the base URL of the link.
   *
   * @return the base URL, or {@code null} for the Instant Snap API's
   */
  public String baseUrl() {
    return baseUrl;
  }

  /**
   * Returns how long, in seconds, a capture of the link is served from cache before a view captures
   * again, from 3600 to 2592000; the server uses 86400 (a day) when it is not set.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer ttlSeconds() {
    return ttlSeconds;
  }

  /**
   * Returns the browser viewport's width in CSS pixels.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer width() {
    return width;
  }

  /**
   * Returns the browser viewport's height in CSS pixels.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer height() {
    return height;
  }

  /**
   * Returns the viewport's device pixel ratio.
   *
   * @return the value, or {@code null} if not set
   */
  public Double deviceScaleFactor() {
    return deviceScaleFactor;
  }

  /**
   * Returns desktop or mobile.
   *
   * @return the value, or {@code null} if not set
   */
  public DeviceCategory deviceCategory() {
    return deviceCategory;
  }

  /**
   * Returns a device to emulate, by its name in the catalog.
   *
   * @return the value, or {@code null} if not set
   */
  public String emulatedDevice() {
    return emulatedDevice;
  }

  /**
   * Returns {@link ScrollMode#SCROLL} for the full page.
   *
   * @return the value, or {@code null} if not set
   */
  public ScrollMode scrollMode() {
    return scrollMode;
  }

  /**
   * Returns whether to grow the viewport to the page's height.
   *
   * @return the value, or {@code null} if not set
   */
  public Boolean autoHeight() {
    return autoHeight;
  }

  /**
   * Returns the tallest a full-page capture may be, in pixels.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer maxHeight() {
    return maxHeight;
  }

  /**
   * Returns the page zoom in percent.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer zoom() {
    return zoom;
  }

  /**
   * Returns whether to block ads.
   *
   * @return the value, or {@code null} if not set
   */
  public Boolean blockAds() {
    return blockAds;
  }

  /**
   * Returns whether to close cookie banners and pop-ups.
   *
   * @return the value, or {@code null} if not set
   */
  public Boolean blockPopUps() {
    return blockPopUps;
  }

  /**
   * Returns whether to press Escape before capturing.
   *
   * @return the value, or {@code null} if not set
   */
  public Boolean pressEscape() {
    return pressEscape;
  }

  /**
   * Returns how long the page may take to load, in milliseconds.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer pageTimeoutMs() {
    return pageTimeoutMs;
  }

  /**
   * Returns the pause between scroll steps of a full-page capture, in milliseconds.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer scrollingDelayMs() {
    return scrollingDelayMs;
  }

  /**
   * Returns the browser's locale, such as {@code en-GB}.
   *
   * @return the value, or {@code null} if not set
   */
  public String locale() {
    return locale;
  }

  /**
   * Returns the browser's time zone, such as {@code Europe/London}.
   *
   * @return the value, or {@code null} if not set
   */
  public String timeZone() {
    return timeZone;
  }

  /**
   * Returns the capture's {@code selectors.hide}.
   *
   * @return the value, or {@code null} if not set
   */
  public String hide() {
    return hide;
  }

  /**
   * Returns the capture's {@code selectors.xpath}.
   *
   * @return the value, or {@code null} if not set
   */
  public String xpath() {
    return xpath;
  }

  /**
   * Returns the capture's {@code selectors.click}.
   *
   * @return the value, or {@code null} if not set
   */
  public String click() {
    return click;
  }

  /**
   * Returns the capture's {@code selectors.hover}.
   *
   * @return the value, or {@code null} if not set
   */
  public String hover() {
    return hover;
  }

  /**
   * Returns the capture's {@code proxy.country}.
   *
   * @return the value, or {@code null} if not set
   */
  public String country() {
    return country;
  }

  /**
   * Returns the width to shrink the image to after the capture (16 to 3840), keeping its
   * proportions.
   *
   * @return the value, or {@code null} if not set
   */
  public Integer thumbWidth() {
    return thumbWidth;
  }

  /** Builds {@link RenderLinkOptions}. */
  public static final class Builder {
    private final String renderSecret;
    private final String keyPrefix;
    private final String url;
    private CaptureFormat format;
    private String baseUrl;
    private Integer ttlSeconds;
    private Integer width;
    private Integer height;
    private Double deviceScaleFactor;
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
    private String locale;
    private String timeZone;
    private String hide;
    private String xpath;
    private String click;
    private String hover;
    private String country;
    private Integer thumbWidth;

    private Builder(String renderSecret, String keyPrefix, String url) {
      this.renderSecret = Objects.requireNonNull(renderSecret, "renderSecret");
      this.keyPrefix = Objects.requireNonNull(keyPrefix, "keyPrefix");
      this.url = Objects.requireNonNull(url, "url");
    }

    /**
     * Sets the image format: {@link CaptureFormat#PNG} (the default), {@link CaptureFormat#JPEG} or
     * {@link CaptureFormat#WEBP}.
     *
     * @param format the format
     * @return this builder
     */
    public Builder format(CaptureFormat format) {
      this.format = format;
      return this;
    }

    /**
     * Sets the base URL of the link, by default {@code https://api.websitescreenshotworld.com}.
     *
     * @param baseUrl the base URL
     * @return this builder
     */
    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    /**
     * Sets how long, in seconds, a capture of the link is served from cache before a view captures
     * again, from 3600 to 2592000; the server uses 86400 (a day) when it is not set.
     *
     * @param ttlSeconds the value
     * @return this builder
     */
    public Builder ttlSeconds(int ttlSeconds) {
      this.ttlSeconds = ttlSeconds;
      return this;
    }

    /**
     * Sets the browser viewport's width in CSS pixels.
     *
     * @param width the value
     * @return this builder
     */
    public Builder width(int width) {
      this.width = width;
      return this;
    }

    /**
     * Sets the browser viewport's height in CSS pixels.
     *
     * @param height the value
     * @return this builder
     */
    public Builder height(int height) {
      this.height = height;
      return this;
    }

    /**
     * Sets the viewport's device pixel ratio.
     *
     * @param deviceScaleFactor the value
     * @return this builder
     */
    public Builder deviceScaleFactor(double deviceScaleFactor) {
      this.deviceScaleFactor = deviceScaleFactor;
      return this;
    }

    /**
     * Sets desktop or mobile.
     *
     * @param deviceCategory the value
     * @return this builder
     */
    public Builder deviceCategory(DeviceCategory deviceCategory) {
      this.deviceCategory = deviceCategory;
      return this;
    }

    /**
     * Sets a device to emulate, by its name in the catalog.
     *
     * @param emulatedDevice the value
     * @return this builder
     */
    public Builder emulatedDevice(String emulatedDevice) {
      this.emulatedDevice = emulatedDevice;
      return this;
    }

    /**
     * Sets {@link ScrollMode#SCROLL} for the full page.
     *
     * @param scrollMode the value
     * @return this builder
     */
    public Builder scrollMode(ScrollMode scrollMode) {
      this.scrollMode = scrollMode;
      return this;
    }

    /**
     * Sets whether to grow the viewport to the page's height.
     *
     * @param autoHeight the value
     * @return this builder
     */
    public Builder autoHeight(boolean autoHeight) {
      this.autoHeight = autoHeight;
      return this;
    }

    /**
     * Sets the tallest a full-page capture may be, in pixels.
     *
     * @param maxHeight the value
     * @return this builder
     */
    public Builder maxHeight(int maxHeight) {
      this.maxHeight = maxHeight;
      return this;
    }

    /**
     * Sets the page zoom in percent.
     *
     * @param zoom the value
     * @return this builder
     */
    public Builder zoom(int zoom) {
      this.zoom = zoom;
      return this;
    }

    /**
     * Sets whether to block ads.
     *
     * @param blockAds the value
     * @return this builder
     */
    public Builder blockAds(boolean blockAds) {
      this.blockAds = blockAds;
      return this;
    }

    /**
     * Sets whether to close cookie banners and pop-ups.
     *
     * @param blockPopUps the value
     * @return this builder
     */
    public Builder blockPopUps(boolean blockPopUps) {
      this.blockPopUps = blockPopUps;
      return this;
    }

    /**
     * Sets whether to press Escape before capturing.
     *
     * @param pressEscape the value
     * @return this builder
     */
    public Builder pressEscape(boolean pressEscape) {
      this.pressEscape = pressEscape;
      return this;
    }

    /**
     * Sets how long the page may take to load, in milliseconds.
     *
     * @param pageTimeoutMs the value
     * @return this builder
     */
    public Builder pageTimeoutMs(int pageTimeoutMs) {
      this.pageTimeoutMs = pageTimeoutMs;
      return this;
    }

    /**
     * Sets the pause between scroll steps of a full-page capture, in milliseconds.
     *
     * @param scrollingDelayMs the value
     * @return this builder
     */
    public Builder scrollingDelayMs(int scrollingDelayMs) {
      this.scrollingDelayMs = scrollingDelayMs;
      return this;
    }

    /**
     * Sets the browser's locale, such as {@code en-GB}.
     *
     * @param locale the value
     * @return this builder
     */
    public Builder locale(String locale) {
      this.locale = locale;
      return this;
    }

    /**
     * Sets the browser's time zone, such as {@code Europe/London}.
     *
     * @param timeZone the value
     * @return this builder
     */
    public Builder timeZone(String timeZone) {
      this.timeZone = timeZone;
      return this;
    }

    /**
     * Sets the capture's {@code selectors.hide}.
     *
     * @param hide the value
     * @return this builder
     */
    public Builder hide(String hide) {
      this.hide = hide;
      return this;
    }

    /**
     * Sets the capture's {@code selectors.xpath}.
     *
     * @param xpath the value
     * @return this builder
     */
    public Builder xpath(String xpath) {
      this.xpath = xpath;
      return this;
    }

    /**
     * Sets the capture's {@code selectors.click}.
     *
     * @param click the value
     * @return this builder
     */
    public Builder click(String click) {
      this.click = click;
      return this;
    }

    /**
     * Sets the capture's {@code selectors.hover}.
     *
     * @param hover the value
     * @return this builder
     */
    public Builder hover(String hover) {
      this.hover = hover;
      return this;
    }

    /**
     * Sets the capture's {@code proxy.country}.
     *
     * @param country the value
     * @return this builder
     */
    public Builder country(String country) {
      this.country = country;
      return this;
    }

    /**
     * Sets the width to shrink the image to after the capture (16 to 3840), keeping its
     * proportions.
     *
     * @param thumbWidth the value
     * @return this builder
     */
    public Builder thumbWidth(int thumbWidth) {
      this.thumbWidth = thumbWidth;
      return this;
    }

    /**
     * Builds the options.
     *
     * @return the options
     */
    public RenderLinkOptions build() {
      return new RenderLinkOptions(this);
    }
  }
}
