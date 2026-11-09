package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.CaptureFormat;
import com.websitescreenshotworld.MediaCategory;
import com.websitescreenshotworld.internal.Json;
import java.time.Instant;
import java.util.UUID;

/** A capture of a scheduled web page, as Auto Schedule webhooks describe it. */
public class ScheduledCapture {
  private UUID id;
  private UUID scheduleId;
  private String scheduleName;
  private String url;
  private String status;
  private MediaCategory mediaCategory;
  private CaptureFormat format;
  private Integer pageHttpStatus;
  private Instant capturedAt;
  private String finalUrl;
  private String mediaSha256;
  private ScheduledPageSource pageSource;
  private UUID sitemapRunId;

  ScheduledCapture() {}

  /**
   * Returns the capture id.
   *
   * @return the id
   */
  public UUID id() {
    return id;
  }

  /**
   * Returns the web page id, which {@link com.websitescreenshotworld.AutoSchedule#triggerNow}
   * takes.
   *
   * @return the id
   */
  public UUID scheduleId() {
    return scheduleId;
  }

  /**
   * Returns the web page's name in the dashboard.
   *
   * @return the name
   */
  public String scheduleName() {
    return scheduleName;
  }

  /**
   * Returns the page captured.
   *
   * @return the URL
   */
  public String url() {
    return url;
  }

  /**
   * Returns the capture status, such as {@code succeeded}, {@code failed} or {@code
   * media_delivered}.
   *
   * @return the status
   */
  public String status() {
    return status;
  }

  /**
   * Returns the kind of file produced.
   *
   * @return the category
   */
  public MediaCategory mediaCategory() {
    return mediaCategory;
  }

  /**
   * Returns the file format.
   *
   * @return the format, or {@code null} if nothing was written
   */
  public CaptureFormat format() {
    return format;
  }

  /**
   * Returns the status the website answered with.
   *
   * @return the HTTP status, or {@code null} if the website did not answer
   */
  public Integer pageHttpStatus() {
    return pageHttpStatus;
  }

  /**
   * Returns when the page was captured.
   *
   * @return the time, or {@code null} if not captured
   */
  public Instant capturedAt() {
    return capturedAt;
  }

  /**
   * Returns where the browser ended up, when the page redirected.
   *
   * @return the URL, or {@code null} if the page did not redirect
   */
  public String finalUrl() {
    return finalUrl;
  }

  /**
   * Returns the lowercase hex SHA-256 digest of the file.
   *
   * @return the digest, or {@code null} if nothing was written
   */
  public String mediaSha256() {
    return mediaSha256;
  }

  /**
   * Returns the page source kept with the capture.
   *
   * @return the page source, or {@code null} if none was kept
   */
  public ScheduledPageSource pageSource() {
    return pageSource;
  }

  /**
   * Returns the sitemap run the capture belongs to.
   *
   * @return the run id, or {@code null} if the capture was not part of a sitemap run
   */
  public UUID sitemapRunId() {
    return sitemapRunId;
  }

  /** Returns the capture as JSON, with the API's field names. */
  @Override
  public String toString() {
    return Json.toTree(this).toString();
  }
}
