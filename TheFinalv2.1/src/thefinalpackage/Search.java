package thefinalpackage;


import lejos.robotics.SampleProvider;

/**
 * The following code represents basic structure of our Search algorithm.
 * @author DPM Team 01
 * 
 */

public class Search {
	public boolean isFound=false;
	BasicNavigator navigation;
	UltrasonicPoller usSensor;
	ColorSensor colorSensor;
	LightSensor lightSensor;
	
	/**
	 * The constructor of the Search Class. Navigation, odometer, lightSensor and ultrasonicSensor are the inputs.
	 * @param navigator the navigator
	 * @param odometer the robot's odometer
	 * @param lightSensor the light sensor used to detect blocks
	 * @param ultrasonicSensor the ultra sonic sensor used to detect objects
	 */
	public Search (Odometer odo, UltrasonicPoller usSensor,ColorSensor colorSensor, LightSensor lightSensor){
		navigation = new BasicNavigator(odo);
		this.usSensor=usSensor;
		this.colorSensor=colorSensor;
		this.lightSensor=lightSensor;
	}
	
	/**
	 * 	doCaptureFlag is the method that does the actual search function. 
	 */
	public void doCaptureFlag(){
		if(colorDetection() == true) {

			/**
			 * If the block is detected, then go to the destinaiton
			 */
			
		}
		else{
			/**
			 * Or else the put the block on the side.
			 */
			putItOnTheSide();
		}
	}
	
	/**
	 * colorDetection returns a boolean, if the block is detected, then true else false. 
	 * @return boolean whether or not it detected the right block or not
	 */
	public boolean colorDetection(){
				
		if(true){
			return true;		
		}else {
			return false;
		}
	}
	/**
	 * If the block is detected, then the robot will put the block aside using putItOnTheSide method
	 * @parem clawMotor the motor that will push/pick up the block.	
	 */
	public void putItOnTheSide(){
				
	}


	
	// method to to check is the object is found.
	public boolean found(){
		return isFound;
	}

}
