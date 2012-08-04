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
import com.libresoft.apps.ARviewer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Shader.TileMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawResource extends View {
	
	public static final float MAX_AZIMUTH_VISIBLE = 30; 
	public static final float MAX_ELEVATION_VISIBLE = 20;
	private static final int BORDER = 5;
	private float MAX_ICON_SIZE;
	private float MED_ICON_SIZE;
	private float MIN_ICON_SIZE;
	private float MAX_TEXT_SIZE;
	private float MED_TEXT_SIZE;
	private float MIN_TEXT_SIZE;
	
	private float CLICK_RANGE;
	private float BIG_CENTER_RANGE;
	private float CENTER_RANGE;

	public static final String ALL_NAMES = "All_names";
	public static final String CENTRAL_NAMES = "Central_names";
	public static final String NO_NAMES = "No_names";
	private static String name_status = ALL_NAMES;
	
	private float posx, posy;
	private String name;
	private float azimuth,elevation;
	private Bitmap bitmap_small;
	private Bitmap bitmap_normal;
	private Bitmap bitmap_clicked;
	private Bitmap bitmap;
	private float icon_size;
	private float text_size;
	private boolean is_clicked;
	private float cx, cy;
	private long age = 0;
	
	private Context mContext;
	
	private OnBoxChangeListener onBoxChangeListener;
	private OnIconClickListener onIconClickListener;
	private OnCenterListener onCenterListener;
	private OnGetIconListener onGetIconListener;
	private OnShowIconListener onShowIconListener;
	
	public DrawResource(Context context, 
			String name, 
			OnGetIconListener onGetIconListener) {
		super(context);
		this.mContext = context;
		this.name = name;
		this.is_clicked = false;
		this.onGetIconListener = onGetIconListener;
		
		MAX_ICON_SIZE = ARUtils.transformPixInDip(context, 56);
		MED_ICON_SIZE = ARUtils.transformPixInDip(context, 36);
		MIN_ICON_SIZE = ARUtils.transformPixInDip(context, 24);
		MAX_TEXT_SIZE = ARUtils.transformPixInDip(context, 14);
		MED_TEXT_SIZE = ARUtils.transformPixInDip(context, 11);
		MIN_TEXT_SIZE = ARUtils.transformPixInDip(context, 10);
		
		CLICK_RANGE = ARUtils.transformPixInDip(context, 25);
		BIG_CENTER_RANGE = ARUtils.transformPixInDip(context, 150);
		CENTER_RANGE = ARUtils.transformPixInDip(context, 20);
	}
	
	public static void setNamesStatus(String name_status){
		DrawResource.name_status = name_status;
	}
	
	public void setIcon(Bitmap icon){
		if(icon == null){
			this.bitmap_clicked = null;
			return;
		}
		
		if(icon != null){
			this.bitmap_clicked = Bitmap.createScaledBitmap(icon, (int)MAX_ICON_SIZE, (int)MAX_ICON_SIZE, true);
		}else{
			this.bitmap_clicked = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), 
					R.drawable.icon_70), (int)MAX_ICON_SIZE, (int)MAX_ICON_SIZE, true);
		}
		this.bitmap_normal = Bitmap.createScaledBitmap(bitmap_clicked, (int)MED_ICON_SIZE, (int)MED_ICON_SIZE, true);
		this.bitmap_small = Bitmap.createScaledBitmap(bitmap_clicked, (int)MIN_ICON_SIZE, (int)MIN_ICON_SIZE, true);
	}
	
	public void setValues (float azimuth, float elevation){
		this.azimuth = azimuth;
		this.elevation = elevation;
	}
	
	public void setClicked(boolean isClick){
		is_clicked = isClick;
		if(isClick)
			bringToFront();
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		int w = canvas.getWidth();
        int h = canvas.getHeight();
        cx = ((float)w) / 2;
        cy = ((float)h) / 2;
        //float inclination;
        
        /* Calculating if the icon should be drawn */
        if ((Math.abs(azimuth)>MAX_AZIMUTH_VISIBLE) || 
        		(Math.abs(elevation)>MAX_ELEVATION_VISIBLE)){
        	if(onBoxChangeListener != null)
        		onBoxChangeListener.onChange(-1, -1, -1, -1);
        	if(onShowIconListener != null)
        		onShowIconListener.onShow(false);
        	return;
        }
        if(onShowIconListener != null)
    		onShowIconListener.onShow(true);
        
        /* If there is no icon, we request for one */
        if((bitmap_clicked == null) && (onGetIconListener != null))
        		onGetIconListener.onGetIcon();
        
        /* Calculating the screen pixel that corresponds to the node */
        posx = cx + (float)(Math.sin(Math.toRadians(azimuth))/Math.sin(Math.toRadians(MAX_AZIMUTH_VISIBLE))) * cx;
        posy = cy + (float)(Math.sin(Math.toRadians(elevation))/Math.sin(Math.toRadians(MAX_ELEVATION_VISIBLE))) * cy;
        
        /* Check if the tag is into the big central region */
        boolean isCenter = false;
        if(isOnCircularArea(cx, cy, posx, posy, ARUtils.transformPixInDip(mContext, BIG_CENTER_RANGE)))
        	isCenter = true;
        
        /* Check if the tag is into the central region */
        if(isOnCircularArea(cx, cy, posx, posy, CENTER_RANGE))
        	if(onCenterListener != null)
        		onCenterListener.onCenter();
        
        /* Calculating the sizes of the elements */
		if(is_clicked){
			icon_size = MAX_ICON_SIZE;
			bitmap = bitmap_clicked;
			text_size = MAX_TEXT_SIZE;
		}else if(isCenter){
			icon_size = MED_ICON_SIZE;
			bitmap = bitmap_normal;
			text_size = MED_TEXT_SIZE;
		}else{
			icon_size = MIN_ICON_SIZE;
			bitmap = bitmap_small;
			text_size = MIN_TEXT_SIZE;
		}
		
		/* Checking the names painting */
		boolean isName = false;
		if(name_status.equals(ALL_NAMES))
			isName = true;
		else if((name_status.equals(CENTRAL_NAMES)) && isCenter)
			isName = true;
        
        /* Painting the icon bg box */
		Paint paint_bg = new Paint();
		paint_bg.setARGB(200,0,0,0);
		paint_bg.setShader(new LinearGradient(posx, posy - (icon_size/2) - BORDER, 
				posx, posy + (icon_size/2) + BORDER, Color.WHITE, Color.BLACK, TileMode.MIRROR));
		paint_bg.setAntiAlias(true);
		
		RectF backRect = new RectF (posx - icon_size/2 - BORDER, posy - (icon_size/2 + BORDER),
				posx + icon_size/2 + BORDER, posy + icon_size/2 + BORDER);
		
		canvas.drawRoundRect(backRect, BORDER/2, BORDER/2, paint_bg);
		
        /* Painting the icon */
		if(bitmap != null)
			canvas.drawBitmap(bitmap, posx - (icon_size/2), posy - (icon_size/2), null);
		
		if(is_clicked || isName){
			/* Painting the arrow */
			Paint paint = new Paint();
			paint.setARGB(250,255,255,255);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			paint.setTextAlign(Align.CENTER);
			paint.setTypeface(Typeface.SANS_SERIF);

			Path path = new Path();
			path.moveTo(posx - (icon_size/4), posy + (icon_size/2) + 2*BORDER);
			path.lineTo(posx, posy + (icon_size/2) + BORDER);
			path.lineTo(posx + (icon_size/4), posy + (icon_size/2) + 2*BORDER);

			canvas.drawPath(path, paint);

			/* Painting the name bg box */
			paint.setTextSize(text_size);
			String text = name;
			float tlongitude = paint.measureText(text);
			if(tlongitude > 2*icon_size){
				tlongitude = 2*icon_size;
				paint.setTextAlign(Align.LEFT);
			}

			paint_bg.setShader(new LinearGradient(posx, posy + (icon_size/2) + 2*BORDER, 
					posx, posy + (icon_size/2) + 3*BORDER + text_size, Color.WHITE, Color.BLACK, TileMode.MIRROR));
			paint_bg.setAntiAlias(true);

			backRect = new RectF (posx - tlongitude/2 - 2*BORDER, posy + (icon_size/2) + 2*BORDER,
					posx + tlongitude/2 + 2*BORDER, posy + (icon_size/2) + 3*BORDER + text_size);

			canvas.drawRoundRect(backRect, BORDER/2, BORDER/2, paint_bg);

			/* Painting the name */
			path = new Path();
			path.moveTo(posx - tlongitude/2, posy + (icon_size/2) + 2*BORDER + (BORDER + text_size)*3/4);
			path.lineTo(posx + tlongitude/2, posy + (icon_size/2) + 2*BORDER + (BORDER + text_size)*3/4);

			canvas.drawTextOnPath(text, path, 0, 0, paint);
		}
		
		// calculating box padding
		if(is_clicked && (onBoxChangeListener != null)){
			int left = (int)Math.max(0, (posx - ARUtils.transformPixInDip(mContext, 125)));
			int top = (int)(posy + (icon_size/2) + 3*BORDER + text_size);
			int right = (int)Math.max(0, (w - (left + ARUtils.transformPixInDip(mContext, 250))));
			int bottom = (int)Math.max(0, (h - (top + ARUtils.transformPixInDip(mContext, 200))));

			onBoxChangeListener.onChange(left, top, right, bottom);
		}
		age = System.currentTimeMillis();
		super.onDraw(canvas);
	}
	 
	@Override
	public boolean onTouchEvent(MotionEvent event){

		Log.i(name, Integer.toString(event.getAction()));
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			if((System.currentTimeMillis()-age) > 1000)
				break;
			
			float posx = this.posx;
			float posy = this.posy;

			float click_posx = event.getX();
			float click_posy = event.getY();
			
			if(isOnCircularArea(posx, posy, click_posx, click_posy, CLICK_RANGE)){

				is_clicked = !is_clicked;
				bringToFront();

				if(onIconClickListener != null)
					onIconClickListener.onClick(is_clicked);

				return true;
			}
			break;
		}


		return false;
	}
	 
	public boolean forceClick(){
		if((System.currentTimeMillis()-age) > 1000)
			return false;
		bringToFront();

		is_clicked = true;
		if(onIconClickListener != null)
			onIconClickListener.onClick(true);
		
		return true;
	}
	
	private boolean isOnCircularArea(float cx, float cy, float px, float py, float radius){
		return ((Math.pow(px - cx, 2) + Math.pow(py - cy, 2)) <= Math.pow(radius, 2));
	}
	
	
	
	/* Interfaces zone */

	public void setOnBoxChangeListener(OnBoxChangeListener onBoxChangeListener){
		this.onBoxChangeListener = onBoxChangeListener;
	}

	public void unregisterBoxListener(){
		this.onBoxChangeListener = null;
		is_clicked = false;
	}

	public void setOnIconClickListener(OnIconClickListener onIconClickListener){
		this.onIconClickListener = onIconClickListener;
	}

	public void unregisterIconClickListener(){
		this.onIconClickListener = null;
	}
	
	public void setOnCenterListener(OnCenterListener onCenterListener){
		this.onCenterListener = onCenterListener;
	}

	public void unregisterCenterListener(){
		this.onCenterListener = null;
	}
	
	public void setOnShowIconListener(OnShowIconListener onShowIconListener){
		this.onShowIconListener = onShowIconListener;
	}

	/* Interfaces */

	public interface OnBoxChangeListener {
		public abstract void onChange(int left, int top, int right, int bottom);
	}

	public interface OnIconClickListener {
		public abstract void onClick(boolean isOpen);
	}
	
	public interface OnCenterListener {
		public abstract void onCenter();
	}
	
	public interface OnGetIconListener {
		public abstract void onGetIcon();
	}
	
	public interface OnShowIconListener {
		public abstract void onShow(boolean show);
	}

}
