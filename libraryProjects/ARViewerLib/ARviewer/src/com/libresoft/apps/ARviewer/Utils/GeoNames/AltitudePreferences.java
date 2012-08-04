/*
 *
 *  Copyright (C) 2010 GSyC/LibreSoft, Universidad Rey Juan Carlos.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Raúl Román López <rroman@gsyc.es>
 *
 */

package com.libresoft.apps.ARviewer.Utils.GeoNames;

import com.libresoft.apps.ARviewer.R;
import com.libresoft.apps.ARviewer.Location.ARLocationManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AltitudePreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	private static final int DIALOG_SELECT_TALL = 1;
	private static final int DIALOG_SELECT_FLOOR = 2;

	public static final String KEY_USER_HEIGHT			= "userHeight";
	public static final String KEY_FLOOR				= "floor";
	public static final String KEY_USE_FLOOR			= "useFloor";
	public static final String KEY_GPS					= "useGps";
	
	
	EditTextPreference userTestPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.altitude_preferences);
	    initPreferences();
	      
	    // Set up a listener whenever a key changes            
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	private void initPreferences() {
		getPreferenceScreen().findPreference(KEY_USER_HEIGHT).setOnPreferenceClickListener(this);
		getPreferenceScreen().findPreference(KEY_FLOOR).setOnPreferenceClickListener(this);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		Float period = ((float) sharedPreferences.getInt(KEY_USER_HEIGHT, 175)) / 100;
		getPreferenceScreen().findPreference(KEY_USER_HEIGHT).setSummary(Float.toString(period) + " m.");

		getPreferenceScreen().findPreference(KEY_FLOOR).setSummary(Integer.toString(sharedPreferences.getInt(KEY_FLOOR, 0)));
	}

	public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) 
	{
		if (key.equals(KEY_GPS))
			ARLocationManager.getInstance(this).setLocationServiceAltitude(sharedPreferences.getBoolean(key, false));
		
		else if (key.equals(KEY_USER_HEIGHT)) {
			Preference pref = this.findPreference(key);
			if(pref == null) 
				return;
			Float height = ((float)sharedPreferences.getInt(key, 175)) / 100;
			pref.setSummary(Float.toString(height) + " m.");
			
		}else if (key.equals(KEY_FLOOR)) {
			Preference pref = this.findPreference(key);
			if(pref == null) 
				return;
			pref.setSummary(Integer.toString(sharedPreferences.getInt(key, 0)));
		}
	    
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(KEY_USER_HEIGHT)){
			showDialog(DIALOG_SELECT_TALL);
			return true;
		}else if(preference.getKey().equals(KEY_FLOOR)){
			showDialog(DIALOG_SELECT_FLOOR);
			return true;
		}
		return false;
	}
	
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) { 
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		switch(id){
		case DIALOG_SELECT_TALL:
			configureSeekbarDiag(dialog, KEY_USER_HEIGHT, sharedPreferences.getInt(KEY_USER_HEIGHT, 175), 300, " m.", 100, sharedPreferences);
			break;
		case DIALOG_SELECT_FLOOR:
			configureSeekbarDiag(dialog, KEY_FLOOR, sharedPreferences.getInt(KEY_FLOOR, 0), 150, "", 1, sharedPreferences);
			break;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {  
		
		switch(id){
		case DIALOG_SELECT_TALL:
		case DIALOG_SELECT_FLOOR:
			LayoutInflater factory = LayoutInflater.from(this);
			View view = factory.inflate(R.layout.seekbar_num, null);
			
			return new AlertDialog.Builder(this)	    
			.setView(view)
			.setCancelable(true)
			.setPositiveButton(R.string.ok, null)
			.create();
		}
		
		return null;
	}
	
	private void configureSeekbarDiag(final Dialog dialog,
			final String key,
			int progress, 
			int max,
			final String units,
			final int divider,
			final SharedPreferences sharedPreferences){
		
		final TextView tv = (TextView)dialog.findViewById(R.id.tv_sb_text);
		
		String text = "";
		if(divider > 1)
			text += Float.toString(((float)progress)/divider);
		else
			text += Integer.toString(progress);
		tv.setText(text + units);
		
		final SeekBar sb = (SeekBar)dialog.findViewById(R.id.sb_bar); 
		sb.setMax(max);
		sb.setProgress(progress);
		sb.setKeyProgressIncrement(1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				String text = "";
				if(divider > 1)
					text += Float.toString(((float)progress)/divider);
				else
					text += Integer.toString(progress);
				tv.setText(text + units);
				
//				if(progress > 70)
//				tv.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.push_left_in));
			}
		});
		sb.invalidate();
		
		Button bt_ok = ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE);
		bt_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor edit = sharedPreferences.edit();
				edit.putInt(key, sb.getProgress());
				edit.commit();
				dialog.dismiss();
			}
		});
		bt_ok.invalidate();
	}
	
}