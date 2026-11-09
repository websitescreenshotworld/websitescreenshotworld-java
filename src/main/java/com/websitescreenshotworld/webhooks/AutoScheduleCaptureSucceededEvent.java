package com.websitescreenshotworld.webhooks;

/** A scheduled web page was captured. */
public final class AutoScheduleCaptureSucceededEvent extends WebhookEvent {
  private final ScheduledCapture data;

  AutoScheduleCaptureSucceededEvent(Envelope envelope, ScheduledCapture data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public ScheduledCapture data() {
    return data;
  }
}
