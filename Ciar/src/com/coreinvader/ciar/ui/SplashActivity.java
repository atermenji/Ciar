package com.coreinvader.ciar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.coreinvader.ciar.R;

public class SplashActivity extends SherlockFragmentActivity {

    private static final String TAG = "SplashActivity";

    private static final long SPLASH_TIME = 2000;

    private Thread mSplashThread;

    private long mMiliSeconds = 0;
    private boolean mSplashActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_splash);

	mSplashThread = new Thread() {
	    @Override
	    public void run() {
		try {
		    while (mSplashActive && mMiliSeconds < SPLASH_TIME) {
			mMiliSeconds += 100;
			sleep(100);
		    }
		} catch (InterruptedException ex) {
		    Log.e(TAG, "SplashScreen interrupted.", ex);
		} finally {
		    finish();

		    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		    startActivity(intent);
		}
	    }
	};

	mSplashThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	if (event.getAction() == MotionEvent.ACTION_DOWN) {
	    mSplashActive = false;
	}

	return true;
    }

}
