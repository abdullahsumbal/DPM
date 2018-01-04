package thefinalpackage;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * The USLocalizer Class is responsible for the localizing the robot using the Ultrasonic class
 * @author DPM TEAM 01 
 * A detailed description of the class can be found in the Software document under Software Architecturev3.0
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
	private double Wheel_Radius = Main.WHEEL_RADIUS;
	private double Wheel_Base= Main.TRACK;
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
		
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'	
			//do calculation of the turnToAngle

			if(angleA < angleB) {
				odo.setPosition(new double [] {0.0, 0.0, 360-(( 135+((angleA + angleB)/2))-odo.getAng())}, new boolean [] {true, true, true});
			}
			
			else {
				odo.setPosition(new double [] {0.0, 0.0,360-((  -50+ (((angleA + angleB)/2))-odo.getAng()))}, new boolean [] {true, true, true});				
			}	

			// The robot rotate 90 in the clockwise direction to calculate a rough Y coordinate of the robot.
			navigation.turnTo(270,false);
			Y_coordinate= getFilteredData();
			
			// The robot further rotate 90 in the clockwise direction to calculate a rough X coordinate of the robot.
			navigation.turnTo(185, false);
			
			X_coordinate= getFilteredData();
			
			// correction for the value of X, if greater than 28(which is not possible), the robot should rotate 5 degree 
			// clockwise and counter clockwise to until it gets the right angle
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
			
			// set the position of the odometer to X and Y coordinate just calculated above
			// 5 is the distance from the ultrasonic sensor to the center of the robot
			// 30.2 is the length of one box in the field
			odo.setPosition(new double [] {(X_coordinate+5)-30.2, (Y_coordinate+5)-30.2, odo.getAng()}, new boolean [] {true, true, true});
			
			// travel to a point close to (0,0)
			navigation.travelTo(0, 0);
			odo.setPosition(new double [] {0, 0, odo.getAng()}, new boolean [] {true, true, true});
		}  
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
