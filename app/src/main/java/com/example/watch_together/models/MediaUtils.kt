package com.example.watch_together.models

import androidx.compose.ui.graphics.Color
import com.example.watch_together.ui.theme.Green
import com.example.watch_together.ui.theme.Yellow
import java.util.Locale

// Определяем цвет по рейтингу
fun getRatingColor(rating: Double): Color {
    return when {
        rating >= 7.0 -> Green
        rating >= 5.0 -> Yellow
        else -> Color.Red
    }
}

fun formatRuntime(runtimeMinutes: Int): String {
    val hours = runtimeMinutes / 60
    val minutes = runtimeMinutes % 60
    return buildString {
        if (hours > 0) append("$hours ч ")
        if (minutes > 0) append("$minutes мин")
    }.trim()
}

fun formatVoteCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format(Locale.US, "%.1fM", count / 1_000_000f)
        count >= 1_000     -> String.format(Locale.US,"%.1fK", count / 1_000f)
        else -> count.toString()
    }
}

//fun formatGenres(genres: List<Genre>?): String? {
//    return genres
//        ?.asSequence()
//        ?.map { it.name }
//        ?.filter { it.isNotBlank() }
//        ?.map { it.lowercase().replaceFirstChar { ch -> ch.uppercaseChar() } }
//        ?.distinct()
//        ?.joinToString(", ")
//}


