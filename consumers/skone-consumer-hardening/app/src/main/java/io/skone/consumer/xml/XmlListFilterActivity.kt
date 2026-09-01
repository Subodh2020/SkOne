package io.skone.consumer.xml

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.consumer.ConsumerLogic
import io.skone.consumer.Filters
import io.skone.consumer.ListPhase
import io.skone.consumer.PeopleTab
import io.skone.theme.SKThemes
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.overlay.SKSegmentItem
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.widget.SKBottomSheetView
import io.skone.xml.widget.SKButtonView
import io.skone.xml.widget.SKCheckboxView
import io.skone.xml.widget.SKEmptyStateView
import io.skone.xml.widget.SKFabView
import io.skone.xml.widget.SKListItemView
import io.skone.xml.widget.SKProgressIndicatorView
import io.skone.xml.widget.SKScaffoldView
import io.skone.xml.widget.SKSearchBarView
import io.skone.xml.widget.SKSectionHeaderView
import io.skone.xml.widget.SKSegmentedButtonView
import io.skone.xml.widget.SKSnackbarView
import io.skone.xml.widget.SKTabRowView
import io.skone.xml.widget.SKTopAppBarView
import io.skone.xml.widget.SKTextView

/**
 * XML consumer path A — List + Search + Filter.
 * Host owns phase, query, tab, filters, sheet, snackbar.
 */
class XmlListFilterActivity : AppCompatActivity() {
    private val runtime = SKComponentRuntime.create()
    private var phase = ListPhase.Loading
    private var query = ""
    private var tab = PeopleTab.All
    private var draft = Filters()
    private var applied = Filters()
    private var selectedId: String? = null

    private lateinit var contentColumn: LinearLayout
    private lateinit var snack: SKSnackbarView
    private lateinit var sheet: SKBottomSheetView
    private lateinit var unreadBox: SKCheckboxView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SKThemeHelper.install(SKThemes.Light)

        val scaffold = SKScaffoldView(this)
        val topChrome = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val topBar = SKTopAppBarView(this).apply {
            setBarTitle("Directory")
            setNavigationIcon(SKIconKey("skone.icon.back", contentDescription = "Back")) { finish() }
            setActionIcon(SKIconKey("skone.icon.filter", contentDescription = "Filters")) {
                sheet.show()
            }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_topbar"))
            bind(runtime)
        }
        val tabs = SKTabRowView(this).apply {
            setTabItems(PeopleTab.entries.map { SKTabItem(it.id, it.label) })
            setSelectedTabId(tab.id)
            setOnSelectListener { id ->
                tab = PeopleTab.entries.first { it.id == id }
                renderBody()
            }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_tabs"))
            bind(runtime)
        }
        topChrome.addView(topBar)
        topChrome.addView(tabs)
        scaffold.topBarContainer.addView(topChrome)

        contentColumn = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val scroll = ScrollView(this).apply {
            addView(
                contentColumn,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
        scaffold.contentContainer.addView(
            scroll,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )

        snack = SKSnackbarView(this).apply {
            setSnackbarVisible(false)
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_snack"))
            bind(runtime)
        }
        scaffold.snackbarContainer.addView(snack)

        val fab = SKFabView(this).apply {
            setIcon(SKIconKey("skone.icon.add", contentDescription = "Open filters"))
            setOnFabClickListener { sheet.show() }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_fab"))
            bind(runtime)
        }
        scaffold.fabContainer.addView(fab)

        unreadBox = SKCheckboxView(this).apply {
            setLabel("Unread only")
            setChecked(draft.unreadOnly)
            setOnCheckedChangeListener { draft = draft.copy(unreadOnly = it) }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_unread"))
            bind(runtime)
        }
        val archived = SKCheckboxView(this).apply {
            setLabel("Include archived (unavailable)")
            setChecked(false)
            setControlEnabled(false)
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_archived"))
            bind(runtime)
        }
        val segments = SKSegmentedButtonView(this).apply {
            setSegmentItems(
                listOf(
                    SKSegmentItem("all", "All"),
                    SKSegmentItem("team", "Team"),
                    SKSegmentItem("starred", "Starred", enabled = false),
                ),
            )
            setSelectedSegmentId(tab.id)
            setOnSelectListener { id ->
                if (id != "starred") {
                    tab = PeopleTab.entries.first { it.id == id }
                    tabs.setSelectedTabId(tab.id)
                }
            }
            bind(runtime)
        }
        val sheetBody = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(segments)
            addView(unreadBox)
            addView(archived)
            addView(
                SKTextView(this@XmlListFilterActivity).apply {
                    setSkText("Starred segment stays disabled on purpose.")
                    bind(runtime)
                },
            )
        }
        sheet = SKBottomSheetView(this).apply {
            setSheetTitle("Filters")
            setPrimaryAction("Apply", enabled = true) {
                applied = draft
                dismiss()
                snack.setMessage("Filters applied")
                snack.setSnackbarVisible(true)
                renderBody()
            }
            setSecondaryAction("Reset") {
                draft = Filters()
                applied = Filters()
                unreadBox.setChecked(false)
                dismiss()
                renderBody()
            }
            setSheetContent(sheetBody)
            setOnDismissListener { draft = applied; unreadBox.setChecked(applied.unreadOnly) }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_sheet"))
            bind(runtime)
        }

        setContentView(scaffold)
        window.decorView.postDelayed({
            phase = ListPhase.Ready
            renderBody()
        }, 400)
        renderBody()
    }

