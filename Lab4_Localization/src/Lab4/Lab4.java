package Lab4;

import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;



public class Lab4 {

	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final Port usPort = LocalEV3.get().getPort("S3");		
	private static final Port colorPort = LocalEV3.get().getPort("S1");		

	
	public static void main(String[] args) {
		int button_select;
		
		/* Setup ultrasonic sensor
		 * 1. Create a port object attached to a physical port (done above)
		 * 2. Create a sensor instance and attach to port
		 * 3. Create a sample provider instance for the above and initialize operating mode
		 * 4. Create a buffer for the sensor data
		 */
		@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);
		SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
		float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
		
		
		/* Setup color sensor
		 * 1. Create a port object attached to a physical port (done above)
		 * 2. Create a sensor instance and attach to port
		 * 3. Create a sample provider instance for the above and initialize operating mode
		 * 4. Create a buffer for the sensor data
		 */
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		final SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		final float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
				
		// setup the odometer and display
		final Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
		LCDInfo lcd = new LCDInfo(odo);
		
		// perform the ultrasonic localization
		final USLocalizer usl = new USLocalizer(odo, usValue, usData, USLocalizer.LocalizationType.FALLING_EDGE);

		// Press left button to start the call
		button_select = Button.waitForAnyPress();
		while(button_select != Button.ID_LEFT);
			if(button_select == Button.ID_LEFT)
			{
			
				// spawn a new Thread for light and distance Sensor class, so it can stopped later
				(new Thread() {
					public void run() {
						usl.doLocalization();
						
						// perform the light sensor localization
						LightLocalizer lsl = new LightLocalizer(odo, colorValue, colorData);
						lsl.doLocalization();	
					}
				}).start();
			}
	
		// Stop the program.
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);	
		
	}

}
