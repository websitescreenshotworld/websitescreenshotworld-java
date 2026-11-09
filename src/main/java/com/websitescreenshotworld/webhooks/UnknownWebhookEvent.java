package com.websitescreenshotworld.webhooks;

import java.util.Collections;
import java.util.Map;

/**
 * An event this version of the library does not know, such as a type added to the API since. Its
 * data is kept as parsed JSON: maps, lists, strings, numbers, booleans and nulls.
 */
public final class UnknownWebhookEvent extends WebhookEvent {
  private final Map<String, Object> data;

  UnknownWebhookEvent(Envelope envelope, Map<String, Object> data) {
    super(envelope);
    this.data = data;
  }

  /**
   * Returns the event's data as parsed JSON.
   *
   * @return the data, empty if the event had none
   */
  public Map<String, Object> data() {
    return data == null ? Map.of() : Collections.unmodifiableMap(data);
  }
}
