package com.coreinvader.ciar.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.coreinvader.ciar.R;
import com.coreinvader.ciar.provider.CiarContract.MapObjects;
import com.libresoft.apps.ARviewer.ARviewer;
import com.libresoft.sdk.ARviewer.Types.ExtenalInfo;
import com.libresoft.sdk.ARviewer.Types.GenericLayer;
import com.libresoft.sdk.ARviewer.Types.GeoNode;
import com.libresoft.sdk.ARviewer.Types.Photo;

public class ArActivity extends ARviewer implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ID_LOADER_MAP_OBJECTS = 1;

    private ActionBar mActionBar;

    private ArrayList<GeoNode> mGeoNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showMenu = false;
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        getSupportLoaderManager().initLoader(ID_LOADER_MAP_OBJECTS, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_items_mapobjects, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
            case R.id.menu_camera: {
                return true;
            }
            case R.id.menu_map: {
                finish();
                return true;
            }
            case R.id.menu_list: {
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMenu = false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, MapObjects.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        generateGeoNodesFromCursor(data);

        GenericLayer layer = new GenericLayer(0, "", "Map objects", "TROLOLO", "today", new Double(48.010367),
                new Double(37.768421), new Double(0), null, null);
        layer.setNodes(mGeoNodes);
        setMyLayer(layer);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void generateGeoNodesFromCursor(Cursor cursor) {
        if (mGeoNodes != null) mGeoNodes.clear();
        mGeoNodes = new ArrayList<GeoNode>();

        String url = "http://habrastorage.org/storage2/644/c3d/484/644c3d484a9b3fae6fce38aecd666164.jpg";
        while (cursor.moveToNext()) {
            Double latitude = cursor.getDouble(cursor.getColumnIndex(MapObjects.MAP_OBJECT_LATITUDE));
            Double longitude = cursor.getDouble(cursor.getColumnIndex(MapObjects.MAP_OBJECT_LONGITUDE));
            Double distance = cursor.getDouble(cursor.getColumnIndex(MapObjects.MAP_OBJECT_DISCTANCE));
            String name = cursor.getString(cursor.getColumnIndex(MapObjects.MAP_OBJECT_NAME));

            GeoNode geoNode = new Photo(new Integer(0), latitude, longitude, new Double(0), new Double(0), name, name
                    + " : description", url, null, null, null, distance);

            ExtenalInfo info = new ExtenalInfo();
            info.setPhotoNormalUrl("http://habrastorage.org/storage2/644/c3d/484/644c3d484a9b3fae6fce38aecd666164.jpg");
            info.setUrlInfo(name);
            geoNode.setExternalInfo(info);

            mGeoNodes.add(geoNode);
        }
    }
}
