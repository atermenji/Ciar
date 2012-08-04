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

package com.libresoft.apps.ARviewer.Tagging.Content;


import java.io.File;

import com.libresoft.apps.ARviewer.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class ContentPick extends ListActivity{
	
	private static final int MENU_DONE = Menu.FIRST + 1;
	
	private static File[] ls = null;
	private static String path = "";
	private static String mimeType;
	private static int position = -1;
	private static Activity mActivity;
	private FileAdapter mAdapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if(ls == null){
        	String main_path = getIntent().getStringExtra("MAIN_PATH");
        	mimeType = getIntent().getStringExtra("MIME_TYPE");

        	if(main_path == null)
        		end();

        	File file = new File(main_path);

        	if(!file.exists() || !file.isDirectory())
        		end();
        	ls = file.listFiles();
        }
        
        // Set list
        mAdapter = new FileAdapter(this);
        setListAdapter(mAdapter);
    }
	
	protected void onPause(){
		
		super.onPause();
	}
	    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			ls = null;
			position = -1;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id)
    {
    	File file = ls[position];
    	if(!file.isFile())
    		Toast.makeText(this, "This is not a valid file!", Toast.LENGTH_SHORT);
    	else{
    		path = file.getAbsolutePath();
    		ContentPick.position = position;
    		
    		mAdapter.notifyDataSetChanged();
    		
    	}
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	menu.add(0, MENU_DONE, 0, "Done")
    				.setIcon(R.drawable.done);
    	
        super.onCreateOptionsMenu(menu);        
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item) {
    	
    	switch (item.getItemId()) {

    	case MENU_DONE:

    		returnActivity();

    		break;
    	}

        return super.onOptionsItemSelected(item);
    }
	
	private void end(){
    	Toast.makeText(this, "No path specified!", Toast.LENGTH_SHORT);
    	setResult(Activity.RESULT_CANCELED);
    	finish();
	}
	
	private void returnActivity(){
		if (path.equals("")){
			end();
			return;
		}
		Intent i = new Intent();
		i.putExtra("PATH", path);
		setResult(Activity.RESULT_OK, i);
		finish();
	}
	
    public static class FileAdapter extends BaseAdapter
    {

    	
    	private OnClickListener playListener = new OnClickListener() {
    		
    		public void onClick(View v) {
        		Intent i = new Intent(Intent.ACTION_VIEW);
        		i.setDataAndType(Uri.fromFile(ls[position]), mimeType);
        		mActivity.startActivity(i);
    		}
    	};
    	
        private Context mContext;

        public FileAdapter(Context c)
        {
            mContext = c;
        }

        public int getCount()
        {
            // TODO Auto-generated method stub
        	if(ls != null)
        		return ls.length;
        	return 0;
        }

        public Object getItem(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position)
        {
            // TODO Auto-generated method stub
            return position;
        }
        

        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            View view;

            if (convertView == null)
            {
                // Make up a new view
                LayoutInflater inflater = (LayoutInflater) mContext
                                          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.friendship_list, null);
                view.setPadding(10, 20, 0, 20);
                view.setBackgroundResource(R.drawable.background);
            }
            else
            {
                // Use convertView if it is available
                view = convertView;
            }
            Button button = (Button) view.findViewById(R.id.fl_button);
            if(position == ContentPick.position){
            	button.setVisibility(View.VISIBLE);
            	button.setOnClickListener(playListener);
            }else{
            	button.setVisibility(View.GONE);
            	button.setOnClickListener(null);
            }
            
            TextView tv_name = (TextView) view.findViewById(R.id.name);
            tv_name.setTextColor(Color.BLACK);

            File file = ls[position];
            String name = file.getName();
            
            tv_name.setText(name);

            return view;

        }

    }

}