package com.websitescreenshotworld;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

/**
 * The Website Screenshot World client, covering Instant Snap and Auto Schedule.
 *
 * <pre>{@code
 * WebsiteScreenshotWorld client = WebsiteScreenshotWorld.builder().build();
 * Capture capture = client.instantSnap().capture(
 *     CaptureOptions.builder().url("https://example.com").build());
 * }</pre>
 *
 * <p>Each API key defaults to its environment variable, {@code WSW_INSTANT_SNAP_API_KEY} or {@code
 * WSW_AUTO_SCHEDULE_API_KEY}. Either can be left out; calling a product without its key throws
 * {@link IllegalStateException} before anything is sent.
 *
 * <p>A client is immutable and safe to share between threads. Create one and reuse it, so
 * connections are pooled.
 */
public final class WebsiteScreenshotWorld {
  /** Where Instant Snap is served unless {@link Builder#instantSnapBaseUrl} says otherwise. */
  public static final String DEFAULT_INSTANT_SNAP_BASE_URL =
      "https://api.websitescreenshotworld.com";

  /** Where Auto Schedule is served unless {@link Builder#autoScheduleBaseUrl} says otherwise. */
  public static final String DEFAULT_AUTO_SCHEDULE_BASE_URL =
      "https://autoapi.websitescreenshotworld.com";

  /** How long a capture may take unless {@link Builder#captureTimeout} says otherwise. */
  public static final Duration DEFAULT_CAPTURE_TIMEOUT = Duration.ofSeconds(180);

  /** How long other calls may take unless {@link Builder#timeout} says otherwise. */
  public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

  /** How many times a failed call is retried unless {@link Builder#maxRetries} says otherwise. */
  public static final int DEFAULT_MAX_RETRIES = 2;

  private final InstantSnap instantSnap;
  private final AutoSchedule autoSchedule;

  private WebsiteScreenshotWorld(Builder builder) {
    HttpClient httpClient =
        builder.httpClient != null
            ? builder.httpClient
            // Redirects are never followed: the API key would go wherever one pointed. Neither the
            // API nor a download link redirects, so a 3xx is raised as the refusal it is.
            : HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
    HttpTransport transport = new HttpTransport(httpClient, builder.maxRetries);
    this.instantSnap =
        new InstantSnap(
            transport,
            trimSlash(builder.instantSnapBaseUrl),
            keyOrEnv(builder.instantSnapApiKey, "WSW_INSTANT_SNAP_API_KEY"),
            builder.captureTimeout,
            builder.timeout);
    this.autoSchedule =
        new AutoSchedule(
            transport,
            trimSlash(builder.autoScheduleBaseUrl),
            keyOrEnv(builder.autoScheduleApiKey, "WSW_AUTO_SCHEDULE_API_KEY"),
            builder.timeout);
  }

  /**
   * Starts a client.
   *
   * @return a new builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Returns the Instant Snap API.
   *
   * @return Instant Snap
   */
  public InstantSnap instantSnap() {
    return instantSnap;
  }

  /**
   * Returns the Auto Schedule API.
   *
   * @return Auto Schedule
   */
  public AutoSchedule autoSchedule() {
    return autoSchedule;
  }

  private static String keyOrEnv(String key, String variable) {
    String value = key != null ? key : System.getenv(variable);
    return value == null || value.isBlank() ? null : value.trim();
  }

  private static String trimSlash(String url) {
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
  }

  /** Builds a {@link WebsiteScreenshotWorld} client. */
  public static final class Builder {
    private String instantSnapApiKey;
    private String autoScheduleApiKey;
    private String instantSnapBaseUrl = DEFAULT_INSTANT_SNAP_BASE_URL;
    private String autoScheduleBaseUrl = DEFAULT_AUTO_SCHEDULE_BASE_URL;
    private Duration captureTimeout = DEFAULT_CAPTURE_TIMEOUT;
    private Duration timeout = DEFAULT_TIMEOUT;
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private HttpClient httpClient;

    private Builder() {}

    /**
     * Sets the Instant Snap API key. Defaults to {@code WSW_INSTANT_SNAP_API_KEY}.
     *
     * @param apiKey the key
     * @return this builder
     */
    public Builder instantSnapApiKey(String apiKey) {
      this.instantSnapApiKey = apiKey;
      return this;
    }

    /**
     * Sets the Auto Schedule API key. Defaults to {@code WSW_AUTO_SCHEDULE_API_KEY}.
     *
     * @param apiKey the key
     * @return this builder
     */
    public Builder autoScheduleApiKey(String apiKey) {
      this.autoScheduleApiKey = apiKey;
      return this;
    }

    /**
     * Sets where Instant Snap is served, for a proxy or a test server.
     *
     * @param baseUrl the scheme, host and optional path prefix, such as {@code
     *     https://api.websitescreenshotworld.com}
     * @return this builder
     */
    public Builder instantSnapBaseUrl(String baseUrl) {
      this.instantSnapBaseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
      return this;
    }

    /**
     * Sets where Auto Schedule is served, for a proxy or a test server.
     *
     * @param baseUrl the scheme, host and optional path prefix, such as {@code
     *     https://autoapi.websitescreenshotworld.com}
     * @return this builder
     */
    public Builder autoScheduleBaseUrl(String baseUrl) {
      this.autoScheduleBaseUrl = Objects.requireNonNull(baseUrl, "baseUrl");
      return this;
    }

    /**
     * Sets how long a capture call may take, retries included. Defaults to 180 seconds; the API can
     * spend up to 120 seconds on a capture, so keep it above that.
     *
     * @param captureTimeout a positive duration
     * @return this builder
     */
    public Builder captureTimeout(Duration captureTimeout) {
      this.captureTimeout = positive(captureTimeout, "captureTimeout");
      return this;
    }

    /**
     * Sets how long any other call may take, retries included. Defaults to 30 seconds.
     *
     * @param timeout a positive duration
     * @return this builder
     */
    public Builder timeout(Duration timeout) {
      this.timeout = positive(timeout, "timeout");
      return this;
    }

    /**
     * Sets how many times a call is retried after a temporary failure. Defaults to 2, so at most
     * three attempts; 0 turns retries off.
     *
     * @param maxRetries zero or more
     * @return this builder
     */
    public Builder maxRetries(int maxRetries) {
      if (maxRetries < 0) {
        throw new IllegalArgumentException("maxRetries must not be negative");
      }
      this.maxRetries = maxRetries;
      return this;
    }

    /**
     * Sets the {@link HttpClient} to send requests with, for a proxy, an executor or TLS settings
     * of your own. It must not follow redirects ({@code
     * followRedirects(HttpClient.Redirect.NEVER)}, the default of {@link HttpClient#newBuilder()}):
     * a redirect followed would carry the API key to another host, and an answer that came through
     * one is refused.
     *
     * @param httpClient the HTTP client
     * @return this builder
     */
    public Builder httpClient(HttpClient httpClient) {
      this.httpClient = Objects.requireNonNull(httpClient, "httpClient");
      return this;
    }

    /**
     * Builds the client, reading any key not set from its environment variable.
     *
     * @return the client
     */
    public WebsiteScreenshotWorld build() {
      return new WebsiteScreenshotWorld(this);
    }

    private static Duration positive(Duration duration, String name) {
      if (duration.isNegative() || duration.isZero()) {
        throw new IllegalArgumentException(name + " must be positive");
      }
      return duration;
    }
  }
}
