import java.awt.event.ActionListener;

import javax.swing.Timer;

public class Player {
	//ALL VALUES ARBITRARY RIGHT NOW
	
	//Linear movement vars
	final Vector StartPos;
	Vector pos; //The position of the player.
	final double MAX_SPEED = 8; //The maximum speed for the character.
	double currentMaxSpeed;
	double speed; //The current speed for the character.
	final double ACCELERATION = 5; //The acceleration of the character.
	
	//Rotational movement vars
	Vector direction; //The direction the player is facing.
	final double MAX_ROTATION_SPEED = 1.5; //The maximum rotational speed of the character.
	double currentMaxRotationSpeed;
	Vector unRotatedPlane;
	Vector plane; //A vector perpendicular to the direction, representing the camera plane.
	double rotationSpeedNoDrifting;
	double rotationSpeed; //The current rotational speed of the character.
	final double HANDLING = 10; //The rotational acceleration of the character.
	
	boolean isTurning = false; //True if player is turning, false otherwise

	//Drifting var
	long driftStartTime = 0, driftingTime = 0;
	boolean driftingBoost = false;
	boolean canDriftBoost = false;
	boolean isDrifting = false, isDriftingPrevious = false;
	boolean playerDriftStopped = false;
	boolean initiallyTurningRight = false;
	double turboSpeed = 2; //The speed that a boost sets you to.
	Timer driftTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			driftingBoost = false;
		};
	});

	Map map; //map used for wall collisions

	//Checkpoint and lap vars
	int currentCheckpoint;
	int lap;
	boolean win;

	//Player Collision vars
	final double playerWidth = 0.6;
	final double playerHeight = 0.6;
	final double halfPlayerWidth = playerWidth/2;
	final double halfPlayerHeight = playerHeight/2;

	//Constants
	final double[] groundMoveSpeeds = {0.1, 1.0, 0.6, 0.4}; //Wall speed, road speed, grass speed, sand speed.

	//Getter for direction
	public Vector getDirection() {
		return direction;
	}

	public int getLap() {
		return lap;
	}
	
	public void printPos() {
		System.out.printf("%.2f, %.2f\n", pos.x, pos.y);
	}

	public void printDirection() {
		System.out.printf("%.2f, %.2f\n", direction.x, direction.y);
	}

	Player(Map map){
		this.map = map;
		this.pos = new Vector(12, 12);
		this.StartPos = new Vector(12, 12);
		this.direction = new Vector(-1, 0);
		this.unRotatedPlane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));
		this.plane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));
		this.rotationSpeedNoDrifting = 0;
		this.speed = 0;
		this.currentCheckpoint = 0;
		this.lap = 1;
		this.win = false;
		driftTimer.setRepeats(false);
	}
	
	//use this one when we have more characters
	Player(Map map, String character){
		//Creates a new character using a specified character, where char is the selected character.
		//Loads all stats of the character either directly in code, or from a stats.txt file for the character.
		
		//NOTE: some variables can not be constants then!!
		//if (character.equals("Ghost")) {}
			
		this(map);
	}
	
	public synchronized void checkDrifting(boolean uDown, boolean aDown, boolean dDown) {
		isDriftingPrevious = isDrifting;
		if (uDown && (aDown || dDown) && speed > MAX_SPEED * 0.7) {
			isDrifting = true;
		} else if (!uDown){
			isDrifting = false;
			playerDriftStopped = true;
			driftingTime = System.currentTimeMillis() - driftStartTime;
		} else if (speed < MAX_SPEED * 0.7) {
			isDrifting = false;
			playerDriftStopped = false;
			driftingTime = System.currentTimeMillis() - driftStartTime;
		}

		if (isDriftingPrevious && !isDrifting && !uDown && playerDriftStopped && driftingTime > 750) {
			driftingBoost = true;
			driftTimer.restart();
		} else if (!isDriftingPrevious && isDrifting) {
			if (aDown) initiallyTurningRight = true;
			else initiallyTurningRight = false;
			driftStartTime = System.currentTimeMillis();
		}
	}
	
 //Movement
	//accelerates player
	public synchronized void acceleratePlayer(boolean wDown, boolean sDown, double frameTime){
		double currentCarFriction = getCarFriction();
		currentMaxSpeed = MAX_SPEED * currentCarFriction;

		if (driftingBoost) {
			speed = MAX_SPEED * 1.5;
			currentMaxSpeed = MAX_SPEED * 1.5;
		} else if (wDown && !sDown) {
			if (Math.abs(speed + ACCELERATION * frameTime) <= currentMaxSpeed) speed += ACCELERATION * frameTime; //limits max speed
		} else if (sDown && !wDown) {
			if (speed > 0){
				speed -= ACCELERATION * frameTime * 2;
			} else if (Math.abs(speed - ACCELERATION * frameTime * 0.5) <= currentMaxSpeed * 0.5) speed -= ACCELERATION * frameTime * 0.5;
		} else {
			speed *= Math.pow(0.3 * currentCarFriction * currentCarFriction, frameTime);
			//decayRate is % remaining after 1 second
			//speed -= (speed * 0.02) * (frameTime/Renderer.TargetFrameTime);
			if (Math.abs(speed) < 0.05){
				speed = 0;
			}
		}

		// Speed-based FOV Effects (polish for later)
		// Renderer.FOV = Renderer.StandardFOV + Math.pow(1.7, speed);
		// this.unRotatedPlane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));

		if (speed < 0 && speed < -currentMaxSpeed) {
			speed = -currentMaxSpeed;
		} else if (speed > 0 && speed > currentMaxSpeed){
			speed = currentMaxSpeed;
		}
	}

	public synchronized void angularlyAcceleratePlayer(boolean aDown, boolean dDown, double frameTime) {
		if (Math.abs(speed) < MAX_SPEED * 0.6) currentMaxRotationSpeed = MAX_ROTATION_SPEED * (Math.abs(speed) / (MAX_SPEED * 0.6));
		else currentMaxRotationSpeed = MAX_ROTATION_SPEED;

		double driftingRotationLock = 0;
		if (isDrifting) {
			if (initiallyTurningRight) driftingRotationLock = currentMaxRotationSpeed;
			else driftingRotationLock = -currentMaxRotationSpeed;
		}
		if ((aDown && !dDown && speed > 0) || (dDown && !aDown && speed < 0)) {
			if (Math.abs(rotationSpeedNoDrifting + HANDLING * frameTime) <= currentMaxRotationSpeed) rotationSpeedNoDrifting += HANDLING * frameTime; //limits max speed
		} else if ((dDown && !aDown && speed > 0) || (aDown && !dDown && speed < 0)) {
			if (Math.abs(rotationSpeedNoDrifting - HANDLING * frameTime) <= currentMaxRotationSpeed) rotationSpeedNoDrifting -= HANDLING * frameTime;
		} else {
			//rotationSpeedNoDrifting -= (rotationSpeedNoDrifting * 0.03) * (frameTime/Renderer.TargetFrameTime);
			rotationSpeedNoDrifting *= Math.pow(0.06, frameTime);
			if (Math.abs(rotationSpeedNoDrifting) < 0.005) {
				rotationSpeedNoDrifting = 0;
			}
		}
		if (rotationSpeedNoDrifting < 0 && rotationSpeedNoDrifting < -currentMaxRotationSpeed) {
			rotationSpeedNoDrifting = -currentMaxRotationSpeed;
		} else if (rotationSpeedNoDrifting > 0 && rotationSpeedNoDrifting > currentMaxRotationSpeed){
			rotationSpeedNoDrifting = currentMaxRotationSpeed;
		}

		if (isDrifting)	rotationSpeed = rotationSpeedNoDrifting/2 + driftingRotationLock*0.75;
		else rotationSpeed = rotationSpeedNoDrifting;
		
	}

	/**
	 * Moves the position of the player
	 */

	public synchronized void movePlayer(double frameTime) {
		double currentSpeed = speed * frameTime; //the constant value is in squares/second

		Vector moveX = new Vector(direction.x * currentSpeed, 0);
		Vector moveY = new Vector(0, direction.y * currentSpeed);
		CollisionBox playerBox;
		CollisionBox[] adjacentBoxes;
		Vector[] corners;
		boolean colliding;

		Vector newPos = pos.addVec(moveX);
		playerBox = new CollisionBox(newPos.x - halfPlayerWidth, newPos.y - halfPlayerHeight, playerWidth, playerHeight);
		corners = playerBox.getCorners();
		adjacentBoxes = getSurroundingCollisionBoxes(map.wallMap);

		colliding = false;
		for (int i = 0; i < 4; i++) {
			for (CollisionBox box : adjacentBoxes) {
				if (box != null && box.contains(corners[i])){
					colliding = true;
					break;
				}
			}
			if (colliding) break;
		}

		if (!colliding) {
			pos = newPos;
		}

		newPos = pos.addVec(moveY);
		playerBox = new CollisionBox(newPos.x - halfPlayerWidth, newPos.y - halfPlayerHeight, playerWidth, playerHeight);
		corners = playerBox.getCorners();
		adjacentBoxes = getSurroundingCollisionBoxes(map.wallMap);

		colliding = false;
		for (int i = 0; i < 4; i++) {
			for (CollisionBox box : adjacentBoxes) {
				if (box != null && box.contains(corners[i])){
					colliding = true;
					break;
				}
			}
			if (colliding) break;
		}

		if (!colliding) {
			pos = newPos;
		}
		
		//WIP
	}

	public double getCarFriction() {
		return (
			groundMoveSpeeds[sampleGroundMap(pos.x + halfPlayerWidth, pos.y + halfPlayerHeight)] +
			groundMoveSpeeds[sampleGroundMap(pos.x + halfPlayerWidth, pos.y - halfPlayerHeight)] + 
			groundMoveSpeeds[sampleGroundMap(pos.x - halfPlayerWidth, pos.y + halfPlayerHeight)] + 
			groundMoveSpeeds[sampleGroundMap(pos.x - halfPlayerWidth, pos.y - halfPlayerHeight)]
		) / 4;
	}

	private int sampleGroundMap(double x, double y) {
		return map.groundMap[(int)((x) * map.groundMapScale)][(int)((y) * map.groundMapScale)];
	}

	@SuppressWarnings("unused")
	private int sampleGroundMap(Vector v) {
		return sampleGroundMap(v.x, v.y);
	}

	public synchronized void teleportPlayer(double x, double y) {
		pos.addVec(new Vector(x, y));
	}

	public synchronized void turnPlayer(double frameTime){
		double currentRotationSpeed = rotationSpeed * frameTime; //the constant value is in radians/second
		double oldDirX = direction.x;
		direction.x = direction.x * Math.cos(currentRotationSpeed) - direction.y * Math.sin(currentRotationSpeed);
		direction.y = oldDirX * Math.sin(currentRotationSpeed) + direction.y * Math.cos(currentRotationSpeed);
		double rotation = Math.atan2(direction.y, direction.x);
		plane.x = unRotatedPlane.y * Math.sin(rotation);
		plane.y = -unRotatedPlane.y * Math.cos(rotation);
	}

	public synchronized void turnPlayerInstant(double angle){
		double oldDirX = direction.x;
		direction.x = direction.x * Math.cos(angle) - direction.y * Math.sin(angle);
		direction.y = oldDirX * Math.sin(angle) + direction.y * Math.cos(angle);
		double oldPlaneX = plane.x;
		plane.x = plane.x * Math.cos(angle) - plane.y * Math.sin(angle);
		plane.y = oldPlaneX * Math.sin(angle) + plane.y * Math.cos(angle);
	}

	public synchronized void checkCheckpoints() {
		if (currentCheckpoint == map.getNumCheckpoints() - 1) {
			if (map.checkpoints[0].contains(pos)) {
				currentCheckpoint = 0;
				lap++;
				System.out.printf("Lap %d\n", lap);
			}
		} else {
			if (map.checkpoints[currentCheckpoint + 1].contains(pos)) {
				currentCheckpoint++;
				System.out.printf("Checkpoint %d\n", currentCheckpoint);
			}
		}
		if (lap > 3) win = true;
	}

		/*
		 * Moves the player’s position and direction based on the player’s speed and rotational speed.
Accounts for drifting, applying additional movement constraints.
Checks for collisions using the collisions methods.
	

	/*
	 * Checks collisions with walls and obstacles (physical barriers)
Used in movePlayer(). 

	 */

	//helper method
	private CollisionBox[] getSurroundingCollisionBoxes(int[][] wallMap) {
		VectorInt mapSquare = new VectorInt((int)pos.x, (int)pos.y);

		/*
		0 1 2
		3 X 4
		5 6 7
		*/
		CollisionBox[] adjacentTiles = new CollisionBox[8];
		int tile = 0;
		for (int x = -1; x < 2; x++) {
			for (int y = -1; y < 2; y++) {
				if (x == 0 && y == 0) continue;
				if (wallMap[mapSquare.x + x][mapSquare.y + y] > 0) {
					//System.out.printf("%d, %d\n", mapSquare.x + x, mapSquare.y + y);
					adjacentTiles[tile] = new CollisionBox(mapSquare.x + x, mapSquare.y + y, 1, 1);
				} else {
					adjacentTiles[tile] = null;
				}
				tile++;
			}
		}

		return adjacentTiles;
		
	}

	
// 	/*
// 	 * Checks collisions with ground (road vs grass vs sand etc.)
// Returns an integer corresponding to the type of ground currently being collided with.
// 0 for road, 1 for grass, 2 for sand/gravel, 3 for a boost pad, etc.

// 	 */
// 	public int checkGroundCollisions(int[][] groundMap) {
		
// 		int x = 0;
		
// 		return x;
// 	}
}
