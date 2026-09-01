package io.skone.consumer.xml

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import io.skone.component.accessibility.SKAccessibilityConfig
import io.skone.component.framework.SKComponentRuntime
import io.skone.component.framework.icon.SKIconKey
import io.skone.consumer.ShellDest
import io.skone.theme.SKThemes
import io.skone.ui.chrome.SKTabItem
import io.skone.ui.navigation.SKNavigationItem
import io.skone.ui.overlay.SKMenuItem
import io.skone.xml.theme.SKThemeHelper
import io.skone.xml.widget.SKBadgeView
import io.skone.xml.widget.SKBottomAppBarView
import io.skone.xml.widget.SKCardView
import io.skone.xml.widget.SKDropdownMenuView
import io.skone.xml.widget.SKFabView
import io.skone.xml.widget.SKIconButtonView
import io.skone.xml.widget.SKListItemView
import io.skone.xml.widget.SKNavigationBarView
import io.skone.xml.widget.SKScaffoldView
import io.skone.xml.widget.SKSectionHeaderView
import io.skone.xml.widget.SKSnackbarView
import io.skone.xml.widget.SKTabRowView
import io.skone.xml.widget.SKTextView
import io.skone.xml.widget.SKTopAppBarView

/**
 * XML consumer path C — App Shell + Navigation.
 * Host owns destination; no SKOne navigation framework.
 */
class XmlShellActivity : AppCompatActivity() {
    private val runtime = SKComponentRuntime.create()
    private var destination = ShellDest.Home
    private var homeTab = "feed"

    private lateinit var topBar: SKTopAppBarView
    private lateinit var contentColumn: LinearLayout
    private lateinit var snack: SKSnackbarView
    private lateinit var bottomStack: LinearLayout
    private lateinit var fab: SKFabView
    private lateinit var menu: SKDropdownMenuView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SKThemeHelper.install(SKThemes.Light)

        val scaffold = SKScaffoldView(this)
        menu = SKDropdownMenuView(this).apply {
            setMenuItems(
                listOf(
                    SKMenuItem("refresh", "Refresh"),
                    SKMenuItem("help", "Help"),
                    SKMenuItem("out", "Sign out", enabled = false),
                ),
            )
            setOnSelectListener {
                snack.setMessage("Menu: $it")
                snack.setSnackbarVisible(true)
            }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_shell_menu"))
            bind(runtime)
        }
        topBar = SKTopAppBarView(this).apply {
            setBarTitle(destination.label)
            setNavigationIcon(SKIconKey("skone.icon.back", contentDescription = "Back")) { finish() }
            setActionIcon(SKIconKey("skone.icon.more", contentDescription = "More")) {
                menu.showAsDropDown(this)
            }
            setAccessibility(SKAccessibilityConfig(testTag = "xml_shell_topbar"))
            bind(runtime)
        }
        scaffold.topBarContainer.addView(topBar)

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
            setAccessibility(SKAccessibilityConfig(testTag = "xml_shell_snack"))
            bind(runtime)
        }
        scaffold.snackbarContainer.addView(snack)

        fab = SKFabView(this).apply {
            setIcon(SKIconKey("skone.icon.add", contentDescription = "Compose"))
            setOnFabClickListener {
                snack.setMessage("Compose")
                snack.setSnackbarVisible(true)
            }
            bind(runtime)
        }
        scaffold.fabContainer.addView(fab)

        bottomStack = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scaffold.bottomBarContainer.addView(bottomStack)

        setContentView(scaffold)
        renderDestination()
    }

    private fun renderDestination() {
        topBar.setBarTitle(destination.label)
        contentColumn.removeAllViews()
        bottomStack.removeAllViews()
        fab.visibility = if (destination == ShellDest.Home) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }

        if (destination == ShellDest.Home) {
            val bottomBar = SKBottomAppBarView(this).apply {
                setLeading(
                    SKIconButtonView(this@XmlShellActivity).apply {
                        setIcon(SKIconKey("skone.icon.search", contentDescription = "Search"))
                        setOnSkClickListener {
                            snack.setMessage("Search")
                            snack.setSnackbarVisible(true)
                        }
                        bind(runtime)
                    },
                )
                setBarContent(
                    SKTextView(this@XmlShellActivity).apply {
                        setSkText("Quick actions")
                        bind(runtime)
                    },
                )
                bind(runtime)
            }
            bottomStack.addView(bottomBar)
        }

        bottomStack.addView(
            SKNavigationBarView(this).apply {
                setNavigationItems(
                    ShellDest.entries.map {
                        SKNavigationItem(
                            it.id,
                            it.label,
                            SKIconKey("skone.icon.nav", contentDescription = it.label),
                        )
                    },
                )
                setSelectedItemId(destination.id)
                setOnSelectListener { id ->
                    destination = ShellDest.entries.first { it.id == id }
                    renderDestination()
                }
                setAccessibility(SKAccessibilityConfig(testTag = "xml_shell_nav"))
                bind(runtime)
            },
        )

        when (destination) {
            ShellDest.Home -> {
                contentColumn.addView(
                    SKTabRowView(this).apply {
                        setTabItems(
                            listOf(SKTabItem("feed", "Feed"), SKTabItem("following", "Following")),
                        )
                        setSelectedTabId(homeTab)
                        setOnSelectListener { homeTab = it }
                        bind(runtime)
                    },
                )
                contentColumn.addView(
                    SKCardView(this).apply {
                        setOnSkClickListener {
                            snack.setMessage("Card")
                            snack.setSnackbarVisible(true)
                        }
                        bind(runtime)
                        addView(
                            SKTextView(this@XmlShellActivity).apply {
                                setSkText("Welcome · $homeTab")
                                bind(runtime)
                            },
                        )
                    },
                )
                contentColumn.addView(
                    SKSectionHeaderView(this).apply {
                        setHeaderTitle("Highlights")
                        bind(runtime)
                    },
                )
                contentColumn.addView(
                    SKListItemView(this).apply {
                        setHeadline("Consumer hardening")
                        setSupportingText("External Maven app")
                        bind(runtime)
                    },
                )
                contentColumn.addView(
                    SKBadgeView(this).apply {
                        setBadgeText("2")
                        bind(runtime)
                    },
                )
            }
            ShellDest.Activity -> {
                contentColumn.addView(
                    SKSectionHeaderView(this).apply {
                        setHeaderTitle("Activity")
                        bind(runtime)
                    },
                )
                listOf("Filter applied", "Profile saved").forEach { msg ->
                    contentColumn.addView(
                        SKListItemView(this).apply {
                            setHeadline(msg)
                            setSupportingText("Just now")
                            bind(runtime)
                        },
                    )
                }
            }
            ShellDest.Settings -> {
                contentColumn.addView(
                    SKSectionHeaderView(this).apply {
                        setHeaderTitle("Settings")
                        bind(runtime)
                    },
                )
                contentColumn.addView(
                    SKListItemView(this).apply {
                        setHeadline("Close shell example")
                        setOnSkClickListener { finish() }
                        bind(runtime)
                    },
                )
            }
        }
    }
}
