package com.websitescreenshotworld;

import java.util.Objects;

/**
 * Captures through a proxy in another country. Needs the ProxyLocation plan feature; {@link
 * InstantSnap#catalog()} lists the countries.
 */
public final class Proxy {
  private String country;

  private Proxy() {}

  private Proxy(String country) {
    this.country = Objects.requireNonNull(country, "country");
  }

  /**
   * Returns a proxy in a country.
   *
   * @param country a country from {@link Catalog#proxyCountries()}, such as {@code "gb"}
   * @return the proxy
   */
  public static Proxy of(String country) {
    return new Proxy(country);
  }

  /**
   * Returns the country.
   *
   * @return the country
   */
  public String country() {
    return country;
  }
}
