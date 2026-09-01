package io.skone.consumer

/**
 * Host-owned models for consumer Application Examples.
 * No SKOne dependency — unit-testable without Maven artifacts.
 */
enum class ListPhase { Loading, Ready, Error }

enum class PeopleTab(val id: String, val label: String) {
    All("all", "All"),
    Team("team", "Team"),
    Starred("starred", "Starred"),
}

data class Person(
    val id: String,
    val name: String,
    val role: String,
    val team: Boolean,
    val starred: Boolean,
    val unread: Boolean,
)

data class Filters(val unreadOnly: Boolean = false)

data class ProfileDraft(
    val displayName: String = "",
    val email: String = "",
)

sealed class FormStatus {
    data object Idle : FormStatus()
    data object Submitting : FormStatus()
    data object Success : FormStatus()
    data class Failure(val message: String) : FormStatus()
}

enum class ShellDest(val id: String, val label: String) {
    Home("home", "Home"),
    Activity("activity", "Activity"),
    Settings("settings", "Settings"),
}

object ConsumerLogic {
    val people: List<Person> = listOf(
        Person("1", "Ada Lovelace", "Engineer", team = true, starred = true, unread = true),
        Person("2", "Grace Hopper", "Admiral", team = true, starred = false, unread = false),
        Person("3", "Alan Turing", "Researcher", team = false, starred = true, unread = true),
        Person("4", "Katherine Johnson", "Analyst", team = true, starred = false, unread = false),
    )

    fun filter(
        query: String,
        tab: PeopleTab,
        filters: Filters,
        source: List<Person> = people,
    ): List<Person> {
        val q = query.trim()
        return source.asSequence()
            .filter {
                when (tab) {
                    PeopleTab.All -> true
                    PeopleTab.Team -> it.team
                    PeopleTab.Starred -> it.starred
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

    fun canSubmit(draft: ProfileDraft): Boolean =
        draft.displayName.trim().length >= 2 &&
            draft.email.contains("@") &&
            draft.email.substringAfter("@").contains(".")
}
