package com.coreinvader.ciar.service;

import org.apache.http.client.HttpClient;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.coreinvader.ciar.io.RemoteCategoriesHandler;
import com.coreinvader.ciar.io.RemoteExecutor;
import com.coreinvader.ciar.io.RemoteMapObjectsHandler;
import com.coreinvader.ciar.util.HttpHelper;

public class SyncService extends IntentService {

    private static final String TAG = "SyncService";

    public static final String EXTRA_STATUS_RECEIVER = "com.coreinvader.ciar.extra.STATUS_RECEIVER";
    public static final String ACTION_SYNC_COMLETE = "com.coreinvader.ciar.SYNC_COMPLETE";
    public static final String EXTRA_SYNC_COMPLETE = "com.coreinvader.ciar.extra.SYNC_COMPLETE";

    public static final int STATUS_RUNNING = 0x1;
    public static final int STATUS_ERROR = 0x2;
    public static final int STATUS_FINISHED = 0x3;

    private static final String URL_API_GENERAL = "http://ciarservice.heroku.com/api";
    private static final String URL_API_CATEGORIES = URL_API_GENERAL + "/layers/index";
    private static final String URL_API_MAP_OBJECTS = "http://ciarservice.heroku.com//api/map_objects/nearby?longitude=37.749794&latitude=48.039879&distance=10.000000&categories=1,2,3,4,11,12,14";

    private RemoteExecutor mRemoteExecutor;

    public SyncService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final HttpClient httpClient = HttpHelper.getHttpClient(this);
        final ContentResolver resolver = getContentResolver();

        mRemoteExecutor = new RemoteExecutor(httpClient, resolver);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
        if (receiver != null) receiver.send(STATUS_RUNNING, Bundle.EMPTY);

        try {
            mRemoteExecutor.executeGet(URL_API_CATEGORIES, new RemoteCategoriesHandler());
            mRemoteExecutor.executeGet(URL_API_MAP_OBJECTS, new RemoteMapObjectsHandler());
        } catch (Exception ex) {
            Log.e(TAG, "Sync error : " + ex, ex);

            if (receiver != null) {
                final Bundle bundle = new Bundle();
                bundle.putString(Intent.EXTRA_TEXT, ex.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }

        if (receiver != null) receiver.send(STATUS_FINISHED, Bundle.EMPTY);
        sendSyncBroadcast(STATUS_FINISHED);
    }
    
    private void sendSyncBroadcast(int status) {
        Intent syncIntent = new Intent(ACTION_SYNC_COMLETE);
        syncIntent.putExtra(EXTRA_SYNC_COMPLETE, status);
        sendBroadcast(syncIntent);
    }
}
