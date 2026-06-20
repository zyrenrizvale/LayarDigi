package com.layardigi.app.utils

fun extractYoutubeVideoId(url: String): String? {
    if (url.isBlank()) return null
    val trimmed = url.trim()
    
    // If it's already an 11-character video ID
    if (trimmed.length == 11 && trimmed.all { it.isLetterOrDigit() || it == '-' || it == '_' }) {
        return trimmed
    }
    
    val regex = "^(?:https?:\\/\\/)?(?:www\\.|m\\.)?(?:youtube\\.com\\/(?:watch\\?.*v=|embed\\/|v\\/|shorts\\/)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})".toRegex(RegexOption.IGNORE_CASE)
    val matchResult = regex.find(trimmed)
    
    return matchResult?.groupValues?.get(1) ?: run {
        // Fallback for query param `v=` just in case
        if (trimmed.contains("v=")) {
            val vIndex = trimmed.indexOf("v=") + 2
            val end = trimmed.indexOf('&', vIndex).let { if (it == -1) trimmed.length else it }
            val id = trimmed.substring(vIndex, end)
            if (id.length == 11) id else null
        } else {
            null
        }
    }
}
