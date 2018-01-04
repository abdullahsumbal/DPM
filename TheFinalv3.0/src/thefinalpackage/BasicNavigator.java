package thefinalpackage;


import lejos.hardware.motor.EV3LargeRegulatedMotor;
/**
 * This class has all the methods for navigating the robot on the field. 
 * @author DPM TEAM 01 
 *
 */
public class BasicNavigator extends Thread{
	
	// class variables
	final static int FAST = 250, SLOW = 250, ACCELERATION = 4000;
	final static double DEG_ERR = 1.0, CM_ERR = 2.0;
	Odometer odometer;
	EV3LargeRegulatedMotor leftMotor, rightMotor,upperMotor;
	
	/**
	 * empty constructor for extending
	 */
	public BasicNavigator(){	
	}
	
	/**
	 * Constructor which takes in the odometer class object as parameter
	 * @param odo : Odometer class object
	 */
	public BasicNavigator(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.upperMotor = motors[2];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/**
	 * Function to set the motor speeds jointly by calling the forward and backward method.
	 * Positive speed means move forward and negative speed means move backward
	 * @param lSpd : speed for the left motor. data type : float
	 * @param rSpd : speed for the right motor	data type : float
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}
	
	/**
	 * Function to set the motor speeds jointly by calling the forward and backward method.
	 * Positive speed means move forward and negative speed means move backward
	 * @param lSpd : speed for the left motor. data type : integer
	 * @param rSpd : speed for the right motor	data type : integer
	 */
	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * Method to stop the both drive motor
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/**
	 * Method to travel straight to a point on the field using x coordinate and y coordinate as parameters
	 * @param x (double): X coordinate of the field
	 * @param y (double): Y coordinate of the field
	 */
	public void travelTo(double x, double y) {
		double minAng;
		boolean first_time= true;
		while (!checkIfDone(x,y)) {
			minAng = getDestAngle(x,y);
			if(first_time){
			this.turnTo(minAng, false);
			first_time=false;
			}
			this.setSpeeds(FAST, FAST);
		}
		first_time=true;
		this.setSpeeds(0, 0);
	}

	/**
	 * Method to check if the robot has reached its destination by comparing the odometer coordinate with the parameter coordinates 
	 * @param x (double): X coordinate of the field
	 * @param y (double): Y coordinate of the field
	 * @return	True or False : depending if the robot has reached its destination
	 */
	protected boolean checkIfDone(double x, double y) {
		return Math.abs(x - odometer.getX()) < CM_ERR
				&& Math.abs(y - odometer.getY()) < CM_ERR;
	}
	
	/**
	 * Method to confirm is the robot is facing towards the direction in which it has to travel by comparing the odometer angle with the parameter angle
	 * @param angle (double): the desired angle we want.
	 * @return True or False (boolean): depending on the direction of the robot.
	 */
	protected boolean facingDest(double angle) {
		return Math.abs(angle - odometer.getAng()) < DEG_ERR;
	}

	/**
	 * Method to get the angle when it has reached its destination. It calculates the angle by using the X and Y coordinates of the parameter
	 * @param x (double): X coordinate of the field
	 * @param y (double): Y coordinate of the field
	 * @return minAng : it is the calculated angle of the robot.
	 */
	protected double getDestAngle(double x, double y) {
		double minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX()))
				* (180.0 / Math.PI);
		if (minAng < 0) {
			minAng += 360.0;
		}
		return minAng;
	}
	
	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 * @param angle (integer) : Angle in degree robot want to turn.
	 * @param stop (boolean)  : if true , sets the speed of the motors to 0
	 */
	public void turnTo(double angle, boolean stop){
		double rotate_angle;
		boolean turningc;
		double leftangle, rightangle;
		if((360+angle-odometer.getAng())%(360)<180){
			rotate_angle=(360+angle-odometer.getAng())%(360);
			turningc=true;
		}
		else{
			rotate_angle=(360-angle+odometer.getAng())%(360);
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
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);	
		leftMotor.rotate((int) leftangle,true);
		rightMotor.rotate((int) rightangle, false);
		
		if (stop) {
			this.setSpeeds(0, 0);
		}
	}

	/**
	 * Method to go Forward in by a certain distance 
	 * @param distance (double) : distance the robot travels in cm.
	 */
	public void goForward(double distance) {
		 this.travelTo(odometer.getX() + Math.cos(Math.toRadians(this.odometer.getAng())) * distance,
		 odometer.getY() + Math.sin(Math.toRadians(this.odometer.getAng())) * distance);

		}
	
	/**
	 * Description : The method finds the distance we want to travel which a certian wheel radius
	 * @param radius of the wheel
	 * @param distance : desired distance to travel
	 * @return
	 */
	public static int convertDistance(double radius, double distance) {
			return (int) ((180.0 * distance) / (Math.PI * radius));
		}
	
	/**
	 *The method finds the angle the wheel should rotate for the robot to rotate to a certian angle
	 * @param radius of the wheel
	 * @param width : Wheel base
	 * @param angle : The angle robot should robot
	 * @return
	 */
	public static int convertAngle(double radius, double width, double angle) {
			return convertDistance(radius, Math.PI * width * angle / 360.0);
		}

}

