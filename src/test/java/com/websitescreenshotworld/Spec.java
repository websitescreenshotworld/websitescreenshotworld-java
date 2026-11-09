package com.websitescreenshotworld;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.internal.Json;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Reads the shared files in the repository's spec folder. */
final class Spec {
  static final Path ROOT = Path.of("").toAbsolutePath().normalize();

  private Spec() {}

  static Path path(String first, String... more) {
    return ROOT.resolve(Path.of("spec", first)).resolve(Path.of("", more));
  }

  static JsonNode read(Path file) {
    try {
      return Json.readTree(Files.readAllBytes(file));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static List<Path> files(String folder) {
    try (Stream<Path> files = Files.list(path(folder))) {
      return files
          .filter(file -> file.toString().endsWith(".json"))
          .sorted()
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Compares JSON values, treating numbers as equal when their values are, so 2 equals 2.0. */
  static boolean sameJson(JsonNode expected, JsonNode actual) {
    return expected.equals(
        (a, b) -> {
          if (a.isNumber() && b.isNumber()) {
            return new BigDecimal(a.asText()).compareTo(new BigDecimal(b.asText()));
          }
          return a.equals(b) ? 0 : 1;
        },
        actual);
  }
}
