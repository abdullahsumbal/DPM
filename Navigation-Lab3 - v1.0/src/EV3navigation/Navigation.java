package EV3navigation;

import lejos.hardware.Sound;
/*

 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread {
	final static int FAST = 100, SLOW = 40, ACCELERATION = 4000;
	final static double DEG_ERR = 2.0, CM_ERR = 1.0;
	private static  Odometer odometer;
	public EV3LargeRegulatedMotor leftMotor, rightMotor ;
	final double WHEEL_RADIUS = 2.1, WIDTH = 15.4;
	static EV3LargeRegulatedMotor upperMotor;
	private int[] points;
	// variables for calculating the y intercept of the line
	public static double X_destination, X_previous=0;
	public static double Y_destination,Y_previous =0;
	public static double gradient;
	public static double Y_intercept;
	public static boolean right = false, left=false,up=false,down= false;
	

	public Navigation(Odometer odo,int[] points) {
		this.odometer = odo;
		this.points=points;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}
	public void run(){
		// The points (already given to us) of array used in the run method. every two values are x and y.
		for (int i=0;i<4;i=i+2){
			
			// assigning variable
			X_destination=points[i]; Y_destination=points[i+1];
			
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
			
			
			travelTo(points[i],points[i+1]);
			
			// updating the new points
			X_previous=X_destination;
			Y_previous=Y_destination;
			
			// falsing every direction
			up=false;
			down=false;
			right=false;
			left=false;
		}
	}
	/*
	 * Functions to set the motor speeds jointly
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

	public void setSpeeds(int lSpd, int rSpd) {
		//Sound.beep();
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
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		this.setSpeeds(0, 0);
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	
	
	public void turn360(){
		
		
		
	}
	public void turnTo(double angle, boolean stop) {
		
		double error = angle - this.odometer.getAng();

		while (Math.abs(error) > DEG_ERR) {

			error = angle - this.odometer.getAng();

			if (error < -180.0) {
				this.setSpeeds(-SLOW, SLOW);
			} else if (error <= 0.0) {
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
	
	
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);

	}
}