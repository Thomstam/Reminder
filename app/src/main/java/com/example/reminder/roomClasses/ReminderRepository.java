package com.example.reminder.roomClasses;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.reminder.Reminder;

import java.util.List;

public class ReminderRepository {

    private final ReminderDao reminderDao;
    private final LiveData<List<Reminder>> reminders;

    public ReminderRepository(Application application) {
        ReminderDataBase reminderDataBase = ReminderDataBase.getINSTANCE(application);
        reminderDao = reminderDataBase.reminderDao();
        reminders = reminderDao.getAllReminders();
    }

    public void insert(Reminder reminder) {
        insertNewReminder(reminder);
    }

    public void update(Reminder reminder) {
        updateReminder(reminder);
    }

    public void delete(Reminder reminder) {
        deleteReminder(reminder);
    }

    public void deleteAllReminders() {
        deleteAllRemindersTask();
    }

    public LiveData<List<Reminder>> getAllReminders() {
        return reminders;
    }

    private void insertNewReminder(Reminder reminder){
        new Thread(() -> reminderDao.insert(reminder)).start();
    }

    private void updateReminder(Reminder reminder){
        new Thread(() -> reminderDao.updateReminder(reminder)).start();
    }

    private void deleteReminder(Reminder reminder){
        new Thread(() -> reminderDao.deleteReminders(reminder)).start();
    }

    private void deleteAllRemindersTask(){
        new Thread(reminderDao::nukeTable).start();
    }


}
