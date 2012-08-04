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


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.libresoft.apps.ARviewer.Overlays.CustomViews;
import com.libresoft.apps.ARviewer.Tagging.AccurateTag;
import com.libresoft.apps.ARviewer.Tagging.MapTagging;
import com.libresoft.apps.ARviewer.Tagging.Content.ContentAttacher;
import com.libresoft.apps.ARviewer.Tagging.Content.ContentAttacher.OnAttachListener;
import com.libresoft.apps.ARviewer.Tips.ARTipManager;
import com.libresoft.apps.ARviewer.Utils.LocationUtils;
import com.libresoft.sdk.ARviewer.Types.GeoNode;

public class ARTagManager{
	private static final int MODULE_BASE = 10600;
	
	private static final int MENU_TAGGING = MODULE_BASE + 1;
	private static final int MENU_TAGGING_IMMEDIATE = MENU_TAGGING + 1;
	private static final int MENU_TAGGING_FAST = MENU_TAGGING_IMMEDIATE + 1;
	private static final int MENU_TAGGING_MAP = MENU_TAGGING_FAST + 1;
	private static final int MENU_TAGGING_ACCURATE_SIDE = MENU_TAGGING_MAP + 1;
	private static final int MENU_TAGGING_ACCURATE_LINE = MENU_TAGGING_ACCURATE_SIDE + 1;
	
	public static final int DIALOG_MOVE = MODULE_BASE + 1;
	
	public static final int ACTIVITY_MAP = MODULE_BASE + 1;
	public static final int ACTIVITY_RESULT =  ACTIVITY_MAP + 1;
	
	public static final int TAG_NONE = -1;
	public static final int TAG_IMMEDIATE = 0;
	public static final int TAG_FAST = 1;
	public static final int TAG_MAP = 2;
	public static final int TAG_ACCURATE_LINE = 3;
	public static final int TAG_ACCURATE_SIDE = 4;
	
	private OnLocationChangeListener onLocationChangeListener = null;
	private OnTaggingFinishedListener onTaggingFinishedListener = null;
	
	private RelativeLayout tagIFContainer;
	
	private int isLocationServiceOn = -1;
	
	private int savingType;
	private float[] user_location = new float[3];
	private float[] user_location_fixed = new float[3];

	private float[] resource_location = new float[3];
    private static AccurateTag accurateTag;
    private float[] angles = new float[3];
    private float[] res_angles = new float[3];
    private double distance = 0;
	
	private Activity mActivity;
	private ARLayerManager layers;
	private ArrayList<ARGeoNode> res_list;
	
	private ContentAttacher content_attacher = null;
	
	private OnAttachListener onAttachListener = new OnAttachListener() {
		
		@Override
		public void onReady(GeoNode node) {
			if(node != null){
				if((node != null) && (res_list != null))
						res_list.add(new ARGeoNode((ARBase) mActivity, node, layers.getInfoLayer()));
				onTaggingFinishedListener.onFinish(true);	    		
			} else{
				onTaggingFinishedListener.onFinish(false);
			}
			layers.removeExtraElement(tagIFContainer);
		}
	};
	
	OnClickListener fast_click = new OnClickListener() {
		
		public void onClick(View v) {
			distance = CustomViews.getSeekbarValue();
			layers.removeExtraElement((View) v.getParent());
			tagAction();
		}
	};
	
	OnClickListener ok_click = new OnClickListener() {
		public void onClick(View v) {
			tagAction();
		}
	};
	
	public ARTagManager(Activity mActivity, ARLayerManager layers, ArrayList<ARGeoNode> res_list, float[] user_location){
		this.mActivity = mActivity;
		this.layers = layers;
		this.res_list = res_list;
		setSavingType(TAG_NONE);
		setUserLocation(user_location);
	}
	
	public void setLocationServiceOn(int isLocationServiceOn){
		this.isLocationServiceOn = isLocationServiceOn;
	}
	
