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

import com.libresoft.apps.ARviewer.Utils.LocationUtils;

import android.location.Location;


public class AccurateTag{
	
	
	private static final int MAX_POINTS = 10;
	
	private TagElem[] tagElem;
	private int counter;
	
	
	public AccurateTag(){
		this.tagElem = new TagElem[MAX_POINTS];
		counter = 0;
	}
	
	public boolean addElem(Location loc, float orientation, float elevation){
		
		if (counter >= MAX_POINTS)
			return false;
		
		TagElem tag_elem = new TagElem(loc, orientation, elevation);
		
		tagElem[counter] = tag_elem; 
		counter++;
		
		return true;
	}
	
	public boolean addElem(float[] loc, float orientation, float elevation){
		
		if (counter >= MAX_POINTS)
			return false;
		
		Location loc2 = new Location("");
		loc2.setLatitude(loc[0]);
		loc2.setLongitude(loc[1]);
		
		TagElem tag_elem = new TagElem(loc2, orientation, elevation);
		
		tagElem[counter] = tag_elem; 
		counter++;
		
		return true;
	}
	
	public TagElem getElem(int i){
		return tagElem[i];
	}
	
	public Location getLocation(){
		Location loc = null;
		float[] mean_point = {0, 0};
		float[] aux_point = new float[2];
		int count = 0;
		int size = counter ;
		
		try {
			for (int i = (size - 1); i >0 ; i-- ){
				for (int j = (i - 1); j >= 0; j--){
					count ++;
					aux_point = LocationUtils.calculateIntersection(tagElem[i].getPosition(), tagElem[j].getPosition(), 
							tagElem[i].getOrientation(), tagElem[j].getOrientation());
					
					if( Float.isInfinite(aux_point[0]) || Float.isInfinite(aux_point[1])
							|| Float.isNaN(aux_point[0]) || Float.isNaN(aux_point[1]) ){
						count --;
						continue;
					}
					
					mean_point[0] += aux_point[0];
					mean_point[1] += aux_point[1];
				}
			}
			
			if(count <= 0)
				return null;
			
			loc = new Location("");
			loc.setLatitude(mean_point[0] / count);
			loc.setLongitude(mean_point[1] / count);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		return loc;
	}
	
	public float[] getLocationArray(){
		float[] mean_point = {0, 0};
		float[] aux_point = new float[2];
		int count = 0;
		int size = counter;
		
		try {
			for (int i = (size - 1); i >0 ; i-- ){
				for (int j = (i - 1); j >= 0; j--){
					count ++;
					aux_point = LocationUtils.calculateIntersection(tagElem[i].getPosition(), tagElem[j].getPosition(), 
							tagElem[i].getOrientation(), tagElem[j].getOrientation());
					
					if( Float.isInfinite(aux_point[0]) || Float.isInfinite(aux_point[1])
							|| Float.isNaN(aux_point[0]) || Float.isNaN(aux_point[1]) ){
						count --;
						continue;
					}
					
					mean_point[0] += aux_point[0];
					mean_point[1] += aux_point[1];
				}
			}
			
			
			mean_point[0] = (mean_point[0] / count);
			mean_point[1] = (mean_point[1] / count);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		return mean_point;
	}
	
	public float[] getLineLocationArray(){
		float[] mean_point = {0, 0};
		float[] aux_point = new float[2];
		int count = 0;
		int size = counter;
		
		try {
			float master_orientation = tagElem[0].getOrientation();
			for (int i = (size - 1); i >0 ; i-- ){
				for (int j = (i - 1); j >= 0; j--){
					count ++;
					aux_point = LocationUtils.calculateIntersectionElevation(tagElem[i].getPosition(), tagElem[j].getPosition(), 
							master_orientation, tagElem[i].getElevation(), tagElem[j].getElevation());
					
					if( Float.isInfinite(aux_point[0]) || Float.isInfinite(aux_point[1])
							|| Float.isNaN(aux_point[0]) || Float.isNaN(aux_point[1]) ){
						count --;
						continue;
					}
					
					mean_point[0] += aux_point[0];
					mean_point[1] += aux_point[1];
				}
			}
			
			if(count <= 0)
				return null;
			
			mean_point[0] = (mean_point[0] / count);
			mean_point[1] = (mean_point[1] / count);
			
			
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		return mean_point;
	}
	
	public boolean isEmpty(){
		return (counter == 0);
	}
	
}