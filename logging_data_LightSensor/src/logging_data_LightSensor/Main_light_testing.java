package logging_data_LightSensor;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;


import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Main_light_testing {
	static PrintStream writer = System.out;
	private static final Port colorPort = LocalEV3.get().getPort("S2");
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	
	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException,UnsupportedEncodingException{
		int button_select;
		PrintStream writer = null,writer2=null;
		writer = new PrintStream("44fl.csv","UTF-8");
		writer2= new PrintStream("44wfl.csv","UTF-8");
		int current=0;
		int last=0;
		boolean start =true;
		
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		final SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		//final float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
		
		button_select = Button.waitForAnyPress();
		while(button_select != Button.ID_LEFT );
			if(button_select == Button.ID_LEFT)
			{
			
			LightSensor lightsensor = new LightSensor(colorValue);	
			lightsensor.start();
			last=lightsensor.lightreading();
			Thread.sleep(1000);
			try {
				leftMotor.setSpeed(100);
				rightMotor.setSpeed(100);
				leftMotor.forward();
				rightMotor.forward();
				
				
				
				while(start){
					
					current=lightsensor.lightreading();
					int diff = last-current;
					System.out.print(String.format("%d:%d%n",System.currentTimeMillis(),current));
					writer2.println(current);
					
					//System.out.print(String.format("%d:%d%n",System.currentTimeMillis(),diff));
					writer.println(diff);
					
					
					last=current;
					Thread.sleep(100);
					button_select = Button.getButtons();
					if(button_select == Button.ID_RIGHT){
						start=false;
					}
				}
				
				leftMotor.stop();
				rightMotor.stop();				
			} finally  {
				// TODO: handle exception
				writer.close();
				writer.close();
				writer2.close();
				writer2.close();
			}
				
			}
			// Stop the program.
			while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);	
	}
	
}
