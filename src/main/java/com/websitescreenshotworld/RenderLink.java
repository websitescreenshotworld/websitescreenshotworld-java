package com.websitescreenshotworld;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/** Render links: signed URLs of an Instant Snap key that are themselves a screenshot. */
public final class RenderLink {
  private static final String PATH_PREFIX = "/instant-snap/v1/render/";
  private static final String DEFAULT_BASE_URL = "https://api.websitescreenshotworld.com";
  private static final Pattern KEY_PREFIX = Pattern.compile("[0-9a-f]{12}");
  private static final char[] HEX = "0123456789ABCDEF".toCharArray();

  private RenderLink() {}

  /**
   * Builds a render link: a signed URL that is itself a screenshot, for an {@code <img>} tag, an
   * email or a social preview. It is made here, locally - nothing is sent, and no client or API key
   * is needed. Every view of the link that is not already cached makes one Instant Snap capture.
   *
   * @param options what the link captures
   * @return the link
   * @throws IllegalArgumentException when the render secret or URL is empty, the key prefix is not
   *     12 lower-case hex characters, or the format is not png, jpeg or webp
   */
  public static String build(RenderLinkOptions options) {
    Objects.requireNonNull(options, "options");
    if (options.renderSecret().isEmpty()) {
      throw new IllegalArgumentException("The render secret is required.");
    }
    if (!KEY_PREFIX.matcher(options.keyPrefix()).matches()) {
      throw new IllegalArgumentException(
          "The key prefix must be the key's 12-character prefix (lower-case hex).");
    }
    if (options.url().isEmpty()) {
      throw new IllegalArgumentException("The URL is required.");
    }
    String format = options.format() == null ? "png" : options.format().value();
    if (!format.equals("png") && !format.equals("jpeg") && !format.equals("webp")) {
      throw new IllegalArgumentException("The format must be png, jpeg or webp.");
    }

    List<String[]> pairs = new ArrayList<>();
    add(pairs, "url", options.url());
    add(pairs, "ttl", text(options.ttlSeconds()));
    add(pairs, "width", text(options.width()));
    add(pairs, "height", text(options.height()));
    add(
        pairs,
        "device_scale_factor",
        options.deviceScaleFactor() == null
            ? null
            : BigDecimal.valueOf(options.deviceScaleFactor()).stripTrailingZeros().toPlainString());
    add(pairs, "device_category", token(options.deviceCategory()));
    add(pairs, "emulated_device", options.emulatedDevice());
    add(pairs, "scroll_mode", token(options.scrollMode()));
    add(pairs, "auto_height", text(options.autoHeight()));
    add(pairs, "max_height", text(options.maxHeight()));
    add(pairs, "zoom", text(options.zoom()));
    add(pairs, "block_ads", text(options.blockAds()));
    add(pairs, "block_pop_ups", text(options.blockPopUps()));
    add(pairs, "press_escape", text(options.pressEscape()));
    add(pairs, "page_timeout_ms", text(options.pageTimeoutMs()));
    add(pairs, "scrolling_delay_ms", text(options.scrollingDelayMs()));
    add(pairs, "locale", options.locale());
    add(pairs, "time_zone", options.timeZone());
    add(pairs, "hide", options.hide());
    add(pairs, "xpath", options.xpath());
    add(pairs, "click", options.click());
    add(pairs, "hover", options.hover());
    add(pairs, "country", options.country());
    add(pairs, "thumb_width", text(options.thumbWidth()));
    pairs.sort((left, right) -> left[0].compareTo(right[0]));

    StringBuilder canonical =
        new StringBuilder(PATH_PREFIX).append(options.keyPrefix()).append('/').append(format);
    for (int index = 0; index < pairs.size(); index++) {
      canonical.append(index == 0 ? '?' : '&');
      canonical.append(pairs.get(index)[0]).append('=').append(pairs.get(index)[1]);
    }

    String baseUrl = options.baseUrl() == null ? DEFAULT_BASE_URL : options.baseUrl();
    while (baseUrl.endsWith("/")) {
      baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
    }
    return baseUrl + canonical + "&sig=" + sign(options.renderSecret(), canonical.toString());
  }

  private static String text(Object value) {
    return value == null ? null : String.valueOf(value);
  }

  private static String token(Token value) {
    return value == null ? null : value.value();
  }

  private static void add(List<String[]> pairs, String name, String value) {
    if (value != null) {
      pairs.add(new String[] {encode(name), encode(value)});
    }
  }

  /** RFC 3986: every UTF-8 byte but {@code A-Z a-z 0-9 - . _ ~}, upper-case hex. */
  private static String encode(String value) {
    StringBuilder builder = new StringBuilder(value.length());
    for (byte b : value.getBytes(StandardCharsets.UTF_8)) {
      char c = (char) (b & 0xff);
      if ((c >= 'A' && c <= 'Z')
          || (c >= 'a' && c <= 'z')
          || (c >= '0' && c <= '9')
          || c == '-'
          || c == '.'
          || c == '_'
          || c == '~') {
        builder.append(c);
      } else {
        builder.append('%').append(HEX[(b >> 4) & 0xf]).append(HEX[b & 0xf]);
      }
    }
    return builder.toString();
  }

  private static String sign(String secret, String canonical) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] digest = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
      StringBuilder hex = new StringBuilder(digest.length * 2);
      for (byte b : digest) {
        hex.append(Character.forDigit((b >> 4) & 0xf, 16)).append(Character.forDigit(b & 0xf, 16));
      }
      return hex.toString();
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("HmacSHA256 is not available.", e);
    }
  }
}
