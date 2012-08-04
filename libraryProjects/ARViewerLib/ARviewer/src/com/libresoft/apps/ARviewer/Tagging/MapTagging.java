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

package com.libresoft.apps.ARviewer.Tagging;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.libresoft.apps.ARviewer.ARUtils;
import com.libresoft.apps.ARviewer.Constants;
import com.libresoft.apps.ARviewer.R;
import com.libresoft.apps.ARviewer.Maps.Overlays.ResourceOverlay;
import com.libresoft.apps.ARviewer.Utils.MapUtils;


public class MapTagging extends MapActivity {
	
	private static final int MENU_SATELLITE = Menu.FIRST + 1;
	private static final int MENU_END = Menu.FIRST + 2;
	
	private static final int DIALOG_CHARGE = 1;
	
//	private MapView map;
	private MapView map;
//	private RotateableView mRotateableView;
//	private RelativeLayout backLayout;
	private MapController controller;
	public Location currentLocation = null;
	private static float orientation;
	public float[] resource_location = new float[2];
	
	ProgressDialog pd = null;
	
	private boolean capture = false;
	private GeoPoint map_center = null;
	
	public void init(){
		try{
//			LinearLayout zoomLayout = (LinearLayout) findViewById(R.id.location_layout_zoom);

			//map = (RotateableMap) findViewById(R.id.locationMap);
			map = new MapView(this, getString(R.string.map_key));
			
			setContentView(map);
			
			controller = map.getController();
			map.setBuiltInZoomControls(true);
			map.setClickable(true);

//			map.displayZoomControls(true);
			
			showDialog(DIALOG_CHARGE);
			
			map.setSatellite(true);
			map.setStreetView(false);
			

//			map_center = map.getMapCenter();
		}catch(Exception e){
			Toast.makeText(getBaseContext(), 
					"Init: " + e.toString(), 
					Toast.LENGTH_LONG).show();
		}
	}
	
    /** Called when the activity is first created. */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
        	
        	currentLocation = new Location ("TAG");
        	currentLocation.setLatitude(getIntent().getExtras().getFloat("LATITUDE"));
        	currentLocation.setLongitude(getIntent().getExtras().getFloat("LONGITUDE"));
        	
