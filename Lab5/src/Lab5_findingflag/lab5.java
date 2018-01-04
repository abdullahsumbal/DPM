package Lab5_findingflag;


import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MeanFilter;
import lejos.hardware.lcd.TextLCD;


public class lab5 {
	
	// Static Resources:
		// Left motor connected to output A
		// Right motor connected to output D
		// Ultrasonic sensor port connected to input S1
		// Color sensor port connected to input S2
		private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		private static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	 
		static Port port = LocalEV3.get().getPort("S1");

		private static final Port colorPort = LocalEV3.get().getPort("S2");	
		

		
		public static void main(String[] args) {
			int button_select;
			final TextLCD t = LocalEV3.get().getTextLCD();
			
			/* Setup ultrasonic sensor
			 * 1. Create a port object attached to a physical port (done above)
			 * 2. Create a sensor instance and attach to port
			 * 3. Create a sample provider instance for the above and initialize operating mode
			 * 4. Create a buffer for the sensor data
			 */
			@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
//			SensorModes usSensor = new EV3UltrasonicSensor(usPort);
//			SampleProvider usValue = usSensor.getMode("Distance");			// colorValue provides samples from this instance
//			float[] usData = new float[usValue.sampleSize()];				// colorData is the buffer in which data are returned
			// Get an instance of the Ultrasonic EV3 sensor
			SensorModes sensor = new EV3UltrasonicSensor(port);

			// get an instance of this sensor in measurement mode
			SampleProvider distance= sensor.getMode("Distance");

			// stack a filter on the sensor that gives the running average of the last 5 samples
			SampleProvider average = new MeanFilter(distance, 5);

			// initialise an array of floats for fetching samples
			float[] sample = new float[average.sampleSize()];

			
			
			/* Setup color sensor
			 * 1. Create a port object attached to a physical port (done above)
			 * 2. Create a sensor instance and attach to port
			 * 3. Create a sample provider instance for the above and initialize operating mode
			 * 4. Create a buffer for the sensor data
			 */
			SensorModes colorSensor = new EV3ColorSensor(colorPort);
			final SampleProvider colorValue = colorSensor.getMode("ColorID");			// colorValue provides samples from this instance
			final float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
					
			// setup the odometer and display
			final Odometer odo = new Odometer(leftMotor, rightMotor, 30, true);
			//LCDInfo lcd = new LCDInfo(odo);
			
			// perform the ultrasonic localization
			final USLocalizer usl = new USLocalizer(odo, distance, sample, USLocalizer.LocalizationType.FALLING_EDGE);
			final LightandSound lightandsound = new LightandSound (distance, sample,colorValue, colorData, odo);
			final scanning Scan = new scanning(odo,upperMotor, distance, sample,colorValue, colorData);
			
			//display
			t.clear();
			t.drawString("press left: part 1", 0, 1);
			t.drawString("press right: part 2", 0, 2);
			
			
			// Press left button to start the call
			button_select = Button.waitForAnyPress();
			while(button_select != Button.ID_LEFT && button_select != Button.ID_RIGHT);
				if(button_select == Button.ID_LEFT)
				{
				
					// Thred for the part 1 of the lab, 
					(new Thread() {
						public void run() {
							lightandsound.doLightandSound();	
						}
					}).start();
				}
				else{
					
					// part 2 of the lab 
					(new Thread() {
						public void run() {
							usl.doLocalization();
							Scan.scanningatorigin();
						}
					}).start();
					
				}
		
			// Stop the program.
			while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);	
			
		}


}
