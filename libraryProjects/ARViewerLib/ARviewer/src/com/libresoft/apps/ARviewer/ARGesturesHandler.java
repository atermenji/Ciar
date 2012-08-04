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

package com.libresoft.apps.ARviewer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.util.Log;


public class ARGesturesHandler implements OnGesturePerformedListener
{
	
	private GestureLibrary mLibrary;
	
	
	private Hashtable<Object, Object> tGestures = null;
	public void initGestures (Context context, GestureOverlayView gesture_view, int gesture)
	{
		
        mLibrary = GestureLibraries.fromRawResource(context, gesture);
        if (!mLibrary.load()) {
            Log.e("ARGestureHandler", "No gesture library");
        }
 
        GestureOverlayView gestures = gesture_view;
        gestures.addOnGesturePerformedListener(this);
	}
	
	public void setGestures (Hashtable<Object, Object> gestures)
	{
		tGestures = gestures;
		
	}
	

	public void onGesturePerformed (GestureOverlayView overlay, Gesture gesture) {
		
		
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		if (predictions.size() > 0) {
			if (predictions.get(0).score > 1.0) {
				String action = predictions.get(0).name;
				
				Log.e("LGS:", "Gesto");
				
				
				if (tGestures != null)
				{
					Enumeration<Object> e = tGestures.keys();
					
					while (e.hasMoreElements()) {
						
						Object obj = e.nextElement();
						
						if (obj.equals(action))
						{		
							Method m = null;
							m = (Method) tGestures.get(obj);							
							
							try {
	
								m.invoke((Object[])null,(Object[])null);
																
							} catch (IllegalArgumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalAccessException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
				else
				{
					Log.e("LGS:", "Lista Vacia");
				}
				
			}
		}
	}
	
}