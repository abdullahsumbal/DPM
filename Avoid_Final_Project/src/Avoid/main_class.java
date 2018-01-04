package Avoid;

import lejos.hardware.Button;         // importing library 
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class main_class {
	
	// Static Resources:
		// Left motor connected to output A
		// Right motor connected to output D
		// Third Upper Motor connected to B
		// ultrasonic sensor connected to port number 1 of sensor
		private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		private static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
		static final Port usPort = LocalEV3.get().getPort("S1"); 
		
		// Constants
		public static final double Wheel_Radius=2.1;
		
		public static final double Wheel_Base =15.45;
		public static int X_coordinate=0;
		public static int Y_coordinate=0;
		public static boolean onoff =true;
		
		// points to travel 
		public static int[] Wavoid_points ={0,150};
		public static int[] avoid_points={0,60,60, 0};
		
		public static void main(String[] args) {
			
			// class variables
			int button_select;
			
			SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
			SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
			float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
			
		
			
			
			
			
			
			// For Display
			final TextLCD t = LocalEV3.get().getTextLCD();
			
			// some objects that need to be instantiated
			Odometer odometer = new Odometer(leftMotor,rightMotor);
			Navigation navigation =new Navigation(leftMotor,rightMotor,Wheel_Base,Wheel_Radius,Wavoid_points,odometer);
			Display display = new Display(odometer,t);
			Avoid avoid = new Avoid(odometer,leftMotor,rightMotor,upperMotor,usDistance,  usData,navigation );
			
			
			do {
				// clear the display
				t.clear();

				// ask the user to start the lab3 part 1 and part 2
				t.drawString("Press left button for lab 1", 0, 1);
				t.drawString("Press right buttonfor lab 2", 0, 2);
				t.drawString("Sumbal", 0, 3);
				
				button_select = Button.waitForAnyPress();
			}
			while(button_select != Button.ID_LEFT);
			
			if(button_select == Button.ID_LEFT)
			{
				leftMotor.forward();
				leftMotor.flt();
				rightMotor.forward();
				rightMotor.flt();
				
				// The first of the lab , this is with out avoidance
				odometer.start();
				navigation.start();
				display.start();
				avoid.start();
				if(onoff){
				
				}
		
			}
			// to end the program by pressing any key
			while (Button.waitForAnyPress() != Button.ID_ESCAPE);  
			System.exit(0);
			
			
			
		}

}
