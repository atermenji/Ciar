/*
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

package com.libresoft.apps.ARviewer.Overlays;

import com.libresoft.apps.ARviewer.ARUtils;
import com.libresoft.apps.ARviewer.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class CustomViews{
	
	private static double seekbarValue;
	
	public static double getSeekbarValue(){
		return seekbarValue;
	}
	
	public static View createButton(Context mContext, OnClickListener buttonListener){
		LayoutInflater factory = LayoutInflater.from(mContext);
		View view = factory.inflate(R.layout.ar_button, null);
		
		Button button = (Button)view.findViewById(R.id.tag_button_alone);
		
		button.setOnClickListener(buttonListener);
		return view;
	}
	
	public static View createSeekBars(final Activity mActivity,
			double progress, 
			double max,
			final String units,
			final int divider, // 1, 10, 100, 1000...
			final int offset, // < max
			OnClickListener buttonListener){
		
		seekbarValue = progress;
		
		// Loading layout
		LayoutInflater factory = LayoutInflater.from(mActivity);
		View seekbar = factory.inflate(R.layout.seekbar_choice, null);
		
		SeekBar sb = (SeekBar)seekbar.findViewById(R.id.tag_bar);
		Button button = (Button)seekbar.findViewById(R.id.tag_button);
		RelativeLayout rl = (RelativeLayout)seekbar.findViewById(R.id.tag_container);
		
		button.setOnClickListener(buttonListener);
		
		double sb_w = mActivity.getWindowManager().getDefaultDisplay().getWidth() - ARUtils.transformPixInDip(mActivity, 65);
		double box_x = ARUtils.transformPixInDip(mActivity, 10) + 
			ARUtils.transformPixInDip(mActivity, 8) + 
			(((progress * divider) - offset)/((max * divider) - offset)) * (sb_w - ARUtils.transformPixInDip(mActivity, 10) - ARUtils.transformPixInDip(mActivity, 16));
		final DrawTextBox tb = new DrawTextBox(mActivity, box_x, 0);
		
		if (divider > 1)
			tb.setText(Double.toString(progress) + units);
		else
			tb.setText(Integer.toString((int)progress) + units);
		rl.addView(tb);
		
		sb.setMax((int)(max * divider) - offset);
		sb.setProgress((int)(progress * divider) - offset);
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
				if(divider > 1){
					text += Double.toString(((double)progress + offset) / divider);
					seekbarValue = ((double)progress + offset) / divider;
				}else{
					text += Integer.toString(progress + offset);
					seekbarValue = progress + offset;
				}
				tb.setText(text + units);
				double box_x = seekBar.getPaddingLeft() + 
					ARUtils.transformPixInDip(mActivity, 8) + 
					(((double)progress)/seekBar.getMax()) * ((double)seekBar.getWidth() - seekBar.getPaddingLeft() - ARUtils.transformPixInDip(mActivity, 16));
				tb.setCenter(box_x, 0);
				tb.invalidate();
//				if(progress > 70)
//				tv.setAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.push_left_in));
			}
		});
		sb.invalidate();
		
		return seekbar;
	}
	
}
