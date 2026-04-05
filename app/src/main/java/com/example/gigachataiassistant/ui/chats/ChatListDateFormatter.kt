package com.example.gigachataiassistant.ui.chats

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object ChatListDateFormatter {
    private val formatter = DateTimeFormatter.ofPattern(
        "dd MMMM yyyy | HH:mm",
        Locale.forLanguageTag("ru"),
    )

    fun format(epochMillis: Long): String =
        Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .format(formatter)
}
