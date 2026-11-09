package com.websitescreenshotworld;

import com.fasterxml.jackson.databind.JsonNode;
import com.websitescreenshotworld.internal.Json;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/** Runs conformance/server.js, which plays back a fixture's responses and records requests. */
final class ConformanceServer implements AutoCloseable {
  private static final String READY = "listening on ";

  private final Process process;
  private final String baseUrl;
  private final HttpClient http = HttpClient.newHttpClient();

  private ConformanceServer(Process process, String baseUrl) {
    this.process = process;
    this.baseUrl = baseUrl;
  }

  static ConformanceServer start() throws IOException {
    Process process =
        new ProcessBuilder("node", Spec.ROOT.resolve("spec/conformance/server.js").toString())
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start();
    BufferedReader stdout =
        new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
    String line = stdout.readLine();
    if (line == null || !line.startsWith(READY)) {
      process.destroyForcibly();
      throw new IOException("The conformance server did not start: " + line);
    }
    return new ConformanceServer(process, line.substring(READY.length()).trim());
  }

  String baseUrl() {
    return baseUrl;
  }

  /** Queues the fixture's responses and forgets earlier requests. */
  void load(JsonNode fixture) throws IOException, InterruptedException {
    http.send(
        HttpRequest.newBuilder(URI.create(baseUrl + "/__load"))
            .POST(BodyPublishers.ofByteArray(Json.write(fixture)))
            .build(),
        BodyHandlers.discarding());
  }

  /** Returns what the client sent since the last load, in order. */
  JsonNode requests() throws IOException, InterruptedException {
    byte[] body =
        http.send(
                HttpRequest.newBuilder(URI.create(baseUrl + "/__requests")).build(),
                BodyHandlers.ofByteArray())
            .body();
    return Json.readTree(body);
  }

  @Override
  public void close() {
    process.destroy();
    try {
      if (!process.waitFor(5, TimeUnit.SECONDS)) {
        process.destroyForcibly();
      }
    } catch (InterruptedException e) {
      process.destroyForcibly();
      Thread.currentThread().interrupt();
    }
  }
}
