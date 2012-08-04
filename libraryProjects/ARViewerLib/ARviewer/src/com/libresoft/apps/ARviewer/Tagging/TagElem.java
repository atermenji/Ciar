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

import android.location.Location;


public class TagElem{
	
	private Location loc;
	private float orientation, elevation;
	
	public TagElem(Location loc, float orientation, float elevation){
		this.loc = loc;
		this.orientation = orientation;
		this.elevation = elevation;
	}
	
	public float getOrientation(){
		return orientation;
	}
	
	public float getElevation(){
		return elevation;
	}
	
	public float getLatitude(){
		return (float) loc.getLatitude();
	}
	
	public float getLongitude(){
		return (float) loc.getLongitude();
	}
	
	public Location getLocation(){
		return loc;
	}
	
	public float[] getPosition(){
		float[] pos = {(float) loc.getLatitude(), (float) loc.getLongitude()};
		return pos;
	}
	
}