package com.websitescreenshotworld.webhooks;

import java.time.Instant;
import java.util.UUID;

/**
 * A webhook delivery. Each event type is a subclass with a typed {@code data()}; a type this
 * library does not know yet comes back as {@link UnknownWebhookEvent}.
 *
 * <table>
 *   <caption>Event classes</caption>
 *   <tr><th>Product</th><th>Type</th><th>Class</th></tr>
 *   <tr><td>instant_snap</td><td>capture.succeeded</td>
 *       <td>{@link InstantSnapCaptureSucceededEvent}</td></tr>
 *   <tr><td>instant_snap</td><td>capture.failed</td>
 *       <td>{@link InstantSnapCaptureFailedEvent}</td></tr>
 *   <tr><td>auto_schedule</td><td>capture.succeeded</td>
 *       <td>{@link AutoScheduleCaptureSucceededEvent}</td></tr>
 *   <tr><td>auto_schedule</td><td>capture.failed</td>
 *       <td>{@link AutoScheduleCaptureFailedEvent}</td></tr>
 *   <tr><td>auto_schedule</td><td>capture.changed</td><td>{@link CaptureChangedEvent}</td></tr>
 *   <tr><td>auto_schedule</td><td>delivery.completed</td><td>{@link DeliveryCompletedEvent}</td></tr>
 *   <tr><td>auto_schedule</td><td>schedule.paused</td><td>{@link SchedulePausedEvent}</td></tr>
 *   <tr><td>auto_schedule</td><td>sitemap_run.completed</td>
 *       <td>{@link SitemapRunCompletedEvent}</td></tr>
 *   <tr><td>both</td><td>ping</td><td>{@link PingEvent}</td></tr>
 * </table>
 */
public abstract class WebhookEvent {
  private final UUID id;
  private final String type;
  private final Instant createdAt;
  private final Product product;

  WebhookEvent(Envelope envelope) {
    this.id = envelope.id;
    this.type = envelope.type;
    this.createdAt = envelope.createdAt;
    this.product = envelope.product;
  }

  /**
   * Returns the delivery id. A retried delivery keeps its id, so use it to skip duplicates.
   *
   * @return the id
   */
  public final UUID id() {
    return id;
  }

  /**
   * Returns the event type, such as {@code capture.succeeded}.
   *
   * @return the type
   */
  public final String type() {
    return type;
  }

  /**
   * Returns when the event happened.
   *
   * @return the time
   */
  public final Instant createdAt() {
    return createdAt;
  }

  /**
   * Returns the product that sent the event.
   *
   * @return the product
   */
  public final Product product() {
    return product;
  }

  /** The fields every delivery carries, as read from the body. */
  static final class Envelope {
    private UUID id;
    private String type;
    private Instant createdAt;
    private Product product;

    private Envelope() {}

    String type() {
      return type;
    }

    Product product() {
      return product;
    }
  }
}
