package com.websitescreenshotworld.webhooks;

/** A sitemap schedule finished a run. */
public final class SitemapRunCompletedEvent extends WebhookEvent {
  private final SitemapRun data;

  SitemapRunCompletedEvent(Envelope envelope, SitemapRun data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data.
   *
   * @return the data
   */
  public SitemapRun data() {
    return data;
  }
}
