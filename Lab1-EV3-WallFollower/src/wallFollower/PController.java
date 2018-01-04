package wallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 150, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;
	private double error;

	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
		
	}
	
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.
		// (n.b. this was not included in the Bang-bang controller, but easily could have).
		//
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		
		
		// TODO: process a movement based on the us distance passed in (P style)	
		if(distance >=225) // limiting the the distance measured by the US sensor for easier calculation
		{
			distance =200;
		}
		if(distance<=225) // calculating error : negative for too far and positive for too close
		{
			error= bandCenter-distance;	
		}
		
		if(Math.abs(error)<= bandwidth) // if the robot is the bandwidth(5cm) range , move forward. For smoother movement
		{
			leftMotor.setSpeed(motorStraight);					
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}
		
		else if(error>0)  // if robot too close( less than bandwidth) , each motor has its own linear function.
		{
			leftMotor.setSpeed((int)((motorStraight+error*18)));		// this linear function will speed up the left motor			
			rightMotor.setSpeed((int)((motorStraight-error*3)));		// this linear function will speed down the right motor.
			leftMotor.forward();										// hence the robot will turn away from the wall
			rightMotor.forward();
		}
		
		else if(error<0 && distance <=120) // if the robot far (greater than bandwidth)from the wall , each has its own linear function. 
		{	
			leftMotor.setSpeed((int)((motorStraight+(error/1.25))));	// this linear function will speed down the left motor.				
			rightMotor.setSpeed((int)(motorStraight-(error-75)));		// this linear function will speed up the right motor.
			leftMotor.forward();										// hence the robot will turn towards the wall
			rightMotor.forward();
		}
		
		else if(distance >120)	// this condition checks for turns. this helps the robot to perform sharp turns to left.
		{
			leftMotor.setSpeed((int)((motorStraight+(error+200))));		// this linear function will speed down the left motor a lot faster. 			
			rightMotor.setSpeed((int)(motorStraight-(error-75)));		// this linear function will speed up the right motor.
			leftMotor.forward();										// hence the robot will turn towards the wall
			rightMotor.forward();
		}
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
