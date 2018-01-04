package thefinalpackage;


import lejos.hardware.motor.EV3LargeRegulatedMotor;
/**
 * This class has all the methods for navigating the robot on the field. 
 * @author DPM TEAM 01 
 *
 */
public class BasicNavigator extends Thread{
	
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 1.0;
	Odometer odometer;
	EV3LargeRegulatedMotor leftMotor, rightMotor;
	/**
	 * Constructor which takes in the odometer class object as parameter
	 * @param odo : Odometer class object
	 */
	public BasicNavigator(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
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

	/*
	 * Float the two motors jointly
	 */
	/**
	 * Method to stop the both drive motor
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	/**
	 * Method to travel straight to a point on the field using x coordinate and y coordinate as parameters
	 * @param x (double): X coordinate of the field
	 * @param y (double): Y coordinate of the field
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (!checkIfDone(x,y)) {
			minAng = getDestAngle(x,y);
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
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
	
	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	/**
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 * @param angle (integer) : Angle in degree robot want to turn.
	 * @param stop (boolean)  : if true , sets the speed of the motors to 0
	 */
	public void turnTo(double angle, boolean stop) {

		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error < 0.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else if (error > 180.0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			this.setSpeeds(0, 0);
		}
	}
	
	/*
	 * Go foward a set distance in cm
	 */
//	public void goForward(double distance) {
//		this.travelTo(
//				odometer.getX()
//						+ Math.cos(Math.toRadians(this.odometer.getAng()))
//						* distance,
//				odometer.getY()
//						+ Math.sin(Math.toRadians(this.odometer.getAng()))
//						* distance);
//
//	}
	/**
	 * Method to go Forward in by a certain distance 
	 * @param distance (double) : distance the robot travels in cm.
	 */
	public void goForward(double distance) {
		 this.travelTo(odometer.getX() + Math.cos(Math.toRadians(this.odometer.getAng())) * distance,
		 odometer.getY() + Math.sin(Math.toRadians(this.odometer.getAng())) * distance);

		}

}

