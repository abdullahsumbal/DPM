package Avoid;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation extends Thread {
	// creating variable to be laster used in the class.
	private static final int Forward_Speed = 250;
	private static final int Rotate_Speed =150;
	public static  double Wheel_Base;
	public static double Wheel_Radius;
	public static double startingangle=90;
	private static EV3LargeRegulatedMotor leftMotor;
	private static EV3LargeRegulatedMotor rightMotor;
	private static EV3LargeRegulatedMotor upperMotor;
	private static double distance;
	public static double X_coordinate,Y_coordinate;
	private int[] points;
	static boolean isBusy;
	public static double ActualAngle=90;
	public static double RelativeAngle;
	public static double RotationAngle;
	static double preX=0;
	static double preY=0;
	static int count =0;
	Odometer odometer;
	static double Wall_Distance;
	private final int bandCenter=3, bandwidth=25;
	private double error;

	
	

	
	
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,double Wheel_Base, double Wheel_Radius,int[] points,Odometer odometer) {
		// assigning usefull variables from constructor to be used later. getting values from main class to this class
		this.leftMotor=leftMotor;
		this.rightMotor= rightMotor;
		this.upperMotor= upperMotor;
		this.points=points;
		this.odometer=odometer;
		this.Wheel_Base=Wheel_Base;
		this.Wheel_Radius=Wheel_Radius;
	}
	
	public void run(){
		// The points (already given to us) of array used in the run method. every two values are x and y.
		for (int i=0;i<2;i=i+2){
			travelTo(points[i],points[i+1]);
		}
		
	}
		public static void travelTo(double x, double y){
			isBusy=true; //as thread enter this method , it makes isBusy true
			X_coordinate=x; Y_coordinate=y;
			x=x-preX; y=y-preY;
			             

			
			// calculating the distance using two points.
			distance= Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
			
			// calculating the theta the robot should turn. for different cases.
			if(x>0){
				turnTo((Math.toDegrees(Math.atan((y)/(x))))); // changes
			}
			else if(x<0 && y>0){
				turnTo(180+(Math.toDegrees(Math.atan((y)/(-x)))));
			}
			else if(x<0 && y<0){
				turnTo((Math.toDegrees(Math.atan((y)/(x))))-180);
			}
			else if(y==0){
				if(x<0)
				turnTo(180);
				if(x>0)
				turnTo(0);
			}
			else if(x==0 ){
				turnTo(90);
			}
			//Move the robot in a straight after correcting the heading angle.
			leftMotor.setSpeed(Forward_Speed);
			rightMotor.setSpeed(Forward_Speed);

			leftMotor.rotate(convertDistance(Wheel_Radius, distance), true);
			rightMotor.rotate(convertDistance(Wheel_Radius, distance), false);
			
			preX =X_coordinate;
			preY =Y_coordinate;

			isBusy=false; // to make the method not busy. isNavigating ganna return flase, if called
			
		}
		
		public static void turnTo(double theta){
		
//			if(count==0) // only for the first time theta is subtracted from 90(startingangle) because the robot initial angle is 90.
//			
//			{
//				RotationAngle=(Wheel_Base*(startingangle-theta))/(2*(Wheel_Radius));
//
//				leftMotor.setSpeed(100);
//				rightMotor.setSpeed(100);
//				leftMotor.rotate((int) RotationAngle,true);
//				rightMotor.rotate(-(int) (RotationAngle), false);
//			}
//			
//			else{
				// in this condition we subtract the calculated angle from the actualangle(previous angle)
				RotationAngle=(Wheel_Base*(theta-ActualAngle))/(2*(Wheel_Radius));
				
				leftMotor.setSpeed(100);
				rightMotor.setSpeed(100);
				rightMotor.rotate((int) RotationAngle,true);
				leftMotor.rotate(-(int) (RotationAngle), false);// Sound.beep();
			//}
			
			count++;
			ActualAngle=theta;

		}
		
		public static boolean isNavigating(){
			return isBusy;
		}
		
		// methods for odometer movement
		private static int convertDistance(double radius, double distance){
			return (int)((180.0*distance)/(Math.PI*radius));		
		}
		private static int convertAngle(double radius, double width, double angle){
			return convertDistance(radius, Math.PI*width*angle/360.0);
		}
}