package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;

public class FormSetupActivity extends AppCompatActivity {

    RadioButton worstScenario ;
    RadioButton averageScenario;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch locationFeatureSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_setup);
        averageScenario = findViewById(R.id.Average_Case_Scenario);
        worstScenario = findViewById(R.id.Worst_Case_Scenario);
        worstScenario.setVisibility(View.INVISIBLE);
        averageScenario.setVisibility(View.INVISIBLE);
        locationFeatureSwitch = findViewById(R.id.LocationFeature);
    }

    public void switchCheck(View view){
        locationFeatureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                worstScenario.setVisibility(View.VISIBLE);
                averageScenario.setVisibility(View.VISIBLE);
            }else {
                worstScenario.setVisibility(View.INVISIBLE);
                averageScenario.setVisibility(View.INVISIBLE);
            }
        });
    }
}