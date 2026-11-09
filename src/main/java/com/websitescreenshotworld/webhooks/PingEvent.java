package com.websitescreenshotworld.webhooks;

/** A test delivery, sent from the dashboard to check an endpoint. */
public final class PingEvent extends WebhookEvent {
  private final Ping data;

  PingEvent(Envelope envelope, Ping data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public Ping data() {
    return data;
  }
}
