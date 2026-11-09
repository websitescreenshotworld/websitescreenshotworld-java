package com.websitescreenshotworld;

import java.util.Map;

/**
 * What a trigger-now call did.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class TriggerStatus extends Token {
  /** A capture was queued. */
  public static final TriggerStatus TRIGGERED = new TriggerStatus("triggered");

  /** A capture was already queued or running; nothing new was started or charged. */
  public static final TriggerStatus ALREADY_PENDING = new TriggerStatus("already_pending");

  private static final Map<String, TriggerStatus> KNOWN = index(TRIGGERED, ALREADY_PENDING);

  private TriggerStatus(String value) {
    super(value);
  }

  /**
   * Returns the TriggerStatus for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static TriggerStatus of(String value) {
    return lookup(KNOWN, value, TriggerStatus::new);
  }
}
