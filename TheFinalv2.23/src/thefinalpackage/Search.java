package thefinalpackage;


import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
 * The following code represents basic structure of our Search algorithm.
 * @author DPM Team 01
 * 
 */

public class Search extends BasicNavigator {
	public boolean isFound=false;
	BasicNavigator navigation;
	UltrasonicPoller usSensor;
	ColorSensor colorSensor;
	LightSensor lightSensor;
	private Odometer odo;
	private BasicNavigator nav;
	
	
	/**
	 * The constructor of the Search Class. Navigation, odometer, lightSensor and ultrasonicSensor are the inputs.
	 * @param navigator the navigator
	 * @param odometer the robot's odometer
	 * @param lightSensor the light sensor used to detect blocks
	 * @param ultrasonicSensor the ultra sonic sensor used to detect objects
	 */
	public Search (Odometer odo, UltrasonicPoller usSensor, Navigator nav){
		navigation = new BasicNavigator(odo);
		this.usSensor=usSensor;
//		this.colorSensor=colorSensor;
		this.odo=odo;
		this.nav=nav;
	}
	
	/**
	 * 	doCaptureFlag is the method that does the actual search function. 
	 */
	public void doCaptureFlag(){
		double gotoPointX; //the points that are given at the beginning (where the search area is)
		double gotoPointY;
		int colorID;
		int numOfBlocks;
		boolean leftSide = false;
		boolean rightSide = false;
		int degreeForSearch = 250;
		
		nav.upperMotor.rotate(-degreeForSearch); //The claw originally locates on the highest point then by rotates -250 degree, it will be at the height propriate for searching

		gotoPointX = odo.getX(); // get the starting point of X
		gotoPointY = odo.getY(); //get the starting point of y
		
		nav.turnTo(180,false);
		nav.travelTo(gotoPointX - 15, 0);
		nav.turnTo(90, false);
		
		while(odo.getY() < gotoPointY + 90){
			odo.getLeftMotor().setSpeed(200);
			odo.getRightMotor().setSpeed(200);
			odo.getLeftMotor().forward();
			odo.getRightMotor().forward();
			if(usSensor.getDistance() < 15){
				Sound.beep();
				grabPutToLeft();
			}
		}
		
		nav.turnTo(0,false);
		nav.travelTo(gotoPointX + 30, gotoPointY + 90);
		
		nav.turnTo(270, false);
		while(odo.getY() > gotoPointY){
			odo.getLeftMotor().setSpeed(200);
			odo.getRightMotor().setSpeed(200);
			odo.getLeftMotor().forward();
			odo.getRightMotor().forward();
			if(usSensor.getDistance() < 15){
				grabPutToLeft();
			}
		}
		
	}
	

		private void grabPutToLeft() {
			nav.upperMotor.setSpeed(200);
			nav.upperMotor.rotate(-450);
			nav.upperMotor.rotate(550);
			nav.turnTo(90,false);
			nav.upperMotor.rotate(200);
		}
		

//		nav.travelTo(gotoPointX - 15.0, gotoPointY);
//		nav.turnTo(90.0, false);
//		double yDistanceL = odo.getY() + 45.0;
//		while(odo.getY() < yDistanceL) { //in order to search for obstacles on the left side of the search area
//			odo.getLeftMotor().setSpeed(100);
//			odo.getRightMotor().setSpeed(100);
//			odo.getLeftMotor().forward();
//			odo.getRightMotor().forward();
//			
//			if (usSensor.getDistance() < 15) { // if there are blocks in front of the robot
//				leftSide = true;
//				odo.getLeftMotor().stop();
//				odo.getRightMotor().stop();
//			}
//		}
//		
//		if (leftSide = true) { //we can use nav instead of turnTo + travelTo.
//			nav.travelTo(gotoPointX - 15.0, gotoPointY); //SET MOTOR SPEEDS HIGHER (DON'T WASTE TIME)
//			nav.turnTo(0,false);
//			double xDistance = odo.getX() + 90.0;
//			travelTo(xDistance,gotoPointY);
//			nav.turnTo(90,false);
//			double yDistanceR = odo.getY() + 45.0;
//			while(odo.getY() < yDistanceR) { //in order to search for obstacles on the right side of the search area
//				odo.getRightMotor().setSpeed(100);
//				odo.getLeftMotor().setSpeed(100);
//				odo.getRightMotor().forward();
//				odo.getLeftMotor().forward();
//				if (usSensor.getDistance() < 15) {
//					rightSide = true;
//					odo.getLeftMotor().stop();
//					odo.getRightMotor().stop();;
//				}
//			}
//			//GO TO POINT G AND START THE ALGORITHM FROM THERE
//		} else {
//			nav.travelTo(gotoPointX - 15.0, 0);		
//			nav.turnTo(0,false); //face right side
//			
//			nav.travelTo(gotoPointX + 30, 0);//at point F
//			nav.turnTo(90, false);
//		}
//		
//		while(odo.getY() < 75){ //at point F, while the y axis is smaller than 75 
//			odo.getRightMotor().setSpeed(200);
//			odo.getLeftMotor().setSpeed(200);
//			odo.getLeftMotor().forward();
//			odo.getRightMotor().forward();
//			if (usSensor.getDistance()<15){ //need to implement a grabber class
//			//	grabber.grabBlock();
//			//	grabber.putBlockToLeftSide(); //need to implemented, 
//				//for robot facing the postive y direction, the free space is on the leftside of the robot.
//			}else{
//				odo.getLeftMotor().forward();
//				odo.getRightMotor().forward();
//			}
//		}
//		
//		nav.turnTo(0,false);
//		nav.travelTo(odo.getX()+45, odo.getY()); // go to the point E, to test if there 
//		nav.turnTo(270,false);
//		
//		while(odo.getY() > gotoPointY){
//			odo.getLeftMotor().setSpeed(200);
//			odo.getRightMotor().setSpeed(200);
//			odo.getLeftMotor().forward();
//			odo.getRightMotor().forward();
//			
//			if(usSensor.getDistance()<15){
////				grabber.grabBlock();
////				grabber.putBlockToTheRightSide(); // for robot facing the negative y direction, the free space is on the rightSide of the robot
//			}else{
//				odo.getLeftMotor().forward();
//				odo.getRightMotor().forward();
//			}
//		}

		
//		if(colorDetection() == true) {
//
//			/**
//			 * If the block is detected, then go to the destinaiton
//			 */
//			
//		}
//		else{
//			/**
//			 * Or else the put the block on the side.
//			 */
//			putItOnTheSide();
//		}
//	}
//
//	/**
//	 * colorDetection returns a boolean, if the block is detected, then true else false. 
//	 * @return boolean whether or not it detected the right block or not
//	 */
//	public boolean colorDetection(){
//				
//		if(true){
//			return true;		
//		}else {
//			return false;
//		}
//	}
//	/**
//	 * If the block is detected, then the robot will put the block aside using putItOnTheSide method
//	 * @parem clawMotor the motor that will push/pick up the block.	
//	 */
//	public void putItOnTheSide(){
//				
//	}
//
//
//	
//	// method to to check is the object is found.
//	public boolean found(){
//		return isFound;
//	}
}
