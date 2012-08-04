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

import com.libresoft.apps.ARviewer.Overlays.DrawResource;

import android.content.Context;


interface ARNodeDrawingIF{
	
	public DrawResource getDrawn();
	
	public boolean isLoaded();
	
	public void setLoaded(boolean isloaded);
	
	public void setDrawnValues(float azimuth, float abs_azimuth, float elevation, float distance);
	
	public void createDrawn(Context context);
	
	public void bringDrawnToFront();
	
	public void drawSummary(boolean draw);
	
	public void invalidateDrawn();
	
	public float distanceToResource(float[] source);
	
	public float azimuthToResource(float source[]);
	
	public float rollToResource(float distance);
}