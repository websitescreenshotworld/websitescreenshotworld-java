package com.websitescreenshotworld;

import java.util.Map;

/**
 * How a capture answers. The capture methods set it themselves; it comes back on {@link
 * Capture#responseType()}.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class ResponseType extends Token {
  /** The capture's metadata and a download link. */
  public static final ResponseType JSON = new ResponseType("json");

  /** The file itself, in the response body. */
  public static final ResponseType BY_FORMAT = new ResponseType("by_format");

  /** The file uploaded to your own bucket. */
  public static final ResponseType CLOUD_STORAGE = new ResponseType("cloud_storage");

  private static final Map<String, ResponseType> KNOWN = index(JSON, BY_FORMAT, CLOUD_STORAGE);

  private ResponseType(String value) {
    super(value);
  }

  /**
   * Returns the ResponseType for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static ResponseType of(String value) {
    return lookup(KNOWN, value, ResponseType::new);
  }
}
