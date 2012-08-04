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

package com.libresoft.apps.ARviewer.Overlays;

import java.util.ArrayList;

import com.libresoft.apps.ARviewer.ARBase;
import com.libresoft.apps.ARviewer.ARGeoNode;
import com.libresoft.apps.ARviewer.R;
import com.libresoft.apps.ARviewer.Multimedia.AudioPlayer;
import com.libresoft.apps.ARviewer.Utils.LocationUtils;
import com.libresoft.sdk.ARviewer.Types.Audio;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class ARSummaryBox{
	public static final int DIALOG_SURE = 450;
	private static boolean show_remove_button = false;

	private RelativeLayout container;
	private View summary_box;
	private int num_node = -1;
	
	private ArrayList<ARGeoNode> nodes_list = null;
	private ImageView[] image_containers;
	
	private AudioPlayer media_player;
	
	public ARSummaryBox(RelativeLayout container){
		this.container = container;
	}
	
	protected RelativeLayout getContainer(){
		return container;
	}
	
	public static void setShowRemoveButton(boolean show){
		show_remove_button = show;
	}
	
	public void setNodesList(ArrayList<ARGeoNode> nodes_list){
		this.nodes_list = nodes_list;
		if(nodes_list != null)
			createImageContainerList(nodes_list.size());
	}
	
	public ArrayList<ARGeoNode> getNodesList(){
		return nodes_list;
	}
	
	public void setNodeClicked(int num_node){
		this.num_node = num_node;
	}
	
	public int getNodeClicked(){
		return num_node;
	}
	
	public ARGeoNode getNode(){
		if((nodes_list != null) && (num_node > -1))
			return nodes_list.get(num_node);
		return null;
	}
	
	public ARGeoNode getNode(int num_node){
		if((nodes_list != null) && (num_node > -1))
			return nodes_list.get(num_node);
		return null;
	}
	
	protected void setSummaryBox(View summary_box){
		this.summary_box = summary_box;
		
		if(!isBoxDrawn())
			container.addView(this.summary_box, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		else
			container.invalidate();
	}
	
	protected View getSummaryBox(){
		return summary_box;
	}
	
	protected void showBox(){
		if(summary_box != null)
			summary_box.setVisibility(View.VISIBLE);
	}
	
	protected void hideBox(){
		if(summary_box != null)
			summary_box.setVisibility(View.INVISIBLE);
	}
	
	protected void removeSummaryBox(){
		container.removeView(summary_box);
		summary_box = null;
		num_node = -1;
	}
	
	protected void createImageContainerList(int size){
		image_containers = new ImageView[size];
	}
	
	protected void setImageContainer(int position, ImageView image_container){
		if(position > -1)
			image_containers[position] = image_container;
	}

	public void resetAll(){
		removeSummaryBox();
	}
	
	protected boolean isBoxDrawn(){
		return (container.indexOfChild(summary_box) > -1);
	}
	
	public void refreshPhoto(int num_node, Bitmap image){
		if((image_containers[num_node] == null))
			return;
		
		image_containers[num_node].setImageBitmap(image);
//		image_container.invalidate();
	}
	
	protected String getTitle(){
		return getTitle(num_node);
	}
	
	protected String getTitle(int num_node){
		if(num_node == -1)
			return null;
		ARGeoNode arnode = getNode(num_node);
		
		if(arnode == null)
			return null;
		
		return arnode.getTitle();
	}
	
	protected String getDescription(){
		return getDescription(num_node);
	}
	
	protected String getDescription(int num_node){
		if(num_node == -1)
			return null;
		ARGeoNode arnode = getNode(num_node);
		
		if(arnode == null)
			return null;
		
		return arnode.getDescription();
	}

	protected boolean isMedia(){
		return isMedia(num_node);
	}
	
	protected boolean isMedia(int num_node){
		if(num_node == -1)
			return false;
		ARGeoNode arnode = getNode(num_node);
		
		if(arnode == null)
			return false;
		return arnode.isMedia();
	}
	
	protected String getDistance(){
		return getDistance(num_node);
	}
	
	protected String getDistance(int num_node){
		if(num_node == -1)
			return null;
		ARGeoNode arNode = getNode(num_node);
		
		if(arNode == null)
			return null;
		
		String distance = LocationUtils.displayDistance(arNode.getDistance(),
				LocationUtils.AUTO);
		return distance;
	}
	
	protected Bitmap getImage(Activity mActivity){
		return getImage(mActivity, num_node);
	}
	
	protected Bitmap getImage(Activity mActivity, int num_node){
		//set a listener to make the image request to server
		if(num_node == -1)
			return null;
		ARGeoNode arnode = getNode(num_node);
		
		if(arnode == null)
			return null;
		
		return arnode.getIcon(true);
	}
	
	protected void setDrawnClicked(boolean isClick){
		if(num_node == -1)
			return;
		ARGeoNode arnode = getNode();

		if(arnode == null)
			return;
		arnode.setDrawnClicked(isClick);
	}
	
	protected void setDetailsButton(Activity mActivity, View summary_box){
		if(num_node == -1)
			return;
		ARGeoNode arnode = getNode();

		if(arnode == null)
			return;
		// Details button
//		Button bt = (Button)summary_box.findViewById(R.id.ar_button_details);
//		bt.setOnClickListener(arnode.getDetailsClickListener(mActivity));
	}
	
	protected void setRemoveButton(Activity mActivity, View summary_box){
		if(num_node == -1)
			return;
		ARGeoNode arnode = getNode();

		if(arnode == null)
			return;
		// Details button
		Button bt = (Button)summary_box.findViewById(R.id.ar_button_remove);
		if(show_remove_button){
			bt.setVisibility(View.VISIBLE);
			bt.setOnClickListener(arnode.getRemoveNodeListener(mActivity));
		}else
			bt.setVisibility(View.INVISIBLE);
	}
	
	protected void setCloseButton(View summary_box){
		if(num_node == -1)
			return;
		final ARGeoNode arnode = getNode();

		if(arnode == null)
			return;
		
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				if((isMedia(num_node)) && media_player != null)
					media_player.stop();
				arnode.setClicked(false);
			}
		};
		
		Button bt = (Button)summary_box.findViewById(R.id.ar_button_close);
		bt.setOnClickListener(listener);
	}

	protected void setPlayButton(Context mContext, View summary_box){
		setPlayButton(mContext, num_node, summary_box);
	}
	
	protected void setPlayButton(Context mContext, final int num_node, View summary_box){
		if(num_node == -1)
			return;
		ARGeoNode arnode = getNode(num_node);

		if((arnode == null) || (!isMedia(num_node)))
			return;

		if(media_player == null)
			media_player = new AudioPlayer(mContext);

		Button bt = (Button)summary_box.findViewById(R.id.ar_button_play);
		bt.setOnClickListener(media_player.getPlayButtonListener((Audio)arnode.getGeoNode()));
	}
	
	protected void setStopButton(View summary_box){
		if(media_player != null)
			media_player.stop();

		Button bt = (Button)summary_box.findViewById(R.id.ar_button_stop);
		bt.setOnClickListener(media_player.getStopButtonListener());
	}
	
	protected void setDummyButton(int buttonid, View summary_box, final String message){
		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				Log.e("ARSummaryBox", message);
			}
		};
		Button bt = (Button)summary_box.findViewById(buttonid);
		bt.setOnClickListener(listener);
	}
	
	public static Dialog onCreateDialog(final Activity mActivity, int id) {    
    	switch (id) {
    	case DIALOG_SURE:

    		return new AlertDialog.Builder(mActivity)	      
    		.setCancelable(true)
    		.setTitle(R.string.remove)
    		.setMessage(R.string.sure)
    		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    				ARGeoNode.removeNode(((ARBase)mActivity).getLayers(), ((ARBase)mActivity).getResourcesList());
    			}
    		})
    		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {
    			}
    		})
    		.create();
    	}
    	return null;
    }
}