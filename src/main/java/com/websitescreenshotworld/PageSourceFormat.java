package com.websitescreenshotworld;

import java.util.Map;

/**
 * The form in which a capture keeps the page source.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class PageSourceFormat extends Token {
  /** The HTML. */
  public static final PageSourceFormat HTML = new PageSourceFormat("html");

  /** A JSON document. */
  public static final PageSourceFormat JSON = new PageSourceFormat("json");

  /** The visible text. */
  public static final PageSourceFormat TEXT = new PageSourceFormat("text");

  private static final Map<String, PageSourceFormat> KNOWN = index(HTML, JSON, TEXT);

  private PageSourceFormat(String value) {
    super(value);
  }

  /**
   * Returns the PageSourceFormat for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static PageSourceFormat of(String value) {
    return lookup(KNOWN, value, PageSourceFormat::new);
  }
}
