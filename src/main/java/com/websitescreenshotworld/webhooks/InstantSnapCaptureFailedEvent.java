package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.Capture;

/** An Instant Snap capture failed; {@link Capture#errorCode()} says why. */
public final class InstantSnapCaptureFailedEvent extends WebhookEvent {
  private final Capture data;

  InstantSnapCaptureFailedEvent(Envelope envelope, Capture data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public Capture data() {
    return data;
  }
}
