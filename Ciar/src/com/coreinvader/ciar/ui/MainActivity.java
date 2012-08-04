package com.coreinvader.ciar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.coreinvader.ciar.R;
import com.coreinvader.ciar.service.SyncService;
import com.coreinvader.ciar.util.DetachableResultReceiver;

public class MainActivity extends SherlockFragmentActivity {

    private MenuItem mRefreshItem;
    private SyncStatusUpdaterFragment mSyncStatusUpdaterFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FragmentManager fm = getSupportFragmentManager();
        mSyncStatusUpdaterFragment = (SyncStatusUpdaterFragment) fm.findFragmentByTag(SyncStatusUpdaterFragment.TAG);
        if (mSyncStatusUpdaterFragment == null) {
            mSyncStatusUpdaterFragment = new SyncStatusUpdaterFragment();
            fm.beginTransaction().add(mSyncStatusUpdaterFragment, SyncStatusUpdaterFragment.TAG).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_main, menu);

        mRefreshItem = menu.findItem(R.id.menu_refresh);
        mRefreshItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                startSync();
                item.setActionView(R.layout.indeterminate_progress_action);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void startSync() {
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, SyncService.class);
        intent.putExtra(SyncService.EXTRA_STATUS_RECEIVER, mSyncStatusUpdaterFragment.mReceiver);
        startService(intent);
    }

    private void updateRefreshStatus(boolean refreshing) {
        if (mRefreshItem != null) {
            if (refreshing) {
                mRefreshItem.setActionView(R.layout.indeterminate_progress_action);
            } else {
                mRefreshItem.setActionView(null);
            }
        }
    }

    /**
     * A non-UI fragment, retained across configuration changes, that updates its activity's UI when sync status
     * changes.
     */
    public static class SyncStatusUpdaterFragment extends SherlockFragment implements DetachableResultReceiver.Receiver {

        public static final String TAG = SyncStatusUpdaterFragment.class.getName();

        private boolean mSyncing = false;
        private DetachableResultReceiver mReceiver;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            mReceiver = new DetachableResultReceiver(new Handler());
            mReceiver.setReceiver(this);
        }

        /** {@inheritDoc} */
        public void onReceiveResult(int resultCode, Bundle resultData) {
            MainActivity activity = (MainActivity) getActivity();
            if (activity == null) {
                return;
            }

            switch (resultCode) {
                case SyncService.STATUS_RUNNING: {
                    mSyncing = true;
                    break;
                }
                case SyncService.STATUS_FINISHED: {
                    mSyncing = false;
                    break;
                }
                case SyncService.STATUS_ERROR: {
                    // Error happened down in SyncService, show as toast.
                    mSyncing = false;
                    final String errorText = getString(R.string.toast_sync_error)
                            + resultData.getString(Intent.EXTRA_TEXT);
                    Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
                    break;
                }
            }

            activity.updateRefreshStatus(mSyncing);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            ((MainActivity) getActivity()).updateRefreshStatus(mSyncing);
        }
    }
}