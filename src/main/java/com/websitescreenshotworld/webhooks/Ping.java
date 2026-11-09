package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.internal.Json;
import java.time.Instant;
import java.util.UUID;

/** A test delivery to a webhook endpoint. */
public final class Ping {
  private UUID endpointId;
  private String endpointName;
  private String message;
  private Instant sentAt;

  private Ping() {}

  /**
   * Returns the endpoint id.
   *
   * @return the id
   */
  public UUID endpointId() {
    return endpointId;
  }

  /**
   * Returns the endpoint's name in the dashboard.
   *
   * @return the name
   */
  public String endpointName() {
    return endpointName;
  }

  /**
   * Returns a note for a person.
   *
   * @return the message
   */
  public String message() {
    return message;
  }

  /**
   * Returns when the test was sent.
   *
   * @return the time
   */
  public Instant sentAt() {
    return sentAt;
  }

  /** Returns the ping as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
