package logging_data_LightSensor;

import lejos.robotics.SampleProvider;

public class LightSensor extends Thread {
	private SampleProvider colorvalue;
	final float[] colorData;
	private int LSvalue;

	public LightSensor (SampleProvider colorvalue){
		this.colorvalue = colorvalue;
		colorData = new float[colorvalue.sampleSize()];			// colorData is the buffer in which data are returned
		
	}
	
	public int lightreading() {
		colorvalue.fetchSample(colorData, 0); // Read latest sample in buffer
		LSvalue = (int) ((colorData[0])*100); // converting normalized value										
		return LSvalue;
	}
}
