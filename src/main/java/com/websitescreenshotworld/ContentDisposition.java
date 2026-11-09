package com.websitescreenshotworld;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Reads the file name from a {@code Content-Disposition} header (RFC 6266). */
final class ContentDisposition {
  private ContentDisposition() {}

  /**
   * Returns the file name, preferring {@code filename*} over {@code filename}, with any directory
   * part removed so it is safe to use as a path.
   */
  static String filename(String header) {
    String plain = null;
    String extended = null;
    for (String part : splitParameters(header)) {
      int eq = part.indexOf('=');
      if (eq < 0) {
        continue;
      }
      String name = part.substring(0, eq).trim().toLowerCase(Locale.ROOT);
      String value = part.substring(eq + 1).trim();
      if (name.equals("filename*")) {
        extended = decodeExtended(value);
      } else if (name.equals("filename")) {
        plain = unquote(value);
      }
    }
    String filename = extended != null ? extended : plain;
    if (filename == null) {
      return null;
    }
    String base =
        filename.substring(Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\')) + 1);
    return base.isEmpty() || base.equals(".") || base.equals("..") ? null : base;
  }

  private static List<String> splitParameters(String header) {
    List<String> parts = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean quoted = false;
    for (int i = 0; i < header.length(); i++) {
      char c = header.charAt(i);
      if (c == '"') {
        quoted = !quoted;
      } else if (c == '\\' && quoted && i + 1 < header.length()) {
        current.append(c);
        c = header.charAt(++i);
      } else if (c == ';' && !quoted) {
        parts.add(current.toString());
        current.setLength(0);
        continue;
      }
      current.append(c);
    }
    parts.add(current.toString());
    return parts;
  }

  private static String unquote(String value) {
    if (value.length() < 2 || value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') {
      return value;
    }
    StringBuilder out = new StringBuilder();
    for (int i = 1; i < value.length() - 1; i++) {
      char c = value.charAt(i);
      if (c == '\\' && i + 1 < value.length() - 1) {
        c = value.charAt(++i);
      }
      out.append(c);
    }
    return out.toString();
  }

  /** Decodes an RFC 8187 value such as {@code UTF-8''na%C3%AFve.png}. */
  private static String decodeExtended(String value) {
    int first = value.indexOf('\'');
    int second = first < 0 ? -1 : value.indexOf('\'', first + 1);
    if (second < 0) {
      return null;
    }
    Charset charset;
    try {
      charset = Charset.forName(value.substring(0, first));
    } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
      return null;
    }
    String encoded = value.substring(second + 1);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    for (int i = 0; i < encoded.length(); i++) {
      char c = encoded.charAt(i);
      if (c == '%' && isHex(encoded, i + 1)) {
        bytes.write(Integer.parseInt(encoded.substring(i + 1, i + 3), 16));
        i += 2;
      } else if (c < 0x80) {
        bytes.write(c);
      } else {
        return null;
      }
    }
    return new String(bytes.toByteArray(), charset);
  }

  private static boolean isHex(String s, int index) {
    return index + 1 < s.length()
        && Character.digit(s.charAt(index), 16) >= 0
        && Character.digit(s.charAt(index + 1), 16) >= 0;
  }
}
