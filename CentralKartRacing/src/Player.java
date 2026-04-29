
public class Player {
	//ALL VALUES ARBITRARY RIGHT NOW
	
	//Linear movement vars
	Vector pos; //The position of the player.
	final double MAX_SPEED = 1; //The maximum speed for the character.
	double speed; //The current speed for the character.
	final double ACCELERATION = 1; //The acceleration of the character.
	
	//Rotational movment vars
	Vector direction; //The direction the player is facing.
	final double MAX_ROTATION_SPEED = 1; //The maximum rotational speed of the character.
	Vector plane; //A vector perpendicular to the direction, representing the camera plane.
	double rotationSpeed; //The current rotational speed of the character.
	final double HANDLING = 1; //The rotational acceleration of the character.
	
	boolean isTurning = false; //True if player is turning, false otherwise

	//Drifting vars
	boolean isDrifting = false; //True if the player is currently drifting, false otherwise.
	double turboSpeed = 2; //The speed that a boost sets you to.
	
	//Getter for direction
	public Vector getDirection() {
		return direction;
	}

	
	
	//default constructor
	Player(){
		
	}
	
	
	Player(String character){
		//Creates a new character using a specified character, where char is the selected character.
		//Loads all stats of the character either directly in code, or from a stats.txt file for the character.
		
		//NOTE: some variables can not be constants then!!
		//if (character.equals("Ghost")) {}
			
	}
	
	
//	public void getKeyInput() { //Gets keyboard input to accelerate, rotate, and drift.
//		private class KeystrokeListener extends KeyAdapter {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				
//			}

 //Movement
           
            /* 
			if(gc.isKeyDown(87)) {
                
            }
            //move backwards if no wall behind you
            if(gc.isKeyDown(83)) {
                if(map[(int)(pos.x - dir.x * moveSpeed)][(int)(pos.y)] == 0) pos.x -= dir.x * moveSpeed;
                if(map[(int)(pos.x)][(int)(pos.y - dir.y * moveSpeed)] == 0) pos.y -= dir.y * moveSpeed;
            }
            //rotate to the right
            if(gc.isKeyDown(68)) {
                //both camera direction and camera plane must be rotated
                double olddirX = dir.x;
                dir.x = dir.x * Math.cos(-rotSpeed) - dir.y * Math.sin(-rotSpeed);
                dir.y = olddirX * Math.sin(-rotSpeed) + dir.y * Math.cos(-rotSpeed);
                double oldplaneX = plane.x;
                plane.x = plane.x * Math.cos(-rotSpeed) - plane.y * Math.sin(-rotSpeed);
                plane.y = oldplaneX * Math.sin(-rotSpeed) + plane.y * Math.cos(-rotSpeed);
            }
            //rotate to the left
            if(gc.isKeyDown(65)) {
                //both camera direction and camera plane must be rotated
                double olddirX = dir.x;
                dir.x = dir.x * Math.cos(rotSpeed) - dir.y * Math.sin(rotSpeed);
                dir.y = olddirX * Math.sin(rotSpeed) + dir.y * Math.cos(rotSpeed);
                double oldplaneX = plane.x;
                plane.x = plane.x * Math.cos(rotSpeed) - plane.y * Math.sin(rotSpeed);
                plane.y = oldplaneX * Math.sin(rotSpeed) + plane.y * Math.cos(rotSpeed);
            }*/
//	}
	
	//GETTER AND SETTER

	//accelerates player
	public void acceleratePlayer(){
		if (speed <= MAX_SPEED) speed += ACCELERATION; //limits max speed
	}

	//decelerates player
	public void slowPlayer(){
		final double MIN_SPEED = MAX_SPEED*-0.25;
		if (speed >= MIN_SPEED)speed -= ACCELERATION*0.75; //limits min speed
	}


	// public void turnPlayer(){
	// 	//limit turn
	// 	if (rotationSpeed >= -MAX_ROTATION_SPEED && rotationSpeed <= MAX_ROTATION_SPEED){
	// 		rotationSpeed += HANDLING;
	// 	}

	// // 	//noarctan

	// }  //slower vs faster turning

	/**
	 * Moves the position of the player
	 */
	//REMEMBER TO SET rotSpeed to NEGATIVE WHEN TURNING OTHER WAY
	public void movePlayer(double frameTime) {
		speed = frameTime * 5.0; //the constant value is in squares/second
    
		pos.x += direction.x * speed;
		pos.y += direction.y * speed;
		//slight change idea: make direction able to be negative so when backing up, * by -1

		if (isDrifting){

		}
	}

	public void turnPlayer(double frameTime){
		rotationSpeed = frameTime * HANDLING; //the constant value is in radians/second
		if (isTurning){
			double olddirX = direction.x;
            direction.x = direction.x * Math.cos(rotationSpeed) - direction.y * Math.sin(rotationSpeed);
            direction.y = olddirX * Math.sin(rotationSpeed) + direction.y * Math.cos(rotationSpeed);
            double oldplaneX = plane.x;
            plane.x = plane.x * Math.cos(rotationSpeed) - plane.y * Math.sin(rotationSpeed);
            plane.y = oldplaneX * Math.sin(rotationSpeed) + plane.y * Math.cos(rotationSpeed);
		}
	}

		/*
		 * Moves the player’s position and direction based on the player’s speed and rotational speed.
Accounts for drifting, applying additional movement constraints.
Checks for collisions using the collisions methods.
	

	/*
	 * Checks collisions with walls and obstacles (physical barriers)
Used in movePlayer(). 
Returns a boolean array of length 4; each boolean corresponds to a collision with a direction of wall (pos x, pos y, neg x, neg y)
	 */
	
	public boolean[] checkStoppingCollisions(int[][] wallMap) {
		boolean[] wallCollisions = new boolean[4];
		CollisionBox playerBox = new CollisionBox(pos, 1, 1); //the width and height of the player are arbitrary

		for (int x = 0; x<wallMap.length; x++){
			for (int y = 0; y<wallMap[0].length;y++){
				if 
				(wallMap[x][y] == 1){ //if there is a wall here
					CollisionBox wallBox = new CollisionBox(x, y, 1, 1); //the width and height of the wall are arbitrary
					
					/*
					*   1
					* 2   0
					*   3
					*/
					if (playerBox.contains(wallBox.centX + wallBox.halfWidth, wallBox.centY)){
						//if the player box contains the right wall edge
						wallCollisions[0] = true; //collision in positive x direction
					}
					if (playerBox.contains(wallBox.centX - wallBox.halfWidth, wallBox.centY)){
						wallCollisions[2] = true; //collision in negative x direction
					}
					if (playerBox.contains(wallBox.centX, wallBox.centY + wallBox.halfHeight)){
						//if the player box contains the top wall edge
						wallCollisions[1] = true; //collision in positive y direction
						
					}
					if (playerBox.contains(wallBox.centX, wallBox.centY - wallBox.halfHeight)){
						wallCollisions[3] = true; //collision in negative y direction
					}
				}
			}
		}
		return wallCollisions;
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
