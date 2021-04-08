package com.example.reminder

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


@Suppress("NAME_SHADOWING")
class NotificationReceiver : BroadcastReceiver() {

    private val channelId = "Default Channel"
    private val notificationId = 101



    override fun onReceive(context: Context, intent: Intent) {

        val reminder = intent.getParcelableExtra<Reminder>("Reminder")
        val intent = Intent(context , MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,0, intent, 0)

        val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification_alert)
                .setContentTitle(reminder?.nameOfTheReminder)
                .setContentText("Start Getting Ready")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }
}