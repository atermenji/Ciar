/*
 *  Copyright (C) 2009-2011 GSyC/LibreSoft, Universidad Rey Juan Carlos
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
 *  Author : Roberto Calvo Palomino <rocapal@libresoft.es>
 *
 */


package com.libresoft.sdk.ARviewer.Types;

import java.util.Comparator;

import android.location.Location;

public class GeoNodePositionComparator implements Comparator<GeoNode>
{
	private Location mCurrentLocation;
	
	public void setCurrentLocation (Location currentLocation)
	{
		mCurrentLocation = currentLocation;
		
	}
	public int compare(GeoNode node1, GeoNode node2) {
		// TODO Auto-generated method stub
		
		if (node1 == null || node2 ==null)
			return 0;
		
		float dist1 = calculateDistance (mCurrentLocation, node1.getLocation());
		float dist2 = calculateDistance (mCurrentLocation, node2.getLocation());
		
		if (dist1>dist2)
			return +1;
		else if (dist1<dist2)
			return -1;
		else
			return 0;
	}
	
    /**
     * Calculate the distance (in meters) between two Location points
     * 
     * @param Source Location Point
     * @param Destination Location Point
     * 
     * @return The distance in meters.
     */
	
	private static float calculateDistance (Location pointSource, Location pointDest)
	{
		
		if ((pointSource == null) || (pointDest== null))
			return 0;
        
        float dist = pointSource.distanceTo(pointDest);
        
        return dist;
	}
	
	
}