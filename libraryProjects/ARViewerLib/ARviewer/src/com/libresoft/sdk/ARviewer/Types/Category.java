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
 *  Author: Juan Francisco Gato Luis <jfcogato@gsyc.es>
 *
 */

package com.libresoft.sdk.ARviewer.Types;
 
import java.io.Serializable;

public class Category implements Serializable {
	
	private Integer mId;
	private String mName;
	private String mDescription;
	
	private static final long serialVersionUID = -6501648066678686473L;
	
	public Category (Integer id, String name, String description){
		mId = id;
		mName = name;
		mDescription = description;
	}
	
	public Integer getId(){
		return mId;
	}
	
	public String getName(){
		return mName;
	}
	
	public String getDescription(){
		return mDescription;
	}

}
