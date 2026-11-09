package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.internal.Json;
import java.time.Instant;
import java.util.UUID;

/** One run of a sitemap schedule: which pages it found and how their captures went. */
public final class SitemapRun {
  private UUID sitemapRunId;
  private UUID scheduleId;
  private String scheduleName;
  private String sitemapUrl;
  private String status;
  private Instant runAt;
  private Instant completedAt;
  private Integer pagesListed;
  private Integer pagesMatched;
  private Integer pagesQueued;
  private Integer pagesSucceeded;
  private Integer pagesFailed;
  private Integer pagesOverLimit;
  private Integer pagesOverQuota;
  private String message;

  private SitemapRun() {}

  /**
   * Returns the run id, which each of its captures carries as {@code sitemap_run_id}.
   *
   * @return the id
   */
  public UUID sitemapRunId() {
    return sitemapRunId;
  }

  /**
   * Returns the schedule id.
   *
   * @return the id
   */
  public UUID scheduleId() {
    return scheduleId;
  }

  /**
   * Returns the schedule's name in the dashboard.
   *
   * @return the name
   */
  public String scheduleName() {
    return scheduleName;
  }

  /**
   * Returns the sitemap read.
   *
   * @return the URL
   */
  public String sitemapUrl() {
    return sitemapUrl;
  }

  /**
   * Returns how the run ended, such as {@code completed}.
   *
   * @return the status
   */
  public String status() {
    return status;
  }

  /**
   * Returns when the run started.
   *
   * @return the time
   */
  public Instant runAt() {
    return runAt;
  }

  /**
   * Returns when the run finished.
   *
   * @return the time
   */
  public Instant completedAt() {
    return completedAt;
  }

  /**
   * Returns how many pages the sitemap listed.
   *
   * @return the count
   */
  public Integer pagesListed() {
    return pagesListed;
  }

  /**
   * Returns how many listed pages matched the schedule's filters.
   *
   * @return the count
   */
  public Integer pagesMatched() {
    return pagesMatched;
  }

  /**
   * Returns how many pages were queued for capture.
   *
   * @return the count
   */
  public Integer pagesQueued() {
    return pagesQueued;
  }

  /**
   * Returns how many captures succeeded.
   *
   * @return the count
   */
  public Integer pagesSucceeded() {
    return pagesSucceeded;
  }

  /**
   * Returns how many captures failed.
   *
   * @return the count
   */
  public Integer pagesFailed() {
    return pagesFailed;
  }

  /**
   * Returns how many matched pages were skipped for being over the schedule's page limit.
   *
   * @return the count
   */
  public Integer pagesOverLimit() {
    return pagesOverLimit;
  }

  /**
   * Returns how many matched pages were skipped because the plan ran out of captures.
   *
   * @return the count
   */
  public Integer pagesOverQuota() {
    return pagesOverQuota;
  }

  /**
   * Returns a note on the run for a person.
   *
   * @return the message, or {@code null} if there is none
   */
  public String message() {
    return message;
  }

  /** Returns the run as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
