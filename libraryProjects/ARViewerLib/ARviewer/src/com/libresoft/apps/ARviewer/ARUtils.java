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

import java.util.ArrayList;

import com.libresoft.sdk.ARviewer.Types.GeoNode;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;

public class ARUtils {
    
    public static ArrayList<ARGeoNode> cleanNoLocation(Activity activity, 
    		ARLayerManager layers, 
    		ArrayList<GeoNode> list, 
    		float[] location,
    		float distanceFilter){
    	ArrayList<ARGeoNode> cleaned_list = new ArrayList<ARGeoNode>();
    	
    	Location mLocation = new Location("");
    	mLocation.setLatitude(location[0]);
    	mLocation.setLongitude(location[1]);
    	
		Location resLocation = new Location("");
    	for(int i = list.size() - 1; i >= 0 ; i--){
    		GeoNode resource = list.get(i);
    		if(resource == null){
    			Log.e("ARUtils", "Vacio");
    			continue;
    		}
    		if((resource.getLatitude() != -1.0) && (resource.getLongitude() != -1.0)){
    			if(distanceFilter > 0){
    				resLocation.setLatitude(resource.getLatitude());
    				resLocation.setLongitude(resource.getLongitude());
    				resLocation.setAltitude(mLocation.getAltitude());

    				if(mLocation.distanceTo(resLocation) > distanceFilter)
    					continue;
    			}
    			ARGeoNode node = new ARGeoNode((ARBase) activity, resource, layers.getInfoLayer());
    			cleaned_list.add(node);
    		}
    	}
    	
    	return cleaned_list;
    }
    
    public static boolean checkNoAltitudeInfo(ArrayList<ARGeoNode> list){
    	int length = list.size();
    	for(int i = (length - 1); i > -1; i--)
    		if(list.get(i).getGeoNode().getAltitude() < 0)
    			return true;
    	return false;
    }
    
    // Transformation of px in dip
    
    public static float transformPixInDip(Context mContext, float pix){
    	
    	return ((pix * mContext.getResources().getDisplayMetrics().density) + 0.5f);
    	
    }
    
    
}