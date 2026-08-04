# SKOne SDK API Guidelines

This document defines the public API rules for the SKOne Android SDK.
All contributors and modules must follow these guidelines to ensure
long-term binary compatibility, consistency, and developer experience.

## Branding and Naming

| Item         | Rule       | Example                           |
|--------------|------------|-----------------------------------|
| Brand        | SKOne      | —                                 |
| Package root | `io.skone` | `io.skone.core`                   |
| Class prefix | `SK`       | `SKButton`, `SKTheme`, `SKResult` |
| Maven group  | `com.thesubodhgupta.skone` | `com.thesubodhgupta.skone:skone-core` |

Never use ad-hoc names such as `CustomButton`, `MyButton`, or `AwesomeButton`.

## Parameter Order (Public Components)

Every public component API must use this parameter order:

1. `modifier` (Compose) / layout attrs (XML)
2. state / value
3. callbacks
4. content
5. appearance
6. behavior
7. validation
8. accessibility
9. analytics
10. AI configuration

Do not invent random parameter ordering. Prefer industry-standard names:

- `required`, `enabled`, `readOnly`
- `isError`, `supportingText`
- `leadingIcon`, `trailingIcon`
- `keyboardOptions`, `keyboardActions`

## Stability Annotations

| Annotation        | Meaning                                                           |
|-------------------|-------------------------------------------------------------------|
| *(none / stable)* | Public stable API. Breaking changes require a major version bump. |
| `@SKExperimental` | Opt-in experimental API. May change without a major bump.         |
| `@SKInternal`     | Not for public use. May change or be removed at any time.         |

### Experimental Policy

- Annotate with `@SKExperimental` and require `@OptIn(SKExperimental::class)`.
- Document expected graduation path in KDoc when known.
- Before promoting to stable, publish at least one minor release with the API marked experimental.

### Deprecation Policy

1. Annotate with `@Deprecated(message, replaceWith = ReplaceWith(...))`.
2. Keep the symbol for at least one minor release before removal.
3. Removal requires a major version bump (Semantic Versioning).

## Binary Compatibility

- Prefer additive changes (new overloads, new optional parameters with defaults).
- Avoid renaming, removing, or changing parameter types of public APIs.
- Do not expose implementation types in public signatures.
- Prefer interfaces and sealed hierarchies for extension points.
- Use `data class` carefully in public API; prefer stable factory functions when evolution is likely.

## KDoc Requirements

Every public class, interface, function, and property must include KDoc covering:

- Purpose
- Usage example (when non-obvious)
- Compose sample (for UI APIs)
- XML sample (for UI APIs)
- Performance notes (when relevant)
- Accessibility notes (when relevant)

## Architecture Rules

1. **SOLID** and Clean Architecture boundaries.
2. **Plugin-based** extension; no core edits required for new plugins.
3. **Dependency inversion** via interfaces (AI providers, loggers, themes).
4. **Zero reflection** unless strictly required and documented.
5. **No memory leaks**; prefer structured concurrency and lifecycle-aware APIs.
6. **Accessibility first** for all UI surfaces.
7. **Backward compatible** public APIs within a major version.
8. **Semantic Versioning** for all published artifacts.

## Module Boundaries

| Module         | Public surface                                 |
|----------------|------------------------------------------------|
| `skone-common` | Results, errors, logging, annotations          |
| `skone-plugin` | Plugin SPI and registry                        |
| `skone-theme`  | Design token contracts                         |
| `skone-core`   | `SKOne` init, AI SPI, base component contracts |

UI modules (`skone-compose`, `skone-xml`, `skone-ui`, …) must not leak Compose types into non-Compose modules.

## Testing Expectations for Public APIs

- Unit tests for models and registries
- API shape tests where practical
- Compose / accessibility / screenshot tests when UI lands

## Versioning Reminder

- **MAJOR** — breaking public API changes
- **MINOR** — additive, backward-compatible features
- **PATCH** — bug fixes and documentation

Coordinate versions via `skone-bom`.
