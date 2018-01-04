package thefinalpackage;


import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import thefinalpackage.USLocalizer.LocalizationType;

/**
 * Navigator class is responsible for turning , traveling and detecting objects to avoid. The class also extends BasicNavigator class.
 * @author DPM TEAM 01 
 *
 */
public class Navigator extends BasicNavigator {
	
	enum State {
		INIT, TURNING, TRAVELLING, EMERGENCY , LOCALIZING , SEARCHING
	};
	State state;
	
	// class variables
	public Navigator navigation;
	private boolean isNavigating = false;
	private double destx, desty;
	final static int SLEEP_TIME = 50;
	public ColorSensor colorSensor;
	public LightSensor lightSensor;
	UltrasonicPoller usSensor_front;
	UltrasonicPoller usSensor_side;
	Odometer odo;
	boolean resolved= false;
	double distance;
	double starting_x=0, starting_y=0;
	double counter_x=1;
	double counter_y=1;
	
	/**
	 * Constructor which takes Odometer class object and UltrasonicPoller class object as parameter
	 * @param odo : Odometer class object
	 * @param usSensor : UltrasonicPoller class object
	 */
	public Navigator(Odometer odo, UltrasonicPoller usSensor_front,UltrasonicPoller usSensor_side,ColorSensor colorSensor, LightSensor lightSensor) {
		super(odo);
		this.usSensor_front = usSensor_front;
		this.usSensor_side = usSensor_side;
		this.colorSensor= colorSensor;
		this.lightSensor= lightSensor;
		this.odo=odo;
	}

	/**
	 *Method to travel to X and Y coordinate on the field with option of avoiding or not avoiding the obstacle.
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * 
	 * When avoid=true, the nav thread will handle traveling. If you want to
	 * travel without avoidance, this is also possible. In this case,
	 * the method in the Navigation class is used.
	 * @param x (double) : X coordinate of the field we want the robot to travel to.
	 * @param y (double) : Y coordinate of the field we want the robot to travel to.
	 * @param avoid (boolean) : if True then travel to destination with avoiding obstacle on the way. If False then travel to a straight to the desired destination
	 */
	public void travelTo(double x, double y, boolean avoid) {
		if (avoid) {
			destx = x;
			desty = y;
			isNavigating = true;
		} else {
			super.travelTo(x, y);
		}
	}

	/**
	 * Method to start the Thread. The Method calls other Methods and is responsible for checking the current state of the robot. 
	 * Please view the software architecture version 3.0 for a detailed description of this class
	 */
	public void run() {
		ObstacleAvoidance avoidance = null;
		Search search=null;
		state = State.LOCALIZING;
		while (true) {
			switch (state) {
			case LOCALIZING:
				{
					upperMotor.rotate(50);
					USLocalizer usl = new USLocalizer(odo, usSensor_front, USLocalizer.LocalizationType.FALLING_EDGE);
					LightLocalizer lsl = new LightLocalizer(odo, lightSensor);
					usl.doLocalization();
					lsl.doLocalization();
					
					// the position of the robot is set according to the corner it is placed
					if(Main.corner_id == 1) {
						odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});
					}
					else if(Main.corner_id == 2) {
						odo.setPosition(new double [] {10*Main.tile_length, 0.0, 90}, new boolean [] {true, true, true});
					}
					else if(Main.corner_id == 3) {
						odo.setPosition(new double [] {10*Main.tile_length, 10*Main.tile_length, 180}, new boolean [] {true, true, true});
					}
					else {
						odo.setPosition(new double [] {0.0, 10*Main.tile_length, 270}, new boolean [] {true, true, true});
					}
					
					state = State.INIT;
				}
				break;
			case INIT:
				if (isNavigating) {
					state = State.TURNING;
				}
				break;
			
			case TURNING:
				double destAngle = getDestAngle(destx, desty);
				turnTo(destAngle);
				state = State.TRAVELLING;
				break;
			case TRAVELLING:
				if (checkEmergency()) { // order matters!
					state = State.EMERGENCY;
					avoidance = new ObstacleAvoidance(this,odo,usSensor_side);
					avoidance.doavoidance();
					
				}
				else if(checkIfDone(Main.search_X,Main.search_Y)){
					state = State.SEARCHING;
					search=new Search(odo,usSensor_front,this,colorSensor,lightSensor);
					search.doCaptureFlag();	
				} 
				else if (!checkIfDone(destx, desty)) {
					updateTravel();
				
				} else { // Arrived!
					setSpeeds(0, 0);
					isNavigating = false;
					state = State.INIT;
				}
				break;
			case EMERGENCY:
				if (avoidance.resolved()) {
					state = State.TURNING;
				}
				break;
			case SEARCHING:
				if(search.isFound){
					state= State.TURNING;
				}
				break;
			}
		}
	}

	/**
	 * Method to get distance from the UltrasonicPoller class 
	 * @return (boolean) True: distance less than 10 cm. False : distance more than 9 cm.
	 */
	private boolean checkEmergency() {
		return usSensor_front.getDistance() < 23;
	}
	
	/**
	 * The Method turn the robot the desired angle on the gride, it is absolute turning, also determines the minimum angle to trurn
	 * @param angle : desired angle of the roboton the grid
	 */
	public void turnTo(double angle){
		double rotate_angle;
		boolean turningc;
		double leftangle, rightangle;
		if((360+angle-odo.getAng())%(360)<180){
			rotate_angle=(360+angle-odo.getAng())%(360);
			turningc=true;
		}
		else{
			rotate_angle=(360-angle+odo.getAng())%(360);
			turningc=false;
		}
		
		leftangle = convertAngle(Main.WHEEL_RADIUS, Main.TRACK,rotate_angle);
		rightangle=leftangle;
		
		if(turningc){
			leftangle*=-1;
			
		}
		else{
			rightangle*=-1;
		}

		this.leftMotor.setAcceleration(6000);
		this.rightMotor.setAcceleration(6000);
		leftMotor.setSpeed(250);
		rightMotor.setSpeed(250);
	
		leftMotor.rotate((int) leftangle,true);
		rightMotor.rotate((int) rightangle, false);
	}

	/**
	 * Method to correct the direction of the heading while traveling to the destination
	 * 
	 */
	private void updateTravel() {
		
		// To DO correction while travelling
		this.setSpeeds(FAST, FAST);
	}

	/**
	 * Method to Go foward a set distance in cm with or without avoidance.
	 * @param distance (double) : desired distance to travel 
	 * @param avoid (boolean) : True to avoid obstacle on the way , False to not avoid obstacle 
	 */
	public void goForward(double distance, boolean avoid) {
		double x = odometer.getX()
				+ Math.cos(Math.toRadians(this.odometer.getAng())) * distance;
		double y = odometer.getY()
				+ Math.sin(Math.toRadians(this.odometer.getAng())) * distance;

		this.travelTo(x, y, avoid);
	}

	/**
	 * Method to check if the robot is navigating
	 * @return (boolean) True : robot is navigating , False : robot is not navigating  
	 */
	public boolean isTravelling() {
		return isNavigating;
	}
	
}
