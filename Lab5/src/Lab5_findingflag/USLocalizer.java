package Lab5_findingflag;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;


public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	
	private int Rotationspeed=50;
	public static int ROTATION_SPEED = 40;
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private double X_coordinate=0;
	private double Y_coordinate=0;
	private double Wheel_Radius = 2.1;
	private double Wheel_Base= 15.45;
	private final int FILTER_OUT = 20;
	private int filterControl;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}
	
	public void doLocalization() {
		
		// method variables and objects
		int button_select;
		double [] pos = new double [3];
		double angleA=0, angleB=0;
		Navigation navigation = new Navigation(odo);
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			try { Thread.sleep(2000); } catch (InterruptedException e) {} // delay to avoid rotating the robot immediately
			
			// rotate the robot until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			navigation.setSpeeds(-ROTATION_SPEED,ROTATION_SPEED);
			
			while(getFilteredData() < 30) {}
			while(getFilteredData() > 25) {}
			Sound.beep();
			
			odo.getPosition(pos);
			angleA = pos[2];
			
			// switch direction and wait until it sees no wall			
			// keep rotating until the robot sees a wall, then latch the angle
			navigation.setSpeeds(0,0);
			try { Thread.sleep(2000); } catch (InterruptedException e) {}// delay to avoid turning the robot immediately
			navigation.setSpeeds(ROTATION_SPEED,-ROTATION_SPEED);
			try { Thread.sleep(2000); } catch (InterruptedException e) {} // delay to avoid detecting the wall immediately after turning
			
			int distance = getFilteredData();
			while(distance<40){distance=getFilteredData();}
			while(getFilteredData() < 30) {}
			while(getFilteredData() > 25) {}
			Sound.playTone(1200,150);
			
			odo.getPosition(pos);
			angleB = pos[2];
			navigation.setSpeeds(0,0);
			try { Thread.sleep(2000); } catch (InterruptedException e) {} // delay to avoid turning the robot immediately
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'	
			if(angleA < angleB) {
				
				navigation.turnTo( ((angleA + angleB)/2) + 135,true); 
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
			}
			
			else {
				
				navigation.turnTo(  (((angleA + angleB)/2) - 40),true);
				odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});				
			}			
		}  	
			
	}
	
	private int getFilteredData() {
		usSensor.fetchSample(usData, 0);
		int distance =(int) (100*usData[0]);
		
		
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