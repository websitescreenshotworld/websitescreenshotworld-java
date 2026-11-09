# Website Screenshot World for Java

The Java client for [Website Screenshot World](https://websitescreenshotworld.com): capture web pages on
demand with **Instant Snap**, start scheduled captures with **Auto Schedule**, and verify the webhooks both
send.

## Install

Maven:

```xml
<dependency>
  <groupId>com.websitescreenshotworld</groupId>
  <artifactId>websitescreenshotworld-java</artifactId>
  <version>0.1.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("com.websitescreenshotworld:websitescreenshotworld-java:0.1.0")
```

The library needs Java 11 or later and depends only on `jackson-databind`.

## Quick start

Create one client and reuse it. API keys are read from `WSW_INSTANT_SNAP_API_KEY` and
`WSW_AUTO_SCHEDULE_API_KEY` unless you pass them; you only need the key of the product you call.

```java
import com.websitescreenshotworld.*;

WebsiteScreenshotWorld client = WebsiteScreenshotWorld.builder().build();

Capture capture = client.instantSnap().capture(
    CaptureOptions.builder()
        .url("https://example.com")
        .format(CaptureFormat.WEBP)
        .viewport(Viewport.builder().width(1440).height(900).build())
        .build());

System.out.println(capture.status());      // succeeded
System.out.println(capture.downloadUrl()); // valid for 60 minutes
```

Every option the API accepts has a builder method. An option you do not set is not sent, so the API applies
its default; `client.instantSnap().defaults()` returns the current default set and limits.

### Auto Schedule

A web page created in the dashboard can be captured now. The result arrives by webhook.

```java
import java.util.UUID;

TriggerResult result = client.autoSchedule()
    .triggerNow(UUID.fromString("7d4e2b1a-6c5f-4a3e-9d8c-2b1a0f9e8d7c"));

if (result.status() == TriggerStatus.ALREADY_PENDING) {
  System.out.println("A capture was already queued at " + result.triggeredAt());
}
```

`triggerNow` is safe to repeat: while a capture is pending, another call starts nothing new.

## Saving a file

`captureFile` answers with the file itself (up to 64 MB). The body is streamed from the connection rather
than held in memory, so close the result, ideally with try-with-resources.

```java
import java.nio.file.Path;

try (CaptureFile file = client.instantSnap().captureFile(
    CaptureOptions.builder().url("https://example.com").format(CaptureFormat.PNG).build())) {
  file.saveTo(Path.of("example.png"));
  System.out.println(file.width() + "x" + file.height() + ", sha256 " + file.sha256());
}
```

Read `file.body()` instead to pass the stream on, for example to an upload. `file.filename()` is the name the
API suggests.

A capture made with `capture` keeps its file for 24 hours. Fetch it with `download`, to a path or a stream:

```java
client.instantSnap().download(capture, Path.of("example.webp"));
```

## Uploading to your bucket

`captureToBucket` uploads the file to Amazon S3 or any S3-compatible store. The secret access key is sent
to the API and never returned.

```java
Capture capture = client.instantSnap().captureToBucket(
    CaptureOptions.builder()
        .url("https://example.com")
        .s3Delivery(S3Delivery.builder()
            .accessKeyId(System.getenv("AWS_ACCESS_KEY_ID"))
            .secretAccessKey(System.getenv("AWS_SECRET_ACCESS_KEY"))
            .bucket("my-captures")
            .region("eu-west-2")
            .keyPrefix("homepage/")
            .build())
        .build());

System.out.println(capture.s3Delivery().status()); // in_progress
```

## Async captures

`submit` queues a capture and answers at once with it, `queued`, instead of holding the connection while
the browser works. `waitFor` then reads it every 3 seconds (for at most 360 seconds by default) until it is
no longer `queued`, `running` or `pending`:

```java
import java.time.Duration;

InstantSnap instantSnap = client.instantSnap();

Capture queued = instantSnap.submit(
    CaptureOptions.builder()
        .url("https://example.com")
        .timeoutSeconds(240) // browser time, 30-300 s; the server uses 120 when unset
        .build());

Capture capture = instantSnap.waitFor(queued.id());
if (capture.status() == CaptureStatus.SUCCEEDED) {
  instantSnap.download(capture, Path.of("example.png"));
} else if (capture.status() == CaptureStatus.QUEUED || capture.status() == CaptureStatus.RUNNING) {
  // Time ran out first: waitFor returns the capture as last read rather than throwing.
  capture = instantSnap.waitFor(queued.id(), Duration.ofMinutes(10), Duration.ofSeconds(5));
}
```

`submitToBucket` does the same for an upload to your bucket:

```java
Capture queued = instantSnap.submitToBucket(
    CaptureOptions.builder()
        .url("https://example.com")
        .s3Delivery(S3Delivery.builder()
            .accessKeyId(System.getenv("AWS_ACCESS_KEY_ID"))
            .secretAccessKey(System.getenv("AWS_SECRET_ACCESS_KEY"))
            .bucket("my-captures")
            .region("eu-west-2")
            .build())
        .build());
```

- `timeoutSeconds` (sent as `timeout`) is the browser time the capture may use, 30 to 300 seconds, and only
  `submit` and `submitToBucket` accept it; the server refuses it on the other capture methods. It is not
  the time the client waits for an answer, which `RequestOptions.timeout` sets. A `submit` call itself
  uses the ordinary 30-second timeout, since nothing runs while it waits.
- A capture still queued 15 minutes after it was accepted ends `failed` with error code `capture_expired`,
  and is not charged.
- When your organization already has as many async captures waiting or running as it may, `submit` throws
  `RateLimitException` with error code `queue_full` and `retryAfterSeconds()`. The client does not retry it.
- `waitFor` throws whatever `get` throws, and stops at once when the calling thread is interrupted. Instead
  of polling, you can also receive the result by webhook (`callbackUrl`).

## Reading a capture back

`get` returns a capture with a fresh download link, and shows how an upload is going:

```java
Capture latest = client.instantSnap().get(capture.id());
if (latest.s3Delivery().status() == S3DeliveryStatus.COMPLETED) {
  System.out.println("Uploaded as " + latest.s3Delivery().key());
}
```

## Errors and retries

Every error is an unchecked exception extending `WebsiteScreenshotWorldException`, which carries
`errorCode()`, `getMessage()`, `httpStatus()`, `retryAfterSeconds()` and `rawBody()`. Switch on the error
code, never on the status or the message.

| Exception | Error codes |
|---|---|
| `AuthenticationException` | `api_key_invalid` |
| `InvalidRequestException` | `validation_failed`, `s3_destination_invalid`, `web_page_id_invalid`, `idempotency_key_reused`, `sitemap_schedule_not_triggerable` |
| `PlanLimitException` | `quota_exhausted`, `feature_not_available`, `no_trigger_allowance`, `forbidden` |
| `NotFoundException` | `capture_not_found`, `web_page_not_found`, `not_found` |
| `RateLimitException` | `rate_limited`, `queue_full` |
| `CaptureException` | `capture_failed`, `capture_timeout`, `payload_too_large`, `capture_expired` |
| `ServiceException` | `capture_busy`, `service_unavailable`, `internal_error`, `request_cancelled`, `idempotency_key_in_use` |
| `ConnectionException` | none: the request could not be sent, or no answer came back |
| `WebsiteScreenshotWorldException` | a code this version does not know yet |

```java
import com.websitescreenshotworld.errors.*;

try {
  client.instantSnap().capture(CaptureOptions.builder().url("https://example.com").build());
} catch (CaptureException e) {
  System.out.println("The website could not be captured: " + e.getMessage());
} catch (PlanLimitException e) {
  System.out.println("Plan limit reached: " + e.errorCode());
} catch (WebsiteScreenshotWorldException e) {
  System.out.println(e.errorCode() + " (HTTP " + e.httpStatus() + "): " + e.getMessage());
}
```

The client retries `rate_limited`, `capture_busy`, `service_unavailable`, `idempotency_key_in_use` and
connection failures up to twice. It waits as long as the API asks (at most 60 seconds), otherwise 0.5 s and
then 1 s with a little jitter. Nothing else is retried: a `capture_failed`, `capture_timeout` or
`queue_full` fails at once. Retries stop when the call's timeout runs out, and when the calling thread is interrupted.

## Idempotency keys

Every capture sends an `Idempotency-Key`, so a retry never captures twice. By default each call uses a new
random key. Pass your own to make a repeated call, such as a job that runs again after a crash, return the
first capture instead of taking (and paying for) a new one:

```java
Capture capture = client.instantSnap().capture(
    CaptureOptions.builder().url("https://example.com").build(),
    RequestOptions.builder().idempotencyKey("order-1234-hero").build());

System.out.println(capture.idempotentReplayed()); // true when the first capture came back
```

A key is kept for 24 hours. Reusing it with different options raises `InvalidRequestException`
(`idempotency_key_reused`).

## Timeouts

A timeout covers the whole call, retries included. Captures default to 180 seconds, because the API may
spend up to 120 seconds on a capture; everything else defaults to 30 seconds. Change them for the client or
for one call:

```java
import java.time.Duration;

WebsiteScreenshotWorld client = WebsiteScreenshotWorld.builder()
    .captureTimeout(Duration.ofMinutes(4))
    .timeout(Duration.ofSeconds(10))
    .build();

client.instantSnap().capture(
    CaptureOptions.builder().url("https://example.com").build(),
    RequestOptions.builder().timeout(Duration.ofMinutes(5)).build());
```

A call that runs out of time throws `ConnectionException`.

## Webhooks

Each delivery is signed in the `X-WSW-Signature` header. `Webhook.verify` checks the signature and the
timestamp (within 5 minutes by default) and returns a typed event:

```java
import com.websitescreenshotworld.webhooks.*;

WebhookEvent event = Webhook.verify(body, signatureHeader, System.getenv("WSW_WEBHOOK_SECRET"));

if (event instanceof InstantSnapCaptureSucceededEvent) {
  Capture capture = ((InstantSnapCaptureSucceededEvent) event).data();
} else if (event instanceof AutoScheduleCaptureSucceededEvent) {
  ScheduledCapture capture = ((AutoScheduleCaptureSucceededEvent) event).data();
} else if (event instanceof CaptureChangedEvent) {
  ChangedCapture change = ((CaptureChangedEvent) event).data();
} else if (event instanceof UnknownWebhookEvent) {
  // A type added after this version; event.type() and data() are still there.
}
```

The events are `InstantSnapCaptureSucceededEvent`, `InstantSnapCaptureFailedEvent`,
`AutoScheduleCaptureSucceededEvent`, `AutoScheduleCaptureFailedEvent`, `CaptureChangedEvent`,
`DeliveryCompletedEvent`, `SchedulePausedEvent`, `SitemapRunCompletedEvent`, `PingEvent` and
`UnknownWebhookEvent`. A failed check throws `SignatureVerificationException`: answer 400 and ignore the
delivery. `event.id()` stays the same when a delivery is retried, so you can use it to skip duplicates.

**Pass the body exactly as it arrived.** Parsing the JSON and writing it out again changes the bytes, and the
signature stops matching. Read the raw bytes in your framework:

Spring Boot:

```java
@PostMapping("/webhooks/wsw")
public ResponseEntity<Void> receive(
    @RequestBody byte[] body,
    @RequestHeader(name = Webhook.SIGNATURE_HEADER, required = false) String signature) {
  try {
    WebhookEvent event = Webhook.verify(body, signature, secret);
    // handle the event
    return ResponseEntity.noContent().build();
  } catch (SignatureVerificationException e) {
    return ResponseEntity.badRequest().build();
  }
}
```

Jakarta REST (Quarkus, Jersey, RESTEasy):

```java
@POST
@Path("/webhooks/wsw")
@Consumes(MediaType.APPLICATION_JSON)
public Response receive(byte[] body, @HeaderParam(Webhook.SIGNATURE_HEADER) String signature) {
  try {
    WebhookEvent event = Webhook.verify(body, signature, secret);
    // handle the event
    return Response.noContent().build();
  } catch (SignatureVerificationException e) {
    return Response.status(Response.Status.BAD_REQUEST).build();
  }
}
```

Jakarta Servlet:

```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
  byte[] body = request.getInputStream().readAllBytes();
  try {
    WebhookEvent event = Webhook.verify(body, request.getHeader(Webhook.SIGNATURE_HEADER), secret);
    // handle the event
    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
  } catch (SignatureVerificationException e) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}
```

To accept a larger clock difference, or to test with a fixed time, pass a tolerance and a `Clock`:
`Webhook.verify(body, signature, secret, Duration.ofMinutes(10), Clock.systemUTC())`.

## Configuration

| Builder method | Default |
|---|---|
| `instantSnapApiKey` | `WSW_INSTANT_SNAP_API_KEY` |
| `autoScheduleApiKey` | `WSW_AUTO_SCHEDULE_API_KEY` |
| `instantSnapBaseUrl` | `https://api.websitescreenshotworld.com` |
| `autoScheduleBaseUrl` | `https://autoapi.websitescreenshotworld.com` |
| `captureTimeout` | 180 seconds |
| `timeout` | 30 seconds |
| `maxRetries` | 2 |
| `httpClient` | a `java.net.http.HttpClient` that never follows redirects |

Calling a product without its key throws `IllegalStateException` before anything is sent. The client is
immutable and safe to share between threads.

If you pass your own `HttpClient`, build it without redirects, which is `HttpClient.newBuilder()`'s default:

```java
HttpClient http = HttpClient.newBuilder()
    .followRedirects(HttpClient.Redirect.NEVER)
    .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 8080)))
    .build();
WebsiteScreenshotWorld client = WebsiteScreenshotWorld.builder().httpClient(http).build();
```

A client that follows a redirect sends the API key on to wherever it points. Neither the API nor a download
link redirects, so the library refuses any answer that came through a redirect, but by then the key has been
sent.

## Supported Java versions

Java 11 and later. Each release is tested on Java 11 and Java 25.

## Development

The tests run the shared cases in [`spec/`](https://github.com/websitescreenshotworld/websitescreenshotworld-spec) against [`spec/conformance/server.js`](https://github.com/websitescreenshotworld/websitescreenshotworld-spec/blob/main/conformance/server.js),
so they need Node.js 22 or later on the `PATH`. `spec/` is the [websitescreenshotworld-spec](https://github.com/websitescreenshotworld/websitescreenshotworld-spec) submodule: clone with `git clone --recurse-submodules`, or run `git submodule update --init` in an existing clone.

```sh
mvn verify
```

On Java 17 and later, `mvn verify` also checks formatting with google-java-format; `mvn spotless:apply`
fixes it.

## License

MIT. See [LICENSE](LICENSE).
