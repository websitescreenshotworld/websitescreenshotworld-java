package com.websitescreenshotworld;

import java.util.Map;

/**
 * Where an Instant Snap capture is in its life.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class CaptureStatus extends Token {
  /** Still running. */
  public static final CaptureStatus PENDING = new CaptureStatus("pending");

  /**
   * Accepted by {@link InstantSnap#submit} or {@link InstantSnap#submitToBucket} and waiting for a
   * browser.
   */
  public static final CaptureStatus QUEUED = new CaptureStatus("queued");

  /** An async capture a browser is working on. */
  public static final CaptureStatus RUNNING = new CaptureStatus("running");

  /** Captured; the file is kept until {@link Capture#expiresAt()}. */
  public static final CaptureStatus SUCCEEDED = new CaptureStatus("succeeded");

  /** Captured and returned in the response; the file is not kept. */
  public static final CaptureStatus DELIVERED = new CaptureStatus("delivered");

  /** The website could not be captured; see {@link Capture#errorCode()}. */
  public static final CaptureStatus FAILED = new CaptureStatus("failed");

  /** The page did not finish in time. */
  public static final CaptureStatus TIMED_OUT = new CaptureStatus("timed_out");

  /** The file has been deleted. */
  public static final CaptureStatus EXPIRED = new CaptureStatus("expired");

  private static final Map<String, CaptureStatus> KNOWN =
      index(PENDING, QUEUED, RUNNING, SUCCEEDED, DELIVERED, FAILED, TIMED_OUT, EXPIRED);

  private CaptureStatus(String value) {
    super(value);
  }

  /**
   * Returns the CaptureStatus for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static CaptureStatus of(String value) {
    return lookup(KNOWN, value, CaptureStatus::new);
  }
}
