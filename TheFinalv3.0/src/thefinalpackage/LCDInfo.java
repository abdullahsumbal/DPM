package thefinalpackage;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class LCDInfo implements TimerListener{
	
	// class variables
	public static final int LCD_REFRESH = 100;
	private Odometer odo;
	private Timer lcdTimer;
	private TextLCD LCD = LocalEV3.get().getTextLCD();;
	UltrasonicPoller usSensor_front;
	UltrasonicPoller usSensor_side;
	
	// arrays for displaying data
	private double [] pos;
	
	/**
	 * Constructor for the LCDInfo class and takes in the following parameters
	 * @param odo is the odometer class
	 * @param usSensor_front : UltrasonicPoller class
	 * @param usSensor_side : UltrasonicPoller class
	 */
	public LCDInfo(Odometer odo,UltrasonicPoller usSensor_front,UltrasonicPoller usSensor_side) {
		this.odo = odo;
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		this. usSensor_front =  usSensor_front;
		this.usSensor_side=usSensor_side;
		
		// initialise the arrays for displaying data
		pos = new double [3];
		
		// start the timer
		lcdTimer.start();
	}
	
	/**
	 * Method displays information on the screen including
	 * X , Y , Angle and ultrasonic sensor reading from the front and side sensor
	 */
	public void timedOut() { 
		odo.getPosition(pos);
		LCD.clear();
		LCD.drawString("X: ", 0, 0);
		LCD.drawString("Y: ", 0, 1);
		LCD.drawString("H: ", 0, 2);
		LCD.drawString("D_F: ", 0, 3);
		LCD.drawString("D_S: ", 0, 4);
		LCD.drawString(""+(pos[0] )/30.48, 3, 0);
		LCD.drawString(""+(pos[1] )/30.48, 3, 1);
		LCD.drawString(""+pos[2], 3, 2);
		LCD.drawInt(usSensor_front.getDistance(), 3, 3);
		LCD.drawInt(usSensor_side.getDistance(), 3, 4);
		LCD.drawString("S_X: ", 0, 5);
		LCD.drawString("S_Y: ",0, 6);
		LCD.drawInt((int)Main.search_X, 3, 5);
		LCD.drawInt((int)Main.search_Y, 3, 6);

	}
}
