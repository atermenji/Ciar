/*
 *
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.libresoft.apps.ARviewer.Location.ARLocationManager;
import com.libresoft.apps.ARviewer.Location.ARLocationManager.OnLocationUpdateListener;
import com.libresoft.apps.ARviewer.Location.LocationWays;
import com.libresoft.apps.ARviewer.Overlays.ARSummaryBox;
import com.libresoft.apps.ARviewer.Overlays.CamPreview;
import com.libresoft.apps.ARviewer.Overlays.DrawFocus;
import com.libresoft.apps.ARviewer.Overlays.DrawParameters;
import com.libresoft.apps.ARviewer.Overlays.DrawRadar;
import com.libresoft.apps.ARviewer.Overlays.DrawResource;
import com.libresoft.apps.ARviewer.Overlays.DrawUserStatus;
import com.libresoft.apps.ARviewer.ScreenCapture.ScreenshotManager;
import com.libresoft.apps.ARviewer.Utils.LocationUtils;
import com.libresoft.apps.ARviewer.Utils.GeoNames.AltitudeManager;

public class ARBase extends ARActivity{ 
	private static final int ACTIVITY_BIDI_LOC = 1;
	private static final int ACTIVITY_LOC_WAYS = ACTIVITY_BIDI_LOC + 1;
	private static final int ACTIVITY_PREFERENCES = ACTIVITY_LOC_WAYS + 1;
	
	
	private static final int MENU_COMPASS_CORRECTION = Menu.FIRST + 1;
	
	private static final int MENU_LOCATION = MENU_COMPASS_CORRECTION + 1;
	private static final int MENU_INDOOR_LOCATION = MENU_LOCATION + 1;
	private static final int MENU_SERVICE_LOCATION = MENU_INDOOR_LOCATION + 1;
	private static final int MENU_LOCATION_WAYS = MENU_SERVICE_LOCATION + 1;
	
	private static final int MENU_PREFERENCES = MENU_LOCATION_WAYS + 1;
	private static final int MENU_ABOUT = MENU_PREFERENCES + 1;
	
	private static final int DIALOG_PBAR = 0;
	private static final int DIALOG_ABOUT = DIALOG_PBAR + 1;
	
	private static ARBase pointerObject = null;
	
    private CamPreview mPreview;
    private DrawParameters mParameters;
    private DrawFocus mFocus;
    private DrawRadar mRadar;
	private DrawUserStatus mUserStatus;
    
    
    protected float cam_altitude = 0;
    protected float distanceFilter = 0;
    private ARCompassManager compassManager;
    private int idGPS = -1;
    protected static boolean showMenu = true;
    
    private boolean refreshed;
    
    protected static ARTagManager tagManager; 
    private static ScreenshotManager screenshotManager;
    
//    private String[] strCategories = null;
    
    private String altitude_status = AltitudeManager.EXISTING_HEIGHTS;
	
    
	private CamPreview.OnFrameReadyListener frameReadyListener = new CamPreview.OnFrameReadyListener() {
		@Override
		public void onFrame(int[] pixels) {
			Bitmap bm = Bitmap.createBitmap(mPreview.getCameraPreviewSize().width, mPreview.getCameraPreviewSize().height, Bitmap.Config.ARGB_8888);
			bm.setPixels(pixels, 0, mPreview.getCameraPreviewSize().width, 0, 0, mPreview.getCameraPreviewSize().width, mPreview.getCameraPreviewSize().height);
			
			screenshotManager.setBaseBitmap(Bitmap.createScaledBitmap(bm, getLayers().getBaseLayer().getWidth(), getLayers().getBaseLayer().getHeight(), false));
			screenshotManager.setLayers(getLayers().getBaseLayer());
			screenshotManager.takeScreenshot();
		}
	};
    
	private ARCompassManager.OnCompassChangeListener compassListener = new ARCompassManager.OnCompassChangeListener(){

		public void onChange(float[] values) {
			
			float[] values_new = values.clone();

			//THIS PART IS CRITICAL IN ORDER TO REFRESH THE VIEW!!!
			if(mParameters!=null){
				mParameters.setValues(values_new, getLocation(), cam_altitude);
				mParameters.invalidate();
			}

			if(mRadar!=null){
				mRadar.setAzimuth(ARCompassManager.getAzimuth(values_new));
				mRadar.invalidate();
			}

			if (refreshed)
				refreshResourceDrawns(values_new);
			
			if(tagManager != null)
				tagManager.setAngles(values_new);
		}
		
	};
	
    OnLocationUpdateListener locationListener = new OnLocationUpdateListener(){

		public void onUpdate(Location loc) {
			
			float[] location = {(float) loc.getLatitude(), (float) loc.getLongitude(), 0};
			setLocation(location);
			
			if(mUserStatus != null)
				mUserStatus.setLocationServiceActive(true);
			
			if(ARLocationManager.getInstance(getBaseContext()).isLocationServiceAltitude()){
				cam_altitude = (float) loc.getAltitude();
				LocationUtils.setUserHeight(cam_altitude);
				if(mUserStatus != null)
					mUserStatus.setAltitudeLoaded(true);
			}else
				if((tagManager == null) || (tagManager.getSavingType() == -1))
					requestAltitudeInfo();
			if(tagManager != null)
				tagManager.setUserLocation(location);
			
		}
    	
    };
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {

    		ARSummaryBox.setShowRemoveButton(false);
            pointerObject = this;
			refreshed = false;
        	
        	showMenu = true;
        	distanceFilter = 0;
			
			// Hide the window title and notifications bar.
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			// Create our Preview view and set it as the content of our activity.
			mPreview = new CamPreview(this);
			
			// setting the layers which we use as graphical containers
			getLayers().setBaseLayer();
			getLayers().setResourceLayer();
			getLayers().setInfoLayer();
			getLayers().setExtraLayer();
			
			mFocus = new DrawFocus(this);
			mRadar = new DrawRadar(this);
			mUserStatus = new DrawUserStatus(this);
			
			getLayers().addInfoElement(mRadar, null);
			getLayers().addInfoElement(mFocus, null);
			getLayers().addInfoElement(mUserStatus, null);
			
//			if(!loadParameters()){
//				Toast.makeText(getBaseContext(), R.string.no_layer, Toast.LENGTH_LONG).show();
//			}
				
//			loadConfig(false);
			
			ARGeoNode.setRadar(mRadar);			
			
			compassManager = new ARCompassManager(this);
			compassManager.setDrawUserStatusElement(mUserStatus);
			
			screenshotManager = new ScreenshotManager(getBaseContext());
			screenshotManager.setCam(mPreview, frameReadyListener);
			
//			showResources();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), 
					R.string.error_environment, 
					Toast.LENGTH_LONG).show();
			Log.e("ARView", "", e);
		}
        
    }
	
	protected boolean loadParameters(){
		float[] location = new float[3];
		if(getIntent().hasExtra("LATITUDE") && getIntent().hasExtra("LONGITUDE")){
			location[0] =  (float) getIntent().getDoubleExtra("LATITUDE", 0);
			location[1] = (float) getIntent().getDoubleExtra("LONGITUDE", 0);
			ARLocationManager.getInstance(this).setLocation(location[0], location[1], (float)AltitudeManager.NO_ALTITUDE_VALUE);
		}else{
			Location loc = ARLocationManager.getInstance(this).getLastKnownLocation(this);
			location[0] =  (float) loc.getLatitude();
			location[1] = (float) loc.getLongitude();
		}
		setLocation(location);
		requestAltitudeInfo();
		
//		if(strCategories == null)
//		{
//			ArrayList<Category> categories = (ArrayList<Category>) DataManager.getInstance().removeData(DataManager.LAYER_CATEGORIES);
//			strCategories = new String[categories.size()];
//			for (int i=0; i<categories.size();i++)
//				strCategories[i] = String.valueOf(categories.get(i).getId());
//		}
		
		return true;
	}
    
    protected void loadConfig(boolean refresh_altitude){

		SharedPreferences sharedPreferences = 
			PreferenceManager.getDefaultSharedPreferences(this);
		
		String default_controller = "Gaussian";
		if(compassManager.isGyro())
			default_controller = "Gyroscope";
		compassManager.setAzimuthControllerType(sharedPreferences.getString(ARPreferences.KEY_AZ_CONTROLLER, default_controller));
		compassManager.setElevationControllerType(sharedPreferences.getString(ARPreferences.KEY_EL_CONTROLLER, default_controller));
		
		altitude_status = sharedPreferences.getString(ARPreferences.KEY_HEIGHT, 
				AltitudeManager.EXISTING_HEIGHTS);
		useHeight((!altitude_status.equals(AltitudeManager.NO_HEIGHTS)));
		if(refresh_altitude && altitude_status.equals(AltitudeManager.ALL_HEIGHTS))
			actionRequestHeight();
			
		if(isUsingHeight()){
			useThreshold(sharedPreferences.getBoolean(ARPreferences.KEY_IS_DIST_FILTER, false));
			setThreshold(sharedPreferences.getInt(ARPreferences.KEY_DIST_FILTER, 0));
		}
		
		if(sharedPreferences.getBoolean(ARPreferences.KEY_MEASURES, false)){
			if(mParameters == null){
				mParameters = new DrawParameters(this);
				getLayers().addInfoElement(mParameters, null);
			}
		}else{
			if(mParameters!=null){
				getLayers().removeInfoElement(mParameters);
				mParameters = null;
			}
		}
		
		if(mRadar != null)
			mRadar.setRotateCompass(sharedPreferences.getBoolean(ARPreferences.KEY_ROTATING_COMPASS, true));
		ARGeoNode.clearClicked(getResourcesList());
		ARGeoNode.setDinamicSummary(this, getLayers().getExtraLayer(), sharedPreferences.getBoolean(ARPreferences.KEY_MOVE_LABELS, false));
		ARGeoNode.setCenterSummary(sharedPreferences.getBoolean(ARPreferences.KEY_CENTER_LABELS, false));
		DrawResource.setNamesStatus(sharedPreferences.getString(ARPreferences.KEY_NAMES_SHOWING, DrawResource.ALL_NAMES));
		ARGeoNode.setRefreshIcon(getResourcesList(), sharedPreferences.getBoolean(ARPreferences.KEY_IMAGE_ICON, true));
		ARGeoNode.activeSearchSystem(sharedPreferences.getBoolean(ARPreferences.KEY_SEARCH_SYSTEM, true));
    }
    
    protected void showResources(){
    	refreshed = false;
    	ARGeoNode.clearClicked(getResourcesList());
    	getLayers().cleanResouceLayer();
    	
    	ArrayList<ARGeoNode> res_list = null;
    	
    	if(getMyLayer() == null){
    		res_list = getResourcesList();
    	}else{
        	setResourcesList(null);
    		res_list = ARUtils.cleanNoLocation(this, getLayers(), getMyLayer().getNodes(), getLocation(), distanceFilter);
    	}
    	
    	if(res_list == null){
    		return;
    	}

    	if (res_list.size() > 50){
    		ArrayList<ARGeoNode> list = new ArrayList<ARGeoNode>();
    		for(int i = 0; i < 50; i++)
    			list.add(res_list.get(i));
    		res_list.clear();
    		res_list.addAll(list);
    		Toast.makeText(getBaseContext(), 
    				R.string.error_too_much_nodes, 
    				Toast.LENGTH_LONG).show();
    		Log.e("ARView", getString(R.string.error_too_much_nodes));
    	}

    	ARGeoNodeAzimuthComparator comparator = new ARGeoNodeAzimuthComparator();
    	Collections.sort(res_list, comparator);

    	setResourcesList(res_list);

    	mRadar.setResourcesList(res_list);
    	ARGeoNode.setResourcesList(res_list);
    	
    	refreshed = true;
    	
    	if(altitude_status.equals(AltitudeManager.ALL_HEIGHTS))
			actionRequestHeight();
    }
	
    protected void onPause(){
    	super.onPause();
//    	orListener.stopAudio();
    	getLayers().removeBaseElement(mPreview);
    	compassManager.unregisterListeners();
    	if(!(idGPS<0))
    		ARLocationManager.getInstance(this).pauseUpdates();
		
		Location loc = new Location("Manual");
		loc.setLatitude(getLocation()[0]);
		loc.setLongitude(getLocation()[1]);
		loc.setAltitude(cam_altitude);
		ARLocationManager.getInstance(this).setLocation(loc);
		
    }
    
    protected void onResume(){
    	super.onResume();
    	getLayers().addBaseLayer(mPreview);
    	showMenu = true;
    	
    	getLayers().cleanResouceLayer();
    	getLayers().cleanExtraLayer();
    	ARGeoNode.clearClicked(getResourcesList());
    	compassManager.setOnCompassChangeListener(compassListener);
		
    	if(!(idGPS<0))
    		ARLocationManager.getInstance(this).startUpdates(this);
    	
    }
    
    protected void onDestroy(){
    	ARGeoNode.clearBox();
    	ARLocationManager.getInstance(this).stopUpdates();
    	ARLocationManager.getInstance(this).resetLocation();
    	super.onDestroy();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if(showMenu){
    		SubMenu sub1 = menu.addSubMenu(0, MENU_LOCATION, 0, R.string.menu_location)
    		.setIcon(R.drawable.mundo);
    		sub1.add(0,MENU_INDOOR_LOCATION, 0, R.string.menu_location_bidi);
    		sub1.add(0,MENU_LOCATION_WAYS, 0, R.string.menu_location_manual);
    		sub1.add(0,MENU_SERVICE_LOCATION, 0, R.string.menu_location_service);
    	}
    	
    	screenshotManager.onCreateOptionsMenu(menu);
    	
    	if(showMenu)
    		menu.add(0, MENU_PREFERENCES, 0, R.string.menu_settings)
    			.setIcon(R.drawable.spanner_48);
    	menu.add(0, MENU_ABOUT, 0, R.string.menu_about)
	    	.setIcon(R.drawable.about)
	    	.setAlphabeticShortcut('B');
    	
        super.onCreateOptionsMenu(menu);        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
    	
    	if(screenshotManager.onOptionsItemSelected(item))
    		return super.onOptionsItemSelected(item);

    	switch (item.getItemId()) {

    	case MENU_SERVICE_LOCATION:
    		if(idGPS==-1){
    			if(mUserStatus != null)
    				mUserStatus.setLocationServiceOnProgress();
    			idGPS = ARLocationManager.getInstance(this).addLocationListener(locationListener);
    			ARLocationManager.getInstance(this).startUpdates(this);
    		}else{
    			ARLocationManager.getInstance(this).stopUpdates();
    			idGPS = -1;
    			if(mUserStatus != null)
    				mUserStatus.setLocationServiceActive(false);
    		}
    		if(tagManager != null)
    			tagManager.setLocationServiceOn(idGPS);
    		break;

    	case MENU_INDOOR_LOCATION:
    		PackageManager pm = getPackageManager();
    		List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_META_DATA);
    		boolean isBarcode = false;
    		for(int i = 0; i< list.size(); i++){
    			if( list.get(i).packageName.equals("com.google.zxing.client.android")){
    				isBarcode = true;
    				break;
    			}
    		}

    		if (!isBarcode){
    			Toast.makeText(getBaseContext(), 
    					R.string.error_barcode,
    					Toast.LENGTH_LONG).show();
    			break;
    		}

    		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
    		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
    		startActivityForResult(intent, ACTIVITY_BIDI_LOC);
    		break;

    	case MENU_LOCATION_WAYS:
    		Intent intent1 = new Intent(this, LocationWays.class);
    		startActivityForResult(intent1, ACTIVITY_LOC_WAYS);
    		break;

    	case MENU_PREFERENCES:
    		Intent i = new Intent(this, ARPreferences.class);
    		startActivityForResult(i, ACTIVITY_PREFERENCES);
    		break;
    		
    	case MENU_ABOUT:
			showDialog(DIALOG_ABOUT);
			break;
    	}

    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {  
    	Dialog diag = ARLocationManager.getInstance(this).onCreateDialog(this, id);
    	if(diag != null)
    		return diag;
    	
    	switch (id) {
    	case DIALOG_ABOUT:
    		LayoutInflater factory2 = LayoutInflater.from(this);
    		View textEntryView2 = factory2.inflate(R.layout.custom_dialog, null);
    		
    		TextView text2 = (TextView) textEntryView2.findViewById (R.id.dialog_text);
    		text2.setText(getString(R.string.app_name) + " " + getString(R.string.version_arviewer) +
					getString(R.string.revision_arviewer) + "\n" + getString(R.string.about_message));
    		return new AlertDialog.Builder(this)
    		.setIcon(R.drawable.arviewer_32)
    		.setTitle(R.string.about_title)
    		.setView(textEntryView2)
    		.setPositiveButton(R.string.ok, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.setNeutralButton(R.string.about_web, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent myIntent = new Intent(Intent.ACTION_VIEW, 
		 					 Uri.parse("http://www.libregeosocial.org/node/24"));
	    			startActivity(myIntent);
				}
			})
    		.create();
    		
    	case DIALOG_PBAR:
    		ProgressDialog dialog = new ProgressDialog(this);
    		dialog.setMessage(getString(R.string.loading));
    		dialog.setIndeterminate(true);
    		dialog.setCancelable(true);
    		return dialog;
    	}
		return null;
    
	}
    
    private void actionRequestHeight(){
    	final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				refreshed = true;
				removeDialog(DIALOG_PBAR);
			}
		};
		refreshed = false;
		showDialog(DIALOG_PBAR);
		new Thread(){
			public void run(){
				AltitudeManager.updateHeights(getResourcesList());
				handler.sendEmptyMessage(0);
			}
		}.start();
    }

    
	protected void onActivityResult (int requestCode, int resultCode, Intent data) { 
		
		switch (requestCode) { 
	    		
	    	case ACTIVITY_BIDI_LOC:
	    		
	    		if( resultCode != Activity.RESULT_CANCELED ){
		    		String contents = data.getStringExtra("SCAN_RESULT");
	    			try{
	    				String[] info = contents.split(";");
	    				float[] location = {Float.parseFloat(info[0]), Float.parseFloat(info[1]), 
	        					0};
	    				if((location[0] == getLocation()[0]) && (location[1] == getLocation()[1]))
	    					break;
	    				setLocation(location);
	    				cam_altitude = (float) AltitudeManager.getAbsoluteAltitude(this, Float.parseFloat(info[2]), false);
	    				LocationUtils.setUserHeight(cam_altitude);
//	        			loadResources();
	    			}catch(Exception e){
	    				Toast.makeText(getBaseContext(), 
	    						R.string.error_bidi, 
	    						Toast.LENGTH_LONG).show();
	    			}
	    		}else
	    			Toast.makeText(getBaseContext(),  
    						R.string.error_bidi, 
	    					Toast.LENGTH_LONG).show();
	    		
	    		break;
	    		
	    	case ACTIVITY_LOC_WAYS:
    			float[] location = {(float) ARLocationManager.getInstance(this).getLocation().getLatitude(), 
    					(float)  ARLocationManager.getInstance(this).getLocation().getLongitude(), 
    					0};
				if((location[0] == getLocation()[0]) && (location[1] == getLocation()[1]))
					break;
    			setLocation(location);
				requestAltitudeInfo();
//    			loadResources();
	    		break;
	    		
	    	case ACTIVITY_PREFERENCES:
	    		requestAltitudeInfo();
    			
	    		loadConfig(true);
	    		ARGeoNode.setResourcesList(getResourcesList());
	    		break;
	    	
	
            default:
                break; 
		}
	}
	
	private synchronized void requestAltitudeInfo(){
		Log.i("ARView", "Request altitude");
		if(mUserStatus != null)
			mUserStatus.setAltitudeLoaded(false);
		final Handler altHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(isFinishing())
					return;
				Log.i("ARView", "Altitude received");
				if((msg.what == 0) && (mUserStatus != null))
					mUserStatus.setAltitudeLoaded(true);
			}
		};
		
		new Thread(){
			public void run(){
				if(!ARLocationManager.getInstance(getBaseContext()).isLocationServiceAltitude())
					if(cam_altitude != AltitudeManager.NO_ALTITUDE_VALUE)
						cam_altitude = (float) AltitudeManager.getAbsoluteAltitude(
								getBaseContext(), 
								(float) AltitudeManager.getAltitudeFromLatLong(getLocation()[0], getLocation()[1]), 
								true);
				else
					cam_altitude = (float) AltitudeManager.getAbsoluteAltitude(
							getBaseContext(), 
							(float) ARLocationManager.getInstance(getBaseContext()).getLocation().getAltitude(),
							true);
				LocationUtils.setUserHeight(cam_altitude);
				altHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	public static void GestureNext ()
	{		
		if (pointerObject == null)
			return;
		
		int num_click = ARGeoNode.getNodeClicked();
		if(num_click < 0)
			return;
		int fixed_num = num_click;
		do{
			num_click++;
			if(num_click >= pointerObject.getResourcesList().size())
				num_click = 0;
			if(fixed_num == num_click)
				break;
		}while(!pointerObject.getResourcesList().get(num_click).getDrawn().forceClick());
		
		Log.e("Gesture", "NEXT " + Integer.toString(num_click));
		return ;
		
	}
	
	public static void GesturePrevious ()
	{	
		if (pointerObject == null)
			return;
		
		int num_click = ARGeoNode.getNodeClicked();
		if(num_click < 0)
			return;
		int fixed_num = num_click;
		do{
			num_click--;
			if(num_click < 0)
				num_click = pointerObject.getResourcesList().size() - 1;
			if(fixed_num == num_click)
				break;
		}while (!pointerObject.getResourcesList().get(num_click).getDrawn().forceClick());
		
		Log.e("Gesture", "PREVIOUS " + Integer.toString(num_click));
		return;
	}
}
