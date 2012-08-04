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

package com.libresoft.apps.ARviewer.Maps.Overlays;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class ResourceOverlay extends Overlay
{
	private final int mRadius = 5;
	
	private String name;
	private float orientation = 0;
	private boolean isRange = false; 
	
	private Location mLocation;
	
	public void setLocation (Location location)
	{
		this.mLocation = location;
		
	}
	
	public void setOrientation (float orientation){
		this.orientation = orientation;
	}
	
	public void setRange (boolean range){
		this.isRange = range;
	}
	
	public ResourceOverlay(String name)
	{
		this.name = name;
	}
	
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
        GeoPoint geoPoint = new GeoPoint(  (int) (mLocation.getLatitude() * 1E6), 
				  						   (int) (mLocation.getLongitude() * 1E6));
        
		Point point= new Point();
		Projection projection = mapView.getProjection();
	        
		projection.toPixels(geoPoint, point);
		
		RectF oval = new RectF (point.x - mRadius, point.y - mRadius,
								point.x + mRadius, point.y + mRadius);
		
	    
		Paint paint = new Paint();
		paint.setARGB(250,255,255,255);
		paint.setShader(new RadialGradient(point.x, point.y, mRadius, Color.RED, Color.BLACK, TileMode.MIRROR));
		paint.setAntiAlias(true);
		
		Paint backPaint = new Paint();
		backPaint.setARGB(175,0,0,0);
		backPaint.setShader(new LinearGradient(point.x, point.y - 3*mRadius, 
				point.x, point.y + mRadius, Color.rgb(150, 230, 150), Color.BLACK, TileMode.MIRROR));
		backPaint.setAntiAlias(true);
	        
		String text = name;
		RectF backRect = new RectF (point.x + 2 + mRadius, point.y - 3*mRadius,
									point.x + 65, point.y + mRadius);
	        
		canvas.drawOval(oval,paint);
		
		
		if(isRange){
			Paint rangePaint = new Paint();
			rangePaint.setStyle(Paint.Style.FILL);
			rangePaint.setARGB(50, 0, 255, 0);
			backPaint.setAntiAlias(true);
			
			RectF range = new RectF(point.x - canvas.getHeight(), point.y - canvas.getHeight(), point.x + canvas.getHeight(), point.y + canvas.getHeight());
			canvas.drawArc(range, 240 + orientation, 60, true, rangePaint);
		}
		
		if(!text.equals("")){
			canvas.drawRoundRect(backRect, 5, 5, backPaint);
			paint.setShader(null);
			canvas.drawText (text, point.x + 2*mRadius, point.y, paint);
		}
		
		
	}

	
}