    private fun reload(error: Boolean) {
        phase = ListPhase.Loading
        renderBody()
        window.decorView.postDelayed({
            phase = if (error) ListPhase.Error else ListPhase.Ready
            renderBody()
        }, 350)
    }

    private fun renderBody() {
        contentColumn.removeAllViews()
        val search = SKSearchBarView(this).apply {
            setQuery(query)
            setPlaceholder("Search people")
            setOnQueryChangeListener {
                query = it
                renderBody()
            }
            setOnClearListener {
                query = ""
                renderBody()
            }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_search"))
            bind(runtime)
        }
        contentColumn.addView(search)

        val rows = if (phase == ListPhase.Ready) {
            ConsumerLogic.filter(query, tab, applied)
        } else {
            emptyList()
        }
        contentColumn.addView(
            SKSectionHeaderView(this).apply {
                setHeaderTitle("People")
                setSupportingText("${rows.size} visible")
                setAccessibility(SKAccessibilityConfig(testTag = "xml_list_header"))
                bind(runtime)
            },
        )

        when (phase) {
            ListPhase.Loading -> {
                contentColumn.addView(
                    SKProgressIndicatorView(this).apply {
                        setIndeterminateMode(true)
                        setAccessibility(
                            SKAccessibilityConfig(
                                contentDescription = "Loading",
                                testTag = "xml_list_loading",
                            ),
                        )
                        bind(runtime)
                    },
                )
            }
            ListPhase.Error -> {
                contentColumn.addView(
                    SKEmptyStateView(this).apply {
                        setTitle("Couldn’t load people")
                        setDescription("Try again.")
                        setPrimaryAction("Retry") { reload(error = false) }
                        setAccessibility(SKAccessibilityConfig(testTag = "xml_list_error"))
                        bind(runtime)
                    },
                )
            }
            ListPhase.Ready -> {
                if (rows.isEmpty()) {
                    contentColumn.addView(
                        SKEmptyStateView(this).apply {
                            setTitle("No people match")
                            setPrimaryAction("Clear search") {
                                query = ""
                                renderBody()
                            }
                            setAccessibility(SKAccessibilityConfig(testTag = "xml_list_empty"))
                            bind(runtime)
                        },
                    )
                } else {
                    rows.forEach { person ->
                        contentColumn.addView(
                            SKListItemView(this).apply {
                                setHeadline(person.name)
                                setSupportingText(person.role)
                                setSelectedState(selectedId == person.id)
                                setOnSkClickListener {
                                    selectedId = person.id
                                    renderBody()
                                }
                                setAccessibility(
                                    SKAccessibilityConfig(testTag = "xml_list_row_${person.id}"),
                                )
                                bind(runtime)
                            },
                        )
                    }
                }
                contentColumn.addView(
                    SKButtonView(this).apply {
                        setSkText("Simulate error")
                        setOnSkClickListener { reload(error = true) }
                        setAccessibility(SKAccessibilityConfig(testTag = "xml_list_force_error"))
                        bind(runtime)
                    },
                )
            }
        }
    }
}
