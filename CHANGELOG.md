# Changelog

All notable changes to this library are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this library follows
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2026-11-09

### Added

- `WebsiteScreenshotWorld` client covering Instant Snap and Auto Schedule, with API keys read from
  `WSW_INSTANT_SNAP_API_KEY` and `WSW_AUTO_SCHEDULE_API_KEY` by default.
- Instant Snap: `capture`, `captureFile` (streams the file, with its `X-InstantSnap-*` metadata),
  `captureToBucket`, `get`, `download`, `defaults` and `catalog`, with every capture option as an
  immutable builder.
- Auto Schedule: `triggerNow`.
- The `pressEscape` capture option, and the `sitemap_schedule_not_triggerable` error code (an
  `InvalidRequestException`: a sitemap schedule runs only on its schedule), from API version 1.1.0.
- Async Instant Snap: `submit` and `submitToBucket` queue a capture and return it `queued` (using the
  ordinary request timeout), `waitFor` reads it until it finishes or its time runs out, the
  `CaptureOptions.timeoutSeconds` option (sent as `timeout`, 30-300 s, async only), the
  `CaptureStatus.QUEUED` and `RUNNING` statuses, and the `queue_full` (`RateLimitException`, not retried)
  and `capture_expired` (`CaptureException`) error codes. `Capture.mediaCategory()` is `null` while a
  capture is queued or running.
- Automatic retries for rate limits, busy capture slots, unavailable services, idempotency key clashes and
  connection failures, reusing one `Idempotency-Key` per capture call.
- Per-call idempotency keys and timeouts through `RequestOptions`; a timeout covers the whole call, retries
  included.
- Redirects are never followed, so the API key is never sent to another host; a 3xx answer is raised as an
  error, and an answer that came through a redirect followed by an `HttpClient` you passed in is refused.
- One unchecked exception per error kind, all extending `WebsiteScreenshotWorldException`.
- `Webhook.verify` for `X-WSW-Signature` headers, returning typed events for every Instant Snap and Auto
  Schedule webhook, and `UnknownWebhookEvent` for types added later.