	public void setSavingType(int savingType){
		this.savingType = savingType;
		switch(savingType){
		case TAG_IMMEDIATE:
			tagAction();
			return;
		case TAG_FAST:
			tagIFContainer = new RelativeLayout(mActivity);
			tagIFContainer.addView(CustomViews.createSeekBars(mActivity, 1, 500, " m.", 1, 1, fast_click));
			layers.addExtraElement(tagIFContainer, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			ARTipManager.getInstance().showTipLong(mActivity, R.string.label_tip_fast);
			return;
		case TAG_MAP:
			break;
		case TAG_ACCURATE_LINE:
			accurateTag = new AccurateTag();
			break;
		case TAG_ACCURATE_SIDE:
			accurateTag = new AccurateTag();
			break;
		default:
			return;
		}
		tagIFContainer = new RelativeLayout(mActivity);
		tagIFContainer.addView(CustomViews.createButton(mActivity, ok_click));
		layers.addExtraElement(tagIFContainer, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		ARTipManager.getInstance().showTipLong(mActivity, R.string.label_tip_focus);
		
	}
	
	public void setUserLocation(float[] user_location){
		this.user_location = user_location.clone();
	}
	
	public void setUserLocationFixed(float[] user_location){
		this.user_location_fixed = user_location;
	}
	
	public void setResourceLocation(float[] resource_location){
		this.resource_location = resource_location.clone();
	}
	
	public void setAngles(float[] angles){
		this.angles = angles.clone();
	}
	
	public int getSavingType(){
		return savingType;
	}
	
	public float[] getResourceLocation(){
		return resource_location;
	}
	
	public float[] getUserLocation(){
		return user_location;
	}
	
	public float[] getAngles(){
		return angles;
	}
	
    public void launchResult(){
    	//layers.cleanExtraLayer();
    	Location loc = resourceCoords(savingType);
    	savingType = TAG_NONE;
		if(loc == null)
			Toast.makeText(mActivity, 
					"Error",
					Toast.LENGTH_LONG).show();
		else{
			content_attacher = new ContentAttacher(mActivity);
	        content_attacher.setOnAttachListener(onAttachListener);
	        content_attacher.setResourceLocation(loc, false);
			
	        mActivity.showDialog(ContentAttacher.DIALOG_ATTACH);
		}
    	
    }
	
    public Location resourceCoords(int savingType){
    	Location resource =  new Location("TAG");
    	float[] res_location;
    	float res_distance, res_height;
    	
    	
    	switch(savingType){
    	
    	case TAG_IMMEDIATE:
    		res_location = user_location_fixed;
    		break;
    	
    	case TAG_FAST:
    		res_location = LocationUtils.calculateIntersection(user_location_fixed, ARCompassManager.getAzimuth(res_angles), (float)distance);
    		break;
    		
    	case TAG_MAP:
    		res_location = resource_location;
    		break;
    		
    	case TAG_ACCURATE_SIDE:
    		res_location = accurateTag.getLocationArray();
    		break;
    	
    	case TAG_ACCURATE_LINE:
    		res_location = accurateTag.getLineLocationArray();
    		break;
    		
    	default:
    		res_location = new float[2];	
    		
    	}
    	
    	if(res_location==null)
    		return null;
    	
    	res_distance = LocationUtils.calculateDistance(user_location_fixed, res_location);
    	res_height = LocationUtils.calculateResourceHeight(ARCompassManager.getElevation(res_angles), res_distance);
    	
    	resource.setLatitude(res_location[0]);
    	resource.setLongitude(res_location[1]);
    	resource.setAltitude(res_height);
    	
    	return resource;
    }
    
    public boolean tagAction(){
    	user_location_fixed = user_location.clone();
    	res_angles = angles.clone();
		
    	switch(savingType){

    	case TAG_IMMEDIATE:
    		launchResult();
    		break;

    	case TAG_FAST:
    		launchResult();
    		break;

    	case TAG_MAP:

    		Intent i = new Intent(mActivity, MapTagging.class);
    		i.putExtra("LATITUDE", user_location_fixed[0]);
    		i.putExtra("LONGITUDE", user_location_fixed[1]);
    		i.putExtra("AZIMUTH", ARCompassManager.getAzimuth(res_angles));
    		mActivity.startActivityForResult(i, ACTIVITY_MAP);

    		break;

    	case TAG_ACCURATE_SIDE:
    		if(!accurateTag.addElem(user_location_fixed, ARCompassManager.getAzimuth(res_angles), ARCompassManager.getElevation(res_angles))){
    			mActivity.showDialog(DIALOG_MOVE);
    			Toast.makeText(mActivity, 
    					"Limit reached", 
    					Toast.LENGTH_LONG).show();
    		}else{
    			mActivity.showDialog(DIALOG_MOVE);
    			Toast.makeText(mActivity, 
    					"Saved", 
    					Toast.LENGTH_SHORT).show();
    			return false;
    		}
    		break;

    	case TAG_ACCURATE_LINE:
    		if(!accurateTag.addElem(user_location_fixed, ARCompassManager.getAzimuth(res_angles), ARCompassManager.getElevation(res_angles))){
    			mActivity.showDialog(DIALOG_MOVE);
    			Toast.makeText(mActivity, 
    					"Limit reached", 
    					Toast.LENGTH_LONG).show();
    		}else{
    			mActivity.showDialog(DIALOG_MOVE);
    			Toast.makeText(mActivity, 
    					"Saved", 
    					Toast.LENGTH_SHORT).show();
    			return false;
    		}
    		break;
    	}
    	return true;
    }
    
    
    public void onCreateOptionsMenu(Menu menu) {
    	
    	SubMenu sub0 = menu.addSubMenu(0, MENU_TAGGING, 0, R.string.label_method_title)
    	.setIcon(R.drawable.tag);
    	sub0.add(0,MENU_TAGGING_IMMEDIATE, 0, R.string.label_method_immediate);
    	sub0.add(0,MENU_TAGGING_FAST, 0, R.string.label_method_fast);
    	sub0.add(0,MENU_TAGGING_MAP, 0, R.string.label_method_map);
    	sub0.add(0,MENU_TAGGING_ACCURATE_SIDE, 0, R.string.label_method_accurate_side);
    	sub0.add(0,MENU_TAGGING_ACCURATE_LINE, 0, R.string.label_method_accurate_line);
    		
    }
    
    public boolean onOptionsItemSelected (MenuItem item) {
    	
    	switch (item.getItemId()) {

    	case MENU_TAGGING_IMMEDIATE:
    		setSavingType(TAG_IMMEDIATE);
    		break;

    	case MENU_TAGGING_FAST:
    		setSavingType(TAG_FAST);
    		break;

    	case MENU_TAGGING_MAP:
    		setSavingType(TAG_MAP);
    		break;

    	case MENU_TAGGING_ACCURATE_SIDE:
    		setSavingType(TAG_ACCURATE_SIDE);
    		break;

    	case MENU_TAGGING_ACCURATE_LINE:
    		setSavingType(TAG_ACCURATE_LINE);
    		break;

    	default:
    		return false;
    	}
        return true;
    }
    
    public Dialog onCreateDialog(int id) {
    	if(content_attacher != null){
    		Dialog diag = content_attacher.onCreateDialog(id);
    		if(diag != null)
    			return diag;
    	}
    	
    	switch (id) {
    	case DIALOG_MOVE:

    		LayoutInflater factory1 = LayoutInflater.from(mActivity);
    		final View textEntryView1 = factory1.inflate(R.layout.dialog_movement, null);

    		OnClickListener moveStraightListener = new OnClickListener(){
    			
    			public void onClick(View v) {
    				float[] user_location = LocationUtils.moveTo(getUserLocation(), ARCompassManager.getAzimuth(getAngles()), 
    						LocationUtils.MOVE_STRAIGHT, 1);

    				setUserLocation(user_location);
    				setUserLocationFixed(user_location);
    				
    				if(onLocationChangeListener != null)
    					onLocationChangeListener.onChange(user_location);
    				
    				mActivity.dismissDialog(DIALOG_MOVE);
    			}

    		};

    		OnClickListener moveRightListener = new OnClickListener(){
    			
    			public void onClick(View v) {
    				float[] user_location = LocationUtils.moveTo(getUserLocation(), ARCompassManager.getAzimuth(getAngles()), 
    						LocationUtils.MOVE_RIGHT, 1);

    				setUserLocation(user_location);
    				setUserLocationFixed(user_location);
    				
    				if(onLocationChangeListener != null)
    					onLocationChangeListener.onChange(user_location);
    				
    				mActivity.dismissDialog(DIALOG_MOVE);
    			}

    		};

    		OnClickListener moveBackListener = new OnClickListener(){
    			
    			public void onClick(View v) {
    				float[] user_location = LocationUtils.moveTo(getUserLocation(), ARCompassManager.getAzimuth(getAngles()), 
    						LocationUtils.MOVE_BACK, 1);

    				setUserLocation(user_location);
    				setUserLocationFixed(user_location);
    				
    				if(onLocationChangeListener != null)
    					onLocationChangeListener.onChange(user_location);
    				
    				mActivity.dismissDialog(DIALOG_MOVE);
    			}

    		};

    		OnClickListener moveLeftListener = new OnClickListener(){
    			
    			public void onClick(View v) {
    				float[] user_location = LocationUtils.moveTo(getUserLocation(), ARCompassManager.getAzimuth(getAngles()), 
    						LocationUtils.MOVE_LEFT, 1);

    				setUserLocation(user_location);
    				setUserLocationFixed(user_location);
    				
    				if(onLocationChangeListener != null)
    					onLocationChangeListener.onChange(user_location);
    				
    				mActivity.dismissDialog(DIALOG_MOVE);
    			}

    		};

    		Button btnStraight = (Button) textEntryView1.findViewById (R.id.btMoveStraight);
    		btnStraight.setClickable(true);
    		btnStraight.setOnClickListener(moveStraightListener);

    		Button btnBack = (Button) textEntryView1.findViewById (R.id.btMoveBack);
    		btnBack.setClickable(true);
    		btnBack.setOnClickListener(moveBackListener);

    		Button btnLeft = (Button) textEntryView1.findViewById (R.id.btMoveLeft);
    		btnLeft.setClickable(true);
    		btnLeft.setOnClickListener(moveLeftListener);

    		Button btnRight = (Button) textEntryView1.findViewById (R.id.btMoveRight);
    		btnRight.setClickable(true);
    		btnRight.setOnClickListener(moveRightListener);

    		String explaination = "";
    		
    		if(isLocationServiceOn!=-1){
    			btnLeft.setVisibility(View.GONE);
    			btnRight.setVisibility(View.GONE);
    			btnStraight.setVisibility(View.GONE);
    			btnBack.setVisibility(View.GONE);
    			if(getSavingType() == ARTagManager.TAG_ACCURATE_LINE)
    				explaination += mActivity.getString(R.string.label_tip_accurate_buttons_line_gps);
    			else
    				explaination += mActivity.getString(R.string.label_tip_accurate_buttons_side_gps);
    		}else{

    			if(getSavingType() == ARTagManager.TAG_ACCURATE_LINE){
    				btnLeft.setVisibility(View.INVISIBLE);
    				btnRight.setVisibility(View.INVISIBLE);
    				explaination += mActivity.getString(R.string.label_tip_accurate_buttons_line_nogps);

    			}else{
    				btnStraight.setVisibility(View.INVISIBLE);
    				btnBack.setVisibility(View.INVISIBLE);
    				explaination += mActivity.getString(R.string.label_tip_accurate_buttons_side_nogps);
    			}
    		}

			explaination += mActivity.getString(R.string.label_tip_accurate_buttons);
    		TextView tv_explain = (TextView)textEntryView1.findViewById(R.id.tvMoveExplain);
    		tv_explain.setText(explaination);
    		
    		AlertDialog.Builder d = new AlertDialog.Builder(mActivity)
    		.setCancelable(false)
    		.setTitle(R.string.label_method_accurate_title)
    		.setView(textEntryView1)
    		.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {

    				/* User clicked OK so do some stuff */ 
    				layers.removeExtraElement(tagIFContainer);
    				launchResult();
    				mActivity.removeDialog(DIALOG_MOVE);

    			}
    		})
    		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {

    				/* User clicked OK so do some stuff */ 

    				onTaggingFinishedListener.onFinish(false);
    				setSavingType(TAG_NONE);

    				layers.removeExtraElement(tagIFContainer);
    				mActivity.removeDialog(DIALOG_MOVE);
    			}
    		});
    		
