package com.websitescreenshotworld;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import com.websitescreenshotworld.internal.Json;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * The Instant Snap API: capture a web page on demand. Get it from {@link
 * WebsiteScreenshotWorld#instantSnap()}.
 *
 * <p>Every method throws a subclass of {@link
 * com.websitescreenshotworld.errors.WebsiteScreenshotWorldException} when the call fails, after the
 * retries the API allows.
 */
public final class InstantSnap {
  private static final String CAPTURES = "/instant-snap/v1/captures";
  private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(360);
  private static final Duration POLL_INTERVAL = Duration.ofSeconds(3);

  private final HttpTransport transport;
  private final String baseUrl;
  private final String apiKey;
  private final Duration captureTimeout;
  private final Duration timeout;

  InstantSnap(
      HttpTransport transport,
      String baseUrl,
      String apiKey,
      Duration captureTimeout,
      Duration timeout) {
    this.transport = transport;
    this.baseUrl = baseUrl;
    this.apiKey = apiKey;
    this.captureTimeout = captureTimeout;
    this.timeout = timeout;
  }

  /**
   * Captures a page and answers with its metadata and a download link.
   *
   * @param options what to capture
   * @return the capture
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture capture(CaptureOptions options) {
    return capture(options, RequestOptions.none());
  }

  /**
   * Captures a page and answers with its metadata and a download link.
   *
   * @param options what to capture
   * @param requestOptions settings for this call
   * @return the capture
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture capture(CaptureOptions options, RequestOptions requestOptions) {
    return createCapture(options, ResponseType.JSON, false, requestOptions);
  }

  /**
   * Captures a page and answers with the file itself, up to 64 MB. The file is streamed, so close
   * the result when done.
   *
   * @param options what to capture; {@link CaptureOptions.Builder#pageSource} is not available
   * @return the file and its metadata
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public CaptureFile captureFile(CaptureOptions options) {
    return captureFile(options, RequestOptions.none());
  }

  /**
   * Captures a page and answers with the file itself, up to 64 MB. The file is streamed, so close
   * the result when done.
   *
   * @param options what to capture; {@link CaptureOptions.Builder#pageSource} is not available
   * @param requestOptions settings for this call
   * @return the file and its metadata
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public CaptureFile captureFile(CaptureOptions options, RequestOptions requestOptions) {
    ApiRequest request = captureRequest(options, ResponseType.BY_FORMAT, false, requestOptions);
    request.header("Accept", "*/*");
    HttpResponse<InputStream> response = transport.send(request);
    return new CaptureFile(response.body(), response.headers());
  }

  /**
   * Captures a page and uploads the file to your bucket.
   *
   * @param options what to capture, with {@link CaptureOptions.Builder#s3Delivery} set
   * @return the capture; {@link Capture#s3Delivery()} tracks the upload
   * @throws IllegalArgumentException if the options have no {@code s3Delivery}
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture captureToBucket(CaptureOptions options) {
    return captureToBucket(options, RequestOptions.none());
  }

  /**
   * Captures a page and uploads the file to your bucket.
   *
   * @param options what to capture, with {@link CaptureOptions.Builder#s3Delivery} set
   * @param requestOptions settings for this call
   * @return the capture; {@link Capture#s3Delivery()} tracks the upload
   * @throws IllegalArgumentException if the options have no {@code s3Delivery}
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture captureToBucket(CaptureOptions options, RequestOptions requestOptions) {
    if (options.s3Delivery() == null) {
      throw new IllegalArgumentException("captureToBucket needs CaptureOptions.s3Delivery");
    }
    return createCapture(options, ResponseType.CLOUD_STORAGE, false, requestOptions);
  }

  /**
   * Queues a capture and answers at once, without waiting for it to run. The capture comes back
   * {@link CaptureStatus#QUEUED}; follow it with {@link #waitFor(UUID)}, {@link #get(UUID)} or a
   * webhook. One still waiting 15 minutes after it was accepted ends {@link CaptureStatus#FAILED}
   * with {@code capture_expired}, not charged.
   *
   * @param options what to capture; {@link CaptureOptions.Builder#timeoutSeconds} sets the browser
   *     time it may use
   * @return the capture, {@link CaptureStatus#QUEUED}
   * @throws com.websitescreenshotworld.errors.RateLimitException with {@code queue_full} when too
   *     many async captures are waiting or running; not retried
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture submit(CaptureOptions options) {
    return submit(options, RequestOptions.none());
  }

  /**
   * Queues a capture and answers at once, without waiting for it to run. The capture comes back
   * {@link CaptureStatus#QUEUED}; follow it with {@link #waitFor(UUID)}, {@link #get(UUID)} or a
   * webhook. One still waiting 15 minutes after it was accepted ends {@link CaptureStatus#FAILED}
   * with {@code capture_expired}, not charged.
   *
   * @param options what to capture; {@link CaptureOptions.Builder#timeoutSeconds} sets the browser
   *     time it may use
   * @param requestOptions settings for this call; the timeout defaults to the client's ordinary
   *     timeout, not the capture timeout
   * @return the capture, {@link CaptureStatus#QUEUED}
   * @throws com.websitescreenshotworld.errors.RateLimitException with {@code queue_full} when too
   *     many async captures are waiting or running; not retried
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture submit(CaptureOptions options, RequestOptions requestOptions) {
    return createCapture(options, ResponseType.JSON, true, requestOptions);
  }

  /**
   * Queues a capture that uploads the file to your bucket, and answers at once, without waiting for
   * it to run. The capture comes back {@link CaptureStatus#QUEUED}.
   *
   * @param options what to capture, with {@link CaptureOptions.Builder#s3Delivery} set
   * @return the capture, {@link CaptureStatus#QUEUED}
   * @throws IllegalArgumentException if the options have no {@code s3Delivery}
   * @throws com.websitescreenshotworld.errors.RateLimitException with {@code queue_full} when too
   *     many async captures are waiting or running; not retried
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture submitToBucket(CaptureOptions options) {
    return submitToBucket(options, RequestOptions.none());
  }

  /**
   * Queues a capture that uploads the file to your bucket, and answers at once, without waiting for
   * it to run. The capture comes back {@link CaptureStatus#QUEUED}.
   *
   * @param options what to capture, with {@link CaptureOptions.Builder#s3Delivery} set
   * @param requestOptions settings for this call; the timeout defaults to the client's ordinary
   *     timeout, not the capture timeout
   * @return the capture, {@link CaptureStatus#QUEUED}
   * @throws IllegalArgumentException if the options have no {@code s3Delivery}
   * @throws com.websitescreenshotworld.errors.RateLimitException with {@code queue_full} when too
   *     many async captures are waiting or running; not retried
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture submitToBucket(CaptureOptions options, RequestOptions requestOptions) {
    if (options.s3Delivery() == null) {
      throw new IllegalArgumentException("submitToBucket needs CaptureOptions.s3Delivery");
    }
    return createCapture(options, ResponseType.CLOUD_STORAGE, true, requestOptions);
  }

  /**
   * Reads a capture every 3 seconds until it is no longer queued, running or pending, for at most
   * 360 seconds.
   *
   * @param id the capture id, as {@link #submit} returned it
   * @return the finished capture, or the capture as last read if it was still unfinished when the
   *     time ran out
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture waitFor(UUID id) {
    return waitFor(id, WAIT_TIMEOUT, POLL_INTERVAL, RequestOptions.none());
  }

  /**
   * Reads a capture every {@code pollInterval} until it is no longer queued, running or pending.
   *
   * @param id the capture id, as {@link #submit} returned it
   * @param timeout how long to keep reading
   * @param pollInterval the pause between reads; zero reads again at once
   * @return the finished capture, or the capture as last read if it was still unfinished when the
   *     time ran out
   * @throws IllegalArgumentException if a duration is negative
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture waitFor(UUID id, Duration timeout, Duration pollInterval) {
    return waitFor(id, timeout, pollInterval, RequestOptions.none());
  }

  /**
   * Reads a capture every {@code pollInterval} until it is no longer queued, running or pending.
   * Each read is a {@link #get(UUID, RequestOptions)}, and a refusal is thrown as from {@code get}.
   * Interrupting the calling thread stops it at once.
   *
   * @param id the capture id, as {@link #submit} returned it
   * @param timeout how long to keep reading
   * @param pollInterval the pause between reads; zero reads again at once
   * @param requestOptions settings for each read
   * @return the finished capture, or the capture as last read if it was still unfinished when the
   *     time ran out
   * @throws IllegalArgumentException if a duration is negative
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture waitFor(
      UUID id, Duration timeout, Duration pollInterval, RequestOptions requestOptions) {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(timeout, "timeout");
    Objects.requireNonNull(pollInterval, "pollInterval");
    if (timeout.isNegative() || pollInterval.isNegative()) {
      throw new IllegalArgumentException("timeout and pollInterval must not be negative");
    }
    long deadline = System.nanoTime() + timeout.toNanos();
    while (true) {
      if (Thread.currentThread().isInterrupted()) {
        throw new WebsiteScreenshotWorldException(
            "Interrupted while waiting for the capture", null);
      }
      Capture capture = get(id, requestOptions);
      if (!unfinished(capture.status())) {
        return capture;
      }
      long remaining = deadline - System.nanoTime();
      if (remaining <= 0) {
        return capture;
      }
      long pause = Math.min(pollInterval.toNanos(), remaining);
      if (pause > 0) {
        try {
          TimeUnit.NANOSECONDS.sleep(pause);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          throw new WebsiteScreenshotWorldException("Interrupted while waiting for the capture", e);
        }
      }
    }
  }

  /**
   * Reads a capture back, with a fresh download link.
   *
   * @param id the capture id
   * @return the capture
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture get(UUID id) {
    return get(id, RequestOptions.none());
  }

  /**
   * Reads a capture back, with a fresh download link.
   *
   * @param id the capture id
   * @param requestOptions settings for this call
   * @return the capture
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Capture get(UUID id, RequestOptions requestOptions) {
    Objects.requireNonNull(id, "id");
    ApiRequest request =
        ApiRequest.get(uri(CAPTURES + "/" + id), timeout(requestOptions, timeout))
            .header("X-Api-Key", requireKey())
            .header("Accept", "application/json");
    return transport.sendForJson(request, Capture.class);
  }

  /**
   * Writes a capture's file to {@code target}, replacing any file there.
   *
   * @param capture a capture with a {@linkplain Capture#downloadUrl() download link}
   * @param target where to write
   * @throws IllegalArgumentException if the capture has no download link
   * @throws UncheckedIOException if the file cannot be written
   */
  public void download(Capture capture, Path target) {
    download(capture, target, RequestOptions.none());
  }

  /**
   * Writes a capture's file to {@code target}, replacing any file there.
   *
   * @param capture a capture with a {@linkplain Capture#downloadUrl() download link}
   * @param target where to write
   * @param requestOptions settings for this call
   * @throws IllegalArgumentException if the capture has no download link
   * @throws UncheckedIOException if the file cannot be written
   */
  public void download(Capture capture, Path target, RequestOptions requestOptions) {
    Objects.requireNonNull(target, "target");
    try (InputStream in = openDownload(capture, requestOptions)) {
      Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Writes a capture's file to {@code target}, which is left open.
   *
   * @param capture a capture with a {@linkplain Capture#downloadUrl() download link}
   * @param target where to write
   * @throws IllegalArgumentException if the capture has no download link
   * @throws UncheckedIOException if the file cannot be read or written
   */
  public void download(Capture capture, OutputStream target) {
    download(capture, target, RequestOptions.none());
  }

  /**
   * Writes a capture's file to {@code target}, which is left open.
   *
   * @param capture a capture with a {@linkplain Capture#downloadUrl() download link}
   * @param target where to write
   * @param requestOptions settings for this call
   * @throws IllegalArgumentException if the capture has no download link
   * @throws UncheckedIOException if the file cannot be read or written
   */
  public void download(Capture capture, OutputStream target, RequestOptions requestOptions) {
    Objects.requireNonNull(target, "target");
    try (InputStream in = openDownload(capture, requestOptions)) {
      in.transferTo(target);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Returns the values a capture takes for options it does not send, and the limits. Needs no API
   * key.
   *
   * @return the default set
   */
  public Defaults defaults() {
    return defaults(RequestOptions.none());
  }

  /**
   * Returns the values a capture takes for options it does not send, and the limits. Needs no API
   * key.
   *
   * @param requestOptions settings for this call
   * @return the default set
   */
  public Defaults defaults(RequestOptions requestOptions) {
    ApiRequest request =
        ApiRequest.get(uri("/instant-snap/v1/defaults"), timeout(requestOptions, timeout))
            .header("Accept", "application/json");
    return transport.sendForJson(request, Defaults.class);
  }

  /**
   * Returns the devices a capture can emulate and the countries it can use a proxy in.
   *
   * @return the catalog
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Catalog catalog() {
    return catalog(RequestOptions.none());
  }

  /**
   * Returns the devices a capture can emulate and the countries it can use a proxy in.
   *
   * @param requestOptions settings for this call
   * @return the catalog
   * @throws IllegalStateException if the client has no Instant Snap API key
   */
  public Catalog catalog(RequestOptions requestOptions) {
    ApiRequest request =
        ApiRequest.get(uri("/instant-snap/v1/catalog"), timeout(requestOptions, timeout))
            .header("X-Api-Key", requireKey())
            .header("Accept", "application/json");
    return transport.sendForJson(request, Catalog.class);
  }

  private static boolean unfinished(CaptureStatus status) {
    return CaptureStatus.QUEUED.equals(status)
        || CaptureStatus.RUNNING.equals(status)
        || CaptureStatus.PENDING.equals(status);
  }

  private Capture createCapture(
      CaptureOptions options,
      ResponseType responseType,
      boolean async,
      RequestOptions requestOptions) {
    ApiRequest request =
        captureRequest(options, responseType, async, requestOptions)
            .header("Accept", "application/json");
    HttpResponse<InputStream> response = transport.send(request);
    boolean replayed =
        response
            .headers()
            .firstValue("Idempotent-Replayed")
            .map("true"::equalsIgnoreCase)
            .orElse(false);
    return HttpTransport.readJson(response, Capture.class).replayed(replayed);
  }

  private ApiRequest captureRequest(
      CaptureOptions options,
      ResponseType responseType,
      boolean async,
      RequestOptions requestOptions) {
    Objects.requireNonNull(options, "options");
    String key = requireKey();
    ObjectNode body = (ObjectNode) Json.toTree(options);
    body.put("response_type", responseType.value());
    if (async) {
      body.put("async", true);
    }
    String idempotencyKey =
        requestOptions.idempotencyKey() != null
            ? requestOptions.idempotencyKey()
            : UUID.randomUUID().toString();
    // Nothing runs while an async capture's caller waits, so it uses the ordinary timeout.
    Duration requestTimeout = timeout(requestOptions, async ? timeout : captureTimeout);
    return ApiRequest.post(uri(CAPTURES), Json.write(body), requestTimeout)
        .header("X-Api-Key", key)
        .header("Idempotency-Key", idempotencyKey);
  }

  private InputStream openDownload(Capture capture, RequestOptions requestOptions) {
    Objects.requireNonNull(capture, "capture");
    if (capture.downloadUrl() == null) {
      throw new IllegalArgumentException(
          "The capture has no download_url; by_format and cloud_storage captures have none");
    }
    // The link is signed and points at the media host, so the API key is not sent with it.
    ApiRequest request =
        ApiRequest.get(URI.create(capture.downloadUrl()), timeout(requestOptions, timeout))
            .header("Accept", "*/*");
    return transport.send(request).body();
  }

  private String requireKey() {
    if (apiKey == null) {
      throw new IllegalStateException(
          "No Instant Snap API key: set WSW_INSTANT_SNAP_API_KEY or call "
              + "WebsiteScreenshotWorld.builder().instantSnapApiKey(...)");
    }
    return apiKey;
  }

  private URI uri(String path) {
    return URI.create(baseUrl + path);
  }

  private static Duration timeout(RequestOptions requestOptions, Duration fallback) {
    Objects.requireNonNull(requestOptions, "requestOptions");
    return requestOptions.timeout() != null ? requestOptions.timeout() : fallback;
  }
}
