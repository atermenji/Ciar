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

package com.libresoft.apps.ARviewer.Overlays;

import com.libresoft.apps.ARviewer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.hardware.SensorManager;
import android.view.View;

public class DrawUserStatus extends View{
	private static final int BORDER = 10;
	
	private boolean isAltitudeLoaded = false;
	private boolean isLocationServiceActive = false;
	private boolean isLocationServiceOnProgress = false;
	private int compassAccurate = SensorManager.SENSOR_STATUS_UNRELIABLE;
	
	private Bitmap altitude_status = null;
	private Bitmap location_status = null;
	private Bitmap compass_status = null;
	
	public DrawUserStatus(Context context){
		super(context);
	}
	
	public void setLocationServiceActive(boolean active){
		if (isLocationServiceOnProgress || (isLocationServiceActive != active)){
			isLocationServiceOnProgress = false;
			if(active)
				location_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.gps_on);
			else
				location_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.gps_off);
			this.isLocationServiceActive = active;
			invalidate();
		}
	}
	
	public void setLocationServiceOnProgress(){
		location_status = BitmapFactory.decodeResource(
				getContext().getResources(), 
				R.drawable.gps_progress);
		isLocationServiceOnProgress = true;
		invalidate();
	}
	
	public void setAltitudeLoaded(boolean loaded){
		if(isAltitudeLoaded != loaded){
			if(loaded)
				altitude_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.altitude_on);
			else
				altitude_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.altitude_off);
			this.isAltitudeLoaded = loaded;
			invalidate();
		}
	}
	
	public void setCompassAccurate(int accuracy){
		if(compassAccurate != accuracy){
			switch(accuracy){
			case SensorManager.SENSOR_STATUS_UNRELIABLE:
				compass_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.compass_off);
				break;
			case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
				compass_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.compass_high);
				break;
			default:
				compass_status = BitmapFactory.decodeResource(
						getContext().getResources(), 
						R.drawable.compass_medium);
				break;
			}
			this.compassAccurate = accuracy;
			invalidate();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		int w = canvas.getWidth();
		int h = canvas.getHeight();
		
		if(altitude_status == null)
			altitude_status = BitmapFactory.decodeResource(
					getContext().getResources(), 
					R.drawable.altitude_off);
		
		if(location_status == null)
			location_status = BitmapFactory.decodeResource(
					getContext().getResources(), 
					R.drawable.gps_off);
		
		if(compass_status == null)
			compass_status = BitmapFactory.decodeResource(
					getContext().getResources(), 
					R.drawable.compass_off);
        
        float center_x = w - BORDER - location_status.getWidth();
        float center_y = h - BORDER - location_status.getHeight();

        /* Painting the location service status indicator */
        canvas.drawBitmap(location_status, center_x, center_y - 2*location_status.getHeight(), null);

        /* Painting the compass accuracy indicator */
        canvas.drawBitmap(compass_status, center_x, center_y - compass_status.getHeight(), null);
        
        /* Painting the altitude status indicator */
        canvas.drawBitmap(altitude_status, center_x, center_y, null);
        
		super.onDraw(canvas);
	}
	
}