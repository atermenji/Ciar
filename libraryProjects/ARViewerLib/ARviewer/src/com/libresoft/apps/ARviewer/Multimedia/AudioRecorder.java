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

package com.libresoft.apps.ARviewer.Multimedia;

import java.sql.Time;

import com.libresoft.apps.ARviewer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


public class AudioRecorder{
	
	private static AudioManager aManager;

	private Activity mActivity;

	private volatile Thread th;

	private String time_str = "00:00";
	private String name;
	
	public AudioRecorder(Activity mActivity){
		this.mActivity = mActivity;
	}
	
	public Dialog getRecordDialog(final int id, final OnFinishListener listener){
		LayoutInflater factory = LayoutInflater.from(mActivity);
		final View textEntryView = factory.inflate(R.layout.record_audio, null);

		final TextView time = (TextView) textEntryView.findViewById (R.id.record_tv);
		final ProgressBar pb = (ProgressBar) textEntryView.findViewById (R.id.record_pb);
		pb.setMax(10000);
		
		ImageButton bt_record = (ImageButton) textEntryView.findViewById (R.id.record_button_record);
		ImageButton bt_stop = (ImageButton) textEntryView.findViewById (R.id.record_button_stop);
		ImageButton bt_play = (ImageButton) textEntryView.findViewById (R.id.record_button_play);
		
		final Handler mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(th!=null){
					int progress = aManager.getMaxAmplitude();
					time.setText(time_str);
					if(progress > pb.getMax())
						progress = pb.getMax();
					pb.setProgress(progress);
				}
			}
		};
		
		final Handler playHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(th!=null){
					int progress = (int)(aManager.getProgress(250) * pb.getMax());
					time.setText(time_str);
					if(progress > pb.getMax())
						progress = pb.getMax();
					pb.setProgress(progress);
				}
			}
		};
		
		final OnCompletionListener onCompletionListener = new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				th = null;
				if(aManager != null)
					aManager.stopPlayer();
			}
		};
		
		OnClickListener playListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if((name == null) || (th != null))
					return;
				aManager = new AudioManager(mActivity);
				aManager.setName(name);
				aManager.setOnCompletionListener(onCompletionListener);
				aManager.startPlayer();
				
				th = new Thread(){
					public void run(){
						int counter = 0;
						long clock = 0;
						while(Thread.currentThread().equals(th)){
							try {
								Thread.sleep(250);
								counter += 250;
								if(counter >= 1000){
									counter = 0;
									clock ++;
								}
								time_str = calculateTime(clock);
								playHandler.sendEmptyMessage(0);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								Log.e("ContentCreation", "", e);
							}
						}

					}
				};

				th.start();
			}
		};

		OnClickListener stopListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				th = null;
				if(aManager != null){
					aManager.stopRecording();
					aManager.stopPlayer();
				}
			}
		};
		
		OnClickListener recordListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if((name == null) || (th != null))
					return;
				
				aManager = new AudioManager(mActivity);
				aManager.setName(name);
				aManager.startRecording();
				
				th = new Thread(){
					public void run(){
						int counter = 0;
						long clock = 0;
						while(Thread.currentThread().equals(th)){
							try {
								Thread.sleep(250);
								counter += 250;
								if(counter >= 1000){
									counter = 0;
									clock ++;
								}
								time_str = calculateTime(clock);
								mHandler.sendEmptyMessage(0);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								Log.e("ContentCreation", "", e);
							}
						}

					}
				};

				th.start();
			}
		};
		
		bt_record.setOnClickListener(recordListener);
		bt_stop.setOnClickListener(stopListener);
		bt_play.setOnClickListener(playListener);
		
		return new AlertDialog.Builder(mActivity)	        
		.setTitle("Recording Audio")
		.setCancelable(false)
		.setView(textEntryView)
		.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				th = null;
				if(aManager != null){
					aManager.stopRecording();
					aManager.stopPlayer();
				}
				mActivity.removeDialog(id);
				listener.onFinish(true);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				th = null;
				if(aManager != null){
					aManager.stopRecording();
					aManager.stopPlayer();
				}
				mActivity.removeDialog(id);
				listener.onFinish(false);
			}
		})
		.create();
	}
	
	private String calculateTime(long time){
		Time tme = new Time( time * 1000);
		String minutes = Integer.toString(tme.getMinutes());
		if(tme.getMinutes() < 10)
			minutes = "0" + minutes;
		String seconds = Integer.toString(tme.getSeconds());
		if(tme.getSeconds() < 10)
			seconds = "0" + seconds;
		return minutes + ":" + seconds;
	}

	public void doRecording (String name, int id){
		this.name = name;
		
		mActivity.showDialog(id);
	}
	
	public String getPath(){
		if(aManager != null)
			return aManager.getPath();
		return null;
	}
	
	
	
	public interface OnFinishListener {
		public abstract void onFinish(boolean isFinished);
	}
}