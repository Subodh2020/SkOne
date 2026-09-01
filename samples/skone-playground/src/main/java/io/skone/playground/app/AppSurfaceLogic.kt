package io.skone.playground.app

/**
 * Pure helpers for Application Examples. Host screens own Compose state;
 * these functions stay UI-free so unit tests can cover flows without a device.
 */

enum class ListLoadPhase {
    Loading,
    Ready,
    Error,
}

enum class PeopleTab(val id: String, val label: String) {
    All("all", "All"),
    Team("team", "Team"),
    Starred("starred", "Starred"),
}

data class DirectoryPerson(
    val id: String,
    val name: String,
    val role: String,
    val team: Boolean,
    val starred: Boolean,
    val unread: Boolean,
)

data class DirectoryFilters(
    val unreadOnly: Boolean = false,
    /** Intentionally unsupported / disabled in the filter sheet. */
    val includeArchived: Boolean = false,
)

data class FormFieldSnapshot(
    val displayName: String = "",
    val email: String = "",
    val title: String = "",
    val notificationsEnabled: Boolean = true,
    val marketingOptIn: Boolean = false,
)

sealed class FormUiStatus {
    data object Idle : FormUiStatus()
    data object Submitting : FormUiStatus()
    data object Success : FormUiStatus()
    data class Failure(val message: String) : FormUiStatus()
}

enum class ShellDestination(val id: String, val label: String) {
    Home("home", "Home"),
    Activity("activity", "Activity"),
    Settings("settings", "Settings"),
}

object AppSurfaceLogic {
    val sampleDirectory: List<DirectoryPerson> = listOf(
        DirectoryPerson("1", "Ada Lovelace", "Engineer", team = true, starred = true, unread = true),
        DirectoryPerson("2", "Grace Hopper", "Admiral", team = true, starred = false, unread = false),
        DirectoryPerson("3", "Alan Turing", "Researcher", team = false, starred = true, unread = true),
        DirectoryPerson("4", "Katherine Johnson", "Analyst", team = true, starred = false, unread = false),
        DirectoryPerson("5", "Guest User", "Visitor", team = false, starred = false, unread = false),
    )

    fun filterDirectory(
        people: List<DirectoryPerson>,
        query: String,
        tab: PeopleTab,
        filters: DirectoryFilters,
    ): List<DirectoryPerson> {
        val q = query.trim()
        return people.asSequence()
            .filter { person ->
                when (tab) {
                    PeopleTab.All -> true
                    PeopleTab.Team -> person.team
                    PeopleTab.Starred -> person.starred
                }
            }
            .filter { !filters.unreadOnly || it.unread }
            .filter {
                q.isEmpty() ||
                    it.name.contains(q, ignoreCase = true) ||
                    it.role.contains(q, ignoreCase = true)
            }
            .toList()
    }

    fun isDisplayNameValid(value: String): Boolean = value.trim().length >= 2

    fun isEmailValid(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.contains("@") && trimmed.substringAfter("@").contains(".")
    }

    fun canSubmitProfile(snapshot: FormFieldSnapshot): Boolean =
        isDisplayNameValid(snapshot.displayName) && isEmailValid(snapshot.email)

    fun shellTitle(destination: ShellDestination): String = when (destination) {
        ShellDestination.Home -> "Home"
        ShellDestination.Activity -> "Activity"
        ShellDestination.Settings -> "Settings"
    }
}
