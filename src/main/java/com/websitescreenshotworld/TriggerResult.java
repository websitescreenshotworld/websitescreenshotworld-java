package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;
import java.time.Instant;
import java.util.UUID;

/**
 * What {@link AutoSchedule#triggerNow} did. The capture's result arrives later by webhook, with the
 * web page id as {@code schedule_id}.
 */
public final class TriggerResult {
  private TriggerStatus status;
  private UUID webPageId;
  private TriggeredBy triggeredBy;
  private Instant triggeredAt;
  private String message;

  private TriggerResult() {}

  /**
   * Returns whether a capture was queued or one was already pending.
   *
   * @return the status
   */
  public TriggerStatus status() {
    return status;
  }

  /**
   * Returns the web page id.
   *
   * @return the id
   */
  public UUID webPageId() {
    return webPageId;
  }

  /**
   * Returns who started the pending capture: this call, or someone in the dashboard.
   *
   * @return the origin
   */
  public TriggeredBy triggeredBy() {
    return triggeredBy;
  }

  /**
   * Returns when the capture was queued; for {@link TriggerStatus#ALREADY_PENDING}, the original
   * trigger.
   *
   * @return the time
   */
  public Instant triggeredAt() {
    return triggeredAt;
  }

  /**
   * Returns one sentence for a person.
   *
   * @return the message
   */
  public String message() {
    return message;
  }

  /** Returns the result as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
