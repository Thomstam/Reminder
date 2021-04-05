package com.example.reminder;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class NotificationPanel extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}