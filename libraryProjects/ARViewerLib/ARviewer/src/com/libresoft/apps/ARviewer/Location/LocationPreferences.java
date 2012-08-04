/*
 *
 *  Copyright (C) 2011 GSyC/LibreSoft, Universidad Rey Juan Carlos.
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

package com.libresoft.apps.ARviewer.Location;

import com.libresoft.apps.ARviewer.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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

public class LocationPreferences extends PreferenceActivity implements OnPreferenceClickListener {
	private static final int DIALOG_SELECT_PERIOD = 2;
	private static final int DIALOG_SELECT_DISTANCE = 3;
	
	public static String KEY_LOCATION_PROVIDERS  = "location_providers";
	public static String KEY_LOCATION_UNITS 	 = "location_unit";
	public static String KEY_LOCATION_PERIOD   = "location_period";
	public static String KEY_LOCATION_DISTANCE	 = "location_distance";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.location_preferences);
        initPreferences();
    }
    
    private void initPreferences() {
		getPreferenceScreen().findPreference(KEY_LOCATION_PERIOD).setOnPreferenceClickListener(this);
		getPreferenceScreen().findPreference(KEY_LOCATION_DISTANCE).setOnPreferenceClickListener(this);
	}


	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(KEY_LOCATION_PERIOD)){
			showDialog(DIALOG_SELECT_PERIOD);
			return true;
		}else if(preference.getKey().equals(KEY_LOCATION_DISTANCE)){
			showDialog(DIALOG_SELECT_DISTANCE);
			return true;
		}
		return false;
	}
	
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) { 
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		switch(id){
		case DIALOG_SELECT_PERIOD:
			configureSeekbarDiag(dialog, KEY_LOCATION_PERIOD, sharedPreferences.getInt(KEY_LOCATION_PERIOD, 2), 30, "", 1, 1, sharedPreferences);
			break;
		case DIALOG_SELECT_DISTANCE:
			configureSeekbarDiag(dialog, KEY_LOCATION_DISTANCE, sharedPreferences.getInt(KEY_LOCATION_DISTANCE, 10), 100, " m.", 1, 0, sharedPreferences);
			break;
		}
	}
	
	@Override
    protected Dialog onCreateDialog(int id) {  
		
		switch(id){
		case DIALOG_SELECT_PERIOD:
		case DIALOG_SELECT_DISTANCE:
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
			final int offset,
			final SharedPreferences sharedPreferences){
		
		final TextView tv = (TextView)dialog.findViewById(R.id.tv_sb_text);
		
		String text = "";
		if(divider > 1)
			text += Float.toString(((float)progress)/divider);
		else
			text += Integer.toString(progress);
		tv.setText(text + units);
		
		final SeekBar sb = (SeekBar)dialog.findViewById(R.id.sb_bar); 
		sb.setMax(max - offset);
		sb.setProgress(progress - offset);
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
					text += Float.toString(((float)progress + offset)/divider);
				else
					text += Integer.toString(progress + offset);
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
				edit.putInt(key, sb.getProgress() + offset);
				edit.commit();
				dialog.dismiss();
			}
		});
		bt_ok.invalidate();
	}
}