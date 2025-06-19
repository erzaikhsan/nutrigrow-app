package com.project.labs.nutrigrow.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.event.DetailEventActivity

fun showNotification(context: Context, title: String, message: String, eventId: String) {
    val channelId = "event_reminder_channel"
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "Kegiatan Hari Ini", NotificationManager.IMPORTANCE_HIGH)
        manager.createNotificationChannel(channel)
    }

    // Intent ke DetailActivity dengan data eventId
    val intent = Intent(context, DetailEventActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("id", eventId)
        putExtra("from_notification", true)
    }

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_medical_information_24)
        .setContentTitle(title)
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    manager.notify(1001, notification)
}