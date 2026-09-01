package io.skone.playground.docs

import io.skone.playground.catalog.CatalogId

/**
 * Bundled documentation articles for the in-app docs site.
 */
data class DocArticle(
    val id: CatalogId,
    val title: String,
    val body: String,
)

object PlaygroundDocs {
    val articles: List<DocArticle> = listOf(
        DocArticle(
            id = CatalogId.DocOverview,
            title = "SDK Overview",
            body = """
                SKOne is a modular Android developer platform (Kotlin, API 24+).

                Modules
                • skone-bom — Bill of Materials
                • skone-theme — Design tokens + theme engine
                • skone-core — Component framework
                • skone-forms — Form controller & engines
                • skone-ui — Shared widget contracts
                • skone-compose / skone-xml — UI bridges + widgets

                Consume via the BOM. Prefer design tokens — never hardcode colors,
                typography, spacing, or dimensions in production widgets.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocPlayground,
            title = "Playground Guide",
            body = """
                This app is the official showcase for every future SKOne component.

                • Catalog — browse widgets, frameworks, samples, docs
                • Gallery — live previews
                • Editors — tweak properties and preview
                • Codegen — copy Compose or XML
                • Theme switcher — Light / Dark / System
                • Search — filter the catalog
                • Samples — integration recipes

                When you add a production widget, register it in PlaygroundCatalog
                and wire gallery + editor + codegen.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkText,
            title = "SKText",
            body = """
                SKText / SKTextView is the reference display widget.

                Compose: io.skone.compose.widget.SKText
                XML: io.skone.xml.widget.SKTextView

                Visuals resolve through SKAppearanceConfig + theme tokens.
                Supports annotated spans, overflow, alignment, and accessibility.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkTextField,
            title = "SKTextField",
            body = """
                SKTextField / SKTextFieldView is the flagship input.

                Auto-registers with SKFormController when provided.
                Uses ValidationEngine, FormatterEngine, InputMask, and FocusChain.
                Supports label, hint, supportingText, icons, masks, IME, error/success.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkRadioButton,
            title = "SKRadioButton",
            body = """
                Single-option radio — Compose SKRadioButton / XML SKRadioButtonView.

                Prefer SKRadioGroup for exclusive selection.
                Exposes radio role, selected state, and disabled state for TalkBack.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkRadioGroup,
            title = "SKRadioGroup",
            body = """
                Exclusive selection container for radio options.

                Compose uses CompositionLocal scoping; XML uses SKRadioGroupView + SKRadioGroupController.
                One selected String value at a time.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkChip,
            title = "SKChip",
            body = """
                Foundational selectable chip — Compose SKChip / XML SKChipView.

                Supports selected/enabled, label, optional leading icon (decorative by default).
                Filter/input/assist/suggestion variants are deferred.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkSlider,
            title = "SKSlider",
            body = """
                Continuous or stepped Float value — Compose SKSlider / XML SKSliderView.

                Exposes progress/range semantics and current value for TalkBack.
                Range/dual-thumb/vertical deferred.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkProgressIndicator,
            title = "SKProgressIndicator",
            body = """
                Linear or circular progress — determinate or indeterminate.

                Uses lightweight platform animation; no custom animation framework.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkDivider,
            title = "SKDivider",
            body = """
                Decorative horizontal or vertical hairline separator.

                Hidden from TalkBack by default.
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkCard,
            title = "SKCard",
            body = """
                Foundational surface container with token elevation/shape.

                Optional onClick makes the card interactive (button role).
            """.trimIndent(),
        ),
        DocArticle(
            id = CatalogId.DocSkSnackbar,
            title = "SKSnackbar",
            body = "Host-controlled snackbar with optional action and polite live region.",
        ),
        DocArticle(
            id = CatalogId.DocSkDialog,
            title = "SKDialog",
            body = "Generic modal. XML uses programmatic SKDialogHost (window-level).",
        ),
        DocArticle(
            id = CatalogId.DocSkAlertDialog,
            title = "SKAlertDialog",
            body = "Title + message + confirm/dismiss actions.",
        ),
        DocArticle(
            id = CatalogId.DocSkTopAppBar,
            title = "SKTopAppBar",
            body = "Title bar; navigation/action icons require explicit contentDescription.",
        ),
        DocArticle(
            id = CatalogId.DocSkNavigationBar,
            title = "SKNavigationBar",
            body = "Exclusive destination selection with Tab + selected semantics.",
        ),
        DocArticle(
            id = CatalogId.DocSkListItem,
            title = "SKListItem",
            body = "List row with headline, optional supporting/leading/trailing; clickable/selected optional.",
        ),
        DocArticle(
            id = CatalogId.DocSkSectionHeader,
            title = "SKSectionHeader",
            body = "Lightweight section title; optional supporting text and action.",
        ),
        DocArticle(
            id = CatalogId.DocSkScaffold,
            title = "SKScaffold",
            body = "Screen shell with top/content/bottom + optional snackbar and FAB slots; safe-drawing insets by default.",
        ),
        DocArticle(
            id = CatalogId.DocSkSearchBar,
            title = "SKSearchBar",
            body = "Host-owned search query with clear action and IME Search; reuses text-input patterns.",
        ),
        DocArticle(
            id = CatalogId.DocSkEmptyState,
            title = "SKEmptyState",
            body = "Host-driven empty / zero-results content with optional icon and actions.",
        ),
        DocArticle(
            id = CatalogId.DocSkFab,
            title = "SKFab",
            body = "Minimal FAB with required accessibility description; optional Scaffold bottom-end slot.",
        ),
        DocArticle(
            id = CatalogId.DocSkTabRow,
            title = "SKTabs / SKTabRow",
            body = "Exclusive tab row with selected/disabled Tab semantics; NavigationBar-style items API.",
        ),
        DocArticle(
            id = CatalogId.DocSkBadge,
            title = "SKBadge",
            body = "Compact count or decorative-dot badge; dots silent unless contentDescription is set.",
        ),
        DocArticle(
            id = CatalogId.DocSkAvatar,
            title = "SKAvatar",
            body = "Identity avatar with host image content and initials fallback; no image loader.",
        ),
        DocArticle(
            id = CatalogId.DocSkMenu,
            title = "SKMenu",
            body = "Reusable menu item list; host owns visibility and placement.",
        ),
        DocArticle(
            id = CatalogId.DocSkDropdownMenu,
            title = "SKDropdownMenu",
            body = "Host-controlled dropdown popup with selection and outside/back dismiss.",
        ),
        DocArticle(
            id = CatalogId.DocSkTooltip,
            title = "SKTooltip",
            body = "Host-controlled tooltip hint; prefer host action CD to avoid noisy announcements.",
        ),
        DocArticle(
            id = CatalogId.DocSkBottomAppBar,
            title = "SKBottomAppBar",
            body = "Bottom chrome with leading/content/trailing slots; optional FAB layout slot.",
        ),
        DocArticle(
            id = CatalogId.DocSkBottomSheet,
            title = "SKBottomSheet",
            body = "Lean host-controlled bottom sheet with optional primary/secondary actions.",
        ),
        DocArticle(
            id = CatalogId.DocSkSegmentedButton,
            title = "SKSegmentedButton",
            body = "Exclusive segmented selection control; TabRow-style items API.",
        ),
    )

    fun find(id: CatalogId): DocArticle? = articles.firstOrNull { it.id == id }
}
