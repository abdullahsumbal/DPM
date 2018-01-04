package thefinalpackage;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
/**
 * The USLocalizer Class is responsible for the localizing the robot using the Ultrasonic class
 * @author DPM TEAM 01 
 *
 */
public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	
	public static int ROTATION_SPEED = 250;
	private Odometer odo;
	UltrasonicPoller usSensor;
	private float[] usData;
	private LocalizationType locType;
	private double X_coordinate=0;
	private double Y_coordinate=0;
	private double Wheel_Radius = 2.1;
	private double Wheel_Base= 15.9;
	private final int FILTER_OUT = 20;
	private int filterControl;
	
	/**
	 * Constructor: Create an instance of a class by assigning the following variables
	 * @param odo : Odometer Class
	 * @param usSensor : UltraonicPoller Class
	 * @param locType: Localization type (LocalizationType.FALLING_EDGE)
	 */
	public USLocalizer(Odometer odo,  UltrasonicPoller usSensor, LocalizationType locType) {
		
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		//this.navigation=navigation;
	}
	/**
	 * doLocalization Method which handles the Ultrasonic Sensor Localization
	 */
	public void doLocalization() {
		
		// method variables and objects
		int button_select;
		double [] pos = new double [3];
		double angleA=0, angleB=0;
		BasicNavigator navigation = new BasicNavigator(odo);
		
	
		if (locType == LocalizationType.FALLING_EDGE) {
		//start the falling edge localization.
		//try { Thread.sleep(1000); } catch (InterruptedException e) {} // delay to avoid rotating the robot immediately
			
			// rotate the robot until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			// robot rotates to the left.
			navigation.setSpeeds(-ROTATION_SPEED,ROTATION_SPEED);
			
			//when the distance between the robot and the wall is between 32 and 45, noted as position "pos"
			while(getFilteredData() < 36) {}
			try { Thread.sleep(500); } catch (InterruptedException e) {}
			while(getFilteredData() > 32) {}
			//button_select = Button.waitForAnyPress();
			Sound.beep();
			
			//get the odo of this position
			odo.getPosition(pos);
			
			//get the angle
			angleA = pos[2];
			
			// switch direction and wait until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			// Set the thread to sleep for 2s. 
			navigation.setSpeeds(0,0);
			try { Thread.sleep(50); } catch (InterruptedException e) {}// delay to avoid turning the robot immediately
			

			// robot rotates to the right. 
			navigation.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
			try { Thread.sleep(50); } catch (InterruptedException e) {} // delay to avoid detecting the wall immediately after turning
			
			//int distance = getFilteredData();
			//while(distance<40){distance=getFilteredData();}
			while(getFilteredData() < 36) {}
			try { Thread.sleep(1000); } catch (InterruptedException e) {}
			while(getFilteredData() > 32) {}
			Sound.playTone(1200,150);
			
			// get the odo postion when the distance between the robot and the wall is betwee 32 and 36.
			odo.getPosition(pos);

			// get the angle from the robot to the wall.  
			angleB = pos[2];
		
			//navigation.setSpeeds(0,0);

			//try { Thread.sleep(200); } catch (InterruptedException e) {} // delay to avoid turning the robot immediately
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'	
		
			//do calculation of the turnToAngle

			if(angleA < angleB) {
				//Sound.twoBeeps();
				//button_select = Button.waitForAnyPress();
				//navigation.turnTo( 135+((angleA + angleB)/2),true); 
				
				odo.setPosition(new double [] {0.0, 0.0, 360-(( 135+((angleA + angleB)/2))-odo.getAng())}, new boolean [] {true, true, true});
			}
			
			else {
				//Sound.twoBeeps();
				//navigation.turnTo(  -50+ (((angleA + angleB)/2)),true);
				odo.setPosition(new double [] {0.0, 0.0,360-((  -50+ (((angleA + angleB)/2))-odo.getAng()))}, new boolean [] {true, true, true});				
			}	
			
			
			// end of first part of the lab 4, Press any key to continue
			//button_select = Button.waitForAnyPress();
			
			//try { Thread.sleep(1000); } catch (InterruptedException e) {}
			
			// The robot rotate 90 in the clockwise direction to calculate a rough Y coordinate of the robot.
//			navigation.leftMotor.setSpeed(ROTATION_SPEED);
//			navigation.rightMotor.setSpeed(ROTATION_SPEED);
//			navigation.leftMotor.rotate((int) (90*(Wheel_Base/2)/Wheel_Radius),true);
//			navigation.rightMotor.rotate((int) (-90*(Wheel_Base/2)/Wheel_Radius), false);
			//try { Thread.sleep(200); } catch (InterruptedException e) {}
			navigation.turnTo(270,true);
			Y_coordinate= getFilteredData();
			
			// The robot further rotate 90 in the clockwise direction to calculate a rough X coordinate of the robot.
			navigation.leftMotor.setSpeed(200);
			navigation.rightMotor.setSpeed(200);
			navigation.turnTo(185, true);
			//try { Thread.sleep(200); } catch (InterruptedException e) {}
			X_coordinate= getFilteredData();
			
			boolean stop= true;
			int count=1;
			while(X_coordinate>28 && stop){
				if(count%2==0){
				navigation.turnTo(185, false);
				}
				navigation.turnTo(180, false);
				X_coordinate= getFilteredData();
				
				if(X_coordinate<28){
					stop=false;
				}
				count++;
			}
			
			//button_select = Button.waitForAnyPress();
			
			// set the position of the odometer to X and Y coordinate just calculated above
			// 5 is the distance from the ultrasonic sensor to the center of the robot
			// 30.2 is the length of one box in the field
			odo.setPosition(new double [] {(X_coordinate+5)-30.2, (Y_coordinate+5)-30.2, odo.getAng()}, new boolean [] {true, true, true});
	
			//try { Thread.sleep(1000); } catch (InterruptedException e) {}
			
			// travel to a point close to (0,0)
			navigation.travelTo(-1, -1);
			odo.setPosition(new double [] {0, 0, odo.getAng()}, new boolean [] {true, true, true});
			//navigation.turnTo(0, true);
		
		
	}  
//		else {
//			/*
//			 * The robot should turn until it sees the wall, then look for the
//			 * "rising edges:" the points where it no longer sees the wall.
//			 * This is very similar to the FALLING_EDGE routine, but the robot
//			 * will face toward the wall for most of it.
//			 */
//			
//			try { Thread.sleep(2000); } catch (InterruptedException e) {}
//			
//			// rotate the robot until it sees no wall			
//			// keep rotating until the robot sees a wall, then latch the angle
//			navigation.setSpeeds(-ROTATION_SPEED,ROTATION_SPEED);
//			
//			while(getFilteredData() > 36) {}
//			while(getFilteredData() < 32) {}
//			Sound.beep();
//			
//			odo.getPosition(pos);
//			angleA = pos[2];
//			
//			// switch direction and wait until it sees no wall			
//			// keep rotating until the robot sees a wall, then latch the angle
//			navigation.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
//			try { Thread.sleep(2000); } catch (InterruptedException e) {}
//			
//			int distance = getFilteredData();
//			while(distance<40){distance=getFilteredData();}
//			while(getFilteredData() > 36) {}
//			while(getFilteredData() < 32) {}
//			Sound.playTone(1200,150);
//			
//			odo.getPosition(pos);
//			angleB = pos[2];
//			
//			// angleA is clockwise from angleB, so assume the average of the
//			// angles to the right of angleB is 45 degrees past 'north'								
//			if(angleA < angleB) {
//				
//				navigation.turnTo( ((angleA + angleB)/2) + 140,true);
//				try { Thread.sleep(2000); } catch (InterruptedException e) {}
//				
//				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
//			}
//			
//			else {
//				
//				navigation.turnTo(  (((angleA + angleB)/2) - 42),true);
//				try { Thread.sleep(2000); } catch (InterruptedException e) {}
//				
//				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});				
//			}	
//			
//			// end of first part of the lab 4, Press any key to continue
//			button_select = Button.waitForAnyPress();
//			try { Thread.sleep(2000); } catch (InterruptedException e) {}
//			
//			// The robot rotate 90 in the clockwise direction to calculate a rough Y coordinate of the robot.
//			navigation.leftMotor.setSpeed(ROTATION_SPEED);
//			navigation.rightMotor.setSpeed(ROTATION_SPEED);
//
//			navigation.leftMotor.rotate((int) (90*(Wheel_Base/2)/Wheel_Radius),true);
//			navigation.rightMotor.rotate((int) (-90*(Wheel_Base/2)/Wheel_Radius), false);
//			try { Thread.sleep(2000); } catch (InterruptedException e) {}
//			Y_coordinate= getFilteredData();
//			
//			// The robot further rotate 90 in the clockwise direction to calculate a rough X coordinate of the robot.
//			navigation.leftMotor.setSpeed(ROTATION_SPEED);
//			navigation.rightMotor.setSpeed(ROTATION_SPEED);
//			navigation.leftMotor.rotate((int) (90*(Wheel_Base/2)/Wheel_Radius),true);
//			navigation.rightMotor.rotate((int) (-90*(Wheel_Base/2)/Wheel_Radius), false);
//			try { Thread.sleep(2000); } catch (InterruptedException e) {}
//			X_coordinate= getFilteredData();
//			
//			// set the position of the odometer to X and Y coordinate just calculated above
//			// 5 is the distance from the ultrasonic sensor to the center of the robot
//			// 30.2 is the length of one box in the field
//			odo.setPosition(new double [] {(X_coordinate+5)-30.2, (Y_coordinate+5)-30.2, odo.getAng()}, new boolean [] {true, true, true});
//	
//			try { Thread.sleep(2000); } catch (InterruptedException e) {}
//			
//			// travel to a point close to (0,0)
//			navigation.travelTo(-6, -6);
//			odo.setPosition(new double [] {0, 0, odo.getAng()}, new boolean [] {true, true, true});
//			navigation.turnTo(0, true);
//			}
	}
	
	/**
	 * Method to get the readings from the ultrasonic Snesor. There are two Filters applied to the ultrasonic sensor reading including filtering out the 255 and limiting the 
	 * distance to 50 
	 * @return distance(integer): ultrasonic Sensor reading (multiplied by 100)
	 */
	private int getFilteredData() {
		
		int distance = usSensor.getDistance();
		//Rudimentary filter from wall following lab
				if (distance == 255 && filterControl < FILTER_OUT) {
					// bad value, do not set the distance var, however do increment the filter value
					filterControl ++;
				} else if (distance == 255){
					// true 255, therefore set distance to 255
					distance = distance;
				} else {
					// distance went below 255, therefore reset everything.
					filterControl = 0;
					distance = distance;
				}
		
		if(distance>50){
			distance=50;
			}
		return distance;
	}
	
}
