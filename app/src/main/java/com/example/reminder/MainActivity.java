package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private ReminderViewModel reminderViewModelCurrent;
    private ReminderViewModel reminderViewModelCompleted;
    private ReminderViewModel reminderViewModelAll;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setMenu();

        setRecyclerCustom();

        setTheViewModel();

        createNewReminderForm();

        recyclerOnClick();

        setSpinnerMenu();
    }

    private void setMenu() {
        Toolbar toolbar = findViewById(R.id.mainToolBar);
        toolbar.inflateMenu(R.menu.main_activity_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.deleteAllReminders) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("DELETE ALL REMINDERS")
                        .setMessage("CAUTION: Are you sure you want to delete all the reminders?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> reminderViewModelCurrent.deleteAllReminders())
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else if (item.getItemId() == R.id.notificationSettings) {
                Toast.makeText(MainActivity.this, "Jump To Notification Settings", Toast.LENGTH_SHORT).show();
                notificationFragment();
            } else if (item.getItemId() == R.id.about) {
                Toast.makeText(MainActivity.this, "Jump to about fragment", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }

    private void notificationFragment(){
        Intent intent = new Intent(MainActivity.this , SettingsActivity.class);
        startActivity(intent);
    }

    private void setSpinnerMenu(){
        Spinner spinner = findViewById(R.id.sqlQueries);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.SQL_queries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    if (reminderViewModelAll.getReminders().hasActiveObservers()){
                        reminderViewModelAll.getReminders().removeObservers(MainActivity.this);
                    }
                    if (reminderViewModelCompleted.getSelectCompletedReminders().hasActiveObservers()){
                        reminderViewModelAll.getSelectCompletedReminders().removeObservers(MainActivity.this);
                    }
                    reminderViewModelCurrent.getSelectCurrentReminders().observe(MainActivity.this, reminders -> recyclerCustom.setReminderList(reminders));
                }else if (position == 1){
                    if (reminderViewModelAll.getReminders().hasActiveObservers()){
                        reminderViewModelAll.getReminders().removeObservers(MainActivity.this);
                    }
                    if (reminderViewModelCurrent.getSelectCurrentReminders().hasActiveObservers()){
                        reminderViewModelCurrent.getSelectCurrentReminders().removeObservers(MainActivity.this);
                    }
                    reminderViewModelCompleted.getSelectCompletedReminders().observe(MainActivity.this, reminders -> recyclerCustom.setReminderList(reminders));
                }else {
                    if (reminderViewModelCurrent.getSelectCurrentReminders().hasActiveObservers()){
                        reminderViewModelCurrent.getSelectCurrentReminders().removeObservers(MainActivity.this);
                    }
                    if (reminderViewModelCompleted.getSelectCompletedReminders().hasActiveObservers()){
                        reminderViewModelAll.getSelectCompletedReminders().removeObservers(MainActivity.this);
                    }
                    reminderViewModelAll.getReminders().observe(MainActivity.this, reminders -> recyclerCustom.setReminderList(reminders));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        reminderViewModelCurrent = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModelCompleted = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModelAll = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModelCurrent.updateTable();


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
                reminderViewModelCurrent.insert(reminder);
                SettingsActivity notification = new SettingsActivity();
                notification.scheduleNotification(notification.getNotification(reminder), reminder.getTimeToNotifyForTheReminder());
            }
        }
        if (requestCode == REQUEST_CODE_FOR_EDIT_PANEL) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                String query = (String) data.getExtras().get("QueryToExecute");
                Reminder reminder = (Reminder) data.getExtras().get("Reminder");
                if (query.equals("Delete")) {
                    reminderViewModelCurrent.delete(reminder);
                } else {
                    reminderViewModelCurrent.update(reminder);
                }
                recyclerCustom.notifyDataSetChanged();
            }
        }
    }
}