public class Player {
	//ALL VALUES ARBITRARY RIGHT NOW
	
	//Linear movement vars
	final Vector StartPos;
	Vector pos; //The position of the player.
	final double MAX_SPEED = 5; //The maximum speed for the character.
	double currentMaxSpeed;
	double speed; //The current speed for the character.
	final double ACCELERATION = 0.01; //The acceleration of the character.
	
	//Rotational movement vars
	Vector direction; //The direction the player is facing.
	final double MAX_ROTATION_SPEED = 2; //The maximum rotational speed of the character.
	double currentMaxRotationSpeed;
	Vector plane; //A vector perpendicular to the direction, representing the camera plane.
	double rotationSpeed; //The current rotational speed of the character.
	final double HANDLING = 0.02; //The rotational acceleration of the character.
	
	boolean isTurning = false; //True if player is turning, false otherwise

	//Drifting vars
	boolean isDrifting = false; //True if the player is currently drifting, false otherwise.
	double turboSpeed = 2; //The speed that a boost sets you to.
	Map map; //map used for wall collisions

	int currentCheckpoint;
	int lap;

	//Player Collision vars
	final double playerWidth = 0.4;
	final double playerHeight = 0.4;
	final double halfPlayerWidth = playerWidth/2;
	final double halfPlayerHeight = playerHeight/2;

	//Constants
	final double FrameMovementMultiplier = 500;

	final double[] groundMoveSpeeds = {0.1, 1.0, 0.6, 0.4}; //Wall speed, road speed, grass speed, sand speed.

	//Getter for direction
	public Vector getDirection() {
		return direction;
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
		this.plane = new Vector(0, 0.88);
		this.rotationSpeed = 0;
		this.speed = 0;
		this.currentCheckpoint = 0;
		this.lap = 1;
	}
	
	Player(Map map, String character){
		//Creates a new character using a specified character, where char is the selected character.
		//Loads all stats of the character either directly in code, or from a stats.txt file for the character.
		
		//NOTE: some variables can not be constants then!!
		//if (character.equals("Ghost")) {}
			
		this(map);
	}
	
	
 //Movement
	//accelerates player
	public synchronized void acceleratePlayer(boolean wDown, boolean sDown, double frameTime){
		double currentGroundMoveSpeed = groundMoveSpeeds[map.groundMap[(int)(pos.x * map.groundMapScale)][(int)(pos.y * map.groundMapScale)]];
		double currentAcceleration = ACCELERATION * frameTime * FrameMovementMultiplier * currentGroundMoveSpeed;
		currentMaxSpeed = MAX_SPEED * currentGroundMoveSpeed;

		if (wDown && !sDown) {
			if (Math.abs(speed + currentAcceleration) <= currentMaxSpeed) speed += currentAcceleration; //limits max speed
		} else if (sDown && !wDown) {
			if (speed > 0){
				speed -= currentAcceleration * 2;
			} else if (Math.abs(speed - currentAcceleration * 0.5) <= currentMaxSpeed * 0.5) speed -= currentAcceleration * 0.5;
		} else {
			speed *= (1 - frameTime);
			if (Math.abs(speed) < 0.05){
				speed = 0;
			}
		}

		if (speed < 0 && speed < -currentMaxSpeed) {
			speed = -currentMaxSpeed;
		} else if (speed > 0 && speed > currentMaxSpeed){
			speed = currentMaxSpeed;
		}
	}

	public synchronized void angularlyAcceleratePlayer(boolean aDown, boolean dDown, double frameTime) {
		double currentHandling = HANDLING * frameTime * FrameMovementMultiplier;

		if (Math.abs(speed) < 3) currentMaxRotationSpeed = MAX_ROTATION_SPEED * (Math.abs(speed) / 3);
		else currentMaxRotationSpeed = MAX_ROTATION_SPEED;
		
		if (aDown && !dDown) {
			if (Math.abs(rotationSpeed + currentHandling) <= currentMaxRotationSpeed) rotationSpeed += currentHandling; //limits max speed
		} else if (dDown && !aDown) {
			if (Math.abs(rotationSpeed - currentHandling) <= currentMaxRotationSpeed) rotationSpeed -= currentHandling;
		} else {
			rotationSpeed *= (1 - frameTime * 4);
			if (Math.abs(rotationSpeed) < 0.005) {
				rotationSpeed = 0;
			}
		}

		if (rotationSpeed < 0 && rotationSpeed < -currentMaxRotationSpeed) {
			rotationSpeed = -currentMaxRotationSpeed;
		} else if (rotationSpeed > 0 && rotationSpeed > currentMaxRotationSpeed){
			rotationSpeed = currentMaxRotationSpeed;
		}
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
		if (isDrifting){

		}
	}

	public synchronized void teleportPlayer(double x, double y) {
		pos.addVec(new Vector(x, y));
	}

	public synchronized void turnPlayer(double frameTime){
		double currentRotationSpeed = rotationSpeed * frameTime; //the constant value is in radians/second
		double oldDirX = direction.x;
		direction.x = direction.x * Math.cos(currentRotationSpeed) - direction.y * Math.sin(currentRotationSpeed);
		direction.y = oldDirX * Math.sin(currentRotationSpeed) + direction.y * Math.cos(currentRotationSpeed);
		double oldPlaneX = plane.x;
		plane.x = plane.x * Math.cos(currentRotationSpeed) - plane.y * Math.sin(currentRotationSpeed);
		plane.y = oldPlaneX * Math.sin(currentRotationSpeed) + plane.y * Math.cos(currentRotationSpeed);
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
