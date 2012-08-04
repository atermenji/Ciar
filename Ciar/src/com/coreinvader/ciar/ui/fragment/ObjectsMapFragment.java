package com.coreinvader.ciar.ui.fragment;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.coreinvader.ciar.R;
import com.coreinvader.ciar.provider.CiarContract.MapObjects;

public class ObjectsMapFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final float ZOOM_LEVEL = 15;

    private static final int ID_LOADER_MAP_OBJECTS = 1;

    private MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mapview, null);

        mMapView = (MapView) root.findViewById(R.id.yandex_map);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(ID_LOADER_MAP_OBJECTS, null, this);

        // List<MapObject> mapObjects = ((MapObjectsActivity) getActivity()).getMapObjects();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_LOADER_MAP_OBJECTS: {
                return new CursorLoader(getActivity(), MapObjects.CONTENT_URI, null, null, null, null);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        initMap(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    private void initMap(Cursor cursor) {
        MapController mapController = mMapView.getMapController();
        OverlayManager overlayManager = mapController.getOverlayManager();
        Overlay overlay = new Overlay(mapController);

        while (cursor.moveToNext()) {
            Bitmap overlayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stub_mapobject);

            double latitude = cursor.getDouble(cursor.getColumnIndex(MapObjects.MAP_OBJECT_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(MapObjects.MAP_OBJECT_LONGITUDE));
            String name = cursor.getString(cursor.getColumnIndex(MapObjects.MAP_OBJECT_NAME));

            OverlayItem mapObjectOverlay = new OverlayItem(new GeoPoint(latitude, longitude), overlayBitmap);
            BalloonItem mapObjectBalloon = new BalloonItem(mapObjectOverlay.getGeoPoint(), null);
            mapObjectBalloon.setText(name);
            mapObjectOverlay.setBalloonItem(mapObjectBalloon);

            overlay.addOverlayItem(mapObjectOverlay);
        }

        overlayManager.addOverlay(overlay);
        mapController.setZoomCurrent(ZOOM_LEVEL);
    }
}
