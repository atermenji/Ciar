/*
 *
 *  Copyright (C) 2009 GSyC/LibreSoft, Universidad Rey Juan Carlos.
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

package com.libresoft.apps.ARviewer.Location;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.libresoft.apps.ARviewer.R;
import com.libresoft.apps.ARviewer.Maps.Overlays.PositionOverlay;
import com.libresoft.apps.ARviewer.Tips.ARTipManager;
import com.libresoft.apps.ARviewer.Utils.MapUtils;

public class LocationWays extends MapActivity {
	private MapView map;
	private MapController controller;
	public Location currentLocation = null;
	
	ProgressDialog pd = null;
	
	private static final int MENU_SATELLITE = Menu.FIRST + 1;
	private static final int MENU_LOCATION_MANUAL = Menu.FIRST + 2;
	
	private static final int DIALOG_MANUAL_LOCATION = 1;
	
	private boolean capture = false;
	private GeoPoint map_center = null;
	
	public void init(){
		
//		map = (MapView) findViewById(R.id.locationMap);
		map = new MapView(this, getString(R.string.map_key));
		setContentView(map);
		controller = map.getController();

		//Adding a zoom control
		map.setBuiltInZoomControls(true);
		
		map.setClickable(true);
//		map.setOnTouchListener(touchListener);
		
		if(currentLocation!=null){
			setPositionInMap(17);
		}
		map.setSatellite(false);
		map.setStreetView(false);
		
		map_center = map.getMapCenter();
	}
	
    /** Called when the activity is first created. */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        currentLocation = ARLocationManager.getInstance(this).getLocation();
        
        if ((currentLocation == null) || (currentLocation.getLatitude() == ARLocationManager.NO_LATLONG) || 
        		(currentLocation.getLongitude() == ARLocationManager.NO_LATLONG)){
        	currentLocation = new Location ("no location");
        }
        
        init();
        ARTipManager.getInstance().showTipLong(this, R.string.map_location_start);
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
		    	
				break;
			
			case MotionEvent.ACTION_UP:
				if (!capture)
					break;
				capture = false;
				
				Location loc = MapUtils.getLocFromXY(ev.getX(), ev.getY(), map, 0);
				
				setCurrentLocation(loc);
				setPositionInMap(0);
				
				Toast.makeText(getBaseContext(), 
                        "Location updated", 
                        Toast.LENGTH_SHORT).show();
				break;
				
			default:
				if(Build.VERSION.SDK_INT > Build.VERSION_CODES.DONUT)
					capture = false;
				break;
			}
    		
    	}
    	
    	return res;
    }
    
	private void setPositionInMap(int zoom){
		
		GeoPoint point = new GeoPoint(  (int) (currentLocation.getLatitude() * 1E6), 
				  (int) (currentLocation.getLongitude() * 1E6));
		
		controller.animateTo(point);
		if(zoom != 0)
			controller.setZoom(zoom);
		//OverlayItem marker = new OverlayItem(point, "You", "Now");
		PositionOverlay myposOverlay = new PositionOverlay(this);
		myposOverlay.setPoint(point);
        map.getOverlays().clear();
	    final List<Overlay> overlays = map.getOverlays();	   
	    overlays.add( myposOverlay );
	}
	
	private void setCurrentLocation(Location loc){
		currentLocation = loc;
		ARLocationManager.getInstance(this).setLocation(loc);
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
        
    	switch (id) {
		        
	    	case DIALOG_MANUAL_LOCATION:
	    		
	    		LayoutInflater factory3 = LayoutInflater.from(this);
	    		final View textEntryView3 = factory3.inflate(R.layout.manual, null);
	    		
	    		final EditText edit2 = (EditText) textEntryView3.findViewById (R.id.txtmanual);
	    		edit2.setText("");
	    		
		        return new AlertDialog.Builder(this)
		        .setView(textEntryView3)
		        .setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		            	if (!edit2.getText().toString().equals(""))
		            		searchManual(edit2.getText().toString());
		            	else
		            		Toast.makeText(getBaseContext(), 
		                            R.string.lw_empty_address, 
		                            Toast.LENGTH_LONG).show();
		            }
		        })
		        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {}
		        })
		        .create();
	
	    }
    	
    	return null;
    }
	
    public void searchManual(String address){
		//manual = (LinearLayout) findViewById(R.id.loc_manual);

		Geocoder gcod = new Geocoder(getApplicationContext());
		try {
			List<Address> addressList = gcod.getFromLocationName(address, 1);

			if (addressList.isEmpty()){

				Toast.makeText(getBaseContext(), 
						R.string.lw_not_found, 
						Toast.LENGTH_LONG).show();
			}
			if (addressList.get(0).hasLatitude() && addressList.get(0).hasLongitude()){
				
				Location loc = new Location("Manual");
				loc.setLatitude(addressList.get(0).getLatitude());
				loc.setLongitude(addressList.get(0).getLongitude());
				
				setCurrentLocation(loc);
				setPositionInMap(17);
				//Send coordinates to the server
				Toast.makeText(getBaseContext(), 
                        R.string.lw_update, 
                        Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(getBaseContext(), 
						R.string.lw_fail, 
						Toast.LENGTH_LONG).show();

		} catch (Exception e) {
			Log.e("LocationWays", "", e);
		}
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        super.onCreateOptionsMenu(menu);
		
        menu.add(0, MENU_SATELLITE, 0, R.string.lw_toggle_sat)
				.setIcon(R.drawable.eye)
				.setAlphabeticShortcut('S');
        
        menu.add(0, MENU_LOCATION_MANUAL, 0 , R.string.search)
        			  .setIcon(R.drawable.mundo);
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	
    	switch (item.getItemId()) {
    		
    	case MENU_SATELLITE:
    		map.setSatellite(!map.isSatellite());
	        break;
    		
    	case MENU_LOCATION_MANUAL:
    		showDialog(DIALOG_MANUAL_LOCATION);
    		break;
    		
        }
    	
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
}