package com.coreinvader.ciar.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.coreinvader.ciar.R;

public class SettingsActivity extends PreferenceActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
    
}
