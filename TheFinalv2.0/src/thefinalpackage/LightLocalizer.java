package thefinalpackage;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
/**
 * The LightLocalizer Class is responsible for the localizing the robot using the light sensor
 * @author DPM TEAM 01 
 *
 */
public class LightLocalizer {
	private Odometer odo;
	private LightSensor lightSensor;
	private float[] colorData;
	private double LS_TO_CENTER =17;
	private int LSvalue;
	private double angleX, angleY, lengthX, lengthY, deltaTheta;
	private int currentLSValue, count;
	private boolean rotate= true;
	private int Axis_Correction=80;
	private double Wheel_Radius = 2.1;
	private double Wheel_Base= 15.45;
	private int Rotationspeed=50;
	/**
	 * Constructor : Create an instance of a class by assigning the following variables
	 * @param odo : Odometer Class
	 * @param lightSensor : LightSensor class
	 */
	public LightLocalizer(Odometer odo, LightSensor lightSensor) {
		this.odo = odo;
		this.lightSensor = lightSensor;
		
	}
	/**
	 * doLocalization Method which handles the Light Sensor Localization
	 */
	public void doLocalization() {
		
		/*	instruction this class
		 *  drive to location listed in tutorial
		 * 	start rotating and clock all 4 gridlines
		 * 	do trig to compute (0,0) and 0 degrees
		 *  when done travel to (0,0) and turn to 0 degrees
		 */
	int button_select;
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		
		// Setting up method variables and objects.
		BasicNavigator navigation = new BasicNavigator(odo);
		double[] pos = new double[3];
		double[] gridlines = new double[5];
		
		// set up very thing to zero.
		odo.setPosition(new double [] {0, 0, 0}, new boolean [] {true, true, true});
		
		// geting theta from from the odometer class
		odo.getPosition(pos);
		count = 0;
		
		// turn the robot anticlockwise
		navigation.setSpeeds(-40, 40);
		
		while(rotate){
			currentLSValue = lightreading();
			if(currentLSValue <43){
				
				odo.getPosition(pos);
				gridlines[count]=pos[2];
				Sound.buzz();
				count++;
			}
			if(count==4 && odo.getAng()>358){
				
				rotate=false;
				navigation.setSpeeds(0,0);
			}
		}// End of While loop
		
		// continue rotating the robot for 5 more degree, this is a correction step.
		navigation.leftMotor.setSpeed(Rotationspeed);
		navigation.rightMotor.setSpeed(Rotationspeed);
		navigation.leftMotor.rotate((int) (-5*(Wheel_Base/2)/Wheel_Radius),true);
		navigation.rightMotor.rotate((int) (5*(Wheel_Base/2)/Wheel_Radius), false);
		
		try { Thread.sleep(2000); } catch (InterruptedException e) {}
		
		// calculation for angle and x coordinate and y coordinate
		// formulas were provided in tutorial
		angleX = gridlines[1] - gridlines[3];
		angleY = gridlines[0] - gridlines[2];
		
		lengthX = Math.abs(LS_TO_CENTER * Math.cos(Math.toRadians(angleY/2)));
		lengthY = Math.abs(LS_TO_CENTER * Math.cos(Math.toRadians(angleX/2)));
		
		deltaTheta = (angleY/2) - gridlines[0] + 90 + 180;
		
		// set the new position in odometer
		odo.setPosition(new double [] {-(lengthX), -(lengthY), deltaTheta-Axis_Correction}, new boolean [] {true, true, true});
		Sound.playTone(1000,150);
		
		// in the end turn and travel to (0,0)
		navigation.travelTo(0.0, 0.0);
		navigation.turnTo(0.0,true);	
		
		// set the angle to zero and axis to the o as well
		odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});
		button_select = Button.waitForAnyPress();
	}
	/**
	 * Method to get the value light sensor reading value
	 * @return LSvalue (integer) : value read by the light Sensor.
	 */
	public int lightreading() {
		
		LSvalue = lightSensor.lightreading(); // converting normalized value										
		return LSvalue;
	}

}
