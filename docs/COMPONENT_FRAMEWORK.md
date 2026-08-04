# SKOne Component Framework

Internal framework that every future SKOne widget inherits.
**No production widgets** are defined here — only contracts, managers, and UI bases.

## Architecture

```mermaid
flowchart TB
  subgraph contracts [Contracts - skone-core]
    C[SKComponent]
    I[SKInteractiveComponent]
    In[SKInputComponent]
    S[SKSelectableComponent]
    N[SKNavigationComponent]
    A[SKAIComponent]
    I --> C
    In --> I
    S --> I
    N --> I
    A --> C
  end

  subgraph managers [Managers]
    Focus[SKFocusManager]
    Val[SKValidationManager]
    State[SKStateManager]
    Events[SKEventDispatcher]
    Anim[SKAnimationManager]
  end

  subgraph runtime [Runtime]
    RT[SKComponentRuntime]
    RT --> Focus
    RT --> Val
    RT --> State
    RT --> Events
    RT --> Anim
  end

  C --> RT

  subgraph bridges [UI Bases]
    Compose[SKComposeComponent]
    Xml[SKXmlComponent]
  end

  Compose --> C
  Xml --> C
```

## Contracts

| Contract | Purpose |
|----------|---------|
| `SKComponent` | Identity, config, attach/detach lifecycle |
| `SKInteractiveComponent` | Click, long-click, focus |
| `SKInputComponent<T>` | Value, change events, validation |
| `SKSelectableComponent<T>` | Single/multi selection |
| `SKNavigationComponent` | Destination navigation intent |
| `SKAIComponent` | Component-scoped AI execution |

## Managers

| Manager | Responsibility |
|---------|----------------|
| `SKFocusManager` | Focus ownership across components |
| `SKValidationManager` | Register validators; validate one/all |
| `SKStateManager` | Observe/update `SKComponentState` |
| `SKEventDispatcher` | Typed component events + subscribers |
| `SKAnimationManager` | Motion-token driven animation requests |

## Supporting abstractions

- **Layout** — `SKLayoutSpec`, constraints (framework-agnostic)
- **Icons** — `SKIconProvider` / `SKIconKey` (no drawable coupling in core)
- **Analytics** — `SKAnalyticsHook` emitting via config
- **Plugin lifecycle** — `SKComponentPlugin` hooks on attach/detach/event
- **DSL** — `skComponent { }` builds `SKComponentConfig` fluently

## Compose / XML bases

| Module | Type | Role |
|--------|------|------|
| `skone-compose` | `SKComposeComponent` + `rememberSKComponentRuntime` | Host runtime in composition |
| `skone-xml` | `SKXmlComponent` | View base wired to runtime |

Neither module ships production widgets.

## Related

- [Design System](DESIGN_SYSTEM.md)
- [ADR 0008](adr/0008-component-framework.md)
- [API Guidelines](SDK_API_GUIDELINES.md)
