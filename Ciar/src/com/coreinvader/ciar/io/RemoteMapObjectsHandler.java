package com.coreinvader.ciar.io;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.net.Uri;

import com.coreinvader.ciar.provider.CiarContract;
import com.coreinvader.ciar.provider.CiarContract.MapObjects;
import com.coreinvader.ciar.provider.CiarContract.MapObjectsColumns;
import com.coreinvader.ciar.util.HandlerUtils;

/**
 * Handle a remote {@link JSONArray} that defines a set of {@link MapObjects} entries. 
 */
public class RemoteMapObjectsHandler extends JsonHandler {

    public RemoteMapObjectsHandler() {
	super(CiarContract.CONTENT_AUTHORITY);
    }
    
    @Override
    public ArrayList<ContentProviderOperation> parse(JSONArray jsonArray, ContentResolver resolver)
	    throws JSONException, IOException {
	final ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
	
	for (int i = 0; i < jsonArray.length(); i++) {
	    JSONObject jsonMapObject = jsonArray.getJSONObject(i);
	    
	    final String mapObjectId = HandlerUtils.sanitizeId(jsonMapObject.getString("id"));
	    final Uri mapObjectUri = MapObjects.buildMapObjectUri(mapObjectId);
	    
	    //TODO check mapobject last updated
	    
	    batch.add(ContentProviderOperation.newDelete(mapObjectUri).build());
	    
	    final ContentProviderOperation.Builder mapObjectBuilder = 
		    ContentProviderOperation.newInsert(MapObjects.CONTENT_URI);
	    mapObjectBuilder.withValue(MapObjects.MAP_OBJECT_ID, mapObjectId);
	    mapObjectBuilder.withValue(MapObjects.CATEGORY_ID, jsonMapObject.getString("category_id"));
	    mapObjectBuilder.withValue(MapObjects.MAP_OBJECT_NAME, jsonMapObject.getString("name"));
	    mapObjectBuilder.withValue(MapObjects.MAP_OBJECT_ADDRESS, jsonMapObject.getString("address"));
	    mapObjectBuilder.withValue(MapObjects.MAP_OBJECT_DISCTANCE, jsonMapObject.getString("distance"));
	    mapObjectBuilder.withValue(MapObjectsColumns.MAP_OBJECT_CATEGORY_NAME, 
		    jsonMapObject.getString("category_name"));
	    mapObjectBuilder.withValue(MapObjects.MAP_OBJECT_LATITUDE, jsonMapObject.getString("latitude"));
	    mapObjectBuilder.withValue(MapObjects.MAP_OBJECT_LONGITUDE, jsonMapObject.getString("longitude"));
	    batch.add(mapObjectBuilder.build());
	}
	
	return batch;
    }

}
