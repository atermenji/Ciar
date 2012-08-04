package com.coreinvader.ciar.ui;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.coreinvader.ciar.R;
import com.coreinvader.ciar.ui.fragment.ObjectsListFragment;
import com.coreinvader.ciar.ui.fragment.ObjectsMapFragment;

public class MapObjectsActivity extends SherlockFragmentActivity {

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_objects);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
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
//            GenericLayer layer = new GenericLayer(0, "", "Map objects", "TROLOLO", "today", new Double(48.010367), new Double(37.768421), new Double(0), null, null);
//            layer.setNodes(mGeoNodes);
//
//            Intent intent = new Intent("com.libresoft.apps.ARviewer.VIEWER");
//            intent.putExtra("LAYER", layer);
//            intent.putExtra("LATITUDE", new Double(48.010367));
//            intent.putExtra("LONGITUDE", new Double(37.768421));
//            startActivity(intent);
            
            Intent intent = new Intent(this, ArActivity.class);
            startActivity(intent);
            
            // Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setDataAndType(Uri.parse(RestDataManager.URL_API_MAPOBJECTS_ALL),
            // "application/mixare-json");
            // startActivity(intent);
            return true;
        }
        case R.id.menu_map: {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_objects, new ObjectsMapFragment())
                    .commit();
            return true;
        }
        case R.id.menu_list: {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map_objects, new ObjectsListFragment())
                    .commit();
            return true;
        }
        }

        return super.onOptionsItemSelected(item);
    }
}
