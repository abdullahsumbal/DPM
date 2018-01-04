package wallFollower;
import lejos.hardware.motor.*;
import lejos.hardware.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorLow, motorHigh;
	private int distance;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter-10;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		Sound.beep();
		
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance; int x=0; 
	///*	
	  // TODO: process a movement based on the us distance passed in (BANG-BANG style)
		//boolean x=true;
		
		if(Math.abs(distance-bandCenter)<=(bandwidth-2) ) // IF distance is +/- 3 from the bandCenter, motor will go forward at same speed.
		{									 // It makes the robot movement smoother						
			leftMotor.setSpeed(motorHigh);				
			rightMotor.setSpeed(motorHigh);
			leftMotor.forward();
			rightMotor.forward();
		}
		else if(distance-bandCenter<0 ) // Condition to check if the robot to close to the wall 
		{	
			
			if(distance<14)				//condition: if very close to the wall, then rotate to right side
			{
				leftMotor.setSpeed(motorHigh);				
				rightMotor.setSpeed(motorHigh);
				leftMotor.forward();
				rightMotor.backward();
			}
			
			else if(distance >=14 && distance <23) // The condition tells the robot to turn gradually to turn right
			{
				leftMotor.setSpeed(motorHigh);				
				rightMotor.setSpeed(motorLow);
				leftMotor.forward();
				rightMotor.forward();
			}

		}
		
		else if(distance-bandCenter>0) // Condition to check if the robot is too far from the wall
		{	
			
			if(distance>28 && distance <50) // if robot is a little more than the Bandcenter , the robot gradually turns left
			{
				leftMotor.setSpeed(motorLow);				
				rightMotor.setSpeed(motorHigh);                                                                                                               
				leftMotor.forward();
				rightMotor.forward();
			}
			else if(distance>=50 )   // if too far from the wall , makes a sharp turn to the left .
			{
				leftMotor.setSpeed(motorHigh);				
				rightMotor.setSpeed(motorHigh*2);                                                                                                               
				leftMotor.forward();
				rightMotor.forward();
			}
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
