package Avoid;


import lejos.hardware.lcd.TextLCD;

public class Display extends Thread {
	
	// initializing some class variables
	private static final long Display_Period=250;
	private TextLCD t;
	Odometer odometer;
	
	
	//constructor
	public Display(Odometer  odometer,TextLCD t){
		this.odometer=odometer;
		this.t=t;	
	}
	
	//run method(required for thread to run)
	public void run(){
		// creating method variables
		long DisplayStart, DisplayEnd;
		double[] position = new double[3];
		
		t.clear();
		while(true){
			
			// the line will show information of the movement of the robot
			DisplayStart = System.currentTimeMillis();
			t.drawString("X =         ", 0 , 0);
			t.drawString("Y =         ", 0 , 1);
			t.drawString("Angle =       ", 0 , 2);
			if(Navigation.isBusy==false){
				t.drawString("Busy=   false      ", 3, 3);
			}
			else{
				t.drawString("Busy=   true     ", 3, 3);
			}
			
			
			//get the odometer information
			odometer.getPosition(position, new boolean [] {true,true,true});
			//get the postion from the Waypoints class of the robot
			for(int i=0;i<3;i++){
				t.drawString(formattedDoubleToString(position[i],2),3,i);
			}
			
			// throttle the OdometryDisplay
			DisplayEnd = System.currentTimeMillis();
			if (DisplayEnd - DisplayStart < Display_Period) {
				try {
					Thread.sleep(Display_Period - (DisplayEnd - DisplayStart));
				} catch (InterruptedException e) {}
			
			
			
			}
		}

	}
	
	private static String formattedDoubleToString (double x,int places){
		String result = "";
		String stack = "";
		long t;
		
		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";
		
		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long)x;
			if (t < 0)
				t = -t;
			
			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;
			}
			
			result += stack;
		}
		
		// put the decimal, if needed
		if (places > 0) {
			result += ".";
		
			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = x - Math.floor(x);
				x *= 10.0;
				result += Long.toString((long)x);
			}
		}
		
		return result;
		
	}
}
