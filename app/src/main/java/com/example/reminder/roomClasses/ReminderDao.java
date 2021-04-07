package com.example.reminder.roomClasses;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.reminder.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Delete
    void deleteReminders(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    @Query("DELETE FROM Reminder")
    void nukeTable();

    @Query("SELECT * FROM Reminder ORDER BY id DESC")
    LiveData<List<Reminder>> getAllReminders();

    @Query("SELECT * FROM Reminder WHERE isCompleted = 1 ORDER BY id DESC")
    LiveData<List<Reminder>> selectCompletedReminders();

    @Query("SELECT * FROM Reminder WHERE isCompleted = 0 ORDER BY id DESC")
    LiveData<List<Reminder>> selectCurrentReminders();

    @Query("UPDATE Reminder SET isCompleted = 1 WHERE strftime('%s', 'now') * 1000 > timeOfTheEvent")
    void updateTable();
}
