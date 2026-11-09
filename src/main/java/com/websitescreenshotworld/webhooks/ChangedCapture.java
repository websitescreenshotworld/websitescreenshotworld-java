package com.websitescreenshotworld.webhooks;

import java.util.UUID;

/** A scheduled capture and how much it differs from the previous one. */
public final class ChangedCapture extends ScheduledCapture {
  private Double changePercent;
  private UUID comparedToCaptureId;
  private Boolean changeImageAvailable;

  private ChangedCapture() {}

  /**
   * Returns how much of the page changed.
   *
   * @return the change, from 0 to 100
   */
  public Double changePercent() {
    return changePercent;
  }

  /**
   * Returns the capture this one was compared with.
   *
   * @return the capture id
   */
  public UUID comparedToCaptureId() {
    return comparedToCaptureId;
  }

  /**
   * Returns whether an image highlighting the change is available in the dashboard.
   *
   * @return the flag
   */
  public Boolean changeImageAvailable() {
    return changeImageAvailable;
  }
}