        	orientation = getIntent().getExtras().getFloat("AZIMUTH");
        	init();
        }catch(Exception e){
        	Toast.makeText(getBaseContext(), 
        			"Create: " + e.toString(), 
        			Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
    	int zoom = map.getZoomLevel();
    	boolean res = super.dispatchTouchEvent(ev);
    	if(zoom == map.getZoomLevel()){
    		
			switch(ev.getAction()){
			
			case MotionEvent.ACTION_DOWN:

				map_center = map.getMapCenter();
				capture = true;
				break;
				
			case MotionEvent.ACTION_MOVE:
				
				GeoPoint new_map_center = map.getMapCenter();
		    	
		    	double distance = Math.sqrt(Math.pow(new_map_center.getLatitudeE6()-map_center.getLatitudeE6(),2)+
		    			Math.pow(new_map_center.getLongitudeE6()-map_center.getLongitudeE6(),2));
		    	
		    	map_center = new_map_center;
		    	
		    	if(distance > 1)
		    		capture = false;
				return false;
			
			case MotionEvent.ACTION_UP:
				
				if (!capture)
					break;
				capture = false;
				
				Location loc = MapUtils.getLocFromXY(ev.getX(), ev.getY(), map, 0);
				
				setResourceLocation(loc);
				setPositionInMap();
				
				Toast.makeText(getBaseContext(), 
                        R.string.map_resource_updated, 
                        Toast.LENGTH_SHORT).show();
				break;
				
			default:
				if(Build.VERSION.SDK_INT > Constants.ANDROID_DONUT)
					capture = false;
				break;
			}
    		
    	}
    	
    	return res;
    }
    
	private void setPositionInMap(){
		
        ResourceOverlay myposOverlay = new ResourceOverlay("User");
        myposOverlay.setLocation(currentLocation);
        myposOverlay.setOrientation(orientation);
        myposOverlay.setRange(true);
        
        map.getOverlays().clear();
	    final List<Overlay> overlays = map.getOverlays();	   
	    overlays.add( myposOverlay );
	    
	    if(resource_location[0] != 0){
	    	Location loc = new Location("TAG");
	    	loc.setLatitude(resource_location[0]);
	    	loc.setLongitude(resource_location[1]);

	    	ResourceOverlay resposOverlay = new ResourceOverlay("Resource");
	    	resposOverlay.setLocation(loc);
	    	overlays.add(resposOverlay);
	    }
	}
	
	private void setInitialConfiguration(){
	    controller.setZoom(18);
	    
	    controller.setCenter(new GeoPoint( (int) (currentLocation.getLatitude() * 1E6),
	    		(int) (currentLocation.getLongitude() * 1E6)));
	    
	    
	    Point point= new Point();
		Projection projection = map.getProjection();
		projection.toPixels(new GeoPoint( (int) (currentLocation.getLatitude() * 1E6),
	    		(int) (currentLocation.getLongitude() * 1E6)), point);
		
	    Location l = new Location("");
	    float[] p = calculateOverlayPosition(point, orientation);
	    l = MapUtils.getLocFromXY(p[0], p[1], map, 0);
	    
	    controller.setCenter(new GeoPoint( (int) (l.getLatitude() * 1E6),
	    		(int) (l.getLongitude() * 1E6)));
	}
	
	private float[] calculateOverlayPosition (Point point, float orientation_){
		float[] p = new float[2];
		float orientation = orientation_;
		
		if((orientation < 60) || (orientation >= 300)){
			//DOWN
			if(orientation >= 300)
				orientation = orientation - 360;
			p[0] = (float) (Math.sin(Math.toRadians((orientation/60)*90)) * ARUtils.transformPixInDip(this, 150) +
					point.x);
			p[1] = point.y - ARUtils.transformPixInDip(this, 200);
		}else if((orientation >= 60) && (orientation < 120)){
			//LEFT
			p[0] = point.x + ARUtils.transformPixInDip(this, 150);
			p[1] = (float) (Math.sin(Math.toRadians(((orientation-90)/30)*90)) * ARUtils.transformPixInDip(this, 200) +
					point.y);
		}else if((orientation >= 120) && (orientation < 240)){
			//TOP
			p[0] = (float) (-Math.sin(Math.toRadians(((orientation-180)/60)*90)) * ARUtils.transformPixInDip(this, 150) +
					point.x);
			p[1] = point.y + ARUtils.transformPixInDip(this, 200);
		}else if((orientation >= 240) && (orientation < 300)){
			//RIGHT
			p[0] = point.x - ARUtils.transformPixInDip(this, 150);
			p[1] = (float) (-Math.sin(Math.toRadians(((orientation-270)/30)*90)) * ARUtils.transformPixInDip(this, 200) +
					point.y);
		}
		
		return p;
	}
	
	private void setResourceLocation(Location loc){
		resource_location[0] = (float) loc.getLatitude();
		resource_location[1] = (float) loc.getLongitude();
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        super.onCreateOptionsMenu(menu);
		
        menu.add(0, MENU_SATELLITE, 0, R.string.map_toggle_sat)
				.setIcon(R.drawable.eye)
				.setAlphabeticShortcut('S');
        
        menu.add(0, MENU_END, 0, R.string.done)
        		.setIcon(R.drawable.done);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	
    	switch (item.getItemId()) {
    		
    	case MENU_SATELLITE:
    		if (map.isSatellite())
	    		map.setSatellite(false);
	    	else
	    		map.setSatellite(true);
	        break;
	        
    	case MENU_END:
    		
    		Intent resultIntent = new Intent();
    		resultIntent.putExtra("RES_LATITUDE", resource_location[0]);
    		resultIntent.putExtra("RES_LONGITUDE", resource_location[1]);
    		
    		setResult(Activity.RESULT_OK, resultIntent);
    		finish();
    		
    		break;
    		
    	}
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {       
    	
    	switch (id) {
    	
	    	case DIALOG_CHARGE:
	    		
		        return new AlertDialog.Builder(this)	
		        .setTitle(R.string.map_tagging_charge_title)
		        .setMessage(R.string.map_tagging_charge_message)
		        .setCancelable(false)
		        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	
		                /* User clicked OK so do some stuff */ 
		            	if(currentLocation!=null){
		    				setPositionInMap();
		    				setInitialConfiguration();
		    			}
		            }
		        })
		        .create();

        	
    	}
		return null;
    
	}
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}