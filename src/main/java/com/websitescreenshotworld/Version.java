package com.websitescreenshotworld;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/** The library's version, filled in from the build. */
final class Version {
  static final String NUMBER = load();

  private Version() {}

  static String userAgent() {
    return "websitescreenshotworld-java/"
        + NUMBER
        + " (Java "
        + System.getProperty("java.version")
        + ")";
  }

  private static String load() {
    try (InputStream in = Version.class.getResourceAsStream("version.properties")) {
      if (in == null) {
        throw new IllegalStateException("version.properties is missing from the library");
      }
      Properties properties = new Properties();
      properties.load(in);
      return properties.getProperty("version");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
