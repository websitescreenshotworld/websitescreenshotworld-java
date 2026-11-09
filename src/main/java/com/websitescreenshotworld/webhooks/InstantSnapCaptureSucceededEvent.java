package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.Capture;

/**
 * An Instant Snap capture succeeded. The data is the capture as {@link
 * com.websitescreenshotworld.InstantSnap#get} returns it, without download links; call {@code get}
 * for a fresh one.
 */
public final class InstantSnapCaptureSucceededEvent extends WebhookEvent {
  private final Capture data;

  InstantSnapCaptureSucceededEvent(Envelope envelope, Capture data) {
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
