package com.websitescreenshotworld;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * The Auto Schedule API: start a scheduled web page's capture now. Get it from {@link
 * WebsiteScreenshotWorld#autoSchedule()}. The result arrives by webhook; see {@link
 * com.websitescreenshotworld.webhooks.Webhook}.
 */
public final class AutoSchedule {
  private final HttpTransport transport;
  private final String baseUrl;
  private final String apiKey;
  private final Duration timeout;

  AutoSchedule(HttpTransport transport, String baseUrl, String apiKey, Duration timeout) {
    this.transport = transport;
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.timeout = timeout;
  }

  /**
   * Queues a capture of a web page now. Calling again while that capture is pending starts nothing
   * new and answers {@link TriggerStatus#ALREADY_PENDING}, so the call is safe to repeat.
   *
   * @param webPageId the web page id from the dashboard, sent as {@code schedule_id} in webhooks
   * @return what the call did
   * @throws IllegalStateException if the client has no Auto Schedule API key
   */
  public TriggerResult triggerNow(UUID webPageId) {
    return triggerNow(webPageId, RequestOptions.none());
  }

  /**
   * Queues a capture of a web page now. Calling again while that capture is pending starts nothing
   * new and answers {@link TriggerStatus#ALREADY_PENDING}, so the call is safe to repeat.
   *
   * @param webPageId the web page id from the dashboard, sent as {@code schedule_id} in webhooks
   * @param requestOptions settings for this call; the idempotency key is not used
   * @return what the call did
   * @throws IllegalStateException if the client has no Auto Schedule API key
   */
  public TriggerResult triggerNow(UUID webPageId, RequestOptions requestOptions) {
    Objects.requireNonNull(webPageId, "webPageId");
    Objects.requireNonNull(requestOptions, "requestOptions");
    if (apiKey == null) {
      throw new IllegalStateException(
          "No Auto Schedule API key: set WSW_AUTO_SCHEDULE_API_KEY or call "
              + "WebsiteScreenshotWorld.builder().autoScheduleApiKey(...)");
    }
    URI uri = URI.create(baseUrl + "/auto-schedule/v1/web-pages/" + webPageId + "/trigger-now");
    Duration callTimeout =
        requestOptions.timeout() != null ? requestOptions.timeout() : this.timeout;
    ApiRequest request =
        ApiRequest.post(uri, null, callTimeout)
            .header("X-Api-Key", apiKey)
            .header("Accept", "application/json");
    return transport.sendForJson(request, TriggerResult.class);
  }
}
