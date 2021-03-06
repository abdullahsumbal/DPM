package thefinalpackage;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class deals with avoiding the obstacle by doing wall following
 * @author DPM TEAM 01 
 *
 */
public class ObstacleAvoidance extends Thread {

	Navigator nav;
	boolean safe;
	Odometer odometer;
	EV3LargeRegulatedMotor leftMotor, rightMotor,upperMotor;
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	UltrasonicPoller usSensor;
	public boolean do_wallfollowing=false;
	private final int bandCenter =8, bandwidth=2;
	private int angle=90;
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
		//nav.turnTo(nav.odo.getAng()-90, true);
		
		leftMotor.setSpeed(200);
		rightMotor.setSpeed(200);
		leftMotor.rotate((int) (90*(16.05/2)/2.1),true);
		rightMotor.rotate((int) (-90*(16.05/2)/2.1), false);
		
		//button_select = Button.waitForAnyPress();
		
		while(start){
			if(Math.abs(usSensor.getDistance()-bandCenter)<=(bandwidth) ) // IF usSensor.getDistance() is +/- 2 from the bandCenter, motor will go forward at same speed.
			{		
				// It makes the robot movement smoother						
				leftMotor.setSpeed(150);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
			}
			else if(usSensor.getDistance()-bandCenter<-2 ) {// Condition to check if the robot to close to the wall 
				//condition: if very close to the wall, then rotate to right side
				leftMotor.setSpeed(200);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
			
			}
			else if(usSensor.getDistance()-bandCenter>2){ // Condition to check if the robot is too far from the wall
				
				if(usSensor.getDistance()<15){
					leftMotor.setSpeed(150);				
					rightMotor.setSpeed(200);
					leftMotor.forward();
					rightMotor.forward();
				}
				else if(usSensor.getDistance()<25){
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
					//Sound.beep();
					safe = true;
					start=false;	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo(0);
				}
			}
			
			else if(Main.right){
				if(odometer.getY()-Main.gradient*odometer.getX()>Main.Y_intercept+2){
					//Sound.beep();
					safe = true;
					start=false;	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo(0);
				}
			}
			else if(Main.up){
				if(odometer.getX()<Main.X_previous-1){
					//Sound.beep();
					safe = true;
					start=false;		
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo(0);
				}
			}
			else if(Main.down){
				if(odometer.getX()>Main.X_previous+1){
					//Sound.beep();
					safe = true;
					start=false;	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo(0);
				}
			}
			
		}
		
		
//		while(start){
//		
//			if(Math.abs(usSensor.getDistance()-bandCenter)<=(bandwidth) ) // IF usSensor.getDistance() is +/- 3 from the bandCenter, motor will go forward at same speed.
//			{		
//				
//// It makes the robot movement smoother						
//				leftMotor.setSpeed(150);				
//				rightMotor.setSpeed(150);
//				leftMotor.forward();
//				rightMotor.forward();
//		
//			}
//			else if(usSensor.getDistance()-bandCenter<0 ) {// Condition to check if the robot to close to the wall 
//							//condition: if very close to the wall, then rotate to right side
//				
//				if(usSensor.getDistance() <18)	
//				{
//					leftMotor.setSpeed(150);				
//					rightMotor.setSpeed(150);
//					leftMotor.forward();
//					rightMotor.backward();
//				}
//				
//				if(usSensor.getDistance() <15) // if the usSensor.getDistance() is less than 15 it will turn the upper motor with sensor towards the wall.
//				{
////				upperMotor.setSpeed(100);
////				upperMotor.rotateTo((int) (angle*0.90));
////					leftMotor.setSpeed(200);
////					rightMotor.setSpeed(200);
////					leftMotor.rotate((int) (90*(16/2)/2.1),true);
////					rightMotor.rotate((int) (-90*(16/2)/2.1), false);
//					nav.turnTo(nav.odo.getAng()-90, true);
//				}
//			}
//			
//			else if(usSensor.getDistance()-bandCenter>0){ // Condition to check if the robot is too far from the wall
//				
//				
//				if(usSensor.getDistance()>28 && usSensor.getDistance() <50){ // if robot is a little more than the Bandcenter , the robot gradually turns left
//				
//					leftMotor.setSpeed(50);				
//					rightMotor.setSpeed(100);                                                                                                               
//					leftMotor.forward();
//					rightMotor.forward();
////					upperMotor.setSpeed(100);
////					upperMotor.rotateTo((int) (angle*0.80));
//				}
//				else if(usSensor.getDistance()>=50 ) {  // if too far from the wall , makes a sharp turn to the left .
//				
//					leftMotor.setSpeed(75);				
//					rightMotor.setSpeed(150);                                                                                                               
//					leftMotor.forward();
//					rightMotor.forward();
////					upperMotor.setSpeed(100);
////					upperMotor.rotateTo((int) (angle*0.80));
//				}
//			}
//			
//			// to stop the wall following we are ganna make stop equal to false. it will stop the while loop
//			if(Main.left){
//				if(odometer.getY()-Main.gradient*odometer.getX()<Main.Y_intercept-1){
//					Sound.beep();
//					safe = true;
//					start=false;	
////					upperMotor.setSpeed(100);
////					upperMotor.rotateTo(0);
//				}
//			}
//			
//			else if(Main.right){
//				if(odometer.getY()-Main.gradient*odometer.getX()>Main.Y_intercept+1){
//					Sound.beep();
//					safe = true;
//					start=false;	
////					upperMotor.setSpeed(100);
////					upperMotor.rotateTo(0);
//				}
//			}
//			else if(Main.up){
//				if(odometer.getX()<Main.X_previous-1){
//					Sound.beep();
//					safe = true;
//					start=false;		
////					upperMotor.setSpeed(100);
////					upperMotor.rotateTo(0);
//				}
//			}
//			else if(Main.down){
//				if(odometer.getX()>Main.X_previous+1){
//					Sound.beep();
//					safe = true;
//					start=false;	
////					upperMotor.setSpeed(100);
////					upperMotor.rotateTo(0);
//				}
//			}
//			
//		}
		
	}


	/**
	 * Method to check if the Robot was avoided the obstacle.
	 * @return safe (boolean) True : has asvoided and can continue its actual path,  False: keeping avoiding.
	 */
	public boolean resolved() {
		return safe;
	}
}