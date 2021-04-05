package com.example.reminder.formsPackage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.reminder.R;
import com.example.reminder.Reminder;
import com.example.reminder.googleAPI.DestinationActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class FormSetupActivity extends AppCompatActivity {

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private TimePicker dueTime;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private TextView durationToDestination;
    private RadioGroup transitModes;
    private RadioGroup scenarioCases;
    private ImageButton mapsButton;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch locationFeatureSwitch;
    private CalendarView dueDate;
    private String dayPicked;
    private Date strDate;
    private static final int REQUEST_CODE = 666;
    private ArrayList<String> timeForDirections = new ArrayList<>();
    private int idForScenario;
    private int idForMode;
    private EditText reminderName;
    private Spinner notificationDropdown;
    private ScrollView scrollView;
    private String trafficModel;
    private String typeOfTransfer;
    private Calendar calendar;
    private long timeOfTheEvent;



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_setup);
        reminderName = findViewById(R.id.reminderName);
        scenarioCases = findViewById(R.id.Average_Worst_Scenario);
        transitModes = findViewById(R.id.transitModes);
        durationToDestination = findViewById(R.id.durationToDestination);
        notificationDropdown = findViewById(R.id.notificationSpinner);
        dueTime = findViewById(R.id.dueTime);
        scrollView = findViewById(R.id.scrollView);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.notification_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notificationDropdown.setAdapter(adapter);
        dueTime.setIs24HourView(true);


        if (!isServicesUpdated()){
            locationFeatureSwitch.setVisibility(View.GONE);
        }
        radioGroupTransitModes();
        setScenarioMode();
        dueDateCalculator();
        switchCheck();
        openGoogleMaps();
        completeReminder();

        dueDate.setDate(System.currentTimeMillis());
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        dayPicked = sdf.format(calendar.getTime());
        try {
            strDate = sdf.parse(dayPicked);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void switchCheck(){
        locationFeatureSwitch = findViewById(R.id.LocationFeature);
        locationFeatureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            scenarioCases.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            transitModes.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mapsButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            durationToDestination.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            locationFeatureSwitch.setError(!isChecked ? null : null);
        });
    }

    private void openGoogleMaps(){
        mapsButton = findViewById(R.id.DestinationCalculator);
        mapsButton.setOnClickListener(v -> {
            if (dayPicked != null){
                Intent googleMaps = new Intent(FormSetupActivity.this, DestinationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("TimeInUTC", String.valueOf(convertCurrentTimeToUTC()));
                bundle.putString("TimeInUTCMinusOneHour", String.valueOf(convertCurrentTimeToUTC() - 3600000));
                googleMaps.putExtras(bundle);
                startActivityForResult(googleMaps, REQUEST_CODE);
            }else {
                Toast.makeText(this, "Set Time and Date", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isServicesUpdated(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(FormSetupActivity.this);
        if ( available == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(FormSetupActivity.this, available, ERROR_DIALOG_REQUEST);
            assert dialog != null;
            dialog.show();
        }else {
            Toast.makeText(this, "You cant use map feature in this app", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void dueDateCalculator(){
        dueDate = findViewById(R.id.dueDate);
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

    private long convertCurrentTimeToUTC(){
        return strDate.getTime() + dueTime.getCurrentHour() * 3600000 + dueTime.getCurrentMinute() * 60000 - TimeZone.getDefault().getRawOffset();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (timeForDirections != null) {
                    timeForDirections.clear();
                }
                timeForDirections = (ArrayList<String>) data.getExtras().getSerializable("timeDirections");
                transitModes.check(R.id.drivingMode);
                scenarioCases.check(R.id.Average_Case_Scenario);
                durationToDestination.setText(timeForDirections.get(1));
                trafficModel = "Average";
                typeOfTransfer = "Driving";
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void radioGroupTransitModes(){
        transitModes.setOnCheckedChangeListener((group, checkedId) -> {
            idForMode = transitModes.getCheckedRadioButtonId();
            if (!timeForDirections.isEmpty()){
                if (idForScenario == R.id.Average_Case_Scenario && idForMode == R.id.drivingMode){
                    durationToDestination.setText(timeForDirections.get(1));
                    trafficModel = "Average";
                    typeOfTransfer = "Driving";
                }else if (idForScenario == R.id.Worst_Case_Scenario && idForMode == R.id.drivingMode){
                    durationToDestination.setText(timeForDirections.get(2));
                    trafficModel = "Worst";
                    typeOfTransfer = "Driving";
                }else if (idForMode == R.id.walkingMode){
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
                if (idForScenario == R.id.Average_Case_Scenario && idForMode == R.id.drivingMode){
                    durationToDestination.setText(timeForDirections.get(1));
                    trafficModel = "Average";
                    typeOfTransfer = "Driving";
                }else if (idForScenario == R.id.Worst_Case_Scenario && idForMode == R.id.drivingMode){
                    durationToDestination.setText(timeForDirections.get(2));
                    trafficModel = "Worst";
                    typeOfTransfer = "Driving";
                }else if (idForMode == R.id.walkingMode){
                    durationToDestination.setText(timeForDirections.get(0));
                    trafficModel = "Average";
                    typeOfTransfer = "Walking";
                }
            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void completeReminder(){
        ImageButton completeReminder = findViewById(R.id.completeReminder);
        completeReminder.setOnClickListener(v -> {
            if (reminderName.getText().toString().equals("")){
               reminderName.setError("Empty Field");
               scrollView.scrollTo(reminderName.getScrollX(), reminderName.getScrollY());
               return;
            }
            if (locationFeatureSwitch.isChecked()){
                if (timeForDirections.isEmpty()){
                    Toast.makeText(this, "Open Google maps Or close this feature", Toast.LENGTH_SHORT).show();
                    scrollView.scrollTo(mapsButton.getScrollX(), mapsButton.getScrollY());
                    return;
                }else if (System.currentTimeMillis() >= notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString()) - notificationIncludingDistanceCalculation(durationToDestination.getText().toString())){
                    Toast.makeText(this, "Time to destination plus notification time points to time that have passed", Toast.LENGTH_LONG).show();
                    return;
                }
            }else{
                if (System.currentTimeMillis() >= notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString())){
                    Toast.makeText(this, "Invalid notification time selected", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Check your time/date/Notification time", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Reminder reminder;
            long timeToNotifyTheUser;
            @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", dueTime.getCurrentHour(), dueTime.getCurrentMinute());
            if (locationFeatureSwitch.isChecked()){
                if (trafficModel == null || typeOfTransfer == null){
                    trafficModel = "Average";
                    typeOfTransfer = "Driving";
                }
                timeToNotifyTheUser = notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString()) - notificationIncludingDistanceCalculation(durationToDestination.getText().toString());
                reminder = new Reminder(reminderName.getText().toString(), dayPicked, time, timeForDirections, timeToNotifyTheUser, true, trafficModel, typeOfTransfer, timeOfTheEvent, false);
            }else {
                timeToNotifyTheUser = notificationTimeInMilsCalculator(notificationDropdown.getSelectedItem().toString());
                reminder = new Reminder(reminderName.getText().toString(), dayPicked, time, timeForDirections, timeToNotifyTheUser, false, "", "", timeOfTheEvent, false);
            }
            Intent intent = new Intent();
            intent.putExtra("Reminder", reminder);
            setResult(RESULT_OK, intent);
            finish();

        });
    }

    private long notificationTimeInMilsCalculator(String text){
        String[] split = text.split("\\s+");
        long timeForNotificationToSubtract;
        if (split[1].equals("hours")){
            timeForNotificationToSubtract = Long.parseLong(split[0]) * 3600000;
        }else {
            timeForNotificationToSubtract = Long.parseLong(split[0]) * 60000;
        }
        timeOfTheEvent = strDate.getTime() + dueTime.getCurrentHour() * 3600000 + dueTime.getCurrentMinute() * 60000;
        return  timeOfTheEvent - timeForNotificationToSubtract;
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