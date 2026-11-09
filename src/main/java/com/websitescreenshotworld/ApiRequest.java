package com.websitescreenshotworld;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/** One call to make, possibly more than once. */
final class ApiRequest {
  private final String method;
  private final URI uri;
  private final byte[] body;
  private final Duration timeout;
  private final Map<String, String> headers = new LinkedHashMap<>();

  private ApiRequest(String method, URI uri, byte[] body, Duration timeout) {
    this.method = method;
    this.uri = uri;
    this.body = body;
    this.timeout = timeout;
    if (body != null) {
      headers.put("Content-Type", "application/json");
    }
  }

  static ApiRequest get(URI uri, Duration timeout) {
    return new ApiRequest("GET", uri, null, timeout);
  }

  static ApiRequest post(URI uri, byte[] body, Duration timeout) {
    return new ApiRequest("POST", uri, body, timeout);
  }

  ApiRequest header(String name, String value) {
    headers.put(name, value);
    return this;
  }

  URI uri() {
    return uri;
  }

  Duration timeout() {
    return timeout;
  }

  HttpRequest toHttpRequest(Duration attemptTimeout, String userAgent) {
    HttpRequest.Builder builder =
        HttpRequest.newBuilder(uri)
            .timeout(attemptTimeout)
            .header("User-Agent", userAgent)
            .method(
                method, body == null ? BodyPublishers.noBody() : BodyPublishers.ofByteArray(body));
    headers.forEach(builder::header);
    return builder.build();
  }
}
