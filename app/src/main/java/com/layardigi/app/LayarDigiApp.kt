package com.layardigi.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.layardigi.app.data.repository.AuthRepository
import com.layardigi.app.data.repository.Role

class LayarDigiApp : Application() {
    companion object {
        lateinit var instance: LayarDigiApp
            private set

        fun getPrefs() = instance.getSharedPreferences("layardigi_session", Context.MODE_PRIVATE)

        const val NOTIF_CHANNEL_ID = "layardigi_channel"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Restore session from SharedPreferences
        val prefs = getPrefs()
        val username = prefs.getString("username", null)
        val roleStr = prefs.getString("role", null)
        if (username != null && roleStr != null) {
            try {
                AuthRepository.login(username, Role.valueOf(roleStr))
            } catch (_: Exception) {}
        }

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIF_CHANNEL_ID,
                "LayarDigi Notifikasi",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pemesanan tiket dan pengumuman"
            }
            val notifManager = getSystemService(NotificationManager::class.java)
            notifManager?.createNotificationChannel(channel)
        }
    }
}
