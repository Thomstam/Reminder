package com.example.reminder.roomClasses;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.reminder.Reminder;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ReminderViewModel extends AndroidViewModel {

    private final ReminderRepository reminderRepository;
    private final LiveData<List<Reminder>> reminders;

     public ReminderViewModel(@NonNull @NotNull Application application) {
        super(application);
        reminderRepository = new ReminderRepository(application);
        reminders = reminderRepository.getAllReminders();
    }

    public void insert(Reminder reminder){
         reminderRepository.insert(reminder);
    }

    public void update(Reminder reminder){
        reminderRepository.update(reminder);
    }

    public void delete(Reminder reminder){
        reminderRepository.delete(reminder);
    }

    public void deleteAllReminders(){
        reminderRepository.deleteAllReminders();
    }

    public LiveData<List<Reminder>> getReminders(){
         return reminders;
    }
}
