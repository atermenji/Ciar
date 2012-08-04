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

import com.libresoft.apps.ARviewer.ARUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;


public class DrawFocus extends View{
	
	private static float fRADIUS;
	
	public DrawFocus(Context context) {
		super(context);
		fRADIUS = ARUtils.transformPixInDip(context, 5);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		paint.setFakeBoldText(true);
		
		int w = canvas.getWidth();
        int h = canvas.getHeight();
        float cx = ((float)w) / 2 ;
        float cy = ((float)h) / 2 ;

		paint.setStyle(Paint.Style.STROKE);
		canvas.drawCircle(cx, cy, fRADIUS, paint);
		
		super.onDraw(canvas);
	}
}