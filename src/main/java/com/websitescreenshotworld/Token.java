package com.websitescreenshotworld;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A string value from a set the API may extend, such as a capture status or a format.
 *
 * <p>Each subclass has a constant for every value this library knows and an {@code of(String)}
 * factory that accepts any value, so a value added to the API later still comes through. {@code of}
 * returns the constant for a known value, so known values can be compared with {@code ==} as well
 * as {@link #equals(Object)}.
 */
public abstract class Token {
  private final String value;

  /**
   * Creates a token.
   *
   * @param value the value as the API spells it
   */
  protected Token(String value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  /**
   * Returns the value as the API spells it.
   *
   * @return the wire value, never {@code null}
   */
  public final String value() {
    return value;
  }

  /**
   * Indexes known constants by value, for use by a subclass's {@code of} factory.
   *
   * @param constants the known constants
   * @param <T> the token type
   * @return an unmodifiable map from value to constant
   */
  @SafeVarargs
  protected static <T extends Token> Map<String, T> index(T... constants) {
    Map<String, T> map = new HashMap<>();
    for (T constant : constants) {
      map.put(constant.value(), constant);
    }
    return Collections.unmodifiableMap(map);
  }

  /**
   * Returns the known constant for {@code value}, or a new token for an unknown one.
   *
   * @param known the subclass's known constants
   * @param value the value to look up
   * @param create creates a token for an unknown value
   * @param <T> the token type
   * @return the token for {@code value}
   */
  protected static <T extends Token> T lookup(
      Map<String, T> known, String value, Function<String, T> create) {
    T constant = known.get(Objects.requireNonNull(value, "value"));
    return constant != null ? constant : create.apply(value);
  }

  @Override
  public final boolean equals(Object other) {
    return other != null && other.getClass() == getClass() && ((Token) other).value.equals(value);
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  /** Returns the value as the API spells it. */
  @Override
  public final String toString() {
    return value;
  }
}
