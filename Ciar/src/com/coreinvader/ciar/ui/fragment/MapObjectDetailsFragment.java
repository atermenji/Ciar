package com.coreinvader.ciar.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.coreinvader.ciar.R;

public class MapObjectDetailsFragment extends SherlockFragment {
    
    private ImageView mObjectPhoto;
    private TextView mObjectName;
    private TextView mObjectAddress;
    private TextView mObjectCategoryName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View root = inflater.inflate(R.layout.fragment_mapobjectdetails, null);
	
	mObjectPhoto = (ImageView) root.findViewById(R.id.iv_mapobject_photo);
	mObjectName = (TextView) root.findViewById(R.id.tv_mapobject_title);
	mObjectAddress = (TextView) root.findViewById(R.id.tv_mapobject_address);
	mObjectCategoryName = (TextView) root.findViewById(R.id.tv_mapobject_category_name);
	
        return root;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mObjectPhoto.setImageResource(R.drawable.im_nophoto);
    }
}
