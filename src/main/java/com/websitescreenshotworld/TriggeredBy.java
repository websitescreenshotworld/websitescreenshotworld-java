package com.websitescreenshotworld;

import java.util.Map;

/**
 * Who started a scheduled web page's capture.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class TriggeredBy extends Token {
  /** A call to the Auto Schedule API. */
  public static final TriggeredBy API = new TriggeredBy("api");

  /** Someone in the dashboard. */
  public static final TriggeredBy UI = new TriggeredBy("ui");

  private static final Map<String, TriggeredBy> KNOWN = index(API, UI);

  private TriggeredBy(String value) {
    super(value);
  }

  /**
   * Returns the TriggeredBy for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static TriggeredBy of(String value) {
    return lookup(KNOWN, value, TriggeredBy::new);
  }
}
