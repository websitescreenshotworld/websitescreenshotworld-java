package com.websitescreenshotworld;

import com.websitescreenshotworld.internal.Json;
import com.websitescreenshotworld.webhooks.Product;

/**
 * Whose API key this is: what {@link InstantSnap#account()} and {@link AutoSchedule#account()}
 * answer. It never includes the key itself.
 */
public final class Account {
  private String keyName;
  private String keyPrefix;
  private Product product;
  private String email;

  private Account() {}

  /**
   * Returns the name given to the key in the dashboard (Developer Tools, API Keys).
   *
   * @return the name, or {@code null} when the key has none
   */
  public String keyName() {
    return keyName;
  }

  /**
   * Returns the key's public prefix, as the dashboard shows it. It is not enough to use the key.
   *
   * @return the prefix
   */
  public String keyPrefix() {
    return keyPrefix;
  }

  /**
   * Returns the product the key belongs to.
   *
   * @return the product
   */
  public Product product() {
    return product;
  }

  /**
   * Returns the email of the member who created the key.
   *
   * @return the email, or {@code null} when they are no longer a member
   */
  public String email() {
    return email;
  }

  /** Returns the account as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
