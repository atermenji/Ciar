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

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.libresoft.apps.ARviewer.Utils.LocationUtils;
import com.libresoft.apps.ARviewer.Utils.GeoNames.AltitudeManager;
import com.libresoft.sdk.ARviewer.Types.GenericLayer;

public class ARActivity extends SherlockFragmentActivity{
	
	private ARLayerManager layers;
	
	private ArrayList<ARGeoNode> resources_list = null;
	private GenericLayer mylayer = null;
	private float[] location = new float[3];  // designed for [latitude, longitude, altitude]
	private boolean is_threshold;
	private float threshold;
	private boolean use_height = true;
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layers = new ARLayerManager(this);
    }
    
    protected void setResourcesList(ArrayList<ARGeoNode> resources_list){
    	this.resources_list = resources_list;
    }
    
    protected void setMyLayer(GenericLayer mylayer){
    	this.mylayer = mylayer;
    }
    
    protected void useHeight(boolean use_height){
    	this.use_height = use_height;
    }
    
    protected boolean isUsingHeight(){
    	return use_height;
    }
    
    protected void useThreshold(boolean is_threshold){
    	this.is_threshold = is_threshold;
    }
    
    protected void setThreshold(float threshold){
    	this.threshold = threshold;
    }
    
    public ArrayList<ARGeoNode> getResourcesList(){
    	return resources_list;
    }
    
    protected GenericLayer getMyLayer(){
    	return mylayer;
    }
    
    protected void setLocation(float[] location){
    	this.location = location.clone();
    }
    
    protected float[] getLocation(){
    	return location;
    }
    
    public ARLayerManager getLayers(){
    	return layers;
    }
    
	protected void refreshResourceDrawns(float[] values){

		if(resources_list==null)
			return;
		if(resources_list.isEmpty())
			return;

		int sizeList = resources_list.size();

		ARGeoNode resource;
		for(int i=sizeList; i>0; i--){
			resource = resources_list.get(i-1);
			
			if(resource == null)
				return;
			
			float distance = resource.distanceToResource(location);
			float res_azimuth = resource.azimuthToResource(location);
			float azimuth_user = LocationUtils.calculateAzimuthFromUser(ARCompassManager.getAzimuth(values),res_azimuth);

			float[] resource_loc = {(float)(double)resource.getGeoNode().getLatitude(), (float)(double)resource.getGeoNode().getLongitude()};
			float azimuth_camera = LocationUtils.calculateAngleToCam(location, resource_loc, ARCompassManager.getAzimuth(values), azimuth_user, LocationUtils.DEV_DISTANCE);


			float res_roll;
			if(!use_height || (resource.getGeoNode().getAltitude() == AltitudeManager.NO_ALTITUDE_VALUE) || (is_threshold && (distance > threshold)))
				res_roll = 90;
			else
				res_roll = resource.rollToResource(resource.distanceToResource(location));

			float elevation = LocationUtils.calculateRollFromUser(ARCompassManager.getElevation(values), res_roll);

			if (!resource.isLoaded()){
				resource.createDrawn(this);
				resource.setLoaded(true);
			}

			resource.setDrawnValues(azimuth_camera, res_azimuth, elevation, distance);

			if(!layers.isChildInResourcesList(resource.getDrawn()))
				layers.addResourceElement(resource.getDrawn(), null);
			resource.invalidateDrawn();

		}
	}
}