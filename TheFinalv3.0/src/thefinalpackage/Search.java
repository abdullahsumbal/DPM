package thefinalpackage;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * The following code represents basic structure of our Search algorithm.
 * @author DPM Team 01
 *  A detailed description of the class can be found in the Software document under Software Architecturev3.0
 */

public class Search extends BasicNavigator {
	
	// class variables
	public boolean isFound=false;
	BasicNavigator navigation;
	UltrasonicPoller usSensor;
	ColorSensor colorSensor;
	LightSensor lightSensor;
	private Odometer odo;
	private BasicNavigator nav;
	public double heading_angle;
	public int flagColor;
	public int upDown_X;
	public int upDown_Y;
	public int upDown_Ang;
	public int speed = 200;
	public int detection_distance= 10;
	
	
	/**
	 * The constructor of the Search Class. Navigation, odometer, lightSensor and ultrasonicSensor are the inputs.
	 * @param navigator the navigator
	 * @param odometer the robot's odometer
	 * @param lightSensor the light sensor used to detect blocks
	 * @param ultrasonicSensor the ultra sonic sensor used to detect objects
	 */
	public Search (Odometer odo, UltrasonicPoller usSensor, Navigator nav, ColorSensor colorSensor,LightSensor lightSensor){
		navigation = new BasicNavigator(odo);
		this.usSensor=usSensor;
		this.colorSensor=colorSensor;
		this.odo=odo;
		this.nav=nav;
		this.lightSensor=lightSensor;
	}
	
	/**
	 * 	doCaptureFlag is the method that does the actual search function. 
	 */
	public void doCaptureFlag(){
		flagColor = 2;
		if(Main.up) {
			upDown_X = 1;
			upDown_Y = 1;
			upDown_Ang = 0;
		}
		if(Main.down) {
			upDown_X = -1;
			upDown_Y = -1;
			upDown_Ang = 180;
		}
		if(Main.opponentFlagType == 1) { //light blue
			flagColor = 6;
		}
		else if(Main.opponentFlagType == 2) { //red
			flagColor = 0;
		}
		else if(Main.opponentFlagType == 3) {//yellow
			flagColor = 3;
		}
		else if(Main.opponentFlagType == 4) { // white
			flagColor = 6;
		}
		else {
			flagColor = 2; //dark blue
		}
		
		// localization is commented out
		//LightLocalizer lsl = new LightLocalizer(odo, lightSensor);
		//lsl.doLocalization();
		//odo.setPosition(new double [] {Main.search_X, Main.search_Y, 0}, new boolean [] {true, true, true});
		double x;
		double y;
		nav.travelTo(odo.getX()+(15*upDown_X), odo.getY());
		nav.turnTo(90+upDown_Ang,true);
		
		x=odo.getX();
		y=odo.getY()+(2.5*upDown_Y)*Main.tile_length;
		heading_angle = odo.getAng();
		while(!nav.checkIfDone(x, y)){
		
		nav.setSpeeds(speed, speed);
		if(usSensor.getDistance()<detection_distance){
			navigation.leftMotor.setSpeed(speed);
			navigation.rightMotor.setSpeed(speed);
			navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -15),true);
			navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -15),false);
			nav.upperMotor.rotate(-590);
			navigation.leftMotor.setSpeed(speed);
			navigation.rightMotor.setSpeed(speed);
			navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 20),true);
			navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 20),false);
			
			if(colorSensor.colorreading()==flagColor){
				nav.upperMotor.rotate(335);
				nav.travelTo(Main.dropZone_X,Main.dropZone_Y);
				nav.upperMotor.rotate(-350);
				while(true){}
				
			}
			else{
			grabPutToLeft(upDown_Ang);
			}
		}
		
	}
		
		nav.turnTo(0+upDown_Ang, true);
		
		nav.setSpeeds(300, 300);
		x=odo.getX()+(1*upDown_X)*Main.tile_length;
		y=odo.getY();
		heading_angle = odo.getAng();
		while(!nav.checkIfDone(x, y)){
			nav.setSpeeds(300, 300);
			if(usSensor.getDistance()<detection_distance){
				navigation.leftMotor.setSpeed(speed);
				navigation.rightMotor.setSpeed(speed);
				navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -15),true);
				navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -15),false);
				nav.upperMotor.rotate(-590);
				navigation.leftMotor.setSpeed(speed);
				navigation.rightMotor.setSpeed(speed);
				navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 20),true);
				navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 20),false);
				
				if(colorSensor.colorreading()== flagColor){
					nav.upperMotor.rotate(345);
					nav.travelTo(Main.dropZone_X,Main.dropZone_Y);
					nav.upperMotor.rotate(-350);
					while(true){}
					
				}
				else{
				grabPutToLeft(upDown_Ang);
				}
			}
		}
		nav.turnTo(270+upDown_Ang, true);
		nav.setSpeeds(300, 300);
		x=odo.getX ();
		y=odo.getY() - (2.5*upDown_Y)*Main.tile_length;
		heading_angle = odo.getAng();
		while(!nav.checkIfDone(x,y)){
			nav.setSpeeds(300, 300);
			if(usSensor.getDistance()<detection_distance){
				navigation.leftMotor.setSpeed(speed);
				navigation.rightMotor.setSpeed(speed);
				navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -15),true);
				navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -15),false);
				nav.upperMotor.rotate(-570);
				navigation.leftMotor.setSpeed(speed);
				navigation.rightMotor.setSpeed(speed);
				navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 20),true);
				navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 20),false);
				
				if(colorSensor.colorreading()==flagColor){
					nav.upperMotor.rotate(550);
					nav.travelTo(Main.dropZone_X,Main.dropZone_Y);
					nav.upperMotor.rotate(-560);
					while(true){}
				}
				else{
				grabPutToLeft(upDown_Ang);
				}
			}
		}
		nav.setSpeeds(0, 0);
	}
	
		/**
		 * The Method makes the motor move a way to put the block on the side
		 * @param upDown_Ang : if the robot is down or up on the grid , value changes b/w 0 and 180
		 */
		private void grabPutToLeft(int upDown_Ang) {
			
			nav.upperMotor.rotate(345);
			nav.turnTo(180+upDown_Ang, true);
			navigation.leftMotor.setSpeed(speed);
			navigation.rightMotor.setSpeed(speed);
			navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 10),true);
			navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, 10),false);
			nav.upperMotor.rotate(-350);
			
			navigation.leftMotor.setSpeed(speed);
			navigation.rightMotor.setSpeed(speed);
			navigation.leftMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -10),true);
			navigation.rightMotor.rotate(nav.convertDistance(Main.WHEEL_RADIUS, -10),false);
			nav.upperMotor.rotate(555);
			nav.turnTo(heading_angle, true);
		}
		


}

