package com.project.labs.nutrigrow.ui.screen.about

sealed interface AboutBlock {
    data class Paragraph(val text: String) : AboutBlock
    data class Heading(val text: String) : AboutBlock
    data class Bullets(val items: List<String>) : AboutBlock
    data class Table(val headers: List<String>, val rows: List<List<String>>) : AboutBlock
    data class Formula(val lines: List<String>) : AboutBlock
    data class Note(val text: String) : AboutBlock
    data class Reference(val title: String, val usedFor: String) : AboutBlock
}

data class AboutTopic(
    val id: String,
    val group: String,
    val title: String,
    val summary: String,
    val adminOnly: Boolean = false,
    val blocks: List<AboutBlock>,
)

const val ABOUT_GROUP_APP = "Tentang Aplikasi"
const val ABOUT_GROUP_FEATURE = "Fitur"
const val ABOUT_GROUP_METHOD = "Cara Aplikasi Menghitung"
const val ABOUT_GROUP_SOURCE = "Sumber dan Rujukan"
const val ABOUT_GROUP_LIMIT = "Batasan dan Privasi"

val ABOUT_GROUP_ORDER = listOf(
    ABOUT_GROUP_APP,
    ABOUT_GROUP_FEATURE,
    ABOUT_GROUP_METHOD,
    ABOUT_GROUP_SOURCE,
    ABOUT_GROUP_LIMIT,
)

fun aboutTopicById(id: String?): AboutTopic? =
    ABOUT_TOPICS.firstOrNull { it.id == id }

private fun AboutBlock.searchableText(): String = when (this) {
    is AboutBlock.Paragraph -> text
    is AboutBlock.Heading -> text
    is AboutBlock.Bullets -> items.joinToString(" ")
    is AboutBlock.Table -> (headers + rows.flatten()).joinToString(" ")
    is AboutBlock.Formula -> lines.joinToString(" ")
    is AboutBlock.Note -> text
    is AboutBlock.Reference -> "$title $usedFor"
}

fun aboutTopics(role: String, query: String): List<AboutTopic> {
    val visible = ABOUT_TOPICS.filter { !it.adminOnly || role == "Admin" }
    val keyword = query.trim().lowercase()
    if (keyword.isBlank()) return visible

    return visible.filter { topic ->
        val haystack = buildString {
            append(topic.title).append(' ')
            append(topic.summary).append(' ')
            append(topic.group).append(' ')
            topic.blocks.forEach { append(it.searchableText()).append(' ') }
        }
        haystack.lowercase().contains(keyword)
    }
}
