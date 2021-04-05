package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private RemoteViews remoteViewsSmall;
    private RemoteViews remoteViewsLarge;

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
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> reminderViewModel.deleteAllReminders())
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else if (item.getItemId() == R.id.notificationSettings) {
                Toast.makeText(MainActivity.this, "Jump To Notification Settings", Toast.LENGTH_SHORT).show();
//                notificationFragment();
            } else if (item.getItemId() == R.id.about) {
                Toast.makeText(MainActivity.this, "Jump to about fragment", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
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
                    reminderViewModel.getSelectCurrentReminders().observe(MainActivity.this, reminders -> recyclerCustom.setReminderList(reminders));
                }else if (position == 1){
                    reminderViewModel.getSelectCompletedReminders().observe(MainActivity.this, reminders -> recyclerCustom.setReminderList(reminders));
                    notificationFragment();
                }else {
                    reminderViewModel.getReminders().observe(MainActivity.this, reminders -> recyclerCustom.setReminderList(reminders));
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
        reminderViewModel = ViewModelProviders.of(this).get(ReminderViewModel.class);
        reminderViewModel.updateTable();
        reminderViewModel.getSelectCurrentReminders().observe(this, reminders -> recyclerCustom.setReminderList(reminders));
    }

    public void createNewReminderForm() {
        FloatingActionButton createNewReminder = findViewById(R.id.createNewReminder);
        createNewReminder.setOnClickListener(v -> {
            Intent formSetup = new Intent(MainActivity.this, FormSetupActivity.class);
            startActivityForResult(formSetup, REQUEST_CODE_FOR_NEW_REMINDER);
        });
    }

    private void notificationFragment(){
//        if (findViewById(R.id.notificationFragment) != null){
//            if (savedInstanceState != null){
//
//            }
//        }
//        getSupportFragmentManager().beginTransaction().replace(R.id.notificationFragment, NotificationPanel.class, null).commit();
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
                scheduleNotification(getNotification(reminder), reminder.getTimeToNotifyForTheReminder());
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

    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION, notification);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent);
    }

    private Notification getNotification(Reminder reminder) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id);
        builder.setContentTitle("Time To Start Getting Ready");
        builder.setSmallIcon(R.drawable.ic_notification_alert);
        setRemoteViewsSmall(reminder);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        builder.setCustomContentView(remoteViewsSmall);
        if (reminder.isLocationFeatureActive()){
            setRemoteViewsLarge(reminder);
            builder.setCustomBigContentView(remoteViewsLarge);
        }
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        return builder.build();
    }

    private void setRemoteViewsSmall(Reminder reminder) {
        remoteViewsSmall = new RemoteViews(getPackageName(), R.layout.notification_small);
        remoteViewsSmall.setTextViewText(R.id.nameOfNotificationSmall, reminder.getNameOfTheReminder());
        remoteViewsSmall.setTextViewText(R.id.notificationTimeSmall, reminder.getTimeOfTheReminder());
    }

    private void setRemoteViewsLarge(Reminder reminder) {
        remoteViewsLarge = new RemoteViews(getPackageName(), R.layout.notification_large);
        remoteViewsLarge.setTextViewText(R.id.nameOfNotificationLarge, reminder.getNameOfTheReminder());
        remoteViewsLarge.setTextViewText(R.id.notificationTimeLarge, reminder.getTimeOfTheReminder());
        remoteViewsLarge.setTextViewText(R.id.notificationCaseScenario, reminder.getModelAverageOrWorse());
        if (reminder.getTypeOfTransfer().equals("Walking")){
            remoteViewsLarge.setImageViewResource(R.id.notification_transferType, R.drawable.walk_directions_icon);
            remoteViewsLarge.setTextViewText(R.id.notificationTimeToDestination, reminder.getTimeToGetToDestination().get(0));
        }else if (reminder.getModelAverageOrWorse().equals("Average")){
            remoteViewsLarge.setImageViewResource(R.id.notification_transferType, R.drawable.car_direction_icon);
            remoteViewsLarge.setTextViewText(R.id.notificationTimeToDestination, reminder.getTimeToGetToDestination().get(1));
        }else {
            remoteViewsLarge.setImageViewResource(R.id.notification_transferType, R.drawable.car_direction_icon);
            remoteViewsLarge.setTextViewText(R.id.notificationTimeToDestination, reminder.getTimeToGetToDestination().get(2));
        }
    }
}