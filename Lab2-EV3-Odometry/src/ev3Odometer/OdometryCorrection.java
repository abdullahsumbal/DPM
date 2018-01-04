/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 15;
	private Odometer odometer; 
	private TextLCD t;
	private int[] gridArrayX = {0,0, 15, 45, 60, 60, 45, 15, 0,0};  // array to detect the X axis value from the black lines
	private int[] gridArrayY = {15, 45, 60, 60, 45, 15, 0,0,0,0};		// array to detect the Y axis value from the black lines 
	
	private static final Port LSensor = LocalEV3.get().getPort("S1");  //Get a port instance for each sensor used  
	static SensorModes myLSensor = new EV3ColorSensor(LSensor);			// Get an instance for each sensor 
	static SampleProvider myLSensorValue = myLSensor.getMode("Red");  	//Get an instance of each sensor in measurement mode 
	static float[] sampleLS = new float[myLSensorValue.sampleSize()];	//Allocate buffers for data return             
	int count;
	double LSvalue;
	double Yvalue;
	
	// constructor
	public OdometryCorrection(Odometer odometer,TextLCD t) {
		this.odometer = odometer;
		this.t=t;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		count =0;

		while (true) {
			correctionStart = System.currentTimeMillis();
			myLSensorValue.fetchSample(sampleLS,0);   // Read latest sample in buffer   
			LSvalue=  (int)((sampleLS[0])*100);			// converting normalized value to a scale between 0 to 100

			if(LSvalue <45)					// if the sensor detects less 45 value 
			{
				Sound.beep();				// make beep sound 
				
				odometer.setX(gridArrayX[count]);	// update the value of X on odometer
			
				if(count ==6)
				{
					Yvalue=odometer.getY();			// store the value of Y at count 6
				}
				if(count>=7)						// if the value of the robot remain constant after 6 count.
				{
					odometer.setY(Yvalue);
				}
				else
				{
				odometer.setY(gridArrayY[count]);	// update the value of Y on odometer
				}
				count++;					// increment the counter.
			}

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	public double GetCount() {
		 
			this.LSvalue = LSvalue;
			return this.LSvalue;	
	}
}