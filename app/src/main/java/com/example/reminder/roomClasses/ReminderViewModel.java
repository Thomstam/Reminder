package com.example.reminder.roomClasses;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.reminder.Reminder;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ReminderViewModel extends AndroidViewModel {

    private ReminderRepository reminderRepository;

    private final LiveData<List<Reminder>> reminders;


    public ReminderViewModel(@NonNull @NotNull Application application) {
        super(application);
        reminderRepository = new ReminderRepository(application);
        reminders = reminderRepository.getReminders();
    }

    public LiveData<List<Reminder>> getReminders() { return reminders; }

    public void insert(Reminder reminder) { reminderRepository.insert(reminder); }
}
