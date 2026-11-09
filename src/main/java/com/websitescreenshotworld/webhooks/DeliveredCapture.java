package com.websitescreenshotworld.webhooks;

import java.util.Collections;
import java.util.List;

/** A scheduled capture and how its delivery to each destination went. */
public final class DeliveredCapture extends ScheduledCapture {
  private List<Destination> destinations;

  private DeliveredCapture() {}

  /**
   * Returns each destination the capture was sent to.
   *
   * @return the destinations
   */
  public List<Destination> destinations() {
    return destinations == null ? List.of() : Collections.unmodifiableList(destinations);
  }
}
