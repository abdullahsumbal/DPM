// Lab2.java



import lejos.hardware.Button;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class Lab2 {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final Port csPort = LocalEV3.get().getPort("S1");
	private static final Port usPortfront = LocalEV3.get().getPort("S3");
	private static final Port usPortside = LocalEV3.get().getPort("S4");
	

	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	//"Actual" value of TRACK in order to make a perfect square without correction
	public static final double TRACK = 15.8; //higher makes it turn inward more, lower makes it turn outward more

	public static void main(String[] args) {
		int buttonChoice;
		
		//Implementing the colorSensor in order to get the readings from it. based on lab1(USsensor)
		SensorModes csSensor = new EV3ColorSensor(csPort);
		SampleProvider csRedMode = csSensor.getMode("Red");
		float[] csData = new float[csRedMode.sampleSize()];

		// some objects that need to be instantiated
		
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer odometer = new Odometer(leftMotor, rightMotor, 30, true);
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer,t);
		//I had to pass the sensor which was initialized here into OdometryCorrection.
		OdometryCorrection odometryCorrection = new OdometryCorrection(odometer, csPort, csData, csSensor, csRedMode);

		do {
			// clear the display
			t.clear();

			// ask the user whether the motors should drive in a square or float
			t.drawString("< Left | Right >", 0, 0);
			t.drawString("       |        ", 0, 1);
			t.drawString(" Float | Drive  ", 0, 2);
			t.drawString("motors | in a   ", 0, 3);
			t.drawString("       | square ", 0, 4);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			
			leftMotor.forward();
			leftMotor.flt();
			rightMotor.forward();
			rightMotor.flt();
			
			odometer.start();
			odometryDisplay.start();
			
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			
			odometer.start();
			odometryDisplay.start();
			//odometryCorrection.start();

			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			(new Thread() {
				public void run() {
					SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS, WHEEL_RADIUS, TRACK);
				}
			}).start();
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}