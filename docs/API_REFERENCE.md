# SKOne API Reference

Browsable static HTML API documentation is generated from Dokka and deployed to GitHub Pages.

## Hosted URL

https://subodh2020.github.io/SkOne/api/

Path on the deployed site: `/api/`

## Generate locally

```bash
./gradlew dokkaHtmlAll
./gradlew assembleDocsSite
```

Output:

- Site root: `build/docs-site/`
- API landing page: `build/docs-site/api/index.html`
- Per-module Dokka HTML: `build/docs-site/api/<module>/`

Preview:

```bash
cd build/docs-site && python3 -m http.server 8080
```

Then open http://localhost:8080/api/

## Modules included

- `skone-common`
- `skone-plugin`
- `skone-theme`
- `skone-core`
- `skone-ui`
- `skone-forms`
- `skone-compose`
- `skone-xml`

## Deployment

The `Deploy Documentation` GitHub Actions workflow (`.github/workflows/docs.yml`) runs on pushes to `main` / `master` and on manual dispatch.

It:

1. Runs `./gradlew assembleDocsSite`
2. Uploads `build/docs-site` as a GitHub Pages artifact
3. Deploys static HTML (no Maven publishing)

Maven Central Javadoc publication (`dokkaJavadocJar`) is unchanged and remains part of the library publishing pipeline.

## GitHub Pages setup

Enable **GitHub Pages → Source: GitHub Actions** in the repository settings before the first deployment.
