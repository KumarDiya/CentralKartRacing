import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Player {
	//ALL VALUES ARBITRARY RIGHT NOW
	
	//Linear movement vars
	Vector pos; //The position of the player.
	final double MAX_SPEED; //The maximum speed for the character.
	double currentMaxSpeed;
	double speed; //The current speed for the character.
	final double ACCELERATION; //The acceleration of the character.
	final double BOOSTACCELERATION; //5x the normal accel
	
	//Rotational movement vars
	Vector direction; //The direction the player is facing.
	final double MAX_ROTATION_SPEED; //The maximum rotational speed of the character.
	double currentMaxRotationSpeed;
	Vector unRotatedPlane;
	Vector plane; //A vector perpendicular to the direction, representing the camera plane.
	double rotationSpeedNoDrifting;
	double rotationSpeed; //The current rotational speed of the character.
	final double HANDLING; //The rotational acceleration of the character.
	
	boolean isTurning = false; //True if player is turning, false otherwise

	//Drifting var
	
	double currentFuel = 0;
	final double MAXFUEL;

	boolean isDrifting;
	boolean isDriftingPrevious;
	long driftStartTime;
	long driftLength;
	int driftLevel = 0;
	
	boolean initiallyTurningRight = false;
	double turboSpeed = 1.5; //The speed that a boost sets you to.

	Map map; //map used for wall collisions

	//Checkpoint and lap vars
	int currentCheckpoint;
	private int lap;
	boolean win;

	//Texture and sprite variables
	Sprite sprite;
	Sprite DBsprite; //drift boost sprite

	Texture[] characterTextures;
	Texture[] DBTextures;

	final String[] characterFolderNames = {"blondeGuy", "jeff", "po", "test"};
	final String characterFolder = "CentralKartRacing\\Characters\\";

	int playerFrame = 2; 
	int DBFrame = 0;

	//Player Collision vars
	final double playerWidth = 0.5;
	final double playerHeight = 0.5;
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
		this.pos = map.getStartingPos().duplicate();
		this.sprite = new Sprite(pos, 0);
		this.DBsprite = new Sprite(pos, 0); //REMINDER: MAYBE USE DIFFERENT POS
		this.direction = map.getStartingDir().duplicate();
		this.unRotatedPlane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));
		this.plane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));
		this.rotationSpeedNoDrifting = 0;
		this.speed = 0;
		this.currentCheckpoint = 0;
		this.lap = 1;
		this.win = false;
		MAX_SPEED = 8;
		ACCELERATION = 4;
		BOOSTACCELERATION = 20;
		MAX_ROTATION_SPEED = 1.5;
		HANDLING = 10;
		MAXFUEL = 100;
		loadCharacterTextures();
		loadDriftAndBoostTextures();
	}
	
	//use this one when we have more characters
	Player(Map map, int character){
		this.map = map;
		this.pos = map.getStartingPos().duplicate();
		this.sprite = new Sprite(pos, 0);
		this.DBsprite = new Sprite(pos, 0); //REMINDER: MAYBE USE DIFFERENT POS
		this.direction = map.getStartingDir().duplicate();
		this.unRotatedPlane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));
		this.plane = new Vector(0, Math.tan(Math.toRadians(Renderer.FOV/2)));
		this.rotationSpeedNoDrifting = 0;
		this.speed = 0;
		this.currentCheckpoint = 0;
		this.lap = 1;
		this.win = false;

		//Character stats loading
		//Temporary variables for constant setting
		double tempMaxSpeed = 8, tempAcceleration = 4, tempBoostAcceleration = 20, tempMaxRotationSpeed = 1.5, tempHandling = 10, tempMaxFuel = 100;
        File characterStatsPath = new File(characterFolder + characterFolderNames[character] + "\\stats.txt");
        try {
            FileReader r = new FileReader(characterStatsPath);
            BufferedReader reader = new BufferedReader(r);
            tempMaxSpeed 			= Double.parseDouble(reader.readLine());
			tempAcceleration 		= Double.parseDouble(reader.readLine());
			tempBoostAcceleration 	= Double.parseDouble(reader.readLine());
			tempMaxRotationSpeed 	= Double.parseDouble(reader.readLine());
			tempHandling 			= Double.parseDouble(reader.readLine());
			tempMaxFuel 			= Double.parseDouble(reader.readLine());
            reader.close();
            r.close();
			
		} catch (IOException e) {
			System.out.printf("An error loading the player stats for the player \"%s\" occurred.\n", characterFolderNames[character]);
			tempMaxSpeed = 8;
			tempAcceleration = 4;
			tempBoostAcceleration = 20;
			tempMaxRotationSpeed = 1.5;
			tempHandling = 10;
			tempMaxFuel = 100;
			System.out.println(e);
		}

		MAX_SPEED = tempMaxSpeed;
		ACCELERATION = tempAcceleration;
		BOOSTACCELERATION = tempBoostAcceleration;
		MAX_ROTATION_SPEED = tempMaxRotationSpeed;
		HANDLING = tempHandling;
		MAXFUEL = tempMaxFuel;

		loadCharacterTextures();
		loadDriftAndBoostTextures();
	}
	
	public synchronized void checkDrifting(boolean uDown, boolean aDown, boolean dDown, boolean iDown, double frameTime) {
		isDriftingPrevious = isDrifting;
		if (uDown && (aDown || dDown) && speed > MAX_SPEED * 0.7 && sampleGroundMap(pos.x, pos.y) == 1) { //only drift on road
			isDrifting = true;
		} else if (!uDown){ //redundancy cause some bug
			isDrifting = false;
		} else if (speed < MAX_SPEED * 0.7) {//need certain speed to drift
			isDrifting = false;
		}

		if (!isDriftingPrevious && isDrifting) {
			if (aDown) initiallyTurningRight = true;
			else initiallyTurningRight = false;
			driftStartTime = System.currentTimeMillis(); //timer used for drift length
		}
		if (isDriftingPrevious && !isDrifting){
			
			driftLevel = 0;

		}
		
		//System.out.println(speed);

		if (isDrifting && !iDown) {
        	double chargeRate = 20;

			driftLength = System.currentTimeMillis() - driftStartTime;

			if (driftLength >= 3000) driftLevel = 3;
			else if (driftLength >= 1500) driftLevel = 2;
			else if (driftLength >= 500) driftLevel = 1;
			else driftLevel = 0;

			switch (driftLevel) {
				case 3 -> chargeRate = 40;
				case 2 -> chargeRate = 25;
				case 1 -> chargeRate = 15;
				default -> chargeRate = 5; // level 0
			}
			
			//System.out.println("Drift level: " + driftLevel);

			if (isDrifting && sampleGroundMap(pos.x, pos.y) == 1) {//redundant kind of with the map detection
    			currentFuel += chargeRate*frameTime; //maybe charge rate grows??
			}

			//limit amount of fuel
        	if (currentFuel > MAXFUEL) {
        		currentFuel = MAXFUEL;
        	}

			Vector[] wheelLocations = getWheelLocations();
			for (Vector wheel : wheelLocations) {
				map.darkeningValues.put(wheel, 0);
			}
    	}
	}
	
 //Movement
	//accelerates player
	public synchronized void acceleratePlayer(boolean wDown, boolean sDown, boolean iDown, double frameTime){
		double currentCarFriction = getCarFriction();
		currentMaxSpeed = MAX_SPEED * currentCarFriction; //changes to account for boosts
		
		if (iDown && currentFuel > 0) {
			DBFrame = 3;
			

			if (speed < currentMaxSpeed) {
    			speed += BOOSTACCELERATION * 2 * frameTime;
			} //note: gotta make decelleration slower
			currentMaxSpeed = MAX_SPEED * turboSpeed; 

			double drainRate = 50; //tune
			currentFuel -= drainRate*frameTime;

			if (currentFuel < 0) {
				currentFuel = 0;
			}

		} else {
			DBFrame = 0;
		}
		DBsprite.texture = DBFrame;

		if (isDrifting){
			currentMaxSpeed *= 0.85; //slows player when drifting
		}

		if (wDown && !sDown) {
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
		if ((aDown && !dDown && speed > 0) || (dDown && !aDown && speed < 0)) {//turn left
			
			if (Math.abs(rotationSpeedNoDrifting + HANDLING * frameTime) <= currentMaxRotationSpeed) rotationSpeedNoDrifting += HANDLING * frameTime; //limits max speed

		} else if ((dDown && !aDown && speed > 0) || (aDown && !dDown && speed < 0)) {//turn right
			
			
			if (Math.abs(rotationSpeedNoDrifting - HANDLING * frameTime) <= currentMaxRotationSpeed) rotationSpeedNoDrifting -= HANDLING * frameTime;
		} else {
			//rotationSpeedNoDrifting -= (rotationSpeedNoDrifting * 0.03) * (frameTime/Renderer.TargetFrameTime);
			rotationSpeedNoDrifting *= Math.pow(0.06, frameTime);
			if (Math.abs(rotationSpeedNoDrifting) < 0.005) {
				rotationSpeedNoDrifting = 0;
			}
		}
		if (rotationSpeedNoDrifting < 0 && rotationSpeedNoDrifting < - currentMaxRotationSpeed) {
			rotationSpeedNoDrifting = - currentMaxRotationSpeed;
		} else if (rotationSpeedNoDrifting > 0 && rotationSpeedNoDrifting > currentMaxRotationSpeed){
			rotationSpeedNoDrifting = currentMaxRotationSpeed;
		}


		//turn sprites
		if (rotationSpeedNoDrifting > currentMaxRotationSpeed * 0.75) playerFrame = 1;
		else if (rotationSpeedNoDrifting < currentMaxRotationSpeed * -0.75) playerFrame = 3;
		else playerFrame = 2;


		if (isDrifting)	{
			rotationSpeed = rotationSpeedNoDrifting/2 + driftingRotationLock*0.75; //make rotation lock more harsh
			if (rotationSpeed > 0){
				playerFrame = 0;
				
			}else if (rotationSpeed < 0){
				playerFrame = 4;
			
			}
		} else {
			rotationSpeed = rotationSpeedNoDrifting;
			DBFrame = 0;
		} 

		if (speed < 0){
			playerFrame = 2;
				
		}

		sprite.texture = playerFrame;
		
		
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
			for (CollisionBox box : map.spriteCollisions) {
				if (box.contains(corners[i])){
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
			for (CollisionBox box : map.spriteCollisions) {
				if (box.contains(corners[i])){
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

	private Vector[] getWheelLocations() {
		Vector[] wheelLocations = new Vector[4];
		wheelLocations[0] = new Vector(pos.x + halfPlayerWidth, pos.y + halfPlayerHeight);
		wheelLocations[1] = new Vector(pos.x + halfPlayerWidth, pos.y - halfPlayerHeight);
		wheelLocations[2] = new Vector(pos.x - halfPlayerWidth, pos.y + halfPlayerHeight);
		wheelLocations[3] = new Vector(pos.x - halfPlayerWidth, pos.y - halfPlayerHeight);
		return wheelLocations;
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
				if (lap > 3) {
					win = true;
				}
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

	private void loadCharacterTextures() {
		characterTextures = new Texture[5];
		String folderPath = characterFolder + "blondeGuy" + "\\"; 
		characterTextures[0] = new Texture(folderPath + "leftDrift.png");
		characterTextures[1] = new Texture(folderPath + "leftTurn.png");
		characterTextures[2] = new Texture(folderPath + "straight.png");
		characterTextures[3] = new Texture(folderPath + "rightTurn.png");
		characterTextures[4] = new Texture(folderPath + "rightDrift.png");
	}

	private void loadDriftAndBoostTextures(){
		DBTextures = new Texture[4];
		String folderPath = characterFolder + "blondeGuy" + "\\";
		DBTextures[0] = null; //blank texture for no boost
		DBTextures[1] = new Texture(folderPath + "trailLeft.png");
		DBTextures[2] = new Texture(folderPath + "trailRight.png");
		DBTextures[3] = new Texture(folderPath + "boost.png");
	}
}
