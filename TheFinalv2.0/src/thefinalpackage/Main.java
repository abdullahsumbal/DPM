package thefinalpackage;

import java.io.FileNotFoundException;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;


public class Main {
	
	// Static Resources:
	// opening ports
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
	private static final Port lightPort = LocalEV3.get().getPort("S2");
	private static final Port colorPort = LocalEV3.get().getPort("S3");
	
	
	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.45;
	private static Navigator nav;
	private static Odometer odometer;
	
	// class variables
	public static double X_destination, X_previous=0;
	public static double Y_destination,Y_previous =0;
	public static double gradient;
	public static double Y_intercept;
	public static boolean right = false, left=false, up=false, down=false;
	public static double searchX=60,searchY=60;

	
	public static void main(String[] args) throws FileNotFoundException {
	
		//Log.setLogging(true,true,false,true);
		
		//Uncomment this line to print to a file
//		Log.setLogWriter(System.currentTimeMillis() + ".log");
		
		// some objects that need to be instantiated	
		odometer = new Odometer(leftMotor, rightMotor,upperMotor);
		odometer.start();
		
		// Ultrasonic Sensor 
		final UltrasonicPoller usPoller = new UltrasonicPoller(usSensor);
		usPoller.start();
		
		// Light Sensor with Red mode
		SensorModes lightsensor = new EV3ColorSensor(lightPort);
		final SampleProvider lightValue = lightsensor.getMode("Red");
		LightSensor lightSensor = new LightSensor(lightValue);	
		lightSensor.start();
			
		// Color Sensor with ColorID mode
		SensorModes colorsensor = new EV3ColorSensor(colorPort);
		final SampleProvider colorValue = colorsensor.getMode("ColorID");
		ColorSensor colorSensor = new ColorSensor(colorValue);	
		colorSensor.start();
		
		nav = new Navigator(odometer,usPoller,colorSensor,lightSensor, searchX,searchY);
		nav.start();
		
		completeCourse();
	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	/**
	 * this method has the points we want to travel. These points are hard coded for now.
	 */
	private static void completeCourse() {
		
		int[][] waypoints = {{60,60}};

		
		for(int[] point : waypoints){
			
			X_destination=point[0]; Y_destination=point[1];
			
			//Determining the direction of the robot
			if(X_destination>X_previous){right=true;}
			if(X_previous>X_destination){left=true;}
			if(X_previous==X_destination){
				if(Y_destination>Y_previous){up=true;}
				if(Y_destination<Y_previous){down=true;}
			}
			// determining the gradient
			if(up == false && down ==false)
			{
				gradient=(Y_destination-Y_previous)/(X_destination-X_previous);
			}
			
			// determining the y intercept
			Y_intercept=Y_previous-gradient*X_previous;
			
			nav.travelTo(point[0],point[1],true);
			
			// updating the new points
			X_previous=X_destination;
			Y_previous=Y_destination;

			//checking if the robot is Navigating. When it is done navigating, the loop break and it travel to the next point.
			while(nav.isTravelling()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}