# SKOne Docs Site

Static documentation companion to the Android playground.

## Hosted site

GitHub Pages deploys `build/docs-site` from the **Deploy Documentation** workflow:

- Docs home: https://subodh2020.github.io/SkOne/
- API reference: https://subodh2020.github.io/SkOne/api/

## Open locally

```bash
./gradlew assembleDocsSite
cd build/docs-site && python3 -m http.server 8080
```

Or open the static pages in this directory directly:

```bash
open docs-site/index.html
```

Pages link into the markdown sources under `/docs`. The API reference is generated from Dokka HTML at build time.

See [API Reference guide](../docs/API_REFERENCE.md).
