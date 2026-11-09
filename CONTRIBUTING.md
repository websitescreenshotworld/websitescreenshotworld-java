# Contributing

Thanks for helping.

- **The API is described once, in the spec.** `spec/` is the
  [websitescreenshotworld-spec](https://github.com/websitescreenshotworld/websitescreenshotworld-spec) repository,
  included as a git submodule fixed to one commit. A change that affects the wire (a new option, field or error
  code) starts there, with a test case; then move this repository's pin with
  `git -C spec checkout <commit>` and commit the new pin together with the code that handles it.
- **The tests run the shared cases.** `spec/conformance/server.js` plays back the cases in `spec/fixtures/`, and the
  test suite runs every case, every webhook vector and every event example.

Clone with the submodule, then lint and test. You need Java 17 or later to build (the library runs on Java 11) and Maven, and Node.js 22 or later for the conformance
server:

```sh
git clone --recurse-submodules https://github.com/websitescreenshotworld/websitescreenshotworld-java.git
cd websitescreenshotworld-java
mvn verify
```

Keep pull requests to one change, and add a line to `CHANGELOG.md` under "Unreleased".
