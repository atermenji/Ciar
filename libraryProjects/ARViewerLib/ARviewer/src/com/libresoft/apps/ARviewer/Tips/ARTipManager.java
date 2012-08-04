/*
 *
 *  Copyright (C) 2011 GSyC/LibreSoft, Universidad Rey Juan Carlos.
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

package com.libresoft.apps.ARviewer.Tips;

import android.content.Context;
import android.widget.Toast;

public class ARTipManager{
	private static boolean isTip = true;
	
	private static ARTipManager instance = null;
	
	private ARTipManager(){}
	
	public static ARTipManager getInstance(){
		if(instance == null)
			instance = new ARTipManager();
		return instance;
	}
	
	public static void enableTips(boolean isTip){
		ARTipManager.isTip = isTip;
	}
	
	public void showTipShort(Context mContext, int textId){
		if(isTip)
			Toast.makeText(mContext, textId, Toast.LENGTH_SHORT).show();
	}
	
	public void showTipLong(Context mContext, int textId){
		if(isTip)
			Toast.makeText(mContext, textId, Toast.LENGTH_LONG).show();
	}
	
	
}