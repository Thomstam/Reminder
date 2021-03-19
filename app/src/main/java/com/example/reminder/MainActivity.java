package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.reminder.roomClasses.ReminderDataBase;
import com.example.reminder.roomClasses.ReminderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener{

    private RecyclerCustom recyclerCustom;
    List<Reminder> reminders = new ArrayList<>();
    private static final int REQUEST_CODE = 666;
    private ReminderViewModel reminderViewModel;



    public void createNewReminderForm() {
        FloatingActionButton createNewReminder = findViewById(R.id.createNewReminder);
        createNewReminder.setOnClickListener(v -> {
            Intent formSetup = new Intent(MainActivity.this, FormSetupActivity.class);
            startActivityForResult(formSetup, REQUEST_CODE);
        });
    }



    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Reminder reminder = (Reminder) data.getExtras().get("Reminder");
                reminders.add(reminder);
                recyclerCustom.notifyDataSetChanged();
                reminderViewModel.insert(reminder);
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReminderDataBase db = Room.databaseBuilder(getApplicationContext(),
                ReminderDataBase.class, "database-name").build();

         createNewReminderForm();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerCustom = new RecyclerCustom(reminders, MainActivity.this);

         RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
         recyclerView.setLayoutManager(layoutManager);
         recyclerView.setItemAnimator(new DefaultItemAnimator());
         recyclerView.setAdapter(recyclerCustom);


        reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);


    }


    @Override
    public void onClick(View view, int position) {

    }
}