package com.websitescreenshotworld;

/** Elements to act on before capturing. */
public final class Selectors {
  private String xpath;
  private String hide;
  private String click;
  private String hover;

  private Selectors() {}

  private Selectors(Builder builder) {
    this.xpath = builder.xpath;
    this.hide = builder.hide;
    this.click = builder.click;
    this.hover = builder.hover;
  }

  /**
   * Starts a set of selectors.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the XPath of the element to capture.
   *
   * @return the XPath, or {@code null} if not set
   */
  public String xpath() {
    return xpath;
  }

  /**
   * Returns the CSS selector of elements to hide.
   *
   * @return the selector, or {@code null} if not set
   */
  public String hide() {
    return hide;
  }

  /**
   * Returns the CSS selector of an element to click.
   *
   * @return the selector, or {@code null} if not set
   */
  public String click() {
    return click;
  }

  /**
   * Returns the CSS selector of an element to hover.
   *
   * @return the selector, or {@code null} if not set
   */
  public String hover() {
    return hover;
  }

  /** Builds {@link Selectors}. */
  public static final class Builder {
    private String xpath;
    private String hide;
    private String click;
    private String hover;

    private Builder() {}

    /**
     * Captures only the element this XPath selects. At most 512 characters.
     *
     * @param xpath the XPath
     * @return this builder
     */
    public Builder xpath(String xpath) {
      this.xpath = xpath;
      return this;
    }

    /**
     * Hides elements such as cookie banners: up to 25 comma-separated CSS selectors (an XPath
     * starting with {@code //} works too), at most 2000 characters. Each is applied on its own; one
     * the browser rejects is skipped.
     *
     * @param hide the CSS selectors
     * @return this builder
     */
    public Builder hide(String hide) {
      this.hide = hide;
      return this;
    }

    /**
     * Clicks the element this CSS selector matches. At most 512 characters.
     *
     * @param click the CSS selector
     * @return this builder
     */
    public Builder click(String click) {
      this.click = click;
      return this;
    }

    /**
     * Hovers over the element this CSS selector matches. At most 512 characters.
     *
     * @param hover the CSS selector
     * @return this builder
     */
    public Builder hover(String hover) {
      this.hover = hover;
      return this;
    }

    /**
     * Builds the selectors.
     *
     * @return the selectors
     */
    public Selectors build() {
      return new Selectors(this);
    }
  }
}
