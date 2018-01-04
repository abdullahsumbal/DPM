package dpmTutorial.src.dpm.tutorial;


import java.io.FileNotFoundException;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Main {
	
	// Static Resources:
	// Left motor connected to output A
	// Right motor connected to output D
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3UltrasonicSensor usSensor = new EV3UltrasonicSensor(SensorPort.S1);
	
	
	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.45;
	private static Navigator nav;
	private static Odometer odometer;
	
	//
	public static double X_destination, X_previous=0;
	public static double Y_destination,Y_previous =0;
	public static double gradient;
	public static double Y_intercept;
	public static boolean right = false, left=false,up=false,down= false;

	
	public static void main(String[] args) throws FileNotFoundException {
	
		Log.setLogging(true,true,false,true);
		
		//Uncomment this line to print to a file
//		Log.setLogWriter(System.currentTimeMillis() + ".log");
		
		// some objects that need to be instantiated	
		odometer = new Odometer(leftMotor, rightMotor,upperMotor);
		odometer.start();
		final UltrasonicPoller usPoller = new UltrasonicPoller(usSensor);
		usPoller.start();
		
//		(new Thread() {
//			public void run() {
//				
//			}
//		}).start();
		
		nav = new Navigator(odometer,usPoller);
		nav.start();
		
		completeCourse();
		
		
	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}

	private static void completeCourse() {
		
//		int[][] waypoints = {{60,30},{30,30},{30,60},{60,0}};
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
						
		// falsing every direction
//			up=false;
//			down=false;
//			right=false;
//			left=false;
//			
			
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