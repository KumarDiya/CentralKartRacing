import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class HeadsUpDisplay extends JPanel{
    //everything will be drawn on a jpanel that is on top of the main game 
        int lap;
		double posX, posY;
		long timeStarted;
		long timeElapsed;
		BoostBar bb = new BoostBar();

		BufferedImage minimap;

		Graphics2D g2;
<<<<<<< Updated upstream
        HeadsUpDisplay(Graphics2D g2, int panW, int panH, long timeStarted) {
			this.setPreferredSize(new Dimension(panW, panH));
			this.g2 = g2;
=======
        HeadsUpDisplay(int panW, int panH, long timeStarted, BufferedImage minimap) {
			this.setPreferredSize(new Dimension(panW, panH));
			//this.g2 = g2;
			this.minimap = minimap;
>>>>>>> Stashed changes
			this.setOpaque(false);//enable transparency
			this.timeStarted = timeStarted;
		}

	/*
	*@param lap  			lap number player is currently on
	*@param posX 			x-position of player on the map
	*@param posY 			y-position of player on the map 
	*/
    public void drawHUD(int lap, double posX, double posY){
        drawTimer();
        drawBoostBar();
<<<<<<< Updated upstream
		drawLap();
=======
		drawLap(lap);
		drawMap();
>>>>>>> Stashed changes

    }

	private void drawLap(){
		g2.setFont(new Font("Arial", Font.BOLD, 50));
        g2.setPaint(Color.red);
		g2.drawString("Lap " + String.valueOf(lap), 100, 80); //positioned near the top left
	}
    
    private void drawBoostBar(){
		
		//g2.fillRect();
		g2.setPaint(Color.orange);
		g2.setStroke(new BasicStroke(10));
		g2.fillRect(bb.x, bb.y, bb.width, bb.height); //
		g2.drawRect(bb.x, bb.y, bb.maxFill, bb.height); //outline for the boostbar

    }
    private void drawTimer() {
        //update time elapsed since start of race
		timeElapsed = System.currentTimeMillis() -  timeStarted; 
		int timeMilli = (int)(timeElapsed % 1000)/10;//time shown in milliseconds; divides by 10 to show the first 2 digits rather than all 3
		int timeSec = (int)(timeElapsed/1000 % 60);//time shown in seconds
		int timeMin = (int)(timeElapsed/60000 % 60);


        g2.setFont(new Font("Arial", Font.BOLD, 80));
        g2.setPaint(Color.blue);

		String timeShown = String.format("%02d:%02d:%02d", timeMin, timeSec, timeMilli);
		
        g2.drawString(timeShown, 830, 90); // positioned near the top right
    }	

<<<<<<< Updated upstream
	private void drawMap(){
		//constrain map image to a square area //maybe another jpanel
=======

	// private BufferedImage loadImage(String filename) {
    //     BufferedImage image = null;
    //     try {
            
    //         File file = new File("CentralKartRacing\\testScreenImages\\" + filename);
            
    //         image = ImageIO.read(file);
            
    //         if (image != null) {
    //             System.out.println("Image loaded successfully!");
    //         }
            
    //     } catch (IOException e) {
    //         JOptionPane.showMessageDialog(null, "Image failed to load: " + filename, "ERROR", JOptionPane.ERROR_MESSAGE);
    //     }
    //     return image;
    // }
	
	private void drawMap(){
		
		// circle player tracker / dot

		int MAX_SIZE = 150; //constrain minimap to 150x150 square

		int mapImgWidth = minimap.getWidth(); 
 		int mapImgHeight = minimap.getHeight(); 
		
		int newMapImgWidth;
		int newMapImgHeight;

		double scaleFactor;

		//need to check whether height or width is larger
		// if ((mapImgWidth > MAX_SIZE) || (mapImgWidth > MAX_SIZE)){
				
		// } 
		// if ((widthHeightRatio == 1) || (widthHeightRatio > 1)){ //square or longer than tall
		// 	scaleFactor = mapImgWidth/MAX_SIZE;
		// 	newMapImgWidth = MAX_SIZE; 
		// 	newMapImgHeight = (int)(mapImgHeight/scaleFactor);
			

		// }else{// taller than long
		// 	scaleFactor = mapImgHeight/MAX_SIZE;
		// 	newMapImgHeight = MAX_SIZE; 
		// 	newMapImgWidth = (int)(mapImgWidth/scaleFactor);
		// }

		//more efficient
		if (mapImgWidth >= mapImgHeight) {
    		scaleFactor = (double)MAX_SIZE / mapImgWidth;
		} else {
    		scaleFactor = (double)MAX_SIZE / mapImgHeight;
		}

		newMapImgWidth = (int)(mapImgWidth * scaleFactor);
		newMapImgHeight = (int)(mapImgHeight * scaleFactor);
		
		int mapX = 20; //20 margin from left side
		int mapY = getHeight() - newMapImgHeight - 20; //20 margin from bottom
		g2.drawImage(minimap, mapX, mapY, newMapImgWidth, newMapImgHeight, null); 
>>>>>>> Stashed changes
	}
}

class BoostBar extends Rectangle{
	BoostBar(){
	}
	double boostFill;
	int x = 500; //positioned near the top middle
	int y = 50;
	final int height = 50;
	int maxFill = 200; //width of the boost rectangle when full
	int width = (int)boostFill;

}