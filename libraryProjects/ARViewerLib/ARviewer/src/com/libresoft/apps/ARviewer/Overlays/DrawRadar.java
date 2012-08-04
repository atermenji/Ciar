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

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.libresoft.apps.ARviewer.ARGeoNode;
import com.libresoft.apps.ARviewer.ARUtils;
import com.libresoft.apps.ARviewer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class DrawRadar extends View{

	private static float mRADIUS;
	private static float mRADIUSANGLE;
	private static final float resourceRadius = 2;
	private static float maxRadiusResource;
	private static final float BORDER = 10;
	
	private int point_clicked = -1;
	
	private Context mContext;
	private float azimuth;
	private ArrayList<Point> resources= null;
	
	private Bitmap compass_appearance = null;
	private Bitmap compass_shadow = null;
	
	private Semaphore sem;
	
	private boolean rotate_compass;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			invalidate();
		}
	};
	
	public DrawRadar(Context context){
		super(context);
		
		this.compass_appearance = BitmapFactory.decodeResource(getResources(),R.drawable.compass_simple);
		this.compass_shadow = BitmapFactory.decodeResource(getResources(),R.drawable.compass_simple_shadow);
		this.mContext = context;
		mRADIUS = ((float)compass_appearance.getWidth())/2;
		mRADIUSANGLE = mRADIUS - ARUtils.transformPixInDip(mContext, 4);
		maxRadiusResource = mRADIUSANGLE - ARUtils.transformPixInDip(mContext, resourceRadius);
		azimuth = 0;
		rotate_compass = false;
		sem = new Semaphore(1);
	}
	
	public void setPointClicked(int point_clicked){
		this.point_clicked = point_clicked;
	}
	
	public void setResourcesList(final ArrayList<ARGeoNode> nodes){
		setPointClicked(-1);
		new Thread(){
			public void run(){
				int max = nodes.size();
				
				ArrayList<Point> points = new ArrayList<Point>();
	        	for(int i = 0; i < max; i++){
	        		points.add(nodes.get(i).getPoint());
	        	}	
	        	try {
					sem.acquire();
		        	resources = points;
		        	sem.release();
		        	mHandler.sendEmptyMessage(0);
				} catch (InterruptedException e) {
					Log.e("DrawRadar", "", e);
				}
			}
		}.start();
	}

	public void setAzimuth(float azimuth){
		this.azimuth = azimuth;
	}
	
	public void setRotateCompass(boolean rotate){
		this.rotate_compass = rotate;
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		
        float compass_center_x = mRADIUS + BORDER;
        float compass_center_y = compass_center_x;
        
        // VISION RANGE
        Paint paintRange = new Paint();
		paintRange.setStyle(Paint.Style.FILL);
		paintRange.setShader(new RadialGradient(compass_center_x, compass_center_y, 
				mRADIUSANGLE, Color.WHITE, Color.YELLOW, TileMode.MIRROR));
		
        RectF oval = new RectF(compass_center_x - mRADIUSANGLE, compass_center_y - mRADIUSANGLE, 
        		compass_center_x + mRADIUSANGLE, compass_center_y + mRADIUSANGLE);
        
        float base = 0;
        if(!rotate_compass)
        	base = azimuth;
        canvas.drawArc(oval, base - 90 - DrawResource.MAX_AZIMUTH_VISIBLE,
        		DrawResource.MAX_AZIMUTH_VISIBLE*2, true, paintRange);
        
        // RADAR SHADOW
        canvas.drawBitmap(compass_shadow, compass_center_x - mRADIUS, 
        		compass_center_y - mRADIUS, null);
        
        // ROTATE THE COMPASS
        if(rotate_compass)
        	canvas.rotate(-azimuth, compass_center_x, compass_center_y);
        
        // RADAR BACKGROUND
        canvas.drawBitmap(compass_appearance, compass_center_x - mRADIUS, 
        		compass_center_y - mRADIUS, null);
        
        // RESOURCE POINTS
        try {
			sem.acquire();
			if(resources != null){
	        	Paint paint = new Paint();
	            paint.setStyle(Paint.Style.FILL);
	            paint.setARGB(255, 255, 255, 255);
	            
	        	int max = resources.size();
	        	for(int i = 0; i < max; i++){
	        		if(i == point_clicked)
	                    continue;
	        			
	        		canvas.drawCircle(compass_center_x + ((float)maxRadiusResource * resources.get(i).x)/100, 
	        				compass_center_y + ((float)maxRadiusResource * resources.get(i).y)/100,
	        				resourceRadius, paint);
	        	}
	        	if(point_clicked > -1){
	        		paint.setARGB(255, 255, 0, 0);
	        		canvas.drawCircle(compass_center_x + ((float)maxRadiusResource * resources.get(point_clicked).x)/100, 
	        				compass_center_y + ((float)maxRadiusResource * resources.get(point_clicked).y)/100,
	        				resourceRadius, paint);
	        	}
	        }
			sem.release();
		} catch (InterruptedException e) {
			Log.e("DrawRadar", "", e);
		}
        

		super.onDraw(canvas);
	}
	
}