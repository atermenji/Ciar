/*
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
package com.libresoft.apps.ARviewer;

import java.util.List;

import com.libresoft.apps.ARviewer.Overlays.DrawUserStatus;
import com.libresoft.apps.ARviewer.Utils.ElevationController;
import com.libresoft.apps.ARviewer.Utils.AzimuthGaussianFilter;
import com.libresoft.apps.ARviewer.Utils.AzimuthController;
import com.libresoft.apps.ARviewer.Utils.AzimuthMotionAverageLPF;
import com.libresoft.apps.ARviewer.Utils.ElevationGaussianFilter;
import com.libresoft.apps.ARviewer.Utils.ElevationMotionAverageLPF;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class ARCompassManager{
	public static final String CONTROLLER_GYRO = "Gyroscope";
	public static final String CONTROLLER_GAUSS = "Gaussian";
	public static final String CONTROLLER_PROP = "Proportional";
	
	OnCompassChangeListener onCompassChangeListener = null;
	private SensorManager sm;
	
    private float[] acc_values = new float[3];
    private float[] mag_values = new float[3];
    private float[] gyro_values = new float[3];
    
    private AzimuthController azimuthController = null;
    private AzimuthGaussianFilter azimuthGaussianController = null;
    private AzimuthMotionAverageLPF azimuthMALPFController = null;
    
    private ElevationController elevationController = null;
    private ElevationGaussianFilter elevationGaussianController = null;
    private ElevationMotionAverageLPF elevationMALPFController = null;
    
//  private ButterworthFilter butterFilter = new ButterworthFilter();
//  private KalmanFilter kalmanFilter = new KalmanFilter();
//  private MotionAverageLPF motionAverageLPF = new MotionAverageLPF();
//  private CompassController azimuthPID = new CompassController(0.8f, true);
    
    private SensorEventListener gyroEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			switch(event.sensor.getType()){
			case Sensor.TYPE_GYROSCOPE:
				gyro_values = event.values.clone();
				break;
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};
	
    private SensorEventListener accelEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			switch(event.sensor.getType()){
			case Sensor.TYPE_ACCELEROMETER:
				acc_values = event.values.clone();
				break;
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	};
	
    private SensorEventListener magEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			switch(event.sensor.getType()){
			case Sensor.TYPE_MAGNETIC_FIELD:
				mag_values = event.values.clone();
				OnCompassMeasure();
				break;
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			switch(sensor.getType()){
			case Sensor.TYPE_MAGNETIC_FIELD:
				if(drawUserStatus != null)
					drawUserStatus.setCompassAccurate(accuracy);
				break;
			}
		}
	};
    
    private DrawUserStatus drawUserStatus = null;
	
	public ARCompassManager(Context mContext){
		
		sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
	}
	
    public static float getAzimuth(float[] values){
    	return values[0];
    }
    
    public static float getElevation(float[] values){
    	return values[1];
    }
    
    public void setAzimuthControllerType(String type){
    	if(type.equals(CONTROLLER_GYRO)){
    		azimuthMALPFController = null;
    		if(sm.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0){
    			azimuthGaussianController = null;
    			azimuthController = new AzimuthController();
    		}else{
    			azimuthController = null;
    			azimuthGaussianController = new AzimuthGaussianFilter();
    		}
    	}else if(type.equals(CONTROLLER_GAUSS)){
    		azimuthMALPFController = null;
    		azimuthController = null;
    		azimuthGaussianController = new AzimuthGaussianFilter();
    	}else if(type.equals(CONTROLLER_PROP)){
    		azimuthController = null;
    		azimuthGaussianController = null;
    		azimuthMALPFController = new AzimuthMotionAverageLPF(.8f);
    	}
    }
    
    public void setElevationControllerType(String type){
    	if(type.equals(CONTROLLER_GYRO)){
    		elevationGaussianController = null;
    		if(sm.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0){
    			elevationMALPFController = null;
    			elevationController = new ElevationController();
    		}else{
    			elevationController = null;
    			elevationMALPFController = new ElevationMotionAverageLPF(.5f);
    		}
    	}else if(type.equals(CONTROLLER_GAUSS)){
    		elevationMALPFController = null;
    		elevationController = null;
    		elevationGaussianController = new ElevationGaussianFilter();
    	}else if(type.equals(CONTROLLER_PROP)){
    		elevationGaussianController = null;
    		elevationController = null;
    		elevationMALPFController = new ElevationMotionAverageLPF(.5f);
    	}
    }
	
    private void setSensors(){
    	//Setting a sensor listener for accelerometer, magnetic field and gyroscope sensors
    	
    	try {
    		List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_GYROSCOPE);
    		
    		if(sensors.size()>0){
    			Sensor sensor = sensors.get(0);
    			sm.registerListener(gyroEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    		}

    		sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

    		if(sensors.size()>0){
    			Sensor sensor = sensors.get(0);
    			sm.registerListener(accelEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    		}
    		
    		sensors = sm.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
    		
    		if(sensors.size()>0){
    			Sensor sensor = sensors.get(0);
    			sm.registerListener(magEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    		}
			
		} catch (Exception e) {
			Log.e("ARCompassManager", "", e);
		}
    }
    
    public void setOnCompassChangeListener(OnCompassChangeListener onCompassChangeListener){
    	if (this.onCompassChangeListener == null)
    		setSensors();
    	this.onCompassChangeListener = onCompassChangeListener;
    }
    
    public void setDrawUserStatusElement(DrawUserStatus drawUserStatus){
    	this.drawUserStatus = drawUserStatus;
    }
    
    public void unregisterListeners(){
    	sm.unregisterListener(magEventListener);
    	sm.unregisterListener(accelEventListener);
    	sm.unregisterListener(gyroEventListener);
    	onCompassChangeListener = null;
    }
    
    public boolean isGyro(){
    	return sm.getSensorList(Sensor.TYPE_GYROSCOPE).size() > 0;
    }
    
    private void OnCompassMeasure(){
    	if(onCompassChangeListener == null)
    		return;
    	
    	float[] values = new float[3];
    	float[] inr = new float[9];
    	float[] outr = new float[9];
    	float[] i = new float[9];
    	
    	float[] values_acc = acc_values.clone();
    	float[] values_mag = mag_values.clone();
    	
    	SensorManager.getRotationMatrix(inr, i, values_acc, values_mag);
    	
    	if(inr==null)
    		return;
    	
    	SensorManager.remapCoordinateSystem(inr, SensorManager.AXIS_X, SensorManager.AXIS_Z, outr);
    	
    	SensorManager.getOrientation(outr, values);
    	values[0] = (float) Math.toDegrees(values[0]);
    	values[1] = (float) Math.toDegrees(values[1]);
    	values[2] = (float) Math.toDegrees(values[2]);
    	
    	// correction of the measures
		//elevation
    	values[1] = 90 - values[1];
//    	float[] values = or_values.clone();
//    	
//    	// correction of the measures
//    	//azimuth
//    	values[0] += 80;
//    	if(values[0] > 360)
//    		values[0] -= 360;
//    	//elevation
//    	if(Math.abs(values[1]) > 90)
//    		values[2] = 180 - values[2];
    	
    	// PID controller for azimuth and elevation
    	////////////////////////////////
//    	values[0] = azimuthPID.getValue(values[0]);
//    	values[0] = motionAverageLPF.getValue(values[0]);
//    	values[0] = gaussianFilter.getValue(values[0]);
//    	values[0] = kalmanFilter.getValue(values[0]);
//    	values[0] = butterFilter.getValue(values[0]);
//    	values[0] = gyroFilter.getValue(values[0], gyro_values[0]);
    	
    	if(azimuthController != null)
    		values[0] = azimuthController.getValue(values[0], gyro_values[0]);
    	else if(azimuthGaussianController != null)
    		values[0] = azimuthGaussianController.getValue(values[0]);
    	else if(azimuthMALPFController != null)
    		values[0] = azimuthMALPFController.getValue(values[0]);
    	
    	
    	if(elevationController != null)
    		values[1] = elevationController.getValue(values[1], gyro_values[1]);
    	else if(elevationMALPFController != null)
    		values[1] = elevationMALPFController.getValue(values[1]);
    	else if(elevationGaussianController != null)
    		values[1] = elevationGaussianController.getValue(values[1]);
    	
    	
    	onCompassChangeListener.onChange(values);
    }
    
    
    
	
	public interface OnCompassChangeListener {
		public abstract void onChange(float[] values);
	}
	
}