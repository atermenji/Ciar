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
 *  Authors : Roberto Calvo Palomino <rocapal@libresoft.es>
 *  		  Juan Francisco Gato Luis <jfcogato@libresoft.es>
 *
 */

package com.libresoft.sdk.ARviewer.Types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;


public class GenericLayer extends GeoNode implements Serializable {

	/** 
	 * 
	 */
	private static final long serialVersionUID = -6501648066678686473L;
	
	private String mPattern;
	private Integer mPage;
	
	private String mLayer_type, mName, mDescription;
	private Boolean mWriteable, mFree;
	
	byte[]  mByteBitMapImage;
	
	protected ArrayList<GeoNode> mArrayNodes = new ArrayList<GeoNode>();
	private ArrayList<Category> mCategories = new ArrayList<Category>();
	
	public GenericLayer (Integer id, String layer_type, String name, String description, String since,
			Double latitude, Double longitude, Double altitude, Double radius, String since_position){
		
		super(id, latitude, longitude, altitude, radius, since, since_position );
		
		mWriteable = false;
		mLayer_type = layer_type;
		mName = name;
		mFree = true;
		mDescription = description;
		
		mByteBitMapImage = null;
	}
	
	public boolean getWriteable (){
		return mWriteable;
	}
	public void setWriteable(Boolean mWriteable){
		this.mWriteable = mWriteable;
	}
	public String getNameLayer() {
		return mName;
	}
	public void setNameLayer(String mName) {
		this.mName = mName;
	}
	public String getPattern() {
		return mPattern;
	}
	public void setPattern(String mPattern) {
		this.mPattern = mPattern;
	}
	
	public ArrayList<GeoNode> getNodes()
	{
		return mArrayNodes;
	}
	
	public void setNodes ( ArrayList<GeoNode> nodes )
	{
		mArrayNodes = nodes;
	}
	public String getDescription(){
		return mDescription;
	}
	
	public Bitmap getIcon ()
	{
		return null;
	}
	
	public boolean isBitmapImageLoaded(){
		return (mByteBitMapImage != null);
	}

	public void resetPagination()
	{
		mPage=1;
	}
	
	public void nextPage()
	{
		mPage++;
	}
	
	public void previousPage()
	{
		if (mPage>1)
			mPage--;
	}
	
	public Integer getCurrentPage()
	{
		return mPage;
	}
	
	public void SetCurrentPage(Integer page)
	{
		mPage = page;
	}
	
	
}
