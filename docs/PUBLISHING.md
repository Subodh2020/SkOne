# SKOne Publishing Guide

Production-grade Maven publishing for the SKOne SDK using the **Maven Central Portal Publisher API** (via [nmcp](https://github.com/GradleUp/nmcp)).

**Not used:** deprecated OSSRH staging repositories.

**Milestone:** 1.3.2 — publishing infrastructure. Maven Central releases are published via GitHub Actions (`publish_central=true`).

**Release line:** `1.3.2-alpha04` — API boundary stabilization, contract tests, docs. See [RELEASE_NOTES.md](RELEASE_NOTES.md).

**Current published version on Maven Central:** `1.3.2-alpha04`.

## Currently on Maven Central

As of **`1.3.2-alpha04`** (published), these artifacts are available:

| Artifact | Coordinates |
|----------|-------------|
| BOM | `com.thesubodhgupta.skone:skone-bom:1.3.2-alpha04` |
| Common | `com.thesubodhgupta.skone:skone-common:1.3.2-alpha04` |
| Plugin | `com.thesubodhgupta.skone:skone-plugin:1.3.2-alpha04` |
| Theme | `com.thesubodhgupta.skone:skone-theme:1.3.2-alpha04` |
| Core | `com.thesubodhgupta.skone:skone-core:1.3.2-alpha04` |
| UI | `com.thesubodhgupta.skone:skone-ui:1.3.2-alpha04` |
| Forms | `com.thesubodhgupta.skone:skone-forms:1.3.2-alpha04` |
| Compose | `com.thesubodhgupta.skone:skone-compose:1.3.2-alpha04` |
| XML | `com.thesubodhgupta.skone:skone-xml:1.3.2-alpha04` |

**Recommended consumer dependencies** (see [Getting Started — Choosing SKOne dependencies](GETTING_STARTED.md#choosing-skone-dependencies)):

- **Compose:** `skone-bom` + `skone-compose`
- **XML/View:** `skone-bom` + `skone-xml`

## Coordinates

| Item | Value |
|------|-------|
| Group | `com.thesubodhgupta.skone` |
| Version | `VERSION_NAME` in [`gradle.properties`](../gradle.properties) |
| Artifact IDs | Module names (`skone-core`, `skone-ui`, …) |
| Kotlin packages | `io.skone.*` (unchanged) |
| Site | https://skone.thesubodhgupta.com |
| SCM | https://github.com/Subodh2020/SkOne |
| Portal UI | https://central.sonatype.com/ |
| Maven Central | https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/ |

Example dependency (Compose — recommended):

```kotlin
repositories {
    mavenCentral()
}

implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha04"))
implementation("com.thesubodhgupta.skone:skone-compose")
```

Example dependency (XML/View):

```kotlin
implementation(platform("com.thesubodhgupta.skone:skone-bom:1.3.2-alpha04"))
implementation("com.thesubodhgupta.skone:skone-xml")
```

## Architecture

| Layer | Responsibility |
|-------|----------------|
| `skone.publish` convention | Per-module Maven publication, POM, sources, Dokka javadoc JAR, signing, local repos |
| Root `nmcpAggregation` | Single zip deployment to Central Portal Publisher API |
| `scripts/central-portal.sh` | Status / wait / publish / verify helpers for explicit CI stages |
| `.github/workflows/publish.yml` | Explicit Build → Test → Verify → Dokka → Sign → Upload → Validate → Publish → Verify |

No hidden OSSRH staging. Central publish is always opt-in.

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

Root `nmcpAggregation { publishAllProjectsProbablyBreakingProjectIsolation() }` picks up any project that applies `maven-publish` (via `skone.publish`).

## Versioning

Single source of truth: **`VERSION_NAME`** in `gradle.properties`.

| Stage | Example |
|-------|---------|
| Alpha | `1.3.2-alpha04` (current published release) |
| Beta | `1.3.2-beta01` |
| RC | `1.3.2-rc01` |
| Stable | `1.3.2` |
| Snapshot | `1.3.2-SNAPSHOT` |

Also keep the `skone` version in `gradle/libs.versions.toml` in sync for documentation consistency (publishing reads `VERSION_NAME`).

## How local publish works

```bash
# Inspect every publication
./gradlew printPublishingInfo

# Validate POM / signing configuration
./gradlew verifyPublishing

# Install into ~/.m2
./gradlew publishToMavenLocal

# Publish into ./build/local-maven-repo (CI artifact)
./gradlew publishToLocalTestRepository

# Inspect the Central deployment zip (no upload)
./gradlew nmcpZipAggregation
# → build/nmcp/zip/aggregation.zip
```

Consume from the test repo:

```kotlin
repositories {
    maven { url = uri("/path/to/SkOne/build/local-maven-repo") }
}
```

Local publish never talks to Maven Central. Signing runs only when `SIGNING_KEY` is set.

## How GitHub publish works

Workflow: [`.github/workflows/publish.yml`](../.github/workflows/publish.yml)

| Input | Default | Meaning |
|-------|---------|---------|
| `publish_central` | `false` | When `true`, run Central Portal stages |
| `create_github_release` | `true` | Tag / GH Release for `VERSION_NAME` |
| `debug_publish` | `false` | Extra task / plugin / metadata logs |
| `publishing_type` | `USER_MANAGED` | Explicit validate → publish stages (recommended) |

### Stages (when `publish_central=true`)

1. **Build** — `assemble`
2. **Tests** — `check`
3. **Verify publishing** — `verifyPublishing` + `printPublishingInfo`
4. **Dokka** — `dokkaHtmlAll`
5. **Sign** — `publishToMavenLocal` + `publishToLocalTestRepository` (signing secrets only; no Portal credentials)
6. **Upload bundle** — fail-fast on all four secrets, then `nmcpZipAggregation` + `publishAggregationToCentralPortal` (prints bundle path)
7. **Deployment ID** — parsed from nmcp logs
8. **Wait VALIDATED** — poll every 10s, max 15 minutes (`USER_MANAGED`)
9. **Publish deployment** — Portal API `POST …/deployment/{id}`
10. **Wait PUBLISHED** — poll every 10s, max 15 minutes
11. **Verify artifact** — HTTP check on `repo.maven.apache.org` + Portal UI link

`AUTOMATIC` skips stages 8–9 script calls; nmcp performs validate + publish itself.

Fail-fast: Stage 6 (`requireCentralSecrets`) aborts before upload if any of `SIGNING_KEY`, `SIGNING_PASSWORD`, `CENTRAL_PORTAL_USERNAME`, or `CENTRAL_PORTAL_PASSWORD` is missing. Stage 5 never checks Portal credentials.

## How Maven Central deployment works

1. Gradle builds all publications (AAR/JAR + sources + javadoc + POM + signatures).
2. nmcp packs them into one zip: `build/nmcp/zip/aggregation.zip`.
3. Zip is uploaded to `https://central.sonatype.com/api/v1/publisher/upload`.
4. Portal returns a **Deployment ID**.
5. Portal validates coordinates, signatures, POM, and namespace ownership → `VALIDATED` or `FAILED`.

Namespace `com.thesubodhgupta.skone` must already be verified (it is).

## How deployment gets published

| Mode | Behavior |
|------|----------|
| `USER_MANAGED` (default in CI) | Stops at `VALIDATED`. CI (or Portal UI) calls publish. Then state → `PUBLISHING` → `PUBLISHED`. |
| `AUTOMATIC` | Portal publishes automatically after successful validation. |

After `PUBLISHED`, artifacts appear under:

`https://repo.maven.apache.org/maven2/com/thesubodhgupta/skone/`

Helpers:

```bash
./scripts/central-portal.sh status <deploymentId>
./scripts/central-portal.sh wait <deploymentId> VALIDATED 900
./scripts/central-portal.sh publish <deploymentId>
./scripts/central-portal.sh wait <deploymentId> PUBLISHED 900
./scripts/central-portal.sh verify-maven 1.3.2-alpha04 skone-bom
```

## Expected timelines

| Phase | Typical | Timeout in CI |
|-------|---------|---------------|
| Local assemble / publish | minutes | — |
| Bundle upload | seconds–few minutes | — |
| Portal validation (`VALIDATED`) | ~1–10 minutes | 15 minutes |
| Publish → `PUBLISHED` | ~1–10 minutes | 15 minutes |
| Visible on `repo.maven.apache.org` | often immediate after `PUBLISHED`; can lag | Stage 11 warns if missing |

CDN / mirror propagation can take additional minutes. Stage 11 prints a clear warning if the listing is not yet HTTP 200.

## Signing

| Variable | Purpose |
|----------|---------|
| `SIGNING_KEY` | ASCII-armored private PGP key |
| `SIGNING_PASSWORD` | Key passphrase |

Without these variables, local publish still works; signing is skipped (`signing.required = false`).

Maven Central **requires** signing.

```bash
gpg --export-secret-keys --armor YOUR_KEY_ID
```

## Maven Central credentials

| Variable | Purpose |
|----------|---------|
| `CENTRAL_PORTAL_USERNAME` | Portal user token username |
| `CENTRAL_PORTAL_PASSWORD` | Portal user token password |
| `CENTRAL_PUBLISHING_TYPE` | Optional Gradle override: `AUTOMATIC` or `USER_MANAGED` |

Generate tokens at https://central.sonatype.com/usertoken

## Dokka

Applied automatically by `skone.publish` on Android / JVM libraries.

```bash
./gradlew dokkaHtmlAll
./gradlew :skone-ui:dokkaJavadocJar
```

## Validation tasks

| Task | Scope |
|------|-------|
| `printPublishingInfo` | Coordinates, repo, signed flag |
| `verifyPom` | Coordinates + publication presence |
| `verifySigning` | Signing enabled when `SIGNING_KEY` set |
| `verifyPublishing` | Pom + signing + print |
| `publishToLocalTestRepository` | Dry-run style local repo publish |
| `nmcpZipAggregation` | Build Central zip (no upload) |
| `publishAggregationToCentralPortal` | Upload (+ validate / publish per type) |
| `requireCentralSecrets` | Fail if Central/signing env missing |

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| CI green but nothing on Central | Old OSSRH staging path. Use nmcp Portal API (`publishAggregationToCentralPortal`) with `publish_central=true`. |
| Wrong group/version | Edit `GROUP` / `VERSION_NAME` in `gradle.properties` |
| Missing secrets | Set all four env/secrets; `requireCentralSecrets` fails fast |
| Signing failures | Full armored key in `SIGNING_KEY`; passphrase in `SIGNING_PASSWORD` |
| Validation `FAILED` | Check Portal deployment errors (POM, signatures, duplicate version) |
| Timed out waiting `VALIDATED`/`PUBLISHED` | Check Portal UI; re-run `scripts/central-portal.sh wait …` |
| Artifact URL 404 after `PUBLISHED` | Wait for propagation; Stage 11 warns — check Portal + Maven URL |
| Deployment ID not parsed | Open Portal UI; search publication name `SKOne <version>` |
| Module missing from zip | Ensure `skone.publish` + entry in `publishableProjects` |

## Related

- [ADR 0013](adr/0013-maven-central-publishing.md)
- [ADR 0006](adr/0006-maven-coordinates-and-semver.md)
