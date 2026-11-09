package com.websitescreenshotworld;

/** The position the browser reports to pages that ask for it. */
public final class Geolocation {
  private Double latitude;
  private Double longitude;
  private Double accuracy;

  private Geolocation() {}

  private Geolocation(double latitude, double longitude, Double accuracy) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.accuracy = accuracy;
  }

  /**
   * Returns a position with the API's default accuracy (100 metres).
   *
   * @param latitude from -90 to 90
   * @param longitude from -180 to 180
   * @return the position
   */
  public static Geolocation of(double latitude, double longitude) {
    return new Geolocation(latitude, longitude, null);
  }

  /**
   * Returns a position with an accuracy.
   *
   * @param latitude from -90 to 90
   * @param longitude from -180 to 180
   * @param accuracy in metres, from 1 to 10000
   * @return the position
   */
  public static Geolocation of(double latitude, double longitude, double accuracy) {
    return new Geolocation(latitude, longitude, accuracy);
  }

  /**
   * Returns the latitude.
   *
   * @return the latitude
   */
  public Double latitude() {
    return latitude;
  }

  /**
   * Returns the longitude.
   *
   * @return the longitude
   */
  public Double longitude() {
    return longitude;
  }

  /**
   * Returns the accuracy in metres.
   *
   * @return the accuracy, or {@code null} if not set
   */
  public Double accuracy() {
    return accuracy;
  }
}
