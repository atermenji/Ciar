package com.libresoft.apps.ARviewer.Utils;

import java.util.ArrayList;


public class AzimuthMotionAverageLPF{
	
	private float Kp = 1;
	
	public AzimuthMotionAverageLPF(float Kp){
		this.Kp = Kp;
	}
	
	private ArrayList<Float> last_values;
	
	public float getValue(float new_value){
		if(last_values == null){
			last_values = new ArrayList<Float>();
			last_values.add(new_value);
			return new_value;
		}
		int num_values = last_values.size();
		
		// Storing the new value
		if(num_values < 10){
			last_values.add(new_value);
			num_values ++;
		}else{
			last_values.remove(0);
			last_values.add(new_value);
		}
		
		// Calculate the mean of the last stored values
		float mean = 0;
		for(Float num : last_values){
			mean += num;
			if((new_value - num) <= -180)
				mean += - 360;
			else if((new_value - num) >= 180)
				mean += 360;
		}
		mean = mean/num_values;
		
		// Applying a LPF
		float value = mean + Kp*(new_value - mean);
		
		if(value > 360)
			value = value - 360;
		else if(value < 0)
			value += 360;
		
		return value;
	}
	
}