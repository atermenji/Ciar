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

package com.libresoft.apps.ARviewer.ScreenCapture;

import com.libresoft.apps.ARviewer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ScreenshotUI{
	
	private RelativeLayout base = null;
	private View _interface = null;
	private OnSaveListener onSaveListener = null;
	
	private OnClickListener bt_save_listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (onSaveListener != null){
				RelativeLayout rl = (RelativeLayout)_interface.findViewById(R.id.screenshot_rl);
				
				Bitmap screenshot = Bitmap.createBitmap(rl.getWidth(),rl.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas cv = new Canvas(screenshot);
				rl.draw(cv);
				
				onSaveListener.onSave(screenshot);
			}
			cleanAll();
		}
	};

	private OnClickListener bt_cancel_listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (onSaveListener != null){
				onSaveListener.onSave(null);
			}
			cleanAll();
		}
	};
	
	public ScreenshotUI(RelativeLayout base){
		this.base = base;
	}
	
	public void createInterface(Context mContext, RelativeLayout content){
		LayoutInflater inflater = (LayoutInflater) mContext
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_interface = inflater.inflate(R.layout.screenshot_preview, null);
		
		Button bt_save = (Button) _interface.findViewById(R.id.screenshot_bt_save);
		bt_save.setOnClickListener(bt_save_listener);
		
		Button bt_cancel = (Button) _interface.findViewById(R.id.screenshot_bt_cancel);
		bt_cancel.setOnClickListener(bt_cancel_listener);
		
		RelativeLayout rl = (RelativeLayout)_interface.findViewById(R.id.screenshot_rl);
		rl.addView(content, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		
		base.addView(_interface, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	private void cleanAll(){
		onSaveListener = null;
		base.removeView(_interface);
	}
	
	
	
	public void setOnSaveListener(OnSaveListener listener){
		this.onSaveListener = listener;
	}
	
	public interface OnSaveListener{
    	public abstract void onSave(Bitmap bitmap);
    }
	
}