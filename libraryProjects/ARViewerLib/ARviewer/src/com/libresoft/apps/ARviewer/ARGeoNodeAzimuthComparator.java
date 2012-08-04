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

import java.util.Comparator;

import com.libresoft.apps.ARviewer.Location.ARLocationManager;


public class ARGeoNodeAzimuthComparator implements Comparator<ARGeoNode>
{

	public int compare(ARGeoNode node1, ARGeoNode node2) {
		// TODO Auto-generated method stub
		float[] location = {(float) ARLocationManager.getInstance(null).getLocation().getLatitude(), 
				(float) ARLocationManager.getInstance(null).getLocation().getLongitude()};
		
		float azim1 = node1.azimuthToResource(location);
		float azim2 = node2.azimuthToResource(location);
		
		if (azim1>azim2)
			return +1;
		else if (azim1<azim2)
			return -1;
		else
			return 0;
	}
	
	
}