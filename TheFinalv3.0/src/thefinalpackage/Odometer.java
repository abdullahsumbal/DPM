package thefinalpackage;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Timer;
import lejos.utility.TimerListener;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Keeps track of the robot`s x,y position and its heading theta.
 * @author DPM team 01
 *
 */
public class Odometer extends Thread {

	private EV3LargeRegulatedMotor leftMotor, rightMotor,upperMotor;
	private final int TIMEOUT_PERIOD = 50;
	private double leftRadius, rightRadius, width;
	private double x, y, theta;
	private double[] oldDH, dDH;

	/**
	 * Constructor for the odometer class. Takes in 4 parameters
	 * @param leftMotor the robot`s leftMotor
	 * @param rightMotor the robot`s rightMotor
	 * @param INTERVAL the interval at which it updates the odometer (in milliseconds)
	 * @param autostart tells it if it autostarts or not
	 */
	public Odometer(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor,EV3LargeRegulatedMotor upperMotor) {

		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.upperMotor=upperMotor;

		// default values, modify for your robot
		this.rightRadius = Main.WHEEL_RADIUS;
		this.leftRadius = Main.WHEEL_RADIUS;
		this.width = Main.TRACK;

		this.x = 0.0;
		this.y = 0.0;
		this.theta = 0.0;
		this.oldDH = new double[2];
		this.dDH = new double[2];
	}

	
	/**
	 * calculates the displacement and the heading of the robot according to the TachoCount of the motors
	 * @param data an array that stores the new, calculate, displacement and heading
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI
				/ 360.0;
		data[1] = (rightTacho * rightRadius - leftTacho * leftRadius) / width;
	}

	/**
	 * Recompute the odometer values using the displacement and heading changes
	 */
	public void run() {
		while (true) {
			this.getDisplacementAndHeading(dDH);
			dDH[0] -= oldDH[0];
			dDH[1] -= oldDH[1];

			// update the position in a critical region
			synchronized (this) {
				theta += dDH[1];
				theta = fixDegAngle(theta);

				x += dDH[0] * Math.cos(Math.toRadians(theta));
				y += dDH[0] * Math.sin(Math.toRadians(theta));
			}

			oldDH[0] += dDH[0];
			oldDH[1] += dDH[1];

			try {
				Thread.sleep(TIMEOUT_PERIOD);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * returns X position of odometer
	 * @return x the x position of the odometer
	 */
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	/**
	 * returns Y position of odometer
	 * @return y the y position of odometer
	 */
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	/**
	 * returns theta value of the odometer
	 * @return theta the theta angle of the odometer
	 */
	public double getAng() {
		synchronized (this) {
			return theta;
		}
	}

	/**
	 * sets the x,y and theta values of the odometer to the values stored in the array. 
	 * @param position the x,y and theta values to be updated to
	 * @param update boolean array that tells whether or not to update the value(x, y, theta)
	 */
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/**
	 * stores the position (x,y,theta) in a double array
	 * @param position the array that stores the position : x is index 0, y is index 1, theta is index 2
	 */
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}
	
	/**
	 * returns the position (x,y,theta) in a double array
	 * @return the position in a double array : x is in index 0, y is in index 1, theta is index 2
	 */
	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}

	/**
	 * returns an array containing the left and right motors
	 * @return an array that has stored the leftMotor in index 0 and the rightMotor in index 1 and upperMotor is index 2
	 */
	public EV3LargeRegulatedMotor[] getMotors() {
		return new EV3LargeRegulatedMotor[] { this.leftMotor, this.rightMotor,this.upperMotor};
	}
	
	/**
	 * returns the leftMotor of the robot
	 * @return the leftMotor of the robot
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	
	/**
	 * returns the rightMotor of the robot
	 * @return the rightMotor of the robot
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	/**
	 * for the angle to be positive and wraps around
	 * @param angle the angle to be fixed
	 * @return
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}
	
	/**
	 * returns the smallest angle to turn to
	 * @param a the current heading of the robot
	 * @param b the heading to go to
	 * @return the smallest angle to turnTo
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
}

