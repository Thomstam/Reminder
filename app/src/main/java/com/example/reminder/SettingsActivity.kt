package com.example.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {

    private val defaultNotificationChannel = "default"
    private var remoteViewsSmall: RemoteViews? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setBackButton()

        getPreferences()
    }

    private fun setBackButton(){
        val backButton = findViewById<ImageButton>(R.id.BackButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun getPreferences(){
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        sharedPref.getBoolean("sound", true)
    }

    fun getNotification(reminder: Reminder): Notification{
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(this, defaultNotificationChannel)
        builder.setContentTitle("Time To Start Getting Ready")
        builder.setSmallIcon(R.drawable.ic_notification_alert)
        setRemoteViewsSmall(reminder)
        builder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        builder.setCustomContentView(remoteViewsSmall)
        builder.setAutoCancel(true)
        builder.setChannelId(MainActivity.NOTIFICATION_CHANNEL_ID)
        return builder.build()
    }

    fun scheduleNotification(notification: Notification, delay: Long) {
        val notificationIntent = Intent(this@SettingsActivity, NotificationReceiver::class.java)
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification)
        @SuppressLint("UnspecifiedImmutableFlag") val pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = (getSystemService(ALARM_SERVICE) as AlarmManager)
        alarmManager[AlarmManager.RTC_WAKEUP, delay] = pendingIntent

    }

    private fun setRemoteViewsSmall(reminder: Reminder) {
        remoteViewsSmall = RemoteViews(packageName, R.layout.notification_small)
        remoteViewsSmall!!.setTextViewText(R.id.nameOfNotificationSmall, reminder.nameOfTheReminder)
        remoteViewsSmall!!.setTextViewText(R.id.notificationTimeSmall, reminder.timeOfTheReminder)
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}