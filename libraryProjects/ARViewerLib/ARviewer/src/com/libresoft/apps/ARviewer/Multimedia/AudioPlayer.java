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

package com.libresoft.apps.ARviewer.Multimedia;

import java.io.InputStream;

import com.libresoft.sdk.ARviewer.Types.Audio;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;


public class AudioPlayer{
	
	private static AudioManager aManager;
	
	private Context mContext;
	
	public AudioPlayer(Context mContext){
		this.mContext = mContext;
	}
	
	public void play(Audio audio){
		aManager = new AudioManager(mContext);
		if(aManager.isPlaying())
			return;
		// Downloading
		InputStream is = audio.getAudio();
		
		if(is == null)
			return;
		aManager.saveAudio (is,"tmpaudio");

		// Playing audio    		
		aManager.startPlayer();
	}
	
	public void stop(){
		if(aManager != null)
			aManager.stopPlayer();
	}
	
	public OnClickListener getPlayButtonListener(final Audio audio){
		return new OnClickListener(){
			public void onClick(View v) {
				play(audio);
			}
		};
	}
	
	public OnClickListener getStopButtonListener(){
		return new OnClickListener(){
			public void onClick(View v) {
				stop();
			}
		};
	}
	
}