package thefinalpackage;

import lejos.robotics.SampleProvider;

/**
 * This class fetches and updates the color Sensor value. 
 * @author DPM TEAM 01 
 *
 */
public class ColorSensor extends Thread {
	
	// class variables
	private SampleProvider colorSensor;
	private float[] colorData;
	private int LSvalue;
	
	/**
	 *  Constructor : Create an instance of a class by assigning the following variable
	 * @param colorvalue (SampleProvider) : Abstraction for classes that fetch samples from a sensor and classes that are able to process samples
	 */
	public ColorSensor (SampleProvider colorSensor, float[] colorData){
		
		// colorData is the buffer in which data are returned
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		
	}
	
	/**
	 * Method to get the color ID value.
	 * @return LSvalue (integer): Color ID detected by the Color Sensor
	 */ 
	public int colorreading() {
		
		// Read latest sample in buffer
		colorSensor.fetchSample(colorData, 0);
		
		// converting normalized value
		LSvalue = (int) ((colorData[0])); 
		
		return LSvalue;
	}
}