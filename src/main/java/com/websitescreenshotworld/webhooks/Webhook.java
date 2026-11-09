package com.websitescreenshotworld.webhooks;

import com.websitescreenshotworld.errors.SignatureVerificationException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies webhook deliveries and reads them into typed events.
 *
 * <pre>{@code
 * WebhookEvent event = Webhook.verify(rawBody, request.getHeader("X-WSW-Signature"), secret);
 * if (event instanceof InstantSnapCaptureSucceededEvent) {
 *   Capture capture = ((InstantSnapCaptureSucceededEvent) event).data();
 * }
 * }</pre>
 *
 * <p>Pass the body exactly as it arrived. A framework that parses the JSON and writes it out again
 * changes the bytes, and the signature no longer matches.
 */
public final class Webhook {
  /** The header that carries the signature. */
  public static final String SIGNATURE_HEADER = "X-WSW-Signature";

  /** How far the signature's timestamp may be from now unless a call says otherwise. */
  public static final Duration DEFAULT_TOLERANCE = Duration.ofMinutes(5);

  private Webhook() {}

  /**
   * Checks a delivery's signature with the default tolerance and returns its event.
   *
   * @param body the request body exactly as received, decoded as UTF-8
   * @param signatureHeader the {@code X-WSW-Signature} header, or {@code null} if absent
   * @param secret the endpoint's signing secret
   * @return the event
   * @throws SignatureVerificationException if the signature is missing, malformed, too old or does
   *     not match
   * @throws com.websitescreenshotworld.errors.WebsiteScreenshotWorldException if a signed body is
   *     not a valid event
   */
  public static WebhookEvent verify(String body, String signatureHeader, String secret) {
    return verify(body, signatureHeader, secret, DEFAULT_TOLERANCE, Clock.systemUTC());
  }

  /**
   * Checks a delivery's signature and returns its event.
   *
   * @param body the request body exactly as received, decoded as UTF-8
   * @param signatureHeader the {@code X-WSW-Signature} header, or {@code null} if absent
   * @param secret the endpoint's signing secret
   * @param tolerance how far the signature's timestamp may be from now, either way
   * @param clock the source of now
   * @return the event
   * @throws SignatureVerificationException if the signature is missing, malformed, too old or does
   *     not match
   * @throws com.websitescreenshotworld.errors.WebsiteScreenshotWorldException if a signed body is
   *     not a valid event
   */
  public static WebhookEvent verify(
      String body, String signatureHeader, String secret, Duration tolerance, Clock clock) {
    Objects.requireNonNull(body, "body");
    return verify(body.getBytes(StandardCharsets.UTF_8), signatureHeader, secret, tolerance, clock);
  }

  /**
   * Checks a delivery's signature with the default tolerance and returns its event.
   *
   * @param body the request body exactly as received
   * @param signatureHeader the {@code X-WSW-Signature} header, or {@code null} if absent
   * @param secret the endpoint's signing secret
   * @return the event
   * @throws SignatureVerificationException if the signature is missing, malformed, too old or does
   *     not match
   * @throws com.websitescreenshotworld.errors.WebsiteScreenshotWorldException if a signed body is
   *     not a valid event
   */
  public static WebhookEvent verify(byte[] body, String signatureHeader, String secret) {
    return verify(body, signatureHeader, secret, DEFAULT_TOLERANCE, Clock.systemUTC());
  }

  /**
   * Checks a delivery's signature and returns its event.
   *
   * @param body the request body exactly as received
   * @param signatureHeader the {@code X-WSW-Signature} header, or {@code null} if absent
   * @param secret the endpoint's signing secret
   * @param tolerance how far the signature's timestamp may be from now, either way
   * @param clock the source of now
   * @return the event
   * @throws SignatureVerificationException if the signature is missing, malformed, too old or does
   *     not match
   * @throws com.websitescreenshotworld.errors.WebsiteScreenshotWorldException if a signed body is
   *     not a valid event
   */
  public static WebhookEvent verify(
      byte[] body, String signatureHeader, String secret, Duration tolerance, Clock clock) {
    Objects.requireNonNull(body, "body");
    Objects.requireNonNull(tolerance, "tolerance");
    Objects.requireNonNull(clock, "clock");
    if (secret == null || secret.isEmpty()) {
      throw new IllegalArgumentException("The webhook signing secret is empty");
    }
    if (signatureHeader == null || signatureHeader.isBlank()) {
      throw new SignatureVerificationException("The " + SIGNATURE_HEADER + " header is missing");
    }

    String timestamp = null;
    List<String> signatures = new ArrayList<>();
    for (String part : signatureHeader.split(",")) {
      int eq = part.indexOf('=');
      if (eq < 0) {
        continue;
      }
      String name = part.substring(0, eq).trim();
      String value = part.substring(eq + 1).trim();
      if (name.equals("t") && timestamp == null) {
        timestamp = value;
      } else if (name.equals("v1")) {
        signatures.add(value.toLowerCase(Locale.ROOT));
      }
    }
    if (timestamp == null) {
      throw new SignatureVerificationException("The signature has no timestamp");
    }
    long signedAt;
    try {
      signedAt = Long.parseLong(timestamp);
    } catch (NumberFormatException e) {
      throw new SignatureVerificationException("The signature timestamp is not an integer");
    }
    if (signatures.isEmpty()) {
      throw new SignatureVerificationException("The signature has no v1 value");
    }

    long now = clock.instant().getEpochSecond();
    if (!withinTolerance(now, signedAt, tolerance)) {
      throw new SignatureVerificationException(
          "The signature timestamp is more than " + tolerance.getSeconds() + " seconds from now");
    }

    byte[] expected = sign(secret, timestamp, body).getBytes(StandardCharsets.US_ASCII);
    for (String signature : signatures) {
      if (MessageDigest.isEqual(expected, signature.getBytes(StandardCharsets.US_ASCII))) {
        return WebhookEvents.parse(body);
      }
    }
    throw new SignatureVerificationException("No signature matches the body");
  }

  private static boolean withinTolerance(long now, long signedAt, Duration tolerance) {
    try {
      return Math.abs(Math.subtractExact(now, signedAt)) <= tolerance.getSeconds();
    } catch (ArithmeticException e) {
      return false;
    }
  }

  private static String sign(String secret, String timestamp, byte[] body) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      mac.update((timestamp + ".").getBytes(StandardCharsets.UTF_8));
      byte[] digest = mac.doFinal(body);
      StringBuilder hex = new StringBuilder(digest.length * 2);
      for (byte b : digest) {
        hex.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("HMAC-SHA256 is not available", e);
    }
  }
}
