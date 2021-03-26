package com.example.reminder.formsPackage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.reminder.R;
import com.example.reminder.Reminder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class EditingPanel extends AppCompatActivity {


    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private Reminder tempReminder;
    private CalendarView dueDate;
    private Calendar calendar;
    private String dayPicked;
    private Date strDate;
    private RadioGroup transitModes;
    private int idForMode;
    private int idForScenario;
    private TextView durationToDestination;
    private String trafficModel;
    private String typeOfTransfer;
    private ScrollView scrollView;
    private RadioGroup scenarioCases;
    private Spinner notificationDropdown;
    private final ArrayList<String> timeForDirections = new ArrayList<>();
    private TextView reminderName;
    private TimePicker dueTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_panel);

        Bundle bundle = getIntent().getExtras();
        tempReminder = bundle.getParcelable("TempReminder");

        try {
            initializeFields();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initializeFields() throws ParseException {
        scrollView = findViewById(R.id.scrollViewEditPanel);

        setReminderName();

        timePickerInitialize();

        dueDateCalculator();

        setNotificationDropdown();

        setLocationFeatureAttributes();

        updateReminder();

        deleteReminder();

    }

    private void setReminderName(){
        reminderName = findViewById(R.id.reminderNameEditPanel);
        reminderName.setText(tempReminder.getNameOfTheReminder());
    }

    private void timePickerInitialize(){
        dueTime = findViewById(R.id.dueTimeEditPanel);
        dueTime.setIs24HourView(true);
        String[] split = tempReminder.getTimeOfTheReminder().split(":");
        dueTime.setCurrentHour(Integer.parseInt(split[0]));
        dueTime.setCurrentMinute(Integer.parseInt(split[1]));
    }

    private void dueDateCalculator() throws ParseException {
        dueDate = findViewById(R.id.dueDateEditPanel);
        dueDate.setDate(dueDateInitialize());
        dayPicked = tempReminder.getDateOfTheReminder();
        strDate = sdf.parse(dayPicked);
        dueDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            try {
                calendar = Calendar.getInstance();
                long currentTimeInMillisecondsRightNowPlusOneMinute = calendar.get(Calendar.HOUR_OF_DAY) * 3600000 + calendar.get(Calendar.MINUTE) * 60000 + 60000;
                dayPicked = dayOfMonth + "/" + (month + 1) + "/" + year;
                strDate = sdf.parse(dayPicked);
                if (System.currentTimeMillis() > strDate.getTime() + currentTimeInMillisecondsRightNowPlusOneMinute) {
                    Toast.makeText(this, "Invalid Date Selection \n The day have passed", Toast.LENGTH_SHORT).show();
                    dueDate.setDate(System.currentTimeMillis());
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    dayPicked = sdf.format(calendar.getTime());
                    strDate = sdf.parse(dayPicked);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    private void setNotificationDropdown(){
        notificationDropdown = findViewById(R.id.notificationSpinnerEditPanel);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationDropdown.setAdapter(adapter);
        notificationDropdown.setSelection(0);
    }



    private void setLocationFeatureAttributes(){
        transitModes = findViewById(R.id.transitModesEditPanel);
        scenarioCases = findViewById(R.id.Average_Worst_ScenarioEditPanel);
        durationToDestination = findViewById(R.id.durationToDestinationEditPanel);
        if (tempReminder.isLocationFeatureActive()){
            timeForDirections.addAll(tempReminder.getTimeToGetToDestination());
            durationToDestination.setText(timeForDirections.get(1));
            radioGroupTransitModes();
            setScenarioMode();
        }else {
            transitModes.setVisibility(View.GONE);
            scenarioCases.setVisibility(View.GONE);
            durationToDestination.setVisibility(View.GONE);
        }
    }

    private void radioGroupTransitModes() {
        transitModes.setOnCheckedChangeListener((group, checkedId) -> {
            idForMode = transitModes.getCheckedRadioButtonId();
            if (!timeForDirections.isEmpty()) {
                if (idForScenario == R.id.Average_Case_ScenarioEditPanel && idForMode == R.id.drivingModeEditPanel) {
                    durationToDestination.setText(timeForDirections.get(1));
                    trafficModel = "Average";
                    typeOfTransfer = "Driving";
                } else if (idForScenario == R.id.Worst_Case_ScenarioEditPanel && idForMode == R.id.drivingModeEditPanel) {
                    durationToDestination.setText(timeForDirections.get(2));
                    trafficModel = "Worst";
                    typeOfTransfer = "Driving";
                } else if (idForMode == R.id.walkingModeEditPanel) {
                    durationToDestination.setText(timeForDirections.get(0));
                    trafficModel = "Average";
                    typeOfTransfer = "Walking";
                }
            }
        });
    }

    private void setScenarioMode(){
        scenarioCases.setOnCheckedChangeListener((group, checkedId) -> {
            idForScenario = scenarioCases.getCheckedRadioButtonId();
            if (!timeForDirections.isEmpty()){
                if (idForScenario == R.id.Average_Case_ScenarioEditPanel && idForMode == R.id.drivingModeEditPanel){
                    durationToDestination.setText(timeForDirections.get(1));
                    trafficModel = "Average";
                    typeOfTransfer = "Driving";
                }else if (idForScenario == R.id.Worst_Case_ScenarioEditPanel && idForMode == R.id.drivingModeEditPanel){
                    durationToDestination.setText(timeForDirections.get(2));
                    trafficModel = "Worst";
                    typeOfTransfer = "Driving";
                }else if (idForMode == R.id.walkingModeEditPanel){
                    durationToDestination.setText(timeForDirections.get(0));
                    trafficModel = "Average";
                    typeOfTransfer = "Walking";
                }
            }
        });
    }

    private long dueDateInitialize(){
        long dayInMils = 0;
        try {
            dayInMils = Objects.requireNonNull(sdf.parse(tempReminder.getDateOfTheReminder())).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  dayInMils;
    }

    private void finishEdit(String queryToExecute, Reminder reminder){
        Intent intent = new Intent();
        intent.putExtra("Reminder", reminder);
        intent.putExtra("QueryToExecute", queryToExecute);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void deleteReminder(){
        ImageButton deleteReminder = findViewById(R.id.deleteReminder);
        deleteReminder.setOnClickListener(v -> finishEdit("Delete", tempReminder));
    }

    private void updateReminder(){
        ImageButton updateReminder = findViewById(R.id.updateReminder);
        updateReminder.setOnClickListener(v -> {
            if (reminderName.getText().toString().equals("")){
                reminderName.setError("Empty Field");
                scrollView.scrollTo(reminderName.getScrollX(), reminderName.getScrollY());
                return;
            }
            if (System.currentTimeMillis() >= notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString())){
                Toast.makeText(this, "Invalid notification time selected", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Check your time/date/Notification time", Toast.LENGTH_SHORT).show();
                return;
            }
            tempReminder.setNameOfTheReminder(reminderName.getText().toString());
            tempReminder.setTimeOfTheReminder(timePickerToString());
            tempReminder.setDateOfTheReminder(dayPicked);
            long timeToNotifyTheUser;
            if (tempReminder.isLocationFeatureActive()){
                timeToNotifyTheUser = notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString()) - notificationIncludingDistanceCalculation(durationToDestination.getText().toString());
            }else {
                timeToNotifyTheUser = notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString());
            }
            tempReminder.setTimeToNotifyForTheReminder(timeToNotifyTheUser);
            if (tempReminder.isLocationFeatureActive()){
                tempReminder.setTypeOfTransfer(typeOfTransfer);
                tempReminder.setModelAverageOrWorse(trafficModel);
            }
            finishEdit("Update", tempReminder);
        });
    }

    private String timePickerToString(){
        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", dueTime.getCurrentHour(), dueTime.getCurrentMinute());
        return time;
    }

    private long notificationTimeInMilsCalculator(String text){
        String[] split = text.split("\\s+");
        long timeForNotificationToSubtract;
        if (split[1].equals("hours")){
            timeForNotificationToSubtract = Long.parseLong(split[0]) * 3600000;
        }else {
            timeForNotificationToSubtract = Long.parseLong(split[0]) * 60000;
        }
        return strDate.getTime() + dueTime.getCurrentHour() * 3600000 + dueTime.getCurrentMinute() * 60000 - timeForNotificationToSubtract;
    }

    private long notificationIncludingDistanceCalculation(String text){
        String[] split = text.split("\\s+");
        long timeInMilsToArriveToDestination;
        if (split.length == 4){
            timeInMilsToArriveToDestination = Long.parseLong(split[0])* 3600000 + Long.parseLong(split[2]) * 60000;
        }else {
            if (split[1].equals("hours")){
                timeInMilsToArriveToDestination = Long.parseLong(split[0]) * 3600000;
            }else {
                timeInMilsToArriveToDestination = Long.parseLong(split[0]) * 60000;
            }
        }
        return timeInMilsToArriveToDestination;
    }
}