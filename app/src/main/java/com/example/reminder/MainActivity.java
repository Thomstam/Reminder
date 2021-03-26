package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.formsPackage.EditingPanel;
import com.example.reminder.formsPackage.FormSetupActivity;
import com.example.reminder.roomClasses.ReminderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerCustom recyclerCustom;
    private static final int REQUEST_CODE_FOR_NEW_REMINDER = 666;
    private static final int REQUEST_CODE_FOR_EDIT_PANEL = 555;
    private ReminderViewModel reminderViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRecyclerCustom();

        setTheViewModel();

        createNewReminderForm();

        recyclerOnClick();

    }

    private void setRecyclerCustom() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerCustom = new RecyclerCustom();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerCustom);
    }

    private void setTheViewModel() {
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModel.getReminders().observe(this, reminders -> recyclerCustom.setReminderList(reminders));
    }

    public void createNewReminderForm() {
        FloatingActionButton createNewReminder = findViewById(R.id.createNewReminder);
        createNewReminder.setOnClickListener(v -> {
            Intent formSetup = new Intent(MainActivity.this, FormSetupActivity.class);
            startActivityForResult(formSetup, REQUEST_CODE_FOR_NEW_REMINDER);
        });
    }

    private void recyclerOnClick() {
        recyclerCustom.setOnItemClickListener(reminder -> {
            Intent formSetupActivity = new Intent(MainActivity.this, EditingPanel.class);
            formSetupActivity.putExtra("TempReminder", reminder);
            startActivityForResult(formSetupActivity, REQUEST_CODE_FOR_EDIT_PANEL);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_NEW_REMINDER) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                Reminder reminder = (Reminder) data.getExtras().get("Reminder");
                reminderViewModel.insert(reminder);

            }
        }
        if (requestCode == REQUEST_CODE_FOR_EDIT_PANEL) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String query = (String) data.getExtras().get("QueryToExecute");
                Reminder reminder = (Reminder) data.getExtras().get("Reminder");
                if (query.equals("Delete")) {
                    reminderViewModel.delete(reminder);
                } else {
                    reminderViewModel.update(reminder);
                }
                recyclerCustom.notifyDataSetChanged();
            }
        }
    }
}