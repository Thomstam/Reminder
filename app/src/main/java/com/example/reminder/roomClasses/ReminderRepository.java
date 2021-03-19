package com.example.reminder.roomClasses;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.reminder.Reminder;

import java.util.List;

public class ReminderRepository {

    private ReminderDao reminderDao;
    private LiveData<List<Reminder>> reminders;

    public ReminderRepository(Application application){
        ReminderDataBase db = ReminderDataBase.getDatabase(application);
        reminderDao = db.reminderDao();
        reminders = reminderDao.getAllReminders();
    }


    LiveData<List<Reminder>> getReminders(){
        return reminders;
    }

    void insert(Reminder reminder){
        ReminderDataBase.databaseWriteExecutor.execute(() ->{
          reminderDao.insert(reminder);
        });
    }
}
