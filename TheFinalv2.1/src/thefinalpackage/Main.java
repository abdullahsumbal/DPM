package thefinalpackage;

import java.io.FileNotFoundException;
import java.io.IOException;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import wifi.StartCorner;
import wifi.Transmission;
import wifi.WifiConnection;


public class Main {
	
	private static final String SERVER_IP = "192.168.10.104";
	private static final int TEAM_NUMBER = 1;
	
	// Static Resources:
	// opening ports
	public static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	public static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	public static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	public static final EV3UltrasonicSensor usSensor_front = new EV3UltrasonicSensor(SensorPort.S3);
	public static final EV3UltrasonicSensor usSensor_side = new EV3UltrasonicSensor(SensorPort.S4);
	private static final Port lightPort = LocalEV3.get().getPort("S1");
	private static final Port colorPort = LocalEV3.get().getPort("S2");
	
	
	// Constants
	public static final double WHEEL_RADIUS = 2.1;
	public static final double TRACK = 15.85;
	private static Navigator nav;
	private static Odometer odometer;
	
	// class variables
	public static double opponentHomeZoneBL_X, opponentHomeZoneBL_Y, dropZone_X, dropZone_Y,X_destination, X_previous=0;
	public static double Y_destination,Y_previous =0;
	public static double gradient;
	public static double Y_intercept;
	public static boolean right = false, left=false, up=false, down=false;
	public final static double tile_length= 30.48;
	public static double searchX_1=tile_length*1,searchY_1=tile_length*1;
	public static double searchX_2=tile_length*2,searchY_2=tile_length*2;
	

	
	public static void main(String[] args) throws FileNotFoundException {
		
		final TextLCD text = LocalEV3.get().getTextLCD();
		
//		WifiConnection conn = null;
//		try {
//			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
//		} catch (IOException e) {
//			text.drawString("Connection failed", 0, 8);
//		}
//		
//		// example usage of Transmission class
//		Transmission t = conn.getTransmission();
//		if (t == null) {
//			text.drawString("Failed", 0, 8);
//		} else {
//			StartCorner corner = t.startingCorner;
//			int homeZoneBL_X = t.homeZoneBL_X;
//			int homeZoneBL_Y = t.homeZoneBL_Y;
//			opponentHomeZoneBL_X = t.opponentHomeZoneBL_X;
//			opponentHomeZoneBL_Y = t.opponentHomeZoneBL_Y;
//			dropZone_X = t.dropZone_X;
//			dropZone_Y = t.dropZone_Y;
//			int flagType = t.flagType;
//			int	opponentFlagType = t.opponentFlagType;
//		
//			// print out the transmission information
//			conn.printTransmission();
//		}
	
		//Log.setLogging(true,true,false,true);
		
		//Uncomment this line to print to a file
//		Log.setLogWriter(System.currentTimeMillis() + ".log");
		int buttonChoice;
		
		
		// some objects that need to be instantiated	
		odometer = new Odometer(leftMotor, rightMotor,upperMotor);
		odometer.start();
		
		
		// Ultrasonic Sensor on the front
		final UltrasonicPoller usPoller_front = new UltrasonicPoller(usSensor_front);
		usPoller_front.start();
		
		
		// Ultrasonic Sensor on the sdie
		final UltrasonicPoller usPoller_side = new UltrasonicPoller(usSensor_side);
		usPoller_side.start();
		
		//starting the display
		LCDInfo lcd = new LCDInfo(odometer, usPoller_front, usPoller_side);
		
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
		upperMotor.stop();
		
		do {
			text.clear();
			text.drawString("Left: Start", 0, 0);
			buttonChoice = Button.waitForAnyPress();
		}
		while (buttonChoice != Button.ID_LEFT);
		
		if(buttonChoice == Button.ID_LEFT) {
			nav = new Navigator(odometer,usPoller_front, usPoller_side, colorSensor,lightSensor, searchX,searchY);
			nav.start();
		
		completeCourse();
		}
	
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	/**
	 * this method has the points we want to travel. These points are hard coded for now.
	 */
	private static void completeCourse() {
		
			
			//double [][] waypoints = {{30.48*opponentHomeZoneBL_X,30.48*opponentHomeZoneBL_Y},{30.48*0,30.48*0}};
	
		double [][] waypoints = {{tile_length*0,tile_length*2}};;//,{30.48*2,30.48*2},{30.48*2,30.48*0},{0,0}};
			for(double[] point : waypoints){
				
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
	/**
	 * Methods can calculates the absolute distance to be traveled from one point to the other
	 * @param origin_X The X value of the first point
	 * @param origin_Y The Y value of the first point
	 * @param dest_X The X value of the destination
	 * @param dest_Y The Y value of the destination
	 * @return absoluteDistance the distance that is traveled
	 */
	public static double distanceToTravel(double origin_X, double origin_Y, double dest_X, double dest_Y){
		double absoluteDistance = 0;
		double distance_X = 0;
		double distance_Y = 0;
		distance_X = origin_X - dest_X;
		distance_Y = origin_Y - dest_Y;
		absoluteDistance = Math.sqrt(Math.pow(distance_X, 2) + Math.pow(distance_Y, 2));;
		return absoluteDistance;
	}
	
}