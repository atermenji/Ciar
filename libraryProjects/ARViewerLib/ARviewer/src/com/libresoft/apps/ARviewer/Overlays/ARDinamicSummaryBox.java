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

import java.lang.reflect.Method;
import java.util.Hashtable;

import com.libresoft.apps.ARviewer.ARBase;
import com.libresoft.apps.ARviewer.ARGesturesHandler;
import com.libresoft.apps.ARviewer.R;

import android.app.Activity;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ARDinamicSummaryBox extends ARSummaryBox{

	private GestureOverlayView gesture_view;
	
	public ARDinamicSummaryBox(RelativeLayout container){
		super(container);
	}
	
	public void drawSummaryBox(Activity mActivity, 
			int num_node,
			OnClickListener previousListener, 
			OnClickListener nextListener){
		
		/* Setting the number of the clicked node */
		setNodeClicked(num_node);
		
		/* Creating the box */
		View summary_box = getSummaryBox();
		if(summary_box == null){
			LayoutInflater factory = LayoutInflater.from(mActivity.getBaseContext());
			summary_box = factory.inflate(R.layout.ar_dinamic_summary, null);
			setSummaryBox(summary_box);
		}
		
		TextView tv_description = (TextView)summary_box.findViewById(R.id.ar_body);
		TextView tv_distance = (TextView)summary_box.findViewById(R.id.ar_distance);
		
		tv_description.setText(getDescription());
		tv_distance.setText(getDistance());

		TextView tv_page = (TextView)summary_box.findViewById(R.id.ar_dinamic_pagemark);
		int old_page = getPage(tv_page.getText().toString());
		tv_page.setText(Integer.toString(num_node));
		
		/* The real image will be loaded from the server */
		if(old_page > -1)
			setImageContainer(old_page, null);
		ImageView tv_image = (ImageView)summary_box.findViewById(R.id.ar_image);
		setImageContainer(num_node, tv_image);
		tv_image.setImageBitmap(getImage(mActivity));
		
		if(gesture_view == null){
			gesture_view = (GestureOverlayView)summary_box.findViewById(R.id.ar_gestures);
			initGestures(mActivity.getBaseContext());
		}
		
		if(getNodesList().size() > 1){
			if(previousListener != null){
				Button bt_left = (Button)summary_box.findViewById(R.id.ar_button_left);
				bt_left.setVisibility(View.VISIBLE);
				bt_left.setOnClickListener(previousListener);
			}

			if(nextListener != null){
				Button bt_right = (Button)summary_box.findViewById(R.id.ar_button_right);
				bt_right.setVisibility(View.VISIBLE);
				bt_right.setOnClickListener(nextListener);
			}
		}else{
			Button button = (Button)summary_box.findViewById(R.id.ar_button_left);
			button.setVisibility(View.INVISIBLE);
			button = (Button)summary_box.findViewById(R.id.ar_button_right);
			button.setVisibility(View.INVISIBLE);
		}
		
		LinearLayout ll_media_player = (LinearLayout)summary_box.findViewById(R.id.ar_media_player);
		if(isMedia()){
			ll_media_player.setVisibility(View.VISIBLE);
			setPlayButton(mActivity.getBaseContext(), summary_box);
			setStopButton(summary_box);
		}else
			ll_media_player.setVisibility(View.GONE);
		
		
		setDetailsButton(mActivity, summary_box);
		setRemoveButton(mActivity, summary_box);
		setCloseButton(summary_box);
	}
	
	public void removeSummaryBox(){
		if(gesture_view != null)
			gesture_view.removeAllOnGesturePerformedListeners();
		gesture_view = null;
		super.removeSummaryBox();
	}
	
	public boolean translateSummaryBox(int num_node, int left, int top, int right, int bottom){
		if(getNodeClicked() != num_node)
			return false;
		
		if((left == right) && (top == bottom)){
			if(isBoxDrawn())
				hideBox();
		}else{
			if(isBoxDrawn())
				showBox();
			LinearLayout ll_box = (LinearLayout)getSummaryBox().findViewById(R.id.ar_box);
			ll_box.setPadding(left, top, right, bottom);
		}
		
		return true;
	}
	
	public void resetAll(){
		removeSummaryBox();
	}
	
	private void initGestures(Context mContext){
		try{
			// Init gestures
			ARGesturesHandler gestureHand = new ARGesturesHandler();        
			gestureHand.initGestures(mContext, gesture_view, R.raw.gestures);

			Hashtable<Object, Object> htGestures = new Hashtable<Object, Object>();


			try {

				/* Set the static methods that execute when gesture is recognize */
				Class<ARBase> c = ARBase.class;
				Method m = c.getDeclaredMethod("GestureNext", (Class[]) null);
				Method m2 = c.getDeclaredMethod("GesturePrevious", (Class[]) null);

				htGestures.put((Object)"Previous", m );
				htGestures.put((Object)"Next", m2 );

			} catch (NoSuchMethodException e) {			
				e.printStackTrace();
			}        

			gestureHand.setGestures(htGestures);
			/////////////////////////
		} catch (Exception e) {
			Toast.makeText(mContext, 
					e.toString(), 
					Toast.LENGTH_LONG).show();
		}
	}
	
	private int getPage(String page){
		int num = -1;
		if((page != null) && (!page.equals(""))){
			num = Integer.parseInt(page);
		}
		return num;
	}
}