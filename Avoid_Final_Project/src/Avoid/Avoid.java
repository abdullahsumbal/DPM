package Avoid;



import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class Avoid extends Thread implements TimerListener{
	private SampleProvider usSensor;
	private float[] usData;
	private Odometer odo;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	private Timer lcdTimer;
	private final int bandCenter =20, bandwidth=2;
	private EV3LargeRegulatedMotor leftMotor, rightMotor, upperMotor;
	private int angle=90;
	private Navigation navigation;
	
	public Avoid(Odometer odo, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,EV3LargeRegulatedMotor upperMotor,SampleProvider usSensor, float[] usData,Navigation navigation){
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.upperMotor=upperMotor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.navigation = navigation;
	}
	
	public void run(){
		
		
		while(distance()>25){}
		doavoiding();
		
		
	}
	
	
	public void doavoiding(){
			while(odo.getX()-1<0){
		
		
			if(Math.abs(distance()-bandCenter)<=(bandwidth) ) // IF distance is +/- 3 from the bandCenter, motor will go forward at same speed.
			{									 // It makes the robot movement smoother						
				leftMotor.setSpeed(150);				
				rightMotor.setSpeed(150);
				leftMotor.forward();
				rightMotor.forward();
				
		
			}
			else if(distance()-bandCenter<0 ) {// Condition to check if the robot to close to the wall 
							//condition: if very close to the wall, then rotate to right side
				
				if(distance() <18)	
				{
					leftMotor.setSpeed(150);				
					rightMotor.setSpeed(150);
					leftMotor.forward();
					rightMotor.backward();
				}
				
				if(distance() <15) // if the distance is less than 15 it will turn the upper motor with sensor towards the wall.
				{
				upperMotor.setSpeed(100);
				upperMotor.rotateTo((int) (angle*0.70));
				}
					
				
	
			}
			
			else if(distance()-bandCenter>0){ // Condition to check if the robot is too far from the wall
				
				
				if(distance()>28 && distance() <50){ // if robot is a little more than the Bandcenter , the robot gradually turns left
				
					leftMotor.setSpeed(50);				
					rightMotor.setSpeed(100);                                                                                                               
					leftMotor.forward();
					rightMotor.forward();
					upperMotor.setSpeed(100);
					upperMotor.rotateTo((int) (angle*0.70));
				}
				else if(distance()>=50 ) {  // if too far from the wall , makes a sharp turn to the left .
				
					leftMotor.setSpeed(75);				
					rightMotor.setSpeed(150);                                                                                                               
					leftMotor.forward();
					rightMotor.forward();
					upperMotor.setSpeed(100);
					upperMotor.rotateTo((int) (angle*0.70));
				}
			}
			}
			
	}

	private int distance() {
		usSensor.fetchSample(usData, 0);
		int distance =(int) (100*usData[0]);
		
		if(distance>100){
			distance=100;
			}
		return distance;
	}

	@Override
	public void timedOut() {
		// TODO Auto-generated method stub
		LCD.clear();
		LCD.drawString("distance", 0, 2);
		LCD.drawInt((int)distance(), 3,3 );
		
	}
}
