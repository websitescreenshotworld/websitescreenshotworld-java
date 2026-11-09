# Releasing

1. Move the "Unreleased" section of `CHANGELOG.md` under the new version and date.
2. Set the version: `<version>` in `pom.xml`.
3. Commit, then tag that commit `v<version>`, such as `v0.1.1`, and push the tag.
4. The tag's workflow runs the tests again and publishes to Maven Central as `com.websitescreenshotworld:websitescreenshotworld-java`, signed with the GPG key in the repository secrets `GPG_PRIVATE_KEY` and `GPG_PASSPHRASE`, using the Central Portal user token in `CENTRAL_USERNAME` and `CENTRAL_PASSWORD`.

The libraries for the other languages share the version number, so a change made in all of them is released at
the same version in each.
