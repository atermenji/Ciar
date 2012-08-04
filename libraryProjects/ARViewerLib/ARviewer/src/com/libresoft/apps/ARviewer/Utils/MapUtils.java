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
 *  Author : Roberto Calvo Palomino <rocapal@gsyc.es>
 *  		 Raúl Román <rocapal@gsyc.es>
 *
 */

package com.libresoft.apps.ARviewer.Utils;

import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapUtils {

	
   static public Location getLocFromXY(float x_, float y_inverted, MapView map, float orientation){
    	double screenYsize = map.getHeight();
    	double screenXsize = map.getWidth();
    	GeoPoint center = map.getMapCenter();
    	double latitudeYsize = map.getLatitudeSpan();
    	double longitudeXsize = map.getLongitudeSpan();
    	double x = x_;
    	double y = screenYsize - y_inverted;
    	
    	if (orientation != 0){
        	// rotation problem avoiding
    		double dif_x = x - screenXsize/2;
    		double dif_y = y - screenYsize/2;
    		
    		x = dif_x * Math.cos(Math.toRadians(orientation)) + dif_y * Math.sin(Math.toRadians(orientation)) + screenXsize/2;
    		y = dif_y * Math.cos(Math.toRadians(orientation)) - dif_x * Math.sin(Math.toRadians(orientation)) + screenYsize/2;
    	}
    	
    	
//    	float y = (float) (((float)screenYsize) - y_inverted);
    	//Obtaining how many degrees of Latitude and Longitude has a single pixel
    	double pix_Xlongitude = longitudeXsize/screenXsize;
    	double pix_Ylatitude = latitudeYsize/screenYsize;
    	
    	//Obtaining the distance (in microdegrees) to the center
    	double pixcenter_lat = screenYsize/2;
    	double pixcenter_long = screenXsize/2;
    	
    	double degrees_lat = ((y - pixcenter_lat)*pix_Ylatitude);
    	double degrees_long = ((x - pixcenter_long)*pix_Xlongitude);
    	
    	//Obtaining the coordinates the user has tapped
    	double lat = (degrees_lat + (double) center.getLatitudeE6())/1E6;
    	double lng = (degrees_long + (double) center.getLongitudeE6())/1E6;
    	
    	Location loc = new Location("MANUAL_PROVIDER");
    	loc.setLatitude(lat);
    	loc.setLongitude(lng);
    	
    	return loc;
    }
    
    
}
