package io.skone.playground.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.skone.component.appearance.SKAppearanceConfig
import io.skone.compose.forms.ProvideSKFormController
import io.skone.compose.theme.skTheme
import io.skone.compose.theme.toDp
import io.skone.compose.widget.SKCard
import io.skone.compose.widget.SKChip
import io.skone.compose.widget.SKDivider
import io.skone.compose.widget.SKProgressIndicator
import io.skone.compose.widget.SKRadioButton
import io.skone.compose.widget.SKRadioGroup
import io.skone.compose.widget.SKSlider
import io.skone.compose.widget.SKText
import io.skone.compose.widget.SKTextField
import io.skone.forms.SKFormController
import io.skone.forms.formatter.SKTrimFormatter
import io.skone.forms.mask.SKInputMasks
import io.skone.forms.validation.SKEmailRule
import io.skone.forms.validation.SKRequiredRule
import io.skone.playground.catalog.CatalogId
import io.skone.playground.catalog.PlaygroundCatalog
import io.skone.theme.tokens.SKColorRole
import io.skone.theme.tokens.SKTypographyRole
import io.skone.ui.field.SKImeAction
import io.skone.ui.field.SKKeyboardType
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.widget.SKButtonView
import io.skone.xml.widget.SKScaffoldView
import io.skone.xml.widget.SKTextFieldView
import io.skone.xml.widget.SKTextView
import io.skone.xml.widget.SKTopAppBarView
import io.skone.component.accessibility.SKAccessibilityConfig

@Composable
fun GalleryScreen(
    onOpenWidget: (String) -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("Widget gallery")
        PlaygroundBody("Live previews of production SKOne widgets.")

        PlaygroundSectionTitle("SKText")
        SKText(
            text = "Display Large",
            appearance = SKAppearanceConfig.Text.copy(
                typographyRole = SKTypographyRole.DisplaySmall,
                contentColorRole = SKColorRole.Primary,
            ),
        )
        SKText(
            text = "Body on surface — token driven typography and color roles.",
            appearance = SKAppearanceConfig.Text,
        )
        Button(onClick = { onOpenWidget(CatalogId.SkText.route) }) {
            Text("Open SKText editor")
        }

        PlaygroundSectionTitle("SKTextField")
        var demo by remember { mutableStateOf("") }
        SKTextField(
            modifier = Modifier.fillMaxWidth(),
            value = demo,
            onValueChange = { demo = it },
            fieldId = "gallery_email",
            label = "Email",
            hint = "name@company.com",
            supportingText = "Gallery preview",
            required = true,
        )
        Button(onClick = { onOpenWidget(CatalogId.SkTextField.route) }) {
            Text("Open SKTextField editor")
        }

        PlaygroundSectionTitle("SKRadioGroup")
        var radio by remember { mutableStateOf<String?>("a") }
        SKRadioGroup(
            selectedValue = radio,
            onSelectedChange = { radio = it },
        ) {
            SKRadioButton(value = "a", label = "Option A")
            SKRadioButton(value = "b", label = "Option B")
        }
        Button(onClick = { onOpenWidget(CatalogId.SkRadioGroup.route) }) {
            Text("Open radio editor")
        }

        PlaygroundSectionTitle("SKChip")
        var chipSelected by remember { mutableStateOf(false) }
        SKChip(
            label = "Chip",
            selected = chipSelected,
            onClick = { chipSelected = !chipSelected },
        )
        Button(onClick = { onOpenWidget(CatalogId.SkChip.route) }) {
            Text("Open SKChip editor")
        }

        PlaygroundSectionTitle("SKSlider / Progress / Card")
        var galleryValue by remember { mutableStateOf(0.5f) }
        SKSlider(value = galleryValue, onValueChange = { galleryValue = it })
        SKProgressIndicator(progress = galleryValue)
        SKDivider()
        SKCard(onClick = {}) {
            SKText(text = "Open editors from catalog for full controls.")
        }
        Button(onClick = { onOpenWidget(CatalogId.SkCard.route) }) {
            Text("Open SKCard editor")
        }
    }
}

