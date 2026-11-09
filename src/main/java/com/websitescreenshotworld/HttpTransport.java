package com.websitescreenshotworld;

import com.websitescreenshotworld.errors.ConnectionException;
import com.websitescreenshotworld.errors.WebsiteScreenshotWorldException;
import com.websitescreenshotworld.internal.Json;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/** Sends requests, retrying the failures the API marks as temporary. */
final class HttpTransport {
  private static final Duration FIRST_BACKOFF = Duration.ofMillis(500);
  private static final Duration LONGEST_WAIT = Duration.ofSeconds(60);

  private final HttpClient client;
  private final int maxRetries;
  private final String userAgent = Version.userAgent();

  HttpTransport(HttpClient client, int maxRetries) {
    this.client = client;
    this.maxRetries = maxRetries;
  }

  /**
   * Sends the request until it succeeds, fails for good, runs out of retries or runs out of time.
   * The caller owns the returned body stream.
   */
  HttpResponse<InputStream> send(ApiRequest request) {
    long deadline = System.nanoTime() + request.timeout().toNanos();
    WebsiteScreenshotWorldException failure = null;
    for (int attempt = 0; ; attempt++) {
      long remaining = deadline - System.nanoTime();
      if (remaining <= 0 && failure != null) {
        // The wait before this retry overran the deadline.
        throw failure;
      }
      Duration attemptTimeout = Duration.ofNanos(Math.max(1, remaining));
      Duration wait;
      try {
        HttpResponse<InputStream> response =
            client.send(
                request.toHttpRequest(attemptTimeout, userAgent), BodyHandlers.ofInputStream());
        refuseRedirected(request, response);
        int status = response.statusCode();
        if (status >= 200 && status < 300) {
          return response;
        }
        byte[] body;
        try (InputStream in = response.body()) {
          body = in.readAllBytes();
        }
        failure = ApiErrors.fromResponse(status, response.headers(), body);
        if (!ApiErrors.isRetryable(failure.errorCode())) {
          throw failure;
        }
        wait =
            failure.retryAfterSeconds() != null
                ? min(Duration.ofSeconds(Math.max(0, failure.retryAfterSeconds())), LONGEST_WAIT)
                : backoff(attempt);
      } catch (HttpTimeoutException e) {
        failure =
            new ConnectionException(
                "No answer from " + request.uri().getHost() + " in time: " + e.getMessage(), e);
        wait = backoff(attempt);
      } catch (IOException e) {
        failure =
            new ConnectionException(
                "Could not reach " + request.uri().getHost() + ": " + e.getMessage(), e);
        wait = backoff(attempt);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new WebsiteScreenshotWorldException("The request was interrupted", e);
      }

      if (attempt >= maxRetries || System.nanoTime() + wait.toNanos() >= deadline) {
        throw failure;
      }
      sleep(wait);
    }
  }

  /**
   * Refuses an answer that came from somewhere else. This library's own {@link HttpClient} never
   * follows redirects; one passed in may, and would already have sent the API key along with it.
   */
  private static void refuseRedirected(ApiRequest request, HttpResponse<InputStream> response) {
    if (response.uri().equals(request.uri())) {
      return;
    }
    try {
      response.body().close();
    } catch (IOException ignored) {
      // The answer is refused either way.
    }
    throw new WebsiteScreenshotWorldException(
        "The answer came from "
            + response.uri().getHost()
            + " after a redirect. The HttpClient given to WebsiteScreenshotWorld must not follow"
            + " redirects: build it with followRedirects(HttpClient.Redirect.NEVER).",
        null);
  }

  /** Sends the request and reads a JSON answer into {@code type}. */
  <T> T sendForJson(ApiRequest request, Class<T> type) {
    return readJson(send(request), type);
  }

  static <T> T readJson(HttpResponse<InputStream> response, Class<T> type) {
    byte[] body;
    try (InputStream in = response.body()) {
      body = in.readAllBytes();
    } catch (IOException e) {
      throw new ConnectionException("The answer was cut off: " + e.getMessage(), e);
    }
    T value;
    try {
      value = Json.read(body, type);
    } catch (IOException e) {
      throw unreadable(response.statusCode(), body, e);
    }
    if (value == null) {
      throw unreadable(response.statusCode(), body, null);
    }
    return value;
  }

  private static WebsiteScreenshotWorldException unreadable(
      int status, byte[] body, Throwable cause) {
    WebsiteScreenshotWorldException error =
        new WebsiteScreenshotWorldException(
            "The API answered with a body this library could not read",
            null,
            status,
            null,
            new String(body, StandardCharsets.UTF_8));
    if (cause != null) {
      error.initCause(cause);
    }
    return error;
  }

  private static Duration backoff(int attempt) {
    Duration base = min(FIRST_BACKOFF.multipliedBy(1L << Math.min(attempt, 7)), LONGEST_WAIT);
    long jitter = (long) (base.toNanos() * 0.25 * ThreadLocalRandom.current().nextDouble());
    return base.plusNanos(jitter);
  }

  private static Duration min(Duration a, Duration b) {
    return a.compareTo(b) <= 0 ? a : b;
  }

  private static void sleep(Duration wait) {
    try {
      Thread.sleep(wait.toMillis());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new WebsiteScreenshotWorldException("Interrupted while waiting to retry", e);
    }
  }
}
