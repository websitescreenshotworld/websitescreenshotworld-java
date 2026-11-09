package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.Token;
import java.util.Map;

/**
 * The product that sent a webhook.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class Product extends Token {
  /** Instant Snap. */
  public static final Product INSTANT_SNAP = new Product("instant_snap");

  /** Auto Schedule. */
  public static final Product AUTO_SCHEDULE = new Product("auto_schedule");

  private static final Map<String, Product> KNOWN = index(INSTANT_SNAP, AUTO_SCHEDULE);

  private Product(String value) {
    super(value);
  }

  /**
   * Returns the Product for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static Product of(String value) {
    return lookup(KNOWN, value, Product::new);
  }
}
