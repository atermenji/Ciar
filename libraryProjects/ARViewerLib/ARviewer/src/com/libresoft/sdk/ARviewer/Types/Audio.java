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
 *  Author : Roberto Calvo Palomino <rocapal@gsyc.es>
 *
 */


package com.libresoft.sdk.ARviewer.Types;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class Audio extends GeoNode implements Serializable {

	// Serializable UID
	private static final long serialVersionUID = -9121943636715457236L;
	
	private String mName;
	private String mDescription;
	private String mPath;
	private String mUrl;
	
	
	public Audio(Integer id, String Name, String Description, String Url, String Path,
			     Double latitude, Double longitude,
				 Double altitude, Double radius, String since,
				 String position_since, Double distance) {
		
		super(id, latitude, longitude, altitude, radius, since, position_since);
		
		mName = Name;
		mDescription = Description;
		mPath = Path;
		mUrl = Url;
	}
	
	
	public String getName() {
		return mName;
	}


	public void setName(String mName) {
		this.mName = mName;
	}


	public String getDescription() {
		return mDescription;
	}


	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}


	public String getPath() {
		return mPath;
	}


	public void setPath(String mPath) {
		this.mPath = mPath;
	}
	
	public String getUrl() {
		return mUrl;
	}


	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	
	public InputStream getAudio()
	{
		
		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			
			if (mUrl != null){
				HttpPost httpost = new HttpPost(mUrl);

				/* Send and receive the petition */
				HttpResponse response = httpclient.execute(httpost);
				HttpEntity entity = response.getEntity(); 

				return entity.getContent();
			}else if(mPath != null){
				FileInputStream fin = new FileInputStream(new File(mPath));
				return fin;
			}
			return null;
		}
		
		catch (IOException e) {
	        Log.e("Audio" ,e.getMessage());
	        return null;
		}
	}
}
