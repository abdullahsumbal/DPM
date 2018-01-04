/*
 * Odometer.java
 */

package ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;
	private static int lastTachoL;
	private static int lastTachoR;
	private static int nowTachoL;
	private static int nowTachoR;
	double distL,distR,deltaD,deltaT,dX,dY; 
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;
	private static final double WHEEL_RADIUS = Lab2.WHEEL_RADIUS;
	private static final double TRACK = Lab2.TRACK;

	// lock object for mutual exclusion
	private Object lock;

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here

			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				
				 nowTachoL = leftMotor.getTachoCount();       // get tacho counts for the left Motor
				 nowTachoR = rightMotor.getTachoCount();   	//get tacho counts for the right Motor
				 distL = Math.PI*WHEEL_RADIUS*(nowTachoL-lastTachoL)/180;     // compute wheel 
				 distR = Math.PI*WHEEL_RADIUS*(nowTachoR-lastTachoR)/180;   // displacements   
				 lastTachoL=nowTachoL;           // save tacho counts for next iteration   
				 lastTachoR=nowTachoR;   
				 deltaD = 0.5*(distL+distR);       // compute vehicle displacement 
				 deltaT = (distL-distR)/TRACK;        // compute change in heading   
				 theta +=deltaT;						// update heading 
				 dX = deltaD*Math.sin(theta);			 // compute X component of displacement 
				 dY = deltaD*Math.cos(theta);			 // compute X component of displacement 
				 x=x+dX;								 // update estimates of X and Y position 
				 y=y+dY;	
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = Math.toDegrees(theta);
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}