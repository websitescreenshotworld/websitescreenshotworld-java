package com.websitescreenshotworld.webhooks;

/** A schedule was paused, for example because the plan ran out of captures. */
public final class SchedulePausedEvent extends WebhookEvent {
  private final SchedulePause data;

  SchedulePausedEvent(Envelope envelope, SchedulePause data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public SchedulePause data() {
    return data;
  }
}
