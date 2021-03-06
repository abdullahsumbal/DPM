package dpmTutorial.src.dpm.tutorial;


/*
 * 
 * The Navigator class extends the functionality of the Navigation class.
 * It offers an alternative travelTo() method which uses a state machine
 * to implement obstacle avoidance.
 * 
 * The Navigator class does not override any of the methods in Navigation.
 * All methods with the same name are overloaded i.e. the Navigator version
 * takes different parameters than the Navigation version.
 * 
 * This is useful if, for instance, you want to force travel without obstacle
 * detection over small distances. One place where you might want to do this
 * is in the ObstacleAvoidance class. Another place is methods that implement 
 * specific features for future milestones such as retrieving an object.
 * 
 * 
 */

/**
 * Navigator class is responsible for turning , traveling and detecting objects to avoid. The class also extends BasicNavigator class.
 * @author DPM TEAM 01 
 *
 */
public class Navigator extends BasicNavigator {
	


	enum State {
		INIT, TURNING, TRAVELLING, EMERGENCY
	};

	State state;

	private boolean isNavigating = false;

	private double destx, desty;

	final static int SLEEP_TIME = 50;

	UltrasonicPoller usSensor;
	Odometer odo;

	/**
	 * Constructor which takes Odometer class object and UltrasonicPoller class object as parameter
	 * @param odo : Odometer class object
	 * @param usSensor : UltrasonicPoller class object
	 */
	public Navigator(Odometer odo, UltrasonicPoller usSensor) {
		super(odo);
		this.usSensor = usSensor;
		this.odo=odo;
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm
	 * Will travel to designated position, while constantly updating it's
	 * heading
	 * 
	 * When avoid=true, the nav thread will handle traveling. If you want to
	 * travel without avoidance, this is also possible. In this case,
	 * the method in the Navigation class is used.
	 * 
	 */
	
	/**
	 *Method to travel to X and Y coordinate on the field with option of avoiding or not avoiding the obstacle.
	 * @param x (double) : X coordinate of the field we want the robot to travel to.
	 * @param y (double) : Y coordinate of the field we want the robot to travel to.
	 * @param avoid (boolean) : if True then travel to destination with avoiding obstacle on the way. If False then travel to a straight to the desired destination
	 */
	public void travelTo(double x, double y, boolean avoid) {
		if (avoid) {
			destx = x;
			desty = y;
			isNavigating = true;
		} else {
			super.travelTo(x, y);
		}
	}

	
	/*
	 * Updates the h
	 */
	/**
	 * Method to correct the direction of the heading while traveling to the destination
	 * 
	 */
	private void updateTravel() {
		double minAng;

		minAng = getDestAngle(destx, desty);
		/*
		 * Use the BasicNavigator turnTo here because 
		 * minAng is going to be very small so just complete
		 * the turn.
		 */
		super.turnTo(minAng,false);
		this.setSpeeds(FAST, FAST);
	}

	/**
	 * Method to start the Thread. The Method calls other Methods and is responsible for checking the current state of the robot. 
	 */
	public void run() {
		ObstacleAvoidance avoidance = null;
		state = State.INIT;
		while (true) {
			switch (state) {
			case INIT:
				if (isNavigating) {
					state = State.TURNING;
				}
				break;
			case TURNING:
				/*
				 * Note: you could probably use the original turnTo()
				 * from BasicNavigator here without doing any damage.
				 * It's cheating the idea of "regular and periodic" a bit
				 * but if you're sure you never need to interrupt a turn there's
				 * no harm.
				 * 
				 * However, this implementation would be necessary if you would like
				 * to stop a turn in the middle (e.g. if you were travelling but also
				 * scanning with a sensor for something...)
				 * 
				 */
				double destAngle = getDestAngle(destx, desty);
				turnTo(destAngle);
				if(facingDest(destAngle)){
					setSpeeds(0,0);
					state = State.TRAVELLING;
				}
				break;
			case TRAVELLING:
				if (checkEmergency()) { // order matters!
					state = State.EMERGENCY;
					avoidance = new ObstacleAvoidance(this,odo,usSensor);
					avoidance.start();
				} else if (!checkIfDone(destx, desty)) {
					updateTravel();
				} else { // Arrived!
					setSpeeds(0, 0);
					isNavigating = false;
					state = State.INIT;
				}
				break;
			case EMERGENCY:
				if (avoidance.resolved()) {
					state = State.TURNING;
				}
				break;
			}
			//Log.log(Log.Sender.Navigator, "state: " + state);
			try {
				Thread.sleep(SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to get distance from the UltrasonicPoller class 
	 * @return (boolean) True: distance less than 10 cm. False : distance more than 9 cm.
	 */
	private boolean checkEmergency() {
		return usSensor.getDistance() < 10;
	}

	/**
	 * Method to turn the robot in the desired destination direction 
	 * @param angle : angle we want to travel the robot. 
	 */
	private void turnTo(double angle) {
		double error;
		error = angle - this.odometer.getAng();

		if (error < -180.0) {
			this.setSpeeds(-SLOW, SLOW);
		} else if (error < 0.0) {
			this.setSpeeds(SLOW, -SLOW);
		} else if (error > 180.0) {
			this.setSpeeds(SLOW, -SLOW);
		} else {
			this.setSpeeds(-SLOW, SLOW);
		}

	}

	/*
	 * Go foward a set distance in cm with or without avoidance
	 */
	/**
	 * Method to Go foward a set distance in cm with or without avoidance.
	 * @param distance (double) : desired distance to travel 
	 * @param avoid (boolean) : True to avoid obstacle on the way , False to not avoid obstacle 
	 */
	public void goForward(double distance, boolean avoid) {
		double x = odometer.getX()
				+ Math.cos(Math.toRadians(this.odometer.getAng())) * distance;
		double y = odometer.getY()
				+ Math.sin(Math.toRadians(this.odometer.getAng())) * distance;

		this.travelTo(x, y, avoid);

	}

	/**
	 * Method to check if the robot is navigating
	 * @return (boolean) True : robot is navigating , False : robot is not navigating  
	 */
	public boolean isTravelling() {
		return isNavigating;
	}

}
