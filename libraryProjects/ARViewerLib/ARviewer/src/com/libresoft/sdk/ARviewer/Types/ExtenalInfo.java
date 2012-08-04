/*
 *
 *  Copyright (C) 2008-2010 GSyC/LibreSoft, Universidad Rey Juan Carlos
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

import java.io.Serializable;

public class ExtenalInfo implements Serializable {
	
	// Serializable UID
	private static final long serialVersionUID = -9121943636715457236L;
	
	private String mUrlInfo;
	private String mPhotoThumbUrl;
	private String mPhotoNormalUrl;
	
	public String getUrlInfo() {
		return mUrlInfo;
	}
	public void setUrlInfo(String urlInfo) {
		this.mUrlInfo = urlInfo;
	}
	public String getPhotoThumbUrl() {
		return mPhotoThumbUrl;
	}
	public void setPhotoThumbUrl(String photoThumbUrl) {
		this.mPhotoThumbUrl = photoThumbUrl;
	}
	public String getPhotoNormalUrl() {
		return mPhotoNormalUrl;
	}
	public void setPhotoNormalUrl(String photoNormalUrl) {
		this.mPhotoNormalUrl = photoNormalUrl;
	}


	
}
