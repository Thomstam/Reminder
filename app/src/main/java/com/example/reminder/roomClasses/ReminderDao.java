package com.example.reminder.roomClasses;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.reminder.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM Reminder")
    LiveData <List<Reminder>> getAllReminders();

    @Query("DELETE FROM Reminder")
    void deleteAll();
}
