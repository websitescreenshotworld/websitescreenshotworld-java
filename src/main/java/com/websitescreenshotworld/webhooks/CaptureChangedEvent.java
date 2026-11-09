package com.websitescreenshotworld.webhooks;

/** A scheduled web page looks different from its previous capture. */
public final class CaptureChangedEvent extends WebhookEvent {
  private final ChangedCapture data;

  CaptureChangedEvent(Envelope envelope, ChangedCapture data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public ChangedCapture data() {
    return data;
  }
}
