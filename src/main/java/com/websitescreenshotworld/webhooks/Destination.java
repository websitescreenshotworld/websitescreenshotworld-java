package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.internal.Json;
import java.time.Instant;

/** One place a scheduled capture was delivered to. */
public final class Destination {
  private Integer slot;
  private String name;
  private String provider;
  private String status;
  private Instant completedAt;

  private Destination() {}

  /**
   * Returns the destination's position in the schedule's settings.
   *
   * @return the slot
   */
  public Integer slot() {
    return slot;
  }

  /**
   * Returns the destination's name in the dashboard.
   *
   * @return the name
   */
  public String name() {
    return name;
  }

  /**
   * Returns the kind of destination, such as {@code aws_s3}.
   *
   * @return the provider
   */
  public String provider() {
    return provider;
  }

  /**
   * Returns how the delivery went, such as {@code succeeded}.
   *
   * @return the status
   */
  public String status() {
    return status;
  }

  /**
   * Returns when the delivery finished.
   *
   * @return the time, or {@code null} if it has not finished
   */
  public Instant completedAt() {
    return completedAt;
  }

  /** Returns the destination as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
