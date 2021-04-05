package com.example.reminder.roomClasses;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.reminder.Reminder;

@Database(entities = {Reminder.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ReminderDataBase extends RoomDatabase {

    public abstract ReminderDao reminderDao();
    private static ReminderDataBase INSTANCE;


    public static synchronized ReminderDataBase getINSTANCE(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    ReminderDataBase.class, "Reminder")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
