/*
 *
 *  Copyright (C) 2010 GSyC/LibreSoft, Universidad Rey Juan Carlos.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Raúl Román López <rroman@gsyc.es>
 *
 */

package com.libresoft.apps.ARviewer.Overlays;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamPreview extends SurfaceView implements SurfaceHolder.Callback {
	public int THRESHOLD = 60;
	
	private OnFrameReadyListener onFrameReadyListener = null;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    
    private int WIDTH, HEIGHT;
	
	private PreviewCallback frameReadyListener = new PreviewCallback() {
		
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			setPreviewCallback(null);
			if(onFrameReadyListener == null)
				return;
			
			int[] edges = new int[camera.getParameters().getPreviewSize().width * camera.getParameters().getPreviewSize().height];
			decodeYUV420SP(edges, data, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
			
			onFrameReadyListener.onFrame(edges);
			clearOnFrameReadyListener();
		}
	};
	
	public CamPreview(Context context) {
        super(context);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
//        WIDTH = context.getResources().getDimensionPixelSize(R.dimen.preview_width);
//        HEIGHT = context.getResources().getDimensionPixelSize(R.dimen.preview_height);
        
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    public CamPreview(Context context, AttributeSet attribs) {
        super(context, attribs);
        
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    
    public void setPreviewCallback(PreviewCallback pc){
    	mCamera.setPreviewCallback(pc);
    }
    
    public void takePicture(PictureCallback pic){
    	mCamera.takePicture(null, null, pic);
    }
    
    public void resumePreview(){
    	mCamera.startPreview();
    }

    public void pausePreview(){
    	mCamera.stopPreview();
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            mCamera = Camera.open();
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
	        mCamera.release();
			Log.e("CamPreview", e.toString());
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
    	Camera.Parameters parameters = mCamera.getParameters();
    	if((WIDTH == 0) || (HEIGHT == 0)){
    		try {
    			List<Camera.Size> supportedSizes = null;
    			supportedSizes = parameters.getSupportedPreviewSizes();

    			//preview form factor
    			Log.e("CamPreview", "wxh = " + Integer.toString(w) + "X" + Integer.toString(h));
    			float ff = ((float)w)/((float)h);

    			//holder for the best form factor and size
    			float bff = 0;
    			int bestw = 0;
    			int besth = 0;
    			Iterator<Camera.Size> itr = supportedSizes.iterator();
    			while(itr.hasNext()) {
    				Camera.Size element = itr.next();
    				//current form factor
    				Log.e("CamPreview", "Size = " + Integer.toString(element.width) + "X" + Integer.toString(element.height));
    				float cff = ((float)element.width)/((float)element.height);
    				if ((ff-cff <= ff-bff) && (element.width <= w) && (element.width >= bestw)) {
    					bff=cff;
    					bestw = element.width;
    					besth = element.height;
    				}

    			}

    			WIDTH = bestw;
    			HEIGHT = besth;
    		} catch (Exception ex) {
    			Log.e("CamPreview", "", ex);
    		}
    	}
    	if((WIDTH == 0) || (HEIGHT == 0)){
    		WIDTH = 480;
    		HEIGHT = 320;
    	}
        parameters.setPreviewSize(WIDTH, HEIGHT);
        parameters.setPictureFormat(PixelFormat.JPEG);
        
        mCamera.setParameters(parameters);
        try {
			mCamera.startPreview();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//	        mCamera.release();
			Log.e("CamPreview", "", e);
		}
    }
    
//    public void printCameraParamsInLog(){
//    	Camera.Parameters parameters = mCamera.getParameters();
//    	
//    	List<Size> ls = parameters.getSupportedPreviewSizes();
//    	if(ls != null)
//    		for(Size s : ls)
//    			Log.e("Sizes: ", Integer.toString(s.width) + "x" + Integer.toString(s.height));
//
//    	List<Integer> lf = parameters.getSupportedPreviewFormats();
//    	if(lf != null)
//    		for(Integer f : lf)
//    			Log.e("Formats: ", Integer.toString(f));
//
//    	List<Integer> lfr = parameters.getSupportedPreviewFrameRates();
//    	if(lfr != null)
//    		for(Integer fr : lfr)
//    			Log.e("Frame rates: ", Integer.toString(fr));
//    	
//    	List<Size> lpics = parameters.getSupportedPictureSizes();
//    	if(lpics != null)
//    		for(Size s : lpics)
//    			Log.e("Picture Sizes: ", Integer.toString(s.width) + "x" + Integer.toString(s.height));
//    }
    
    public Size getCameraPreviewSize(){
    	if(mCamera == null)
    		return null;
    	return mCamera.getParameters().getPreviewSize();
    }

    private void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
    	final int frameSize = width * height;

    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}

    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);

    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;

    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }
    
    public void setOnFrameReadyListener(OnFrameReadyListener listener){
    	this.onFrameReadyListener = listener;
    	setPreviewCallback(frameReadyListener);
    }
    
    public void clearOnFrameReadyListener(){
    	setPreviewCallback(null);
    	onFrameReadyListener = null;
    }
    
    public interface OnFrameReadyListener{
    	public abstract void onFrame(int[] pixels);
    }
    
}