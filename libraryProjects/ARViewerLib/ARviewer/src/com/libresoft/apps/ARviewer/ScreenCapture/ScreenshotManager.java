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
package com.libresoft.apps.ARviewer.ScreenCapture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.libresoft.apps.ARviewer.R;
import com.libresoft.apps.ARviewer.File.FileManager;
import com.libresoft.apps.ARviewer.Overlays.CamPreview;
import com.libresoft.apps.ARviewer.Overlays.CamPreview.OnFrameReadyListener;
import com.libresoft.apps.ARviewer.ScreenCapture.ScreenshotUI.OnSaveListener;

public class ScreenshotManager{

	private static final int MENU_TAKE_SCREENSHOT = 10700;

	private Context mContext;
	private RelativeLayout layers = null;
	private Bitmap base_image = null;
	private CamPreview myCam = null;
	private OnFrameReadyListener onFrameReadyListener = null;
	
	private OnSaveListener saveListener = new OnSaveListener() {
		@Override
		public void onSave(Bitmap bitmap) {
			if(bitmap != null){
				FileManager.getInstance().exportImageFile(bitmap);
				Toast.makeText(mContext, "Done", Toast.LENGTH_SHORT).show();
			}
			clearAll();
		}
	};
	
	public ScreenshotManager(Context context){
		this.mContext = context;
	}
	
	public void setLayers (RelativeLayout layers){
		this.layers = layers;
	}
	
	public void setCam(CamPreview myCam, OnFrameReadyListener onFrameReadyListener){
		this.myCam = myCam;
		this.onFrameReadyListener = onFrameReadyListener;
	}
	
	public void setBaseBitmap(Bitmap base_image){
		this.base_image = base_image;
	}
	
	public void clearAll(){
		layers = null;
		base_image.recycle();
		base_image = null;
//		myCam.clearOnFrameReadyListener();
//		myCam = null;
//		onFrameReadyListener = null;
	}
	
	public boolean takeScreenshot(){
		return takeScreenshot(mContext, layers, base_image);
	}
	
	public boolean takeScreenshot(Context context, RelativeLayout layers, Bitmap base){
		
		RelativeLayout rl = null;
		if(base != null){
			rl = new RelativeLayout(context);
			
			Bitmap screenshot = Bitmap.createBitmap(layers.getWidth(), layers.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas cv = new Canvas(screenshot);
			layers.draw(cv);
			
			ImageView iv1 = null;
			ImageView iv2 = null;
//			screenshot2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			
			iv1 = new ImageView(context);
			iv1.setImageBitmap(base);
			rl.addView(iv1, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			
			iv2 = new ImageView(context);
			iv2.setImageBitmap(screenshot);
			rl.addView(iv2, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
			
//			Canvas cv2 = new Canvas(screenshot2);
//			rl.draw(cv2);
		}else
			rl = layers;
		
//		FileManager.getInstance().exportImageFile(screenshot2);
		ScreenshotUI sui = new ScreenshotUI(layers);
		sui.setOnSaveListener(saveListener);
		sui.createInterface(mContext, rl);
		return true;
	}

	public void onCreateOptionsMenu(Menu menu){
		menu.add(0,MENU_TAKE_SCREENSHOT, 0, "Take screenshot")
			.setIcon(R.drawable.camera);
	}

	public boolean onOptionsItemSelected (MenuItem item){
		switch(item.getItemId()){

		case MENU_TAKE_SCREENSHOT:
			if(myCam != null)
				myCam.setOnFrameReadyListener(onFrameReadyListener);
			break;

		default:
			return false;
		}
		return true;
	}
}