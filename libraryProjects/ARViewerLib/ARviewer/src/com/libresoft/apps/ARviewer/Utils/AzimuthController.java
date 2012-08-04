/*
 *  Copyright (C) 2011 GSyC/LibreSoft, Universidad Rey Juan Carlos.
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
package com.libresoft.apps.ARviewer.Utils;

import java.util.ArrayList;

public class AzimuthController{
	private static final int VAR_THRESHOLD = 10;
	private static final float GYRO_THRESHOLD = 0.04f;
	private static final float GYRO_CORRECTION = 0.0035f;
	private static final int ERROR_THRESHOLD = 45;
	private static final int MAX_VALUES = 5;
	
	private boolean stable_phase = false;
	
	private float X = 0;
	private ArrayList<Float> last_values = null;
	
	public AzimuthController(){
		last_values = new ArrayList<Float>();
	}
	
	public float getValue(float new_value, float new_gyro){

		insertMeasure(new_value);
		if(stable_phase){
			stable_phase = doStablePhase(new_value, new_gyro);
		}else{
			stable_phase = doUnstablePhase(new_value);
		}
			
		return X;
	}
	
	private boolean doUnstablePhase(float new_value){
		float var = calculateVar(new_value);
		int num = last_values.size();
		if((num == MAX_VALUES) && (var < VAR_THRESHOLD)){
//			Log.e("GyroController", "EXIT InitialPhase: num_values=" + Integer.toString(num) + "; VAR=" + Float.toString(var));
			X = calculateMean(new_value);
			return true;
		}
		if((new_value - X) <= -180)
			X += - 360;
		else if((new_value - X) >= 180)
			X += 360;
		X = X + .8f*(new_value - X);
		if(X > 360)
			X += -360;
		else if(X < 0)
			X += 360;
//		Log.e("GyroController", "InitialPhase: num_values=" + Integer.toString(num) + "; VAR=" + Float.toString(var));
		return false;
	}
	
	private boolean doStablePhase(float new_value, float new_gyro){
		new_gyro = new_gyro + GYRO_CORRECTION;
		
//		if(Math.abs(new_gyro) < GYRO_THRESHOLD)
//			return true;
		
		// Predicted angle
		X = (float) (X - Math.toDegrees(new_gyro)*0.125);
		if(X > 360)
			X += -360;
		else if(X < 0)
			X += 360;
		
		// Error
		float error = Math.abs(new_value - X);
		/* Transform signal */
		if(error >= 180)
			error = 360 - error;
		
		if(error > ERROR_THRESHOLD){
//			Log.e("GyroController", "EXIT StablePhase: error=" + Float.toString(error));
			last_values.clear();
			return false;
		}
		return true;
	}
	
	private void insertMeasure(float new_value){
		if(last_values.size() < MAX_VALUES){
			last_values.add(new_value);
		}else{
			last_values.remove(0);
			last_values.add(new_value);
		}
	}
	
	private float calculateVar(float new_value){
		float mean = 0;
		int num_values = last_values.size();
		for(Float num : last_values){
			mean += num;
			if((new_value - num) <= -180)
				mean += - 360;
			else if((new_value - num) >= 180)
				mean += 360;
		}
		mean = mean/num_values;
		
		float var = 0;
		for(Float num : last_values){
			float diff = mean - num;
			if(diff <= -180)
				diff += 360;
			else if(diff >= 180)
				diff += -360;
			var += Math.pow(diff, 2);
		}
		return var/num_values;
	}
	
	private float calculateMean(float new_value){
		float mean = 0;
		int num_values = last_values.size();
		for(Float num : last_values){
			mean += num;
			if((new_value - num) <= -180)
				mean += - 360;
			else if((new_value - num) >= 180)
				mean += 360;
		}
		mean = mean/num_values;
		
		if(mean > 360)
			mean += -360;
		else if(mean < 0)
			mean += 360;
		
		return mean;
	}
	
}