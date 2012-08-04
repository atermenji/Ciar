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

package com.libresoft.apps.ARviewer.Utils.GeoNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.libresoft.apps.ARviewer.ARGeoNode;
import com.libresoft.sdk.ARviewer.Types.GeoNode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AltitudeManager{
	public static final double NO_ALTITUDE_VALUE = -10000;
	
	public static final String NO_HEIGHTS = "No_heights";
	public static final String EXISTING_HEIGHTS = "Existing_heights";
	public static final String ALL_HEIGHTS = "All_heights";
	
	private static final String URL = "http://ws.geonames.org/astergdem";
	private static final String URL_B="http://www.geonames.org/gtopo30";
	
	public static double getAltitudeFromLatLong(float latitude, float longitude){
		double altitude = AltitudeManager.NO_ALTITUDE_VALUE;
		
		String data = "?lat=" + Float.toString(latitude) + "&lng=" + Float.toString(longitude);
		
		try{//Aster
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
			HttpGet httpGet = new HttpGet(URL + data);
			
			HttpResponse response = httpclient.execute(httpGet);
	
		    HttpEntity entity = response.getEntity(); 
		    
	        String str = convertStreamToString(entity.getContent());
	        
	        Log.d("GeoNames: ", str);
	        
	        altitude = Double.parseDouble(str);
	        
	        if(altitude < 0)
	        	altitude = 0;
	        
            return altitude;

		}catch (Exception e) {
	        Log.e("GeoNames: ",e.getMessage());
	        return emergencyService(data);
		}
	}
	
	private static double emergencyService(String data){
		double altitude = AltitudeManager.NO_ALTITUDE_VALUE;
		try{//Gtopo30
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
			HttpGet httpGet = new HttpGet(URL_B + data);
			
			HttpResponse response = httpclient.execute(httpGet);
	
		    HttpEntity entity = response.getEntity(); 
		    
	        String str = convertStreamToString(entity.getContent());
	        
	        Log.d("GeoNames: ", str);
	        
	        altitude = Double.parseDouble(str);
	        
	        if(altitude < 0)
	        	altitude = 0;
	        
            return altitude;

		}catch (Exception e) {
	        Log.e("GeoNames: ",e.getMessage());
	        return AltitudeManager.NO_ALTITUDE_VALUE;
		}
		
	}
	
	private static String convertStreamToString (InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8*1024);
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return sb.toString();

    }
	
	public static double getAbsoluteAltitude(Context context, double base_altitude, boolean is_floor){
		double altitude = AltitudeManager.NO_ALTITUDE_VALUE;
		
		if(base_altitude != AltitudeManager.NO_ALTITUDE_VALUE){
			try {
				SharedPreferences sharedPreferences = 
					PreferenceManager.getDefaultSharedPreferences(context);

				float user_height = ((float)sharedPreferences.getInt(AltitudePreferences.KEY_USER_HEIGHT, 175)) / 100;

				if(is_floor && sharedPreferences.getBoolean(AltitudePreferences.KEY_USE_FLOOR, false)){
					int floor_number = sharedPreferences.getInt(AltitudePreferences.KEY_FLOOR, 0);

					altitude = (float) (base_altitude + user_height + floor_number * 3); // 3 meters per room
					Log.d("AltitudeManager", "Using floor");
				}else
					altitude = (float) (base_altitude + user_height);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.e("AltitudeManager", e.toString());
			}
		}
		
		return altitude;
	}
	
	 public static void updateHeights(ArrayList<ARGeoNode> resources_list){
		 if(resources_list == null)
			 return;
		 int length = resources_list.size();
		 for(int i = (length - 1); i > -1; i--){
			 GeoNode resource = resources_list.get(i).getGeoNode();
			 if(resource.getAltitude() == NO_ALTITUDE_VALUE)
				 resource.setAltitude((float)getAltitudeFromLatLong(
						 (float)(double)resource.getLatitude(), 
						 (float)(double)resource.getLongitude()));
		 }
	 }
	
}