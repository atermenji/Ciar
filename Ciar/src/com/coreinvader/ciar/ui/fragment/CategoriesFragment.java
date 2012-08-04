package com.coreinvader.ciar.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.coreinvader.ciar.R;
import com.coreinvader.ciar.provider.CiarContract.Categories;
import com.coreinvader.ciar.service.SyncService;
import com.coreinvader.ciar.ui.MapObjectsActivity;
import com.coreinvader.ciar.ui.adapter.CategoriesAdapter;

public class CategoriesFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CategoriesFragment";
    
    private static final int ID_LOADER_CATEGORIES = 1;
    
    private SyncCompleteReceiver mSyncCompleteReceiver;

    private ListView mCategoriesList;
    private CategoriesAdapter mAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_categories, null);
        
        Log.v(TAG, "creating view categories");
        mCategoriesList = (ListView) root.findViewById(R.id.lv_categories);
        mAdapter = new CategoriesAdapter(getActivity(), null, 0);
        mCategoriesList.setAdapter(mAdapter);

        Button startButton = (Button) root.findViewById(R.id.bt_start);
        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapObjectsActivity.class);
                startActivity(intent);
            }
        });
        
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "starting loader");
        getLoaderManager().initLoader(ID_LOADER_CATEGORIES, null, this);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        if (mSyncCompleteReceiver == null) {
            mSyncCompleteReceiver = new SyncCompleteReceiver();
        }
        
        IntentFilter intentFilter = new IntentFilter(SyncService.ACTION_SYNC_COMLETE);
        getActivity().registerReceiver(mSyncCompleteReceiver, intentFilter);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        if (mSyncCompleteReceiver != null) {
            getActivity().unregisterReceiver(mSyncCompleteReceiver);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_LOADER_CATEGORIES: {
                return new CursorLoader(getActivity(), Categories.CONTENT_URI, null, null, null, null);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(TAG, "load is finished, entries in cursor: " + data.getCount());
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    
    private class SyncCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SyncService.ACTION_SYNC_COMLETE)) {
                getLoaderManager().restartLoader(ID_LOADER_CATEGORIES, null, CategoriesFragment.this);
            }
        }
    }
}
