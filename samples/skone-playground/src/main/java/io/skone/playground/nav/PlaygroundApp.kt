package io.skone.playground.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.skone.playground.catalog.CatalogKind
import io.skone.playground.ui.CatalogScreen
import io.skone.playground.ui.DocArticleScreen
import io.skone.playground.ui.DocsScreen
import io.skone.playground.ui.GalleryScreen
import io.skone.playground.ui.HomeScreen
import io.skone.playground.ui.SampleDetailScreen
import io.skone.playground.ui.SamplesScreen
import io.skone.playground.ui.SearchScreen
import io.skone.playground.ui.WidgetDetailScreen
import io.skone.theme.SKThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundApp(
    themeMode: SKThemeMode,
    onThemeModeChange: (SKThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = PlaygroundTab.entries.any { it.route == currentRoute }

    fun openCatalogEntry(id: String, kind: CatalogKind) {
        when (kind) {
            CatalogKind.Widget -> navController.navigate(PlaygroundRoutes.widget(id))
            CatalogKind.Sample -> navController.navigate(PlaygroundRoutes.sample(id))
            CatalogKind.Doc -> navController.navigate(PlaygroundRoutes.doc(id))
            CatalogKind.Framework -> navController.navigate(PlaygroundRoutes.doc("doc-overview"))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topBarTitle(currentRoute)) },
                navigationIcon = {
                    if (!showBottomBar) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Text("←")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(PlaygroundRoutes.SEARCH) }) {
                        Text("⌕")
                    }
                },
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    PlaygroundTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentRoute == tab.route,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(tab.label.take(1)) },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = PlaygroundRoutes.HOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(PlaygroundRoutes.HOME) {
                HomeScreen(
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    onOpenCatalog = { navController.navigate(PlaygroundRoutes.CATALOG) },
                    onOpenGallery = { navController.navigate(PlaygroundRoutes.GALLERY) },
                    onOpenSearch = { navController.navigate(PlaygroundRoutes.SEARCH) },
                    onOpenDocs = { navController.navigate(PlaygroundRoutes.DOCS) },
                    onOpenSamples = { navController.navigate(PlaygroundRoutes.SAMPLES) },
                    onOpenWidget = { navController.navigate(PlaygroundRoutes.widget(it)) },
                )
            }
            composable(PlaygroundRoutes.CATALOG) {
                CatalogScreen(onOpenEntry = ::openCatalogEntry)
            }
            composable(PlaygroundRoutes.GALLERY) {
                GalleryScreen(onOpenWidget = { navController.navigate(PlaygroundRoutes.widget(it)) })
            }
            composable(PlaygroundRoutes.SEARCH) {
                SearchScreen(onOpenEntry = ::openCatalogEntry)
            }
            composable(PlaygroundRoutes.DOCS) {
                DocsScreen(onOpenDoc = { navController.navigate(PlaygroundRoutes.doc(it)) })
            }
            composable(PlaygroundRoutes.SAMPLES) {
                SamplesScreen(onOpenSample = { navController.navigate(PlaygroundRoutes.sample(it)) })
            }
            composable(
                route = PlaygroundRoutes.WIDGET,
                arguments = listOf(navArgument("widgetId") { type = NavType.StringType }),
            ) { entry ->
                WidgetDetailScreen(widgetId = entry.arguments?.getString("widgetId").orEmpty())
            }
            composable(
                route = PlaygroundRoutes.DOC_ARTICLE,
                arguments = listOf(navArgument("docId") { type = NavType.StringType }),
            ) { entry ->
                DocArticleScreen(docId = entry.arguments?.getString("docId").orEmpty())
            }
            composable(
                route = PlaygroundRoutes.SAMPLE_DETAIL,
                arguments = listOf(navArgument("sampleId") { type = NavType.StringType }),
            ) { entry ->
                SampleDetailScreen(sampleId = entry.arguments?.getString("sampleId").orEmpty())
            }
        }
    }
}

private fun topBarTitle(route: String?): String = when {
    route == null -> "SKOne"
    route.startsWith("widget/") -> "Widget"
    route.startsWith("docs/") && route != PlaygroundRoutes.DOCS -> "Docs"
    route.startsWith("samples/") && route != PlaygroundRoutes.SAMPLES -> "Sample"
    route == PlaygroundRoutes.SEARCH -> "Search"
    else -> PlaygroundTab.entries.firstOrNull { it.route == route }?.label ?: "SKOne"
}
