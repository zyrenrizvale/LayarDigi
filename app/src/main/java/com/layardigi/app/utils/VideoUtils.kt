package com.layardigi.app.utils

fun extractYoutubeVideoId(url: String): String? {
    if (url.isBlank()) return null
    return try {
        if (url.contains("v=")) {
            url.substringAfter("v=").substringBefore("&")
        } else if (url.contains("youtu.be/")) {
            url.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
        } else if (url.contains("embed/")) {
            url.substringAfter("embed/").substringBefore("?")
        } else {
            url.trim()
        }
    } catch (e: Exception) {
        null
    }
}
