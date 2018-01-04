package thefinalpackage;

import lejos.robotics.SampleProvider;

/**
 * This class fetches and updates the light Sensor value. 
 * @author DPM TEAM 01 
 *
 */
public class LightSensor extends Thread {
	
	// class variables
	private SampleProvider lightvalue;
	final float[] lightData;
	private int LSvalue;
	
	/**
	 *  Constructor Create an instance of a class by assigning the following variable
	 * @param lightvaluevalue (SampleProvider) : Abstraction for classes that fetch samples from a sensor and classes that are able to process samples
	 */
	public LightSensor (SampleProvider lightvalue){
		this.lightvalue = lightvalue;
		lightData = new float[lightvalue.sampleSize()];			
	}
	
	/**
	 * Method to get the light Sensor reading value.
	 * @return LSvalue (integer): value detected by the Light Sensor
	 */ 
	public int lightreading() {
		
		// Read latest sample in buffer
		lightvalue.fetchSample(lightData, 0);
		
		// converting normalized value
		LSvalue = (int) ((lightData[0])*100); 
		
		return LSvalue;
	}
}
