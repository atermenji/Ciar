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
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class DrawResourceSearcher extends View{
	private static final int BORDER = 10;
	private float mRadius;
	
	private Bitmap background = null;
	private Bitmap arrow = null;
	private float angle;
	private RelativeLayout container;
	private boolean isVisible = false;
	
	public DrawResourceSearcher(Context context, RelativeLayout container){
		super(context);
		
		this.container = container;
		this.background = BitmapFactory.decodeResource(getResources(), R.drawable.node_search_bg);
		this.arrow = BitmapFactory.decodeResource(getResources(), R.drawable.node_search_arrow);
		mRadius = ((float)arrow.getWidth()/2);
		angle = 0;
	}
	
	public void setVisible(boolean setVisible){
		if(setVisible){
			if(container.indexOfChild(this) == -1){
				container.addView(this, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				isVisible = true;
			}
		}else{
			if(container.indexOfChild(this) > -1){
				container.removeView(this);
				isVisible = false;
			}
		}
	}
	
	public boolean isVisible(){
		return isVisible;
	}

	public void setValues(float azimuth, float elevation){
        if((azimuth == 0) && (elevation == 0))
        	angle = 0;
        else
        	angle = (float)Math.toDegrees(Math.atan2(azimuth, -elevation));
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		int w = canvas.getWidth();
        
        float center_x = w - mRadius - BORDER;
        float center_y = mRadius + BORDER;

        /* Painting the background */
        canvas.drawBitmap(background, center_x - mRadius, center_y - mRadius, null);
        
        /* Rotate the arrow */
        canvas.rotate(angle, center_x, center_y);
        
        /* Painting the arrow */
        canvas.drawBitmap(arrow, center_x - mRadius, center_y - mRadius, null);
        
		super.onDraw(canvas);
	}
	
}