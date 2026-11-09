package com.websitescreenshotworld.webhooks;

/** A scheduled web page could not be captured. */
public final class AutoScheduleCaptureFailedEvent extends WebhookEvent {
  private final ScheduledCapture data;

  AutoScheduleCaptureFailedEvent(Envelope envelope, ScheduledCapture data) {
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
