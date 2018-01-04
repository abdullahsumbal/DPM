package EV3navigation;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class WallFollower extends Thread {
	UltrasonicPoller usPoller;
	private EV3LargeRegulatedMotor leftMotor, rightMotor, upperMotor;
	public boolean do_wallfollowing=false;
	private final int bandCenter =20, bandwidth=2;
	private int angle=90;
	private boolean start= true;
	Navigation navigation;
	Odometer odometer;
	private Object lock;
	
	public WallFollower(UltrasonicPoller usPoller,EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,EV3LargeRegulatedMotor upperMotor,Navigation navigation,Odometer odometer){
		this.usPoller=usPoller;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.upperMotor=upperMotor;
		this.navigation= navigation;
		this.odometer=odometer;
		lock = new Object();
	}
	
	
	@SuppressWarnings("deprecation")
	public void run(){
		
		while(true){
			
		if(usPoller.getDistance()<25){
			do_wallfollowing=true; 
		}
			  
		if(do_wallfollowing){
		navigation.suspend();
			if(Math.abs(usPoller.getDistance()-bandCenter)<=(bandwidth) ) // IF usPoller.getDistance() is +/- 3 from the bandCenter, motor will go forward at same speed.
			{		
				Sound.buzz();
// It makes the robot movement smoother						
				leftMotor.setSpeed(150);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
		
			}
			else if(usPoller.getDistance()-bandCenter<0 ) {// Condition to check if the robot to close to the wall 
							//condition: if very close to the wall, then rotate to right side
				Sound.beep();
				if(usPoller.getDistance() <18)	
				{
					leftMotor.setSpeed(150);				
					rightMotor.setSpeed(150);
					leftMotor.forward();
					rightMotor.backward();
				}
				
				if(usPoller.getDistance() <15) // if the usPoller.getDistance() is less than 15 it will turn the upper motor with sensor towards the wall.
				{
				upperMotor.setSpeed(100);
				upperMotor.rotateTo((int) (angle*0.90));
				}
			}
			
			else if(usPoller.getDistance()-bandCenter>0){ // Condition to check if the robot is too far from the wall
				
				
				if(usPoller.getDistance()>28 && usPoller.getDistance() <50){ // if robot is a little more than the Bandcenter , the robot gradually turns left
				
					leftMotor.setSpeed(50);				
					rightMotor.setSpeed(100);                                                                                                               
					leftMotor.forward();
					rightMotor.forward();
					upperMotor.setSpeed(100);
					upperMotor.rotateTo((int) (angle*0.80));
				}
				else if(usPoller.getDistance()>=50 ) {  // if too far from the wall , makes a sharp turn to the left .
				
					leftMotor.setSpeed(75);				
					rightMotor.setSpeed(150);                                                                                                               
					leftMotor.forward();
					rightMotor.forward();
					upperMotor.setSpeed(100);
					upperMotor.rotateTo((int) (angle*0.80));
				}
			}
			
			// to stop the wall following we are ganna make stop equal to false. it will stop the while loop
			if(navigation.left){
				if(odometer.getY()-navigation.gradient*odometer.getX()<navigation.Y_intercept-1){
					//Sound.beep();
					do_wallfollowing=false;
					navigation.resume();	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			
			else if(navigation.right){
				if(odometer.getY()-navigation.gradient*odometer.getX()>navigation.Y_intercept+1){
					//Sound.beep();
					do_wallfollowing=false;
					navigation.resume();	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			else if(navigation.up){
				if(odometer.getX()<navigation.X_previous-1){
					//Sound.beep();
					do_wallfollowing=false;
					navigation.resume();	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			else if(navigation.down){
				if(odometer.getX()>navigation.X_previous+1){
					//Sound.beep();
					do_wallfollowing=false;
					navigation.resume();	
					upperMotor.setSpeed(100);
					upperMotor.rotateTo(0);
				}
			}
			
		}// if condition
	
		} // always true while loop
	}//run method ends
	
	
	

}