    		if(isLocationServiceOn!=-1)
    			d.setNeutralButton(R.string.skip, new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int whichButton) {}
        		});
    		
    		return d.create();
    	}
    	return null;
    }
    
    public boolean onActivityResult (int requestCode, int resultCode, Intent data) { 
    	
    	if((content_attacher != null) && content_attacher.onActivityResult(requestCode, resultCode, data))
    		return true;
    	
		switch (requestCode) { 
		case ACTIVITY_MAP:

			if( resultCode != Activity.RESULT_CANCELED ) {

				float[] location = {data.getFloatExtra("RES_LATITUDE", 0), data.getFloatExtra("RES_LONGITUDE", 0), 0};
				setResourceLocation(location);

				launchResult();	    		
			} else{
				onTaggingFinishedListener.onFinish(false);
				setSavingType(TAG_NONE);
			}
			return true;

//		case ACTIVITY_RESULT:
//
//			if( resultCode != Activity.RESULT_CANCELED ) {
//				GeoNode node = (GeoNode) data.getSerializableExtra("RES_NODE");
//				if((node != null) && (res_list != null))
//						res_list.add(new ARGeoNode((ARBase) mActivity, node, layers.getInfoLayer()));
//				onTaggingFinishedListener.onFinish(true);	    		
//			} else{
//				onTaggingFinishedListener.onFinish(false);
//			}
//			layers.removeExtraElement(tagIFContainer);
//			return true;
    	default:
    		break;
		}
		return false;
    }
    
    
    
    

    public void setOnLocationChangeListener(OnLocationChangeListener onLocationChangeListener){
    	this.onLocationChangeListener = onLocationChangeListener;
    }
    
    public void setOnTaggingFinishedListener(OnTaggingFinishedListener onTaggingFinishedListener){
    	this.onTaggingFinishedListener = onTaggingFinishedListener;
    }
    
    public void unregisterListeners(){
    	onLocationChangeListener = null;
    	onTaggingFinishedListener = null;
    }
    
    public interface OnLocationChangeListener {
		public abstract void onChange(float[] values);
	}
    
    public interface OnTaggingFinishedListener {
		public abstract void onFinish(boolean success);
	}
}