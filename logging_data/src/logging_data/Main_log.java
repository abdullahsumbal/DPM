package logging_data;


import java.io.FileNotFoundException;

import lejos.robotics.SampleProvider;
import lejos.robotics.filter.MedianFilter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;

public class Main_log{
	static PrintStream writer = System.out;
	//static Port port = LocalEV3.get().getPort("S3");
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor upperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	
	public static void main (String[] args) throws InterruptedException, FileNotFoundException,UnsupportedEncodingException{
		
		int button_select;
		int array [] = null;
		// initializing the logging
		//PrintStream writer = null;
		//writer = new PrintStream("data.csv","UTF-8");
		
		//Initializing diplay
		final TextLCD t = LocalEV3.get().getTextLCD();
//
//		// Get an instance of the Ultrasonic EV3 sensor
//					SensorModes sensor = new EV3UltrasonicSensor(port);
//
//					// get an instance of this sensor in measurement mode
//					SampleProvider distance= sensor.getMode("Distance");
//
//					// stack a filter on the sensor that gives the running average of the last 5 samples
//					SampleProvider median = new MedianFilter(distance, 5);
//
//					//UltrasonicPoller usPoller= new UltrasonicPoller(median);
//					UltrasonicPoller usPoller= new UltrasonicPoller(distance);

		
		while(true){
			
			button_select = Button.waitForAnyPress();
			do{}
			while(button_select != Button.ID_LEFT && button_select != Button.ID_RIGHT && button_select != Button.ID_UP );
			if(button_select == Button.ID_LEFT){
				upperMotor.setSpeed(300);
			upperMotor.rotate(90);
		}
			else if(button_select == Button.ID_UP){
				
				leftMotor.setSpeed(300);
				rightMotor.setSpeed(300);
				leftMotor.rotate((int) (90*(16/2)/2.1),true);
				rightMotor.rotate((int) ( 90*(16/2)/2.1),false);
				
			}
			else{
				upperMotor.setSpeed(300);
				upperMotor.rotate(-90);
			}
			
	}
//		do {
//			// clear the display
//			t.clear();
//			t.drawString("Press left to start", 0, 1);
//			t.drawString("Sumbal", 0, 3);
//		
//		// press button to left button to start  	
//		button_select = Button.waitForAnyPress();
//		}
//		while(button_select != Button.ID_LEFT);
//		if(button_select == Button.ID_LEFT)
//		{
//		
//		////usPoller.start();
//		Thread.sleep(1000);
//		
//		//upperMotor.setSpeed(20);
//		//upperMotor.backward();
//		
//		try {
//			int distance_reading;
//			/*
//			 * if you want, you can increase the number of sample, 
//			 * 
//			 
//			int times=1000;
//			
//			for(int i =1;i<times;i++){
//			*/
//			while(true){
//			distance_reading=usPoller.getDistance();
//
//				//to print the data on the screen and create a log file.
//				System.out.println(String.format("%d:",distance_reading));
//				writer.println(distance_reading);
//				
//				Thread.sleep(100);
//				
//		}
//			
//		} finally  {
//			// TODO: handle exception
//			writer.close();
//			writer.close();
//		}
		
//	}// end of if condition
//		while (Button.waitForAnyPress() != Button.ID_ESCAPE);  
//		System.exit(0);
	}
	
	
}
