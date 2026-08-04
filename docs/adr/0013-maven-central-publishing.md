# ADR 0013: Maven Central Publishing Infrastructure

## Status

Accepted

## Context

SKOne modules need a permanent, zero-duplication publishing system that is
Maven Central Portal compliant (signing, sources, Dokka Javadoc, POM metadata)
and easy for future modules to adopt.

The Maven Central namespace `com.thesubodhgupta.skone` is verified.

## Decision

1. Version **1.3.2** delivers publishing infrastructure only (no Central upload
   until explicitly enabled).
2. Convention plugin **`skone.publish`** (alias `skone.publishing`) configures
   every publishable module (publications, POM, Dokka, signing, local repos).
3. Coordinates:
   - Group: `com.thesubodhgupta.skone` (`GROUP` in `gradle.properties`)
   - Version: `VERSION_NAME` in `gradle.properties`
   - ArtifactId: Gradle project name
4. Kotlin packages remain `io.skone.*` (Maven group ≠ package root).
5. Artifacts: release AAR/JAR, sources JAR, Dokka Javadoc JAR, POM, module
   metadata, optional PGP signatures.
6. Credentials via env: `SIGNING_KEY`, `SIGNING_PASSWORD`,
   `CENTRAL_PORTAL_USERNAME`, `CENTRAL_PORTAL_PASSWORD`.
7. GitHub Actions: CI validates; Publish workflow supports manual + tag releases.
8. **Maven Central uploads use the Portal Publisher API** via
   [nmcp](https://github.com/GradleUp/nmcp) (`nmcpAggregation` at the root).
   Deprecated OSSRH staging repositories are not used.
9. CI default publishing type is **`USER_MANAGED`**: upload → wait `VALIDATED` →
   publish deployment → wait `PUBLISHED` → verify Maven URL, with explicit logs.

## Consequences

- New modules only need `id("skone.publish")`.
- BOM uses the same convention (`java-platform`).
- Central publish is opt-in (`publish_central` workflow input).
- A green CI job without `publish_central` never uploads to Central.
- Local success (`publishToMavenLocal`) does not imply Central visibility.
