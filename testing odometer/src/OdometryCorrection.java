/* 
 * OdometryCorrection.java
 */


import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	
	//importing the color sensor in order to use it
	private static Port csPort;
	private static double sensorValue;
	private static float[] csData;
	private static SensorModes csSensor;
	private static SampleProvider csRedMode;
	
	//constants use in my odometryCorrection code
	private static int xCounter = 0;
	private static int yCounter = 0;
	private static double xValue = 0;
	private static double yValue = 0;

	// constructor
	//importing the sensor in the constructor
	public OdometryCorrection(Odometer odometer, Port csPORT, float[] csDATA, SensorModes csSENSOR, SampleProvider csREDMODE) {
		this.odometer = odometer;
		this.csPort = csPORT;
		this.csData = csDATA;
		this.csSensor = csSENSOR;
		this.csRedMode = csREDMODE;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
		

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			//implementing the colorSensor
			csRedMode.fetchSample(csData, 0);
			sensorValue = csData[0]*100;
			
			double angle = odometer.getAng();
			
			// put your correction code here
			//Re-adjusting the x and y values in order to get the actual values measured by this code.
			double yOffSet = 6.5;
			double xOffSet = 4.0;
			
			// What happens when our robot detects a black line
			if (sensorValue > 20 && sensorValue < 45) {
				
				//when the robot is moving in the positive y-axis(angle in radians)
				//when it crosses the first line, yCounter = 0 so it sets the value to yOffSet + 15
				//when it crosses the second line, yCounter = 1 so it sets the value ot yOffSet + 45
				if (angle < 1.50) {
					yValue = 30*yCounter + 15 + yOffSet ;
					yCounter++;
					odometer.setY(yValue);
					Sound.buzz();
				}
				//when the robot is moving in the negative y-axis(angle in radians)
				//Decrease the counter at first because it starts at yCounter = 2
				else if (angle > 3.0 && angle < 4.5) {
					yCounter--;
					yValue = 30*yCounter + 15 + yOffSet;
					odometer.setY(yValue);
					Sound.twoBeeps();
				}
				
				//when the robot is moving in the positive x-axis(angle in radians)
				if (angle > 1.50 && angle < 3.0) {
					xValue = 30*xCounter + 15 + xOffSet;
					xCounter++;
					odometer.setX(xValue);
					Sound.buzz();
				}
				//when the robot is moving in the negative x-axis(angle in radians)
				else if (angle > 4.5 && angle < 6.0) {
					xCounter--;
					xValue = 30*xCounter + 15 + xOffSet;
					odometer.setX(xValue);
					Sound.twoBeeps();
				}
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
	
	public static double getSensorValue() {
		return sensorValue;
	}
}