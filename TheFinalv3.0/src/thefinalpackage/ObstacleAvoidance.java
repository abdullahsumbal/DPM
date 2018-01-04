package thefinalpackage;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class deals with avoiding the obstacle by doing wall following
 * @author DPM TEAM 01 
 * A detailed description of the class can be found in the Software document under Software Architecturev3.0
 */
public class ObstacleAvoidance extends Thread {

	// class variables
	Navigator nav;
	boolean safe;
	Odometer odometer;
	EV3LargeRegulatedMotor leftMotor, rightMotor,upperMotor;
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	UltrasonicPoller usSensor;
	public boolean do_wallfollowing=false;
	private final int bandCenter =12, bandwidth=4;
	private boolean start=true;
	
	/**
	 * Constructor the takes in the following parameters
	 * @param nav : Navigator class object
	 * @param odo : Odometer class object
	 * @param usSensor : SamplerProvider class object from EV3Ultrasonicsenor package
	 */
	public ObstacleAvoidance(Navigator nav,Odometer odo,UltrasonicPoller usSensor){
		this.nav = nav;
		safe = false;
		this.odometer = odo;
		this.usSensor= usSensor;
		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.upperMotor = motors[2];
		
		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}
	
	/**
	 * Thread to start the thread for the ObstacleAvoidance class. This Method turns on Wall Following and knows when to stop Wall Following.
	 */
	public void doavoidance(){
		int button_select;
		
		leftMotor.setSpeed(200);
		rightMotor.setSpeed(200);
		leftMotor.rotate((int) (90*(Main.TRACK/2)/Main.WHEEL_RADIUS),true);
		rightMotor.rotate((int) (-90*(Main.TRACK/2)/Main.WHEEL_RADIUS), false);
		
		while(start){

			if(Math.abs(usSensor.getDistance()-bandCenter)<=(bandwidth) ) // IF usSensor.getDistance() is +/- 2 from the bandCenter, motor will go forward at same speed.
			{		
				// It makes the robot movement smoother						
				leftMotor.setSpeed(150);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
			}
			else if(usSensor.getDistance()-bandCenter<-bandwidth ) {// Condition to check if the robot to close to the wall 
				//condition: if very close to the wall, then rotate to right side
				leftMotor.setSpeed(250);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
			
			}
			else if(usSensor.getDistance()-bandCenter> bandwidth){ // Condition to check if the robot is too far from the wall
				
				if(usSensor.getDistance()<20){
					leftMotor.setSpeed(150);				
					rightMotor.setSpeed(200);
					leftMotor.forward();
					rightMotor.forward();
				}
				else if(usSensor.getDistance()<40){
					leftMotor.setSpeed(200);				
					rightMotor.setSpeed(300);
					leftMotor.forward();
					rightMotor.forward();
				}

				else {
					leftMotor.setSpeed(150);				
					rightMotor.setSpeed(300);
					leftMotor.forward();
					rightMotor.forward();
					
				}
			}
			// to stop the wall following we are ganna make stop equal to false. it will stop the while loop
			if(Main.left){
				if(odometer.getY()-Main.gradient*odometer.getX()<Main.Y_intercept-1){
					safe = true;
					start=false;	
				}
			}
			
			else if(Main.right){
				if(odometer.getY()-Main.gradient*odometer.getX()>Main.Y_intercept+1){
					safe = true;
					start=false;	
				}
			}
			else if(Main.up){
				if(odometer.getX()<Main.X_previous-1){
					safe = true;
					start=false;		
				}
			}
			else if(Main.down){
				if(odometer.getX()>Main.X_previous+1){
					safe = true;
					start=false;	
				}
			}
		}
	}


	/**
	 * Method to check if the Robot was avoided the obstacle.
	 * @return safe (boolean) True : has asvoided and can continue its actual path,  False: keeping avoiding.
	 */
	public boolean resolved() {
		return safe;
	}
}