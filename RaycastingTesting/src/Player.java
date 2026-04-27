import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

public class Player {
	//ALL VALUES ARBITRARY RIGHT NOW
	
	// double moveSpeed = frameTime * 5.0; //the constant value is in squares/second
    //double rotSpeed = frameTime * 2.0; //the constant value is in radians/second
	
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
	
	//Drifting vars
	boolean drifting = false; //True if the player is currently drifting, false otherwise.
	double turboSpeed = 2; //The speed that a boost sets you to.
	
	//default constructor
	Player(){
		
	}
	
	
//	Player(String character){
//		//Creates a new character using a specified character, where char is the selected character.
//		//Loads all stats of the character either directly in code, or from a stats.txt file for the character.
//		
		//NOTE: some variables can not be constants then!!
//		if (character.equals("Ghost")) {
//			
//		}
//	}
	
//	public void getKeyInput() { //Gets keyboard input to accelerate, rotate, and drift.
//		private class KeystrokeListener extends KeyAdapter {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				
//			}
//	}
	
	//GETTER AND SETTER
	
	public void movePlayer() {
		pos.x += direction.x * speed;
		pos.y += direction.y * speed;
		
		if () {
			
		}
		//slight change idea: make direction able to be negative so when backing up, * by -1
		
		/*
		 * Moves the player’s position and direction based on the player’s speed and rotational speed.
Accounts for drifting, applying additional movement constraints.
Checks for collisions using the collisions methods.

   //Movement
           
            
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
            }
		 */
		
		
	}
	/*
	 * Checks collisions with walls and obstacles (physical barriers)
Used in movePlayer(). 
Returns a boolean array of length 4; each boolean corresponds to a collision with a direction of wall (pos x, pos y, neg x, neg y)

	 */
	public boolean[] checkStoppingCollisions() {
		boolean[] wallCollisions = new boolean[4];
		
		//old collisions
		//if(map[(int)(pos.x + dir.x * moveSpeed)][(int)pos.y] == 0) 
        //if(map[(int)pos.x][(int)(pos.y + dir.y * moveSpeed)] == 0) 
		
		return wallCollisions;
	}
	
	/*
	 * Checks collisions with ground (road vs grass vs sand etc.)
Returns an integer corresponding to the type of ground currently being collided with.
0 for road, 1 for grass, 2 for sand/gravel, 3 for a boost pad, etc.

	 */
	public int checkGroundCollisions() {
		int x = 0;
		
		return x;
	}
	
	
	

}
