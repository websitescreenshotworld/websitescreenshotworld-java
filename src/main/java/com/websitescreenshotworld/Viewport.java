package com.websitescreenshotworld;

/**
 * The browser window a capture uses. Each dimension that is not set takes the API's default (1920
 * by 1080 at scale factor 1).
 */
public final class Viewport {
  private Integer width;
  private Integer height;
  private Double deviceScaleFactor;

  private Viewport() {}

  private Viewport(Builder builder) {
    this.width = builder.width;
    this.height = builder.height;
    this.deviceScaleFactor = builder.deviceScaleFactor;
  }

  /**
   * Starts a viewport.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the width in CSS pixels.
   *
   * @return the width, or {@code null} if not set
   */
  public Integer width() {
    return width;
  }

  /**
   * Returns the height in CSS pixels.
   *
   * @return the height, or {@code null} if not set
   */
  public Integer height() {
    return height;
  }

  /**
   * Returns the device pixel ratio.
   *
   * @return the ratio, or {@code null} if not set
   */
  public Double deviceScaleFactor() {
    return deviceScaleFactor;
  }

  /** Builds a {@link Viewport}. */
  public static final class Builder {
    private Integer width;
    private Integer height;
    private Double deviceScaleFactor;

    private Builder() {}

    /**
     * Sets the width in CSS pixels, from 250 to 7680.
     *
     * @param width the width
     * @return this builder
     */
    public Builder width(int width) {
      this.width = width;
      return this;
    }

    /**
     * Sets the height in CSS pixels, from 250 to 7680.
     *
     * @param height the height
     * @return this builder
     */
    public Builder height(int height) {
      this.height = height;
      return this;
    }

    /**
     * Sets the device pixel ratio, from 1 to 3.
     *
     * @param deviceScaleFactor the ratio
     * @return this builder
     */
    public Builder deviceScaleFactor(double deviceScaleFactor) {
      this.deviceScaleFactor = deviceScaleFactor;
      return this;
    }

    /**
     * Builds the viewport.
     *
     * @return the viewport
     */
    public Viewport build() {
      return new Viewport(this);
    }
  }
}