@Composable
fun SamplesScreen(
    onOpenSample: (String) -> Unit,
) {
    val spacing = skTheme.tokens.spacing
    val samples = PlaygroundCatalog.samples()
    val appExamples = samples.filter {
        it.id == CatalogId.AppListFilter ||
            it.id == CatalogId.AppFormValidation ||
            it.id == CatalogId.AppShellNav
    }
    val otherSamples = samples.filterNot { it in appExamples }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.sm.toDp()),
    ) {
        PlaygroundSectionTitle("Application Examples")
        PlaygroundBody(
            "Production-style screens composed from existing SKOne primitives. " +
                "These prove list/filter, form validation, and app chrome without new widgets.",
        )
        appExamples.forEach { entry ->
            CatalogEntryCard(entry = entry, onClick = { onOpenSample(entry.id.route) })
        }
        PlaygroundSectionTitle("Other samples")
        PlaygroundBody("Smaller integration recipes.")
        otherSamples.forEach { entry ->
            CatalogEntryCard(entry = entry, onClick = { onOpenSample(entry.id.route) })
        }
    }
}

@Composable
fun SampleDetailScreen(sampleId: String) {
    when (sampleId) {
        CatalogId.AppListFilter.route -> io.skone.playground.app.AppListSearchFilterSurface()
        CatalogId.AppFormValidation.route -> io.skone.playground.app.AppFormValidationSurface()
        CatalogId.AppShellNav.route -> io.skone.playground.app.AppShellNavigationSurface()
        CatalogId.SampleForm.route -> FormSampleContent()
        CatalogId.SampleXml.route -> XmlSampleContent()
        else -> PlaygroundBody("Unknown sample: $sampleId")
    }
}

@Composable
private fun FormSampleContent() {
    val spacing = skTheme.tokens.spacing
    val form = remember { SKFormController.create() }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    ProvideSKFormController(form) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(spacing.md.toDp()),
            verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
        ) {
            PlaygroundSectionTitle("Form + SKTextField")
            PlaygroundBody("Fields auto-register with SKFormController.")
            SKTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                fieldId = "email",
                label = "Email",
                hint = "name@company.com",
                required = true,
                rules = listOf(SKRequiredRule(), SKEmailRule()),
                formatter = SKTrimFormatter,
                keyboardType = SKKeyboardType.Email,
                imeAction = SKImeAction.Next,
            )
            SKTextField(
                modifier = Modifier.fillMaxWidth(),
                value = phone,
                onValueChange = { phone = it },
                fieldId = "phone",
                label = "Phone",
                hint = "(555) 123-4567",
                required = true,
                rules = listOf(SKRequiredRule()),
                mask = SKInputMasks.UsPhone,
                keyboardType = SKKeyboardType.Phone,
                imeAction = SKImeAction.Done,
            )
            Button(onClick = { form.validate() }, modifier = Modifier.fillMaxWidth()) {
                Text("Validate")
            }
            Button(onClick = { form.submit() }, modifier = Modifier.fillMaxWidth()) {
                Text("Submit")
            }
        }
    }
}

@Composable
private fun XmlSampleContent() {
    val spacing = skTheme.tokens.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.md.toDp()),
        verticalArrangement = Arrangement.spacedBy(spacing.md.toDp()),
    ) {
        PlaygroundSectionTitle("XML widgets")
        PlaygroundBody(
            "Public SK*View APIs via AndroidView. Full XML recipes for List/Form/Shell: " +
                "docs/architecture/XML_APPLICATION_RECIPES.md",
        )
        AndroidView(
            factory = { context ->
                SKThemeHelper.install(io.skone.theme.SKThemes.Light)
                android.widget.LinearLayout(context).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    val pad = (12 * resources.displayMetrics.density).toInt()
                    setPadding(pad, pad, pad, pad)

                    addView(
                        SKScaffoldView(context).apply {
                            setAccessibility(
                                SKAccessibilityConfig(testTag = "xml_recipe_scaffold"),
                            )
                            topBarContainer.addView(
                                SKTopAppBarView(context).apply {
                                    setBarTitle("XML recipe shell")
                                },
                            )
                            contentContainer.addView(
                                SKTextView(context).apply {
                                    setSkText("Scaffold + TopAppBar from public XML APIs")
                                },
                            )
                            contentContainer.addView(
                                SKTextFieldView(context).apply {
                                    setFieldId("xml_email")
                                    setLabel("Email (XML)")
                                    setHint("name@company.com")
                                    setRequired(true)
                                },
                            )
                            contentContainer.addView(
                                SKButtonView(context).apply {
                                    setSkText("Validate (host owns result)")
                                    setOnSkClickListener { /* host */ }
                                },
                            )
                        },
                        android.widget.LinearLayout.LayoutParams(
                            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                            (280 * resources.displayMetrics.density).toInt(),
                        ),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
