package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.internal.Json;
import java.time.Instant;
import java.util.UUID;

/** A schedule that was paused, and why. */
public final class SchedulePause {
  private UUID scheduleId;
  private String scheduleName;
  private String url;
  private String reason;
  private Instant pausedAt;

  private SchedulePause() {}

  /**
   * Returns the web page id.
   *
   * @return the id
   */
  public UUID scheduleId() {
    return scheduleId;
  }

  /**
   * Returns the web page's name in the dashboard.
   *
   * @return the name
   */
  public String scheduleName() {
    return scheduleName;
  }

  /**
   * Returns the page the schedule captures.
   *
   * @return the URL
   */
  public String url() {
    return url;
  }

  /**
   * Returns why the schedule was paused, such as {@code quota_exhausted}.
   *
   * @return the reason
   */
  public String reason() {
    return reason;
  }

  /**
   * Returns when the schedule was paused.
   *
   * @return the time
   */
  public Instant pausedAt() {
    return pausedAt;
  }

  /** Returns the pause as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
