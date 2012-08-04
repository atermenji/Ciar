/*
 *
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

import java.io.Serializable;


public class Note extends GeoNode implements Serializable {

	// Serializable UID
	private static final long serialVersionUID = -9121943636715457236L;
	
	private String mTitle;
	private String mBody;
	
	public Note (Integer id, String title, String body, 
			    Double latitude, Double longitude, Double altitude, Double radius, String since,
			    String position_since, Double distance)
	{
		super (id, latitude,longitude, altitude, radius, since, position_since);
		
	
		mTitle = title;
		mBody  = body;
	
	}
	
	public void setTitle(String mTitle){
		this.mTitle = mTitle;
	}
	
	public void setBody(String mBody){
		this.mBody = mBody;
	}
	
	public String getTitle()
	{
		return mTitle;
	}
	
	public String getBody()
	{
		return mBody;
	}

	
}
