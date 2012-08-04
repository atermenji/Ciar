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

package com.libresoft.apps.ARviewer;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.libresoft.apps.ARviewer.ARTagManager.OnLocationChangeListener;
import com.libresoft.apps.ARviewer.ARTagManager.OnTaggingFinishedListener;
import com.libresoft.apps.ARviewer.Overlays.ARSummaryBox;
import com.libresoft.apps.ARviewer.Tips.ARTipManager;
import com.libresoft.sdk.ARviewer.Types.GeoNode;
import com.libresoft.sdk.ARviewer.Types.Photo;
import com.libresoft.sdk.ARviewer.Types.User;
import com.libresoft.sdk.ARviewer.Types.Video;

public class ARlabeling extends ARBase{ 

	private static final int MENU_DONE = 101;
	
    private OnLocationChangeListener onTaggingLocationListener = new OnLocationChangeListener() {
		
		@Override
		public void onChange(float[] values) {
			setLocation(values);
		}
	};
	
    private OnTaggingFinishedListener onTaggingFinishedListener = new OnTaggingFinishedListener() {
		
		@Override
		public void onFinish(boolean success) {
			showMenu = true;
			if(success){
				ARTipManager.getInstance().showTipLong(getBaseContext(), R.string.label_tip_finish);
				showResources();
			}else
				Toast.makeText(getBaseContext(), R.string.label_cancel, Toast.LENGTH_SHORT).show();
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		loadParameters();
		loadConfig(false);
		ARSummaryBox.setShowRemoveButton(true);
		tagManager = new ARTagManager(this, getLayers(), getResourcesList(), getLocation());
		tagManager.setOnLocationChangeListener(onTaggingLocationListener);
		tagManager.setOnTaggingFinishedListener(onTaggingFinishedListener);
		showResources();
		
		ARTipManager.getInstance().showTipLong(this, R.string.label_tip_main);
    }
	
	@Override
	protected boolean loadParameters(){
		super.loadParameters();
		setResourcesList(new ArrayList<ARGeoNode>());
		
		return true;
	}
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	
    	if(showMenu){
    		menu.add(0, MENU_DONE, 0, R.string.return_)
    			.setIcon(R.drawable.done);
    		tagManager.onCreateOptionsMenu(menu);
    	}
    	
        super.onPrepareOptionsMenu(menu);        
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {
    	if(tagManager.onOptionsItemSelected(item))
    		return true;
    	
    	switch (item.getItemId()) {
    	case MENU_DONE:
    		returnTaggedNodes();
    		return true;
    	}

    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {       
    	
    	Dialog diag = tagManager.onCreateDialog(id);
    	if(diag != null)
    		return diag;
    	
    	diag = ARSummaryBox.onCreateDialog(this, id);
    	if(diag != null)
    		return diag;
    	
		return super.onCreateDialog(id);
    
	}
    
	protected void onActivityResult (int requestCode, int resultCode, Intent data) { 
		
    	if (tagManager.onActivityResult(requestCode, resultCode, data))
    		return;
    	super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			returnTaggedNodes();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	
	private void returnTaggedNodes(){
		ArrayList<GeoNode> nodes_list = new ArrayList<GeoNode>();
		ArrayList<ARGeoNode> source_nodes_list = getResourcesList();
		
		for(ARGeoNode node: source_nodes_list){
			GeoNode simple_node = node.getGeoNode();
			if(Photo.class.isInstance(simple_node))
				((Photo)simple_node).clearBitmapPhotoThumb();
			else if(Video.class.isInstance(simple_node))
				((Video)simple_node).clearBitmapPhotoThumb();
			else if(User.class.isInstance(simple_node))
				((User)simple_node).clearBitmapAvatarThumb();
			nodes_list.add(simple_node);
		}
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("LABELED_NODES_LIST", nodes_list);
		setResult(Activity.RESULT_OK, resultIntent);
		finish();
	}
}
