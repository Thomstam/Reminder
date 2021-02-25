package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class FormSetupActivity extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Boolean locationPermissionGranted = false;


    RadioButton worstScenario ;
    RadioButton averageScenario;
    ImageButton mapsButton;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch locationFeatureSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_setup);
        averageScenario = findViewById(R.id.Average_Case_Scenario);
        worstScenario = findViewById(R.id.Worst_Case_Scenario);
        mapsButton = findViewById(R.id.DestinationCalculator);
        mapsButton.setVisibility(View.INVISIBLE);
        worstScenario.setVisibility(View.INVISIBLE);
        averageScenario.setVisibility(View.INVISIBLE);
        locationFeatureSwitch = findViewById(R.id.LocationFeature);
        if (!isServicesUpdated()){
            locationFeatureSwitch.setVisibility(View.INVISIBLE);
        }
    }

    public void switchCheck(View view){
        locationFeatureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                worstScenario.setVisibility(View.VISIBLE);
                averageScenario.setVisibility(View.VISIBLE);
                mapsButton.setVisibility(View.VISIBLE);
            }else {
                worstScenario.setVisibility(View.INVISIBLE);
                mapsButton.setVisibility(View.INVISIBLE);
                averageScenario.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void openGoogleMaps(View view){
        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleMaps = new Intent(FormSetupActivity.this, DestinationActivity.class);
                startActivity(googleMaps);
            }
        });
    }

    public boolean isServicesUpdated(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(FormSetupActivity.this);
        if ( available == ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(FormSetupActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else {
            Toast.makeText(this, "You cant use map feature in this app", Toast.LENGTH_SHORT).show();
        }
        return false;
    }





}