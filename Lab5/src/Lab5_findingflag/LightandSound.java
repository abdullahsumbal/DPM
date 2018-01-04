package Lab5_findingflag;

import lejos.robotics.SampleProvider;
import lejos.utility.TimerListener;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LightandSound  implements TimerListener {
	private SampleProvider usSensor;
	public static final int LCD_REFRESH = 100;
	private TextLCD LCD = LocalEV3.get().getTextLCD();
	private float[] usData;
	private final int FILTER_OUT = 20;
	private int filterControl;
	private SampleProvider colorSensor;
	private float[] colorData;
	private int LSvalue;
	private Odometer odo;
	private Timer lcdTimer;
	
	
	
	
	//Constructor
	public LightandSound(SampleProvider usSensor, float[] usData, SampleProvider colorSensor, float[] colorData,Odometer odo){
		this.usSensor = usSensor;
		this.usData = usData;
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
	}
	
	public void doLightandSound(){
		
		LCD.clear();
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		lcdTimer.start();
		
	}
	
	
	
	// getitng light sensor value
	public int lightreading() {
		colorSensor.fetchSample(colorData, 0); // Read latest sample in buffer
		LSvalue = (int) ((colorData[0])); // converting normalized value										
		return LSvalue;
	}
	// get distance method
	private int getFilteredData() {
		usSensor.fetchSample(usData, 0);
		int distance =(int) (100*usData[0]);

		
		if(distance>85){
			distance=85;
			}
		return distance;
	}

	@Override
	public void timedOut() {
		// TODO Auto-generated method stub
		LCD.clear();
		LCD.drawInt(getFilteredData(), 1,1 );
		LCD.drawString("object not detected", 0, 2);
		
		// if condition to check the objection color, eg 2 is for 2 beeps.
		if(getFilteredData()<10 )
		{
			LCD.clear();
			LCD.drawString("Object Detected", 0, 2);
			if(lightreading()==2){
				LCD.drawString("Block   ", 2, 3);
				
			}
			else{
				LCD.drawString("Not Block  ", 2, 3);
				
			}
		}
	}
		
		
}


