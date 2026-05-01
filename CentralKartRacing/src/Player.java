
public class Player {
	//ALL VALUES ARBITRARY RIGHT NOW
	
	//Linear movement vars
	Vector pos; //The position of the player.
	final double MAX_SPEED = 1; //The maximum speed for the character.
	double speed; //The current speed for the character.
	final double ACCELERATION = 3; //The acceleration of the character.
	
	//Rotational movment vars
	Vector direction; //The direction the player is facing.
	final double MAX_ROTATION_SPEED = 1; //The maximum rotational speed of the character.
	Vector plane; //A vector perpendicular to the direction, representing the camera plane.
	double rotationSpeed; //The current rotational speed of the character.
	final double HANDLING = 2; //The rotational acceleration of the character.
	
	boolean isTurning = false; //True if player is turning, false otherwise

	//Drifting vars
	boolean isDrifting = false; //True if the player is currently drifting, false otherwise.
	double turboSpeed = 2; //The speed that a boost sets you to.
	Map map; //map used for wall collisions

	//Player Collision vars
	final double playerWidth = 0.4;
	final double playerHeight = 0.4;
	final double halfPlayerWidth = playerWidth/2;
	final double halfPlayerHeight = playerHeight/2;

	//Getter for direction
	public Vector getDirection() {
		return direction;
	}

	public void printPos() {
		System.out.printf("%.2f, %.2f\n", pos.x, pos.y);
	}

	Player(Map map){
		this.map = map;
		this.pos = new Vector(12, 12);
		this.direction = new Vector(-1, 0);
		this.plane = new Vector(0, 0.66);
		this.rotationSpeed = 0;
		this.speed = 0;
	}
	
	Player(Map map, String character){
		//Creates a new character using a specified character, where char is the selected character.
		//Loads all stats of the character either directly in code, or from a stats.txt file for the character.
		
		//NOTE: some variables can not be constants then!!
		//if (character.equals("Ghost")) {}
			
		this.map = map;
	}
	
	
 //Movement
	//accelerates player
	public void acceleratePlayer(boolean wDown, boolean sDown){
		if (wDown && !sDown) {
			if (Math.abs(speed) <= MAX_SPEED) speed += ACCELERATION; //limits max speed
		} else if (sDown && !wDown) {
			if (Math.abs(speed) <= MAX_SPEED*0.25) speed -= ACCELERATION*0.75;
		} else {
			speed *= 0.5;
		}
	}

	public void angularlyAcceleratePlayer(boolean aDown, boolean dDown) {
		if (aDown && !dDown) {
			if (Math.abs(rotationSpeed) <= MAX_ROTATION_SPEED) rotationSpeed += HANDLING; //limits max speed
		} else if (dDown && !aDown) {
			if (Math.abs(rotationSpeed) <= MAX_ROTATION_SPEED) rotationSpeed -= HANDLING;
		} else {
			rotationSpeed *= 0.9;
		}
	}

	/**
	 * Moves the position of the player
	 */
	//REMEMBER TO SET rotSpeed to NEGATIVE WHEN TURNING OTHER WAY

	public void movePlayer(double frameTime) {
		double currentSpeed = speed * frameTime; //the constant value is in squares/second

		Vector moveX = new Vector(direction.x * currentSpeed, 0);
		Vector moveY = new Vector(0, direction.y * currentSpeed);
		CollisionBox playerBox;
		CollisionBox[] adjacentBoxes;
		Vector[] corners;
		boolean colliding;

		Vector newPosX = pos.addVec(moveX);
		playerBox = new CollisionBox(newPosX.x - halfPlayerWidth, newPosX.y - halfPlayerHeight, playerWidth, playerHeight);
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
			pos = newPosX;
		}

		Vector newPosY = pos.addVec(moveY);
		playerBox = new CollisionBox(newPosY.x - halfPlayerWidth, newPosY.y - halfPlayerHeight, playerWidth, playerHeight);
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
			pos = newPosY;
		}
		
		//WIP
		if (isDrifting){

		}
	}

	public void turnPlayer(double frameTime){
		double currentRotationSpeed = rotationSpeed * frameTime; //the constant value is in radians/second
		double olddirX = direction.x;
		direction.x = direction.x * Math.cos(currentRotationSpeed) - direction.y * Math.sin(currentRotationSpeed);
		direction.y = olddirX * Math.sin(currentRotationSpeed) + direction.y * Math.cos(currentRotationSpeed);
		double oldplaneX = plane.x;
		plane.x = plane.x * Math.cos(currentRotationSpeed) - plane.y * Math.sin(currentRotationSpeed);
		plane.y = oldplaneX * Math.sin(currentRotationSpeed) + plane.y * Math.cos(currentRotationSpeed);
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

		// for (int x = 0; x<wallMap.length; x++){
		// 	for (int y = 0; y<wallMap[0].length;y++){
		// 		if 
		// 		(wallMap[x][y] == 1){ //if there is a wall here
		// 			CollisionBox wallBox = new CollisionBox(x, y, 1, 1); //the width and height of the wall are arbitrary
					
		// 			/*
		// 			*   1
		// 			* 2   0
		// 			*   3
		// 			*/
		// 			if (playerBox.contains(wallBox.centX + wallBox.halfWidth, wallBox.centY)){
		// 				//if the player box contains the right wall edge
		// 				wallCollisions[0] = true; //collision in positive x direction
		// 			}
		// 			if (playerBox.contains(wallBox.centX - wallBox.halfWidth, wallBox.centY)){
		// 				wallCollisions[2] = true; //collision in negative x direction
		// 			}
		// 			if (playerBox.contains(wallBox.centX, wallBox.centY + wallBox.halfHeight)){
		// 				//if the player box contains the top wall edge
		// 				wallCollisions[1] = true; //collision in positive y direction
						
		// 			}
		// 			if (playerBox.contains(wallBox.centX, wallBox.centY - wallBox.halfHeight)){
		// 				wallCollisions[3] = true; //collision in negative y direction
		// 			}
		// 		}
		// 	}
		// }
		//return wallCollisions;
		//old collisions
		//if(map[(int)(pos.x + dir.x * moveSpeed)][(int)pos.y] == 0) 
        //if(map[(int)pos.x][(int)(pos.y + dir.y * moveSpeed)] == 0) 
		
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
