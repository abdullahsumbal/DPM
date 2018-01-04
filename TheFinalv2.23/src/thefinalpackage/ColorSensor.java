package thefinalpackage;

import lejos.robotics.SampleProvider;
/**
 * This class fetches and updates the color Sensor value. 
 * @author DPM TEAM 01 
 *
 */
public class ColorSensor extends Thread {
	private SampleProvider colorvalue;
	final float[] colorData;
	private int CSvalue;
	/**
	 *  Constructor : Create an instance of a class by assigning the following variable
	 * @param colorvalue (SampleProvider) : Abstraction for classes that fetch samples from a sensor and classes that are able to process samples
	 */
	public ColorSensor (SampleProvider colorvalue){
		this.colorvalue = colorvalue;
		colorData = new float[colorvalue.sampleSize()];			// colorData is the buffer in which data are returned
		
	}
	/**
	 * Method to get the color ID value.
	 * @return LSvalue (integer): Color ID detected by the Color Sensor
	 */ 
	public int lightreading() {
		colorvalue.fetchSample(colorData, 0); // Read latest sample in buffer
		CSvalue = (int) ((colorData[0])); // converting normalized value										
		return CSvalue;
	}
}