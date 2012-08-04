package com.coreinvader.ciar.util;

import android.content.Context;
import android.preference.PreferenceManager;

import com.coreinvader.ciar.R;

public class SettingsManager {
    
    private static final int DEF_RADIUS = 30;
    
    private Context mContext;
    
    public SettingsManager(Context context) {
	mContext = context;
    }
    
    public double getSearchRadius() {
	return PreferenceManager.getDefaultSharedPreferences(mContext)
		.getInt(mContext.getString(R.string.autotarget_radius_key), DEF_RADIUS) / 10.0;
    }
}
