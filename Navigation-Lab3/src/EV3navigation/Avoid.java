//package EV3navigation;
//
//
//import lejos.hardware.Sound;
//import lejos.hardware.motor.EV3LargeRegulatedMotor;
//
//
//
//
//
//
//
//public class Avoid {
//	// creating variable to be laster used in the class.
//	private final int bandCenter =20, bandwidth=2;
//	private final int motorStraight = 150, FILTER_OUT = 20;
//	private EV3LargeRegulatedMotor leftMotor, rightMotor, upperMotor;
//	private static final int motorLow = 100;			// Speed of slower rotating wheel (deg/sec)
//	private static final int motorHigh = 200;			// Speed of the faster rotating wheel (deg/seec)
//	Odometer odometer;
//	private int distance;
//	private int filterControl;
//	private double error;
//	private boolean pcontroller;
//	private static double DeltaX,DeltaY;
//	private static double X_coordinate,Y_coordinate;
//	static double preX=0;
//	static double preY=0;
//	static double P_theta;
//	static double theta;
//	private int count=0;
//	private double a=0;
//	private int angle=90;
//	Avoid avoid;
//	
//	public Pcontroller(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,EV3LargeRegulatedMotor upperMotor, Odometer odometer,Navigation avoid) {
//
//		//Default Constructor
//		this.leftMotor = leftMotor;
//		this.rightMotor = rightMotor;
//		this.odometer= odometer;
//		this.upperMotor=upperMotor;
//		filterControl = 0;
//		pcontroller = false;
//		this.avoid=avoid;
//	}
//	
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public void processUSData(int distance) {
//		pcontroller = false;
//		if(count==0){
//			// if the distance is less than 25 it is going to start the modified bang bang class.
//		if(distance<25){ 
//		
//			avoid.suspend();
//			
//			pcontroller = true;
//			preX=odometer.getX();
//			preY=odometer.getY();
//			P_theta=odometer.getAng();
//			
//		}
//			
//		if(pcontroller){
//		
//			if (distance == 255 && filterControl < FILTER_OUT) {
//				// bad value, do not set the distance var, however do increment the filter value
//				filterControl ++;
//			} else if (distance == 255){
//				// true 255, therefore set distance to 255
//				this.distance = distance;
//			} else {
//				// distance went below 255, therefore reset everything.
//				filterControl = 0;
//				this.distance = distance;
//			}
//			
//			
//			if(Math.abs(distance-bandCenter)<=(bandwidth) ) // IF distance is +/- 3 from the bandCenter, motor will go forward at same speed.
//			{									 // It makes the robot movement smoother						
//				leftMotor.setSpeed(150);				
//				rightMotor.setSpeed(150);
//				leftMotor.forward();
//				rightMotor.forward();
//		
//			}
//			else if(distance-bandCenter<0 ) {// Condition to check if the robot to close to the wall 
//							//condition: if very close to the wall, then rotate to right side
//				
//				if(distance <18)	
//				{
//					leftMotor.setSpeed(150);				
//					rightMotor.setSpeed(150);
//					leftMotor.forward();
//					rightMotor.backward();
//				}
//				
//				if(distance <15) // if the distance is less than 15 it will turn the upper motor with sensor towards the wall.
//				{
//				upperMotor.setSpeed(100);
//				upperMotor.rotateTo((int) (angle*0.90));
//				}
//					
//				
//
//			}
//			
//			else if(distance-bandCenter>0){ // Condition to check if the robot is too far from the wall
//				
//				
//				if(distance>28 && distance <50){ // if robot is a little more than the Bandcenter , the robot gradually turns left
//				
//					leftMotor.setSpeed(50);				
//					rightMotor.setSpeed(100);                                                                                                               
//					leftMotor.forward();
//					rightMotor.forward();
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo((int) (angle*0.80));
//				}
//				else if(distance>=50 ) {  // if too far from the wall , makes a sharp turn to the left .
//				
//					leftMotor.setSpeed(75);				
//					rightMotor.setSpeed(150);                                                                                                               
//					leftMotor.forward();
//					rightMotor.forward();
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo((int) (angle*0.80));
//				}
//			}
//			
//			//the robot draws an imaginary line  in direction it is heading. so it knows which side of the line the robot is. after avoid the obstacle
//			// it calculatge the gradients. and uses the y-intercept to know its place. 
//			// y=mx+c
//			//c=y-mx
//			// as the gradient at the diagonal is -1 so the following logical applies.
//			// the code can be extended for further heading directions.
//			//////////////////////////////////
//			
//			if(avoid.left){
//				if(odometer.getY()-avoid.gradient*odometer.getX()<avoid.Y_intercept-1){
//					Sound.beep();
//					pcontroller=false;
//					avoid.resume();	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo((int) (-angle*0.80));
//				}
//			}
//			
//			else if(avoid.right){
//				if(odometer.getY()-avoid.gradient*odometer.getX()>avoid.Y_intercept+1){
//					Sound.beep();
//					pcontroller=false;
//					avoid.resume();	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo((int) (-angle*0.80));
//				}
//			}
//			else if(avoid.up){
//				if(odometer.getX()<avoid.X_previous-1){
//					Sound.beep();
//					pcontroller=false;
//					avoid.resume();	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo((int) (-angle*0.80));
//				}
//			}
//			else if(avoid.down){
//				if(odometer.getX()>avoid.X_previous+1){
//					Sound.beep();
//					pcontroller=false;
//					avoid.resume();	
//					upperMotor.setSpeed(100);
//					upperMotor.rotateTo((int) (-angle*0.80));
//				}
//			}
//			
//			//////////////////////////////
//			/*
//			if(odometer.getX()<-1) // when it moving in 90 degree
//			{
//				Sound.beep();count++;
//				avoid.resume();;
//			}
//			*/
//		}	// poller ends
//				
//	} // count if ends
//		
//			
//} // classs ends
//		
//
//	@Override
//	public int readUSDistance() {
//		// TODO Auto-generated method stub
//		return this.distance;
//	}
//
//}
