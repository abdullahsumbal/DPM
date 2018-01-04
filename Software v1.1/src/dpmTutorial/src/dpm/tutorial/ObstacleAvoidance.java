package dpmTutorial.src.dpm.tutorial;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class ObstacleAvoidance extends Thread {

	Navigator nav;
	boolean safe;
	Odometer odometer;
	EV3LargeRegulatedMotor leftMotor, rightMotor,upperMotor;
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	UltrasonicPoller usSensor;
	public boolean do_wallfollowing=false;
	private final int bandCenter =20, bandwidth=2;
	private int angle=90;
	private boolean start=true;
	
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
	
	
	public void run(){
		
		while(start){
		
			if(Math.abs(usSensor.getDistance()-bandCenter)<=(bandwidth) ) // IF usSensor.getDistance() is +/- 3 from the bandCenter, motor will go forward at same speed.
			{		
				
// It makes the robot movement smoother						
				leftMotor.setSpeed(150);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
		
			}
			else if(usSensor.getDistance()-bandCenter<0 ) {// Condition to check if the robot to close to the wall 
							//condition: if very close to the wall, then rotate to right side
				
				if(usSensor.getDistance() <18)	
				{
					leftMotor.setSpeed(150);				
					rightMotor.setSpeed(150);
					leftMotor.forward();
					rightMotor.backward();
				}
				
				if(usSensor.getDistance() <15) // if the usSensor.getDistance() is less than 15 it will turn the upper motor with sensor towards the wall.
				{
				upperMotor.setSpeed(100);
				upperMotor.rotateTo((int) (angle*0.90));
				}
			}
			
			else if(usSensor.getDistance()-bandCenter>0){ // Condition to check if the robot is too far from the wall
				
				
				if(usSensor.getDistance()>28 && usSensor.getDistance() <50){ // if robot is a little more than the Bandcenter , the robot gradually turns left
				
					leftMotor.setSpeed(50);				
					rightMotor.setSpeed(100);                                                                                                               
					leftMotor.forward();
					rightMotor.forward();
					upperMotor.setSpeed(100);
					upperMotor.rotateTo((int) (angle*0.80));
				}
				else if(usSensor.getDistance()>=50 ) {  // if too far from the wall , makes a sharp turn to the left .
				
					leftMotor.setSpeed(75);				
					rightMotor.setSpeed(150);                                                                                                               
					leftMotor.forward();
					rightMotor.forward();
					upperMotor.setSpeed(100);
					upperMotor.rotateTo((int) (angle*0.80));
				}
			}
			
			// to stop the wall following we are ganna make stop equal to false. it will stop the while loop
			if(Main.left){
				if(odometer.getY()-Main.gradient*odometer.getX()<Main.Y_intercept-1){
					Sound.beep();
					safe = true;
					start=false;	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			
			else if(Main.right){
				if(odometer.getY()-Main.gradient*odometer.getX()>Main.Y_intercept+1){
					Sound.beep();
					safe = true;
					start=false;	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			else if(Main.up){
				if(odometer.getX()<Main.X_previous-1){
					Sound.beep();
					safe = true;
					start=false;		
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			else if(Main.down){
				if(odometer.getX()>Main.X_previous+1){
					Sound.beep();
					safe = true;
					start=false;	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			
		}
		
		/////////////////////////////
		/*
		 * The "avoidance" just stops and turns to heading 0
		 * to make sure that the threads are working properly.
		 * 
		 * If you want to call travelTo from this class you
		 * MUST call travelTo(x,y,false) to go around the
		 * state machine
		 * 
		 * This means that you can't detect a new obstacle
		 * while avoiding the first one. That's probably not something
		 * you were going to do anyway.
		 * 
		 * Otherwise things get complicated and a lot of 
		 * new states will be necessary.
		 * 
		 */
		//UltrasonicPoller usSensor = new UltrasonicPoller(usSensor);
//		Log.log(Log.Sender.avoidance,"avoiding obstacle!");
//		nav.setSpeeds(0, 0);
//		nav.turnTo(0,true);
//		nav.goForward(5, false); //using false means the Navigation method is used
//		Log.log(Log.Sender.avoidance,"obstacle avoided!");
//		safe = true;
	}


	public boolean resolved() {
		return safe;
	}
}
