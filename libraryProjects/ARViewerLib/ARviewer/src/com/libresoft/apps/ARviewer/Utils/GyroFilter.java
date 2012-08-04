
package com.libresoft.apps.ARviewer.Utils;

import java.util.ArrayList;

public class GyroFilter{
	private static final float THRESHOLD = 0.05f;
	
	private float X = 0;
	private ArrayList<Float> last_values = null;
	
	public float getValue(float new_value, float new_gyro){
		if(last_values == null){
			last_values = new ArrayList<Float>();
			last_values.add(new_value);
			X = new_value;
			return X;
		}
		
		if(Math.abs(new_gyro) > THRESHOLD){
			last_values.clear();
			X = (float) (X - Math.toDegrees(new_gyro)*0.125);
		}else{
			// Calculate the variance of the last stored values
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
			var = var/num_values;
			
			/* Transform signal */
			if((new_value - X) <= -180)
				X += - 360;
			else if((new_value - X) >= 180)
				X += 360;

			/* Prediction phase: */
			float X_pred = X;

			/* Update phase: */
			float gain = (1f/12f) *((float) -Math.exp(-Math.pow(new_value - X_pred, 2)/(2*var)) + 1);
			X = X_pred + gain * (new_value - X_pred);
			
			if(X > 360)
				X += -360;
			else if(X < 0)
				X += 360;
		}
		
		// Storing the new value
		if(last_values.size() < 5){
			last_values.add(new_value);
		}else{
			last_values.remove(0);
			last_values.add(new_value);
		}
			
		return X;
	}
	
	
}