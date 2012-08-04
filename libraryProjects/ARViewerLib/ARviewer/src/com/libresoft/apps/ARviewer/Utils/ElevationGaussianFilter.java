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


public class ElevationGaussianFilter{

	private static final int VAR_THRESHOLD = 10;
	private static final int MAX_VALUES = 5;
	private static final int ERROR_THRESHOLD = 20;
	
	private ArrayList<Float> last_values = null;
	
	private boolean stable_phase = false;
	
	private static final float INIT_VALUE = 0;
	
	private float X = INIT_VALUE;
	
	public ElevationGaussianFilter(){
		last_values = new ArrayList<Float>();
	}
	
	public float getValue(float new_value){
		insertMeasure(new_value);
		if(stable_phase){
			stable_phase = doStablePhase(new_value);
		}else{
			stable_phase = doUnstablePhase(new_value);
		}
		
		return X;
	}
	
	private boolean doUnstablePhase(float new_value){
		float var = calculateVar();
		int num = last_values.size();
		if((num == MAX_VALUES) && (var < VAR_THRESHOLD)){
			X = calculateMean();
			return true;
		}
		X = X + .8f*(new_value - X);
		return false;
	}
	
	private boolean doStablePhase(float new_value){
		
		float var = calculateVar();
		
		/* Prediction phase: */
		float X_pred = X;
		
		/* Update phase: */
		float gain = (1f/2f) *((float) -Math.exp(-Math.pow(new_value - X_pred, 2)/(2*var)) + 1);
		X = X_pred + gain * (new_value - X_pred);
		
		
		// Error
		float error = Math.abs(new_value - X);
		
		if(error > ERROR_THRESHOLD){
//			Log.e("GaussianFilter", "EXIT StablePhase: error=" + Float.toString(error));
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
	
	private float calculateVar(){
		float mean = calculateMean();

		int num_values = last_values.size();
		float var = 0;
		for(Float num : last_values)
			var += Math.pow(mean - num, 2);
		
		return var/num_values;
	}
	
	private float calculateMean(){
		float mean = 0;
		int num_values = last_values.size();
		for(Float num : last_values){
			mean += num;
		}
		mean = mean/num_values;
		
		return mean;
	}
}