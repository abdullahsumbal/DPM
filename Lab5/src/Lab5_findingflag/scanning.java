package Lab5_findingflag;

import Lab5_findingflag.USLocalizer.LocalizationType;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class scanning extends Thread implements TimerListener{
	private int count=0;
	private int Rotationspeed=50;
	public static int ROTATION_SPEED = 100;
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
	public static final int LCD_REFRESH = 100;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	private Timer lcdTimer;
	boolean turning = true;
	public double objectdistance;
	public double	a=110,b=0, angle1,angle2;
	private SampleProvider colorSensor;
	private float[] colorData;
	private int LSvalue;
	private static EV3LargeRegulatedMotor upperMotor;
		
	// constructor
	public scanning(Odometer odo, EV3LargeRegulatedMotor upperMotor, SampleProvider usSensor, float[] usData, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.upperMotor= upperMotor;
	}
	

	public void scanningatorigin(){
		
		//Initializing some variables and objects.
		
		Navigation navigation = new Navigation(odo);
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		
		//For Display
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		lcdTimer.start();
	
		// calling the method for the rotating the robot to detect the object
		detectobject(navigation);
		
		// calling the method to do towards the detected object
		goANDfind(navigation);
		
		// makes the robot move slowly when the object is in 4 cm range
		navigation.setSpeeds(20, 20);
		while(getFilteredData()>3){}
		navigation.setFloat();
		navigation.leftMotor.setSpeed(100);
		navigation.rightMotor.setSpeed(100);
		navigation.rightMotor.rotate(+(int) 55,true);
		navigation.leftMotor.rotate(+(int) 55, false);
		
		// is the light sonsor detects an objec
		if(checkcolor()){
			
			// trun in the opposite direction
			navigation.leftMotor.setSpeed(100);
			navigation.rightMotor.setSpeed(100);
			navigation.rightMotor.rotate(-(int) 300,true);
			navigation.leftMotor.rotate(-(int) 300, false);
			
			navigation.turnTo(odo.getAng()+180, true);
			
			// move a bit further away to make some space
			navigation.leftMotor.setSpeed(100);
			navigation.rightMotor.setSpeed(100);
			navigation.rightMotor.rotate(+(int) 100,true);
			navigation.leftMotor.rotate(+(int) 100, false);
			
			// capture the blue object 
			upperMotor.rotate(-100);
			navigation.travelTo(60,60);
			
		}
		// if not blue blue block find the other oject
		else{
			
			
			// asignment requirment
			
			// to go back
			navigation.leftMotor.setSpeed(100);
			navigation.rightMotor.setSpeed(100);
			navigation.rightMotor.rotate(-(int) 200,true);
			navigation.leftMotor.rotate(-(int) 200, false);
			
			// go to 0 , 30 point
			navigation.travelTo(0, 30);
			navigation.turnTo(30, true);
			detectobject(navigation);
			goANDfind(navigation);
			
			// travel to the object.
			navigation.setSpeeds(20, 20);
			while(getFilteredData()>3){}
			navigation.setFloat();
			
			// move closer to the object in slow speed.
			navigation.leftMotor.setSpeed(100);
			navigation.rightMotor.setSpeed(100);
			navigation.rightMotor.rotate(+(int) 55,true);
			navigation.leftMotor.rotate(+(int) 55, false);
			
			// is the light sonsor detects an objec
			if(checkcolor()){
				
				// trun in the opposite direction
				navigation.leftMotor.setSpeed(100);
				navigation.rightMotor.setSpeed(100);
				navigation.rightMotor.rotate(-(int) 300,true);
				navigation.leftMotor.rotate(-(int) 300, false);
				
				navigation.turnTo(odo.getAng()+180, true);
				
				// move a bit further away to make some space
				navigation.leftMotor.setSpeed(100);
				navigation.rightMotor.setSpeed(100);
				navigation.rightMotor.rotate(+(int) 100,true);
				navigation.leftMotor.rotate(+(int) 100, false);
				upperMotor.rotate(-100);
				navigation.travelTo(60,60);
				// capture code comes here
		}	
		}
		
		navigation.turnTo(odo.getAng()+180,true);
					
	}// scanning at origin method ends
	
	private int getFilteredData() {
		usSensor.fetchSample(usData, 0);
		int distance =(int) (100*usData[0]);
		
		if(distance>100){
			distance=100;
			}
		return distance;
	}
	
	public int lightreading() {
		colorSensor.fetchSample(colorData, 0); // Read latest sample in buffer
		LSvalue = (int) ((colorData[0])); // converting normalized value										
		return LSvalue;
	}
	
	private void detectobject(Navigation navigation){
		navigation.leftMotor.setSpeed(100);
		navigation.rightMotor.setSpeed(100);
		navigation.rightMotor.rotate((int) 100,true);
		navigation.leftMotor.rotate(+(int) 100, false);
		
			// turn from counter clockwise
			navigation.setSpeeds(-ROTATION_SPEED+50,ROTATION_SPEED-50);
			
			// turn until it sees a distance of less than 80
	        while(getFilteredData()>80){ }
			
	        navigation.setSpeeds(0, 0);

		// store the vale of the object distance
				objectdistance=getFilteredData();
	}
	// method to go towards the object and perform correction
	private void goANDfind(Navigation navigation){
		
		navigation.setSpeeds(ROTATION_SPEED,ROTATION_SPEED);
		while(getFilteredData()>7){
			
			if(getFilteredData()>90 )
				{
				//Recursion
				detectobject(navigation);
				goANDfind(navigation);
				
			}
		}
		// stop the robot
		navigation.setSpeeds(0,0);
	}
	
	private boolean checkcolor(){
		if(lightreading()==2){
			Sound.beep();
			return true;
		}
		else{
			Sound.beep();
			Sound.beep();
			return false;
		}
		
	}

	@Override
	public void timedOut() {
		LCD.clear();
		LCD.drawString("distance", 0, 2);
		LCD.drawInt((int)getFilteredData(), 3,3 );
		}
		
	
	

}
