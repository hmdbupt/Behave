package com.bupt.hammad.behave;

import java.text.DecimalFormat;

public class MovingAverage {
    private float circularBuffer[];		        //Save sensor's deltaL data
	private float returnAverageOfSensor;  		//Return average of sensor
	private float returnAverageOfSensor2DF;     // Value converted into decimal format .00
	private int Index;					        //Location of sensor data array
	private int count;
	
	//MovingAverage is a constructor of the class MovingAverage
	//It takes in an integer type value and typecasts it into a float type array
	public MovingAverage(int k){
		circularBuffer = new float[k];
		count = 0;
		Index = 0;
		returnAverageOfSensor = 0;
		returnAverageOfSensor2DF = 0;
	}
	
	//Get the average
	public float getValue(){
		//Formats the value to two decimal places Examples 10.00
		DecimalFormat decimalFormat = new DecimalFormat("#.00");
		returnAverageOfSensor2DF = Float.valueOf(decimalFormat.format(returnAverageOfSensor));
		return returnAverageOfSensor2DF;
	}
	
	//Pass the newly acquired sensor data
	public void setValue(float v){
		if (count++ == 0)
		{
			primeBuffer(v);
		}
		float lastValue = circularBuffer[Index];
		//Calculate the sensor average
		returnAverageOfSensor = returnAverageOfSensor + (v-lastValue) / circularBuffer.length;
		circularBuffer[Index] = v;   //Updates the sensor data in the window
		Index = nextIndex(Index);
	}

	private void primeBuffer(float val) {
		for(int i = 0; i < circularBuffer.length; ++i)
		{
			circularBuffer[i]=val;
		}
		returnAverageOfSensor = val;
		
	}

	private int nextIndex(int curIndex) {
		if( curIndex + 1 >= circularBuffer.length)
		{
			return 0;
		}
		return curIndex + 1;
	}
}
