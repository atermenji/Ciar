package com.coreinvader.ciar.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;

import com.actionbarsherlock.app.SherlockListFragment;
import com.coreinvader.ciar.R;
import com.coreinvader.ciar.provider.CiarContract.Categories;
import com.coreinvader.ciar.provider.CiarContract.MapObjects;

public class ObjectsListFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    
    private static final String TAG = "ObjectsListFragment";
    
    private static final int ID_LOADER_MAP_OBJECTS = 1;
    
    private SimpleCursorAdapter mAdapter;
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.list_item_mapobject, null,
                new String[] { 
            		MapObjects.MAP_OBJECT_NAME, 
            		MapObjects.MAP_OBJECT_ADDRESS,
            		MapObjects.MAP_OBJECT_DISCTANCE,
            		MapObjects.MAP_OBJECT_CATEGORY_NAME },
                new int[] { 	
            		R.id.tv_mapobject_title, 
            		R.id.tv_mapobject_address, 
            		R.id.tv_mapobject_distance, 
            		R.id.tv_mapobject_category_name }, 0);
        setListAdapter(mAdapter);
        
        getLoaderManager().initLoader(ID_LOADER_MAP_OBJECTS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_LOADER_MAP_OBJECTS: {
                Log.v(TAG, "map objects is active category uri is " + Categories.buildMapObjectsIsActiveCategoryUri(1));
                return new CursorLoader(getActivity(), 
                        Categories.buildMapObjectsIsActiveCategoryUri(1), null, null, null, null);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "Objects loading finished, entries in a cursor : " + data.getCount());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
