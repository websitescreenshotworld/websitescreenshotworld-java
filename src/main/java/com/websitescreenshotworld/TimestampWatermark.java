package com.websitescreenshotworld;

/**
 * A strip showing the capture time and URL. Sending the block turns it on, so {@code
 * TimestampWatermark.builder().build()} is enough. Not available for video.
 */
public final class TimestampWatermark {
  private Boolean enabled;
  private WatermarkLocation location;
  private String fontColor;
  private String backgroundColor;

  private TimestampWatermark() {}

  private TimestampWatermark(Builder builder) {
    this.enabled = builder.enabled;
    this.location = builder.location;
    this.fontColor = builder.fontColor;
    this.backgroundColor = builder.backgroundColor;
  }

  /**
   * Starts a watermark.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns whether the watermark is drawn.
   *
   * @return the flag, or {@code null} if not set
   */
  public Boolean enabled() {
    return enabled;
  }

  /**
   * Returns where the strip goes.
   *
   * @return the location, or {@code null} if not set
   */
  public WatermarkLocation location() {
    return location;
  }

  /**
   * Returns the text colour.
   *
   * @return the hex colour, or {@code null} if not set
   */
  public String fontColor() {
    return fontColor;
  }

  /**
   * Returns the strip colour.
   *
   * @return the hex colour, or {@code null} if not set
   */
  public String backgroundColor() {
    return backgroundColor;
  }

  /** Builds a {@link TimestampWatermark}. */
  public static final class Builder {
    private Boolean enabled;
    private WatermarkLocation location;
    private String fontColor;
    private String backgroundColor;

    private Builder() {}

    /**
     * Turns the watermark on or off. Defaults to on.
     *
     * @param enabled whether to draw it
     * @return this builder
     */
    public Builder enabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    /**
     * Sets where the strip goes. Defaults to {@link WatermarkLocation#TOP}.
     *
     * @param location the location
     * @return this builder
     */
    public Builder location(WatermarkLocation location) {
      this.location = location;
      return this;
    }

    /**
     * Sets the text colour. Defaults to {@code #000000}.
     *
     * @param fontColor a hex colour
     * @return this builder
     */
    public Builder fontColor(String fontColor) {
      this.fontColor = fontColor;
      return this;
    }

    /**
     * Sets the strip colour. Defaults to {@code #FFFFFF}.
     *
     * @param backgroundColor a hex colour
     * @return this builder
     */
    public Builder backgroundColor(String backgroundColor) {
      this.backgroundColor = backgroundColor;
      return this;
    }

    /**
     * Builds the watermark.
     *
     * @return the watermark
     */
    public TimestampWatermark build() {
      return new TimestampWatermark(this);
    }
  }
}
