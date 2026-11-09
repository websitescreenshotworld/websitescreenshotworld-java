package com.websitescreenshotworld.webhooks;

/** A scheduled capture was delivered to its destinations. */
public final class DeliveryCompletedEvent extends WebhookEvent {
  private final DeliveredCapture data;

  DeliveryCompletedEvent(Envelope envelope, DeliveredCapture data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public DeliveredCapture data() {
    return data;
  }
}
