package thefinalpackage;


import lejos.robotics.SampleProvider;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  50 mS at the end of each loop, then one cycle through the loop
//  is approximately 70 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 14 Hz.
//

/**
 * UltrasonicPoller class which extends Threads. The gets the distance from the Ultrasonic Sensor and converts it to cm.
 * @author DPM TEAM 01 
 * A detailed description of the class can be found in the Software document under Software Architecturev3.0
 */
public class UltrasonicPoller extends Thread {
	private SampleProvider us;
	private float[] usData;
	int distance;

	/**
	 * Constructor which takes in the SampleProvider class of the EV3UltrasonicSensor package.
	 * @param us : SampleProvider class from the EV3UltrasonicSensor package
	 */
	public UltrasonicPoller(SampleProvider us) {
		this.us = us;
		usData = new float[us.sampleSize()];
	}

	/**
	 * Method to run the Thread of the UltrasonicPoller class. The goes in a infinite loop which it fetches the distance from the Ultrasonic sensor
	 */
	public void run() {
		while (true) {
			
			// acquire data
			us.fetchSample(usData, 0); 
			
			 // extract from buffer, cast
			distance = (int) (usData[0] * 100.0);
		}
	}

	/**
	 * getter Method to get the distance from the Ultrasonic sensor.
	 * @return distance (integer) in cm 
	 */
	public int getDistance() {
		return distance;
	}

}