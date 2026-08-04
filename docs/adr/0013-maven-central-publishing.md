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
   every publishable module.
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

## Consequences

- New modules only need `id("skone.publish")`.
- BOM uses the same convention (`java-platform`).
- Central publish is opt-in (`publish_central` workflow input).
