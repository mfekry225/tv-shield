package com.example.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object TvIntegrationManager {
    private const val CHANNEL_ID = "tv_shield_alerts"
    private const val CHANNEL_NAME = "TV Shield Alerts"

    fun showHealthAlert(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "System Health and Performance Alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }

    fun updateHomeScreenChannel(context: Context, ramUsageText: String) {
        // Log or print for debugging since the external TV Provider library is not syncing correctly in this environment
        // To enable home screen channels, ensure androidx.tvprovider:tvprovider is correctly added to build.gradle
    }
}
