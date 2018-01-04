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
	
	public Navigator navigation;
	private boolean isNavigating = false;
	private double destx, desty, searchX=-30, searchY=-30;
	final static int SLEEP_TIME = 50;
	public ColorSensor colorSensor;
	public LightSensor lightSensor;
	UltrasonicPoller usSensor_front;
	UltrasonicPoller usSensor_side;
	Odometer odo;
	boolean resolved= false;

	/**
	 * Constructor which takes Odometer class object and UltrasonicPoller class object as parameter
	 * @param odo : Odometer class object
	 * @param usSensor : UltrasonicPoller class object
	 */
	public Navigator(Odometer odo, UltrasonicPoller usSensor_front,UltrasonicPoller usSensor_side,ColorSensor colorSensor, LightSensor lightSensor,double searchX, double searchY) {
		super(odo);
		this.usSensor_front = usSensor_front;
		this.usSensor_side = usSensor_side;
		this.colorSensor= colorSensor;
		this.lightSensor= lightSensor;
		this.odo=odo;
		this.searchX=searchX;
		this.searchY=searchY;
		
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * 
	 * When avoid=true, the nav thread will handle traveling. If you want to
	 * travel without avoidance, this is also possible. In this case,
	 * the method in the Navigation class is used.
	 * 
	 */
	
	/**
	 *Method to travel to X and Y coordinate on the field with option of avoiding or not avoiding the obstacle.
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
			//super.travelTo(x, y);
		}
	}

	
	/*
	 * Updates the h
	 */
	/**
	 * Method to correct the direction of the heading while traveling to the destination
	 * 
	 */
	private void updateTravel() {
		double minAng;
		minAng = getDestAngle(destx, desty);
		
		
		this.setSpeeds(FAST, FAST);
	}

	/**
	 * Method to start the Thread. The Method calls other Methods and is responsible for checking the current state of the robot. 
	 */
	public void run() {
		ObstacleAvoidance avoidance = null;
		Search search=null;
		state = State.LOCALIZING;
		while (true) {
			switch (state) {
			case LOCALIZING:
				{
					USLocalizer usl = new USLocalizer(odo, usSensor_front, USLocalizer.LocalizationType.FALLING_EDGE);
					LightLocalizer lsl = new LightLocalizer(odo, lightSensor);
					usl.doLocalization();
					lsl.doLocalization();
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
//				else if(!checkIfDone(searchX,searchY)){
//					state = State.SEARCHING;
//					search=new Search(odo, usSensor,colorSensor,lightSensor);
//					search.doCaptureFlag();	
//				} 
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
			//Log.log(Log.Sender.Navigator, "state: " + state);
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to get distance from the UltrasonicPoller class 
	 * @return (boolean) True: distance less than 10 cm. False : distance more than 9 cm.
	 */
	private boolean checkEmergency() {
		return usSensor_front.getDistance() < 20;
	}
	
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
		
//		double rotate_angle= angle;
//		double min_rotate_angle = minimalAngle(rotate_angle);
//
		leftMotor.setSpeed(100);
		rightMotor.setSpeed(100);
//		
//		leftMotor.rotate(convertAngle(Main.WHEEL_RADIUS, Main.TRACK, min_rotate_angle), true);
//		rightMotor.rotate(-convertAngle(Main.WHEEL_RADIUS, Main.TRACK, min_rotate_angle), false);	
		leftMotor.rotate((int) leftangle,true);
		rightMotor.rotate((int) rightangle, false);
		
	}


	/*
	 * Go foward a set distance in cm with or without avoidance
	 */
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
	
	public double minimalAngle(double deltaTheta) {
		if (deltaTheta < -180 ) {
			return deltaTheta + 360;
		}
		
		else if (deltaTheta > 180) {
			return deltaTheta - 360;
		}
		
		else {
			return deltaTheta;
		}
	}

	/**
	 * Method to check if the robot is navigating
	 * @return (boolean) True : robot is navigating , False : robot is not navigating  
	 */
	public boolean isTravelling() {
		return isNavigating;
	}
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

}
