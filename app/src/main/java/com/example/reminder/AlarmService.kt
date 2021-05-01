package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle

class AlarmService(private  val context: Context, val reminder: Reminder) {

    private val alarmManager : AlarmManager? =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    fun setExactAlarm(){
        setTheAlarm(
                getPendingIntent(
                        getIntent().apply {}
                )
        )
    }

    private fun setTheAlarm(pendingIntent: PendingIntent){
        alarmManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.timeToNotifyForTheReminder,
                        pendingIntent
                )
            }else{
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminder.timeToNotifyForTheReminder,
                        pendingIntent
                )
            }
        }
    }
    private fun getIntent() : Intent {
        val intent = Intent(context, NotificationReceiver::class.java)
        val bundle = Bundle()
        bundle.putParcelable("Reminder", reminder)
        intent.putExtra("bundle", bundle)
        return intent
    }

    private fun getPendingIntent(intent: Intent) :PendingIntent =
            PendingIntent.getBroadcast(
                    context,
                    reminder.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )
}