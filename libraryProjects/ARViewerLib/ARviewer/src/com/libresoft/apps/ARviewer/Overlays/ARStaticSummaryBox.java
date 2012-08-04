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


import com.libresoft.apps.ARviewer.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ARStaticSummaryBox extends ARSummaryBox{
	
	private Gallery gallery;
	private Activity mActivity;
	
	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener(){
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			/* Set last clicked node drawn to normal state */
			setDrawnClicked(false);
			
			/* Set new clicked node drawn to click state */
			setNodeClicked(arg2);
			setDrawnClicked(true);
			
			View summary_box = (View) gallery.getParent();
			setCloseButton(summary_box);
			setDetailsButton(mActivity, summary_box);
			setRemoveButton(mActivity, summary_box);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}
	};
	
	public ARStaticSummaryBox(Activity mActivity, RelativeLayout container){
		super(container);
		this.mActivity = mActivity;
	}
	
	public void drawSummaryBox(Context mContext, int num_node){
		setNodeClicked(num_node);
		
		View summary_box = getSummaryBox();
		if(summary_box == null){
			LayoutInflater factory = LayoutInflater.from(mContext);
			summary_box = factory.inflate(R.layout.ar_static_summary, null);
			
			int h = mActivity.getWindowManager().getDefaultDisplay().getHeight();
			summary_box.setPadding(20, h*7/12, 20, 5);
			
			setSummaryBox(summary_box);
			BoxListAdapter mAdapter = new BoxListAdapter(mContext);
			gallery = (Gallery) summary_box.findViewById(R.id.ar_static_gallery);
			gallery.setAdapter(mAdapter);
			gallery.setSpacing(2);
			gallery.setOnItemSelectedListener(onItemSelectedListener);
		}
		
		if((gallery != null) && (num_node != -1))
			gallery.setSelection(num_node, true);
		
	}
	
	public void removeSummaryBox(){
		gallery = null;
		super.removeSummaryBox();
	}
	
	public void resetAll(){
		removeSummaryBox();
	}
	
	
	private class BoxListAdapter extends BaseAdapter {

		private Context mContext;
		private int size;

		public BoxListAdapter(Context c) {
			mContext = c;
		}

		public int getCount() {
			if(getNodesList() == null)
				size = 0;
			else
				size = getNodesList().size();
			return size;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view;

			if (convertView == null) {
				// Make up a new view
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.ar_static_label, null);
			} else {
				// Use convertView if it is available
				view = convertView;
			}
			
			TextView t = (TextView) view.findViewById(R.id.ar_static_title);
			t.setText(getTitle(position));
			
			t = (TextView) view.findViewById(R.id.ar_static_distance);
			t.setText(getDistance(position));
			
			t = (TextView) view.findViewById(R.id.ar_static_description);
			t.setText(getDescription(position));
			
			t = (TextView) view.findViewById(R.id.ar_static_page);
			int old_page = getPage(t.getText().toString());
			String page = Integer.toString(position+1) + "/" + Integer.toString(size);
			if(size != 1){
				if(position == 0)
					page = page + " >";
				else if(position == size-1)
					page = "< " + page;
				else
					page = "< " + page + " >";
			}
			t.setText(page);
			
			if(old_page > -1)
				setImageContainer(old_page, null);
			ImageView img = (ImageView) view.findViewById(R.id.ar_image);
			setImageContainer(position, img);
			img.setImageBitmap(getImage(mActivity, position));
			
			LinearLayout ll_media_player = (LinearLayout)view.findViewById(R.id.ar_media_player);
			if(isMedia(position)){
				ll_media_player.setVisibility(View.VISIBLE);
				setPlayButton(mContext, position, ll_media_player);
				setStopButton(ll_media_player);
			}else
				ll_media_player.setVisibility(View.GONE);
			
			return view;

		}
		
		private int getPage(String page){
			int num = -1;
			if((page != null) && (!page.equals(""))){
				if(page.contains("<"))
					num = Integer.parseInt(page.split("< ")[1].split("/")[0]);
				else
					num = Integer.parseInt(page.split("/")[0]);
			}
			return num;
		}

	}
	
	
}