# SKOne Publishing Guide

Production-grade Maven publishing for the SKOne SDK.

**Milestone:** 1.3.2 — publishing infrastructure (do **not** publish to Maven Central until explicitly ready).

## Coordinates

| Item | Value |
|------|-------|
| Group | `com.thesubodhgupta.skone` |
| Version | `VERSION_NAME` in [`gradle.properties`](../gradle.properties) |
| Artifact IDs | Module names (`skone-core`, `skone-ui`, …) |
| Kotlin packages | `io.skone.*` (unchanged) |
| Site | https://skone.thesubodhgupta.com |
| SCM | https://github.com/Subodh2020/SkOne |

Example dependency:

```kotlin
implementation("com.thesubodhgupta.skone:skone-compose:1.3.2-alpha01")
implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha01"))
```

## Onboarding a new module

1. Create the Android library (or `java-platform` BOM) module.
2. Apply:

```kotlin
plugins {
    alias(libs.plugins.skone.android.library) // if Android
    alias(libs.plugins.skone.publish)
}
```

3. Add the project to `settings.gradle.kts`.
4. Add a BOM constraint in `skone-bom` when it should be version-aligned.
5. Add the path to `publishableProjects` in the root `build.gradle.kts`.

No other publishing configuration is required.

## Versioning

Single source of truth: **`VERSION_NAME`** in `gradle.properties`.

| Stage | Example |
|-------|---------|
| Alpha | `1.3.2-alpha01` |
| Beta | `1.3.2-beta01` |
| RC | `1.3.2-rc01` |
| Stable | `1.3.2` |
| Snapshot | `1.3.2-SNAPSHOT` |

Also keep the `skone` version in `gradle/libs.versions.toml` in sync for documentation consistency (publishing reads `VERSION_NAME`).

## Local publishing

```bash
# Validate POM / signing configuration
./gradlew verifyPublishing

# Install into ~/.m2
./gradlew publishToMavenLocal

# Publish into ./build/local-maven-repo (CI artifact)
./gradlew publishToLocalTestRepository
```

Consume from the test repo:

```kotlin
repositories {
    maven { url = uri("/path/to/SkOne/build/local-maven-repo") }
}
```

## Signing

Environment variables (never commit secrets):

| Variable | Purpose |
|----------|---------|
| `SIGNING_KEY` | ASCII-armored private PGP key (or exported secret key block) |
| `SIGNING_PASSWORD` | Key passphrase |

Without these variables, local publish still works; signing is skipped (`signing.required = false`).

Maven Central **requires** signing.

Export a key for CI:

```bash
gpg --export-secret-keys --armor YOUR_KEY_ID
```

## Maven Central credentials

| Variable | Purpose |
|----------|---------|
| `CENTRAL_PORTAL_USERNAME` | Portal user token username |
| `CENTRAL_PORTAL_PASSWORD` | Portal user token password |

Generate tokens at https://central.sonatype.com/usertoken

Repositories configured by `skone.publish`:

- Releases → OSSRH Staging API (`ossrh-staging-api.central.sonatype.com`)
- Snapshots → Central snapshots repository

## Dokka

Applied automatically by `skone.publish` on Android / JVM libraries.

```bash
./gradlew dokkaHtmlAll          # HTML per module
./gradlew :skone-ui:dokkaJavadocJar
```

Javadoc JARs are attached to Maven publications.

## Validation tasks

| Task | Scope |
|------|-------|
| `verifyPom` | Coordinates + publication presence |
| `verifySigning` | Signing enabled when `SIGNING_KEY` set |
| `verifyPublishing` | Both of the above |
| `publishToLocalTestRepository` | Dry-run style local repo publish |

Root aggregates run across all publishable modules.

## GitHub Actions

### CI (`.github/workflows/ci.yml`)

Checkout → JDK 17 → Gradle cache → `check` → `verifyPublishing` → Dokka

### Publish (`.github/workflows/publish.yml`)

Triggers:

- Manual `workflow_dispatch`
- Tags `v*`

Pipeline: build → tests → quality → Dokka → verify → `publishToMavenLocal` → local test repo → **optional** Central (`publish_central=true`) → GitHub Release

Required secrets for signed / Central releases:

- `SIGNING_KEY`
- `SIGNING_PASSWORD`
- `CENTRAL_PORTAL_USERNAME`
- `CENTRAL_PORTAL_PASSWORD`

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| Wrong group/version | Edit `GROUP` / `VERSION_NAME` in `gradle.properties`, re-sync |
| Dokka task missing | Ensure module applies `skone.publish` and is an Android library |
| Signing failures in CI | Confirm `SIGNING_KEY` is the full armored key; passphrase in `SIGNING_PASSWORD` |
| Central repo missing | Credentials only add the remote when both Portal env vars are set |
| BOM not published | BOM uses `skone.publish` + `java-platform` |

## Related

- [ADR 0013](adr/0013-maven-central-publishing.md)
- [ADR 0006](adr/0006-maven-coordinates-and-semver.md) (coordinates history)
