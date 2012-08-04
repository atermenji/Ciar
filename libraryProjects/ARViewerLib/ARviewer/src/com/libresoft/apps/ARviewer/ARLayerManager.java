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

package com.libresoft.apps.ARviewer;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class ARLayerManager{
	
	private Context mContext;
	private Activity activity;
	
	private RelativeLayout baseLayer;
	private RelativeLayout infoLayer;
	private RelativeLayout resourceLayer;
	private RelativeLayout extraLayer;	
	
	public ARLayerManager(Activity activity){
		this.mContext = activity.getBaseContext();
		this.activity = activity;
	}
	
	public void setBaseLayer(){
		baseLayer = new RelativeLayout(mContext);
		baseLayer.setKeepScreenOn(true);
		activity.setContentView(baseLayer);
	}
	
	public void addBaseLayer(View view){
		baseLayer.addView(view, 0, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public void setInfoLayer(){
		infoLayer = new RelativeLayout(mContext);
		baseLayer.addView(infoLayer, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public void setResourceLayer(){
		resourceLayer = new RelativeLayout(mContext);
		baseLayer.addView(resourceLayer, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public void setExtraLayer(){
		extraLayer = new RelativeLayout(mContext);
		baseLayer.addView(extraLayer, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}
	
	public RelativeLayout getBaseLayer(){
		return baseLayer;
	}
	
	public RelativeLayout getResourceLayer(){
		return resourceLayer;
	}
	
	public RelativeLayout getInfoLayer(){
		return infoLayer;
	}
	
	public RelativeLayout getExtraLayer(){
		return extraLayer;
	}
	
	public void addInfoElement(View view, LayoutParams layoutParams){
		if(layoutParams==null)
			infoLayer.addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		else
			infoLayer.addView(view, layoutParams);
		infoLayer.invalidate();
	}
	
	public void addResourceElement(View view, LayoutParams layoutParams){
		if(layoutParams==null)
			resourceLayer.addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		else
			resourceLayer.addView(view, layoutParams);
		resourceLayer.invalidate();
	}
	
	public void addExtraElement(View view, LayoutParams layoutParams){
		if(layoutParams==null)
			extraLayer.addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		else
			extraLayer.addView(view, layoutParams);
		extraLayer.invalidate();
	}
	
	public boolean isChildInResourcesList(View view){
		return (resourceLayer.indexOfChild(view) != -1);
	}
	
	public boolean isChildInInfoList(View view){
		return (infoLayer.indexOfChild(view) != -1);
	}
	
	public void removeBaseElement(View view){
		if(baseLayer.indexOfChild(view) > -1)
			baseLayer.removeView(view);
	}
	
	public void removeResourceElement(View view){
		if(resourceLayer.indexOfChild(view) > -1)
			resourceLayer.removeView(view);
	}
	
	public void removeInfoElement(View view){
		if(infoLayer.indexOfChild(view) > -1)
			infoLayer.removeView(view);
	}
	
	public void removeExtraElement(View view){
		if(extraLayer.indexOfChild(view) > -1)
			extraLayer.removeView(view);
	}
	
	public void cleanInfoLayer(){
		infoLayer.removeAllViews();
		infoLayer.invalidate();
	}
	
	public void cleanResouceLayer(){
		resourceLayer.removeAllViews();
		resourceLayer.invalidate();
	}
	
	public void cleanExtraLayer(){
		extraLayer.removeAllViews();
		extraLayer.invalidate();
	}
}