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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.libresoft.apps.ARviewer.Overlays.CustomViews;
import com.libresoft.apps.ARviewer.Utils.AsyncTasks.AsyncLGSNodes;
import com.libresoft.apps.ARviewer.Utils.AsyncTasks.AsyncLGSNodes.OnExecutionFinishedListener;
import com.libresoft.sdk.ARviewer.Types.GenericLayer;
import com.libresoft.sdk.ARviewer.Types.GeoNode;

public class ARviewer extends ARBase{ 

	private static final int MENU_DISTANCE_FILTER = 101;
	private static final int DIALOG_EMPTY = 101;

	OnClickListener distFiltClickListener = new OnClickListener() {
		
		public void onClick(View v) {
			showMenu = true;
    		getLayers().removeExtraElement((View) v.getParent());
    		float dist = (float)(CustomViews.getSeekbarValue()*1E3);
    		if(distanceFilter != dist){
    			distanceFilter = dist;
    			showResources();
    		}
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!loadParameters()){
			Toast.makeText(getBaseContext(), R.string.no_layer, Toast.LENGTH_LONG).show();
		}
        loadConfig(false);
        showResources();
    }
	
	@Override
	protected boolean loadParameters(){
		super.loadParameters();
		if(!getIntent().hasExtra("LAYER"))
			return false;
		else{
			setMyLayer((GenericLayer) getIntent().getSerializableExtra("LAYER"));
		}
		
		return true;
	}
    
    @Override
    public void showResources(){
    	super.showResources();
    	if(getResourcesList() == null){
    		Log.e("ARviewer", "Demo mode");
    		setMyLayer(new GenericLayer(0, "", "Demo Layer", "A demo layer to show ARviewer functions", null, null, null, null, null, null));
    		getMyLayer().setNodes(new ArrayList<GeoNode>());
    		new AsyncLGSNodes(this, getMyLayer(), new OnExecutionFinishedListener() {
				@Override
				public void onFinish() {
					if(!isFinishing())
						showResources();
				}
			}).execute();
    		showDialog(DIALOG_EMPTY);
    		return;
    	}
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.clear();
    	
    	if(showMenu){
    		menu.add(0, MENU_DISTANCE_FILTER, 0, R.string.menu_distance)
    		.setIcon(R.drawable.meter);
    	}
    	
        super.onPrepareOptionsMenu(menu);        
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {

    	switch (item.getItemId()) {

    	case MENU_DISTANCE_FILTER:
    		View view = CustomViews.createSeekBars(this, distanceFilter/1E3, 50, " Km.", 10, 0, distFiltClickListener);

    		getLayers().addExtraElement(view, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));

    		showMenu = false;
    		break;
    	}

    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {  
    	
    	switch (id) {
    		
    	case DIALOG_EMPTY:
    		LayoutInflater factory = LayoutInflater.from(this);
    		View textEntryView = factory.inflate(R.layout.custom_dialog, null);
    		
    		TextView text = (TextView) textEntryView.findViewById (R.id.dialog_text);
    		text.setText(R.string.empty_message);
    		
    		return new AlertDialog.Builder(this)
    		.setTitle(R.string.empty_title)
    		.setView(textEntryView)
    		.setPositiveButton(R.string.ok, new Dialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setNeutralButton(R.string.empty_places, new Dialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
			    	i.setData(Uri.parse("market://search?q=pname:com.libresoft.apps.ARviewerPlaces"));
			    	
			    	startActivity(i);
			    	finish();
				}
			})
			.setNegativeButton(R.string.empty_tagging, new Dialog.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
			    	i.setData(Uri.parse("market://search?q=pname:com.libresoft.apps.ARviewerTagging"));
			    	
			    	startActivity(i);
			    	finish();
				}
			})
    		.create();
    	}
		return super.onCreateDialog(id);
    
	}
}
