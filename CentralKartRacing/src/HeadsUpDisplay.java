import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HeadsUpDisplay extends JPanel{
    //everything will be drawn on a jPanel that is on top of the main game 
        
	long timeElapsed;
	
	int panH;
	Color transparentRed = new Color(255, 0, 0, 150); //used for the player dot 
	Font guiFont = new Font("Bahnschrift", Font.BOLD, 30);
	Font startingFont = new Font("Bahnschrift", Font.BOLD, 60);

	double maxSpeedNormal;

	BufferedImage minimap;

	Graphics2D g2;

	Map map;

	HeadsUpDisplay(int panW, int panH, Map map) {
		this.map = map;
		minimap = loadImage("groundTexture.png");
		this.setPreferredSize(new Dimension(panW, panH));
		this.panH = panH;
		this.setOpaque(false);//enable transparency
	}

	private BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        try {
            
            File file = new File(map.getMapFolder() + filename);
            
            image = ImageIO.read(file);
            
            if (image != null) {
                System.out.println("Image loaded successfully!");
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Image failed to load: " + filename, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return image;
    }

	//need a setter method here as the g2 is created after the hud constructor is created
	public void setG2 (Graphics2D g2){
		this.g2 = g2;
	}	
	/*
	*@param lap  			lap number player is currently on
	*@param posX 			x-position of player on the map
	*@param posY 			y-position of player on the map 
	*/
    public void drawHUD(int lap, double posX, double posY, double currentF, double maxF, double playerSpeed) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f)); //make everything translucent
		if (timeElapsed < 0) {
			drawStartingTimer();
		} else {
			drawTimer();
			drawBoostBar(currentF, maxF);
			drawLap(lap);
			drawMap(posX, posY);
			drawSpeedometer(playerSpeed);
		}
    }

	private void drawLap(int l){
		g2.setFont(guiFont);
        g2.setPaint(Color.white);
		g2.drawString("Lap " + String.valueOf(l), 50, 50); //positioned near the top left
	}
    
    private void drawBoostBar(double currentF, double maxF){

		int x = 340; //positioned near the top middle
		int y = 23;
		final int height = 57;
		final int maxFill = 226; //width of the boost rectangle when full

		int width = (int)(maxFill * (currentF/maxF));

		g2.setPaint(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.fillRect(x, y, width, height); //
		g2.drawRect(x, y, maxFill, height); //outline for the boostBar

    }
    private void drawTimer() {
		
		int timeMilli = (int)(timeElapsed % 1000)/10;//time shown in milliseconds; divides by 10 to show the first 2 digits rather than all 3
		int timeSec = (int)(timeElapsed/1000 % 60);//time shown in seconds
		int timeMin = (int)(timeElapsed/60000 % 60);

        g2.setFont(guiFont);
        g2.setPaint(Color.white);

		String timeShown = String.format("%02d:%02d:%02d", timeMin, timeSec, timeMilli);
		
        g2.drawString(timeShown, 680, 57); // positioned near the top right
    }	

	private void drawSpeedometer(double speed) { //simple speedometer 
		// int x = 300; //positioned near the bottom right
		// int y = panH - 100;
		// final int MAXHEIGHT = 170;
		// final int WIDTH = 50;

		g2.setFont(guiFont);
		g2.setPaint(Color.white);

		// int height = (int)(MAXHEIGHT * (speed/maxSpeedNormal));

		String speedShown = String.format("%d", (int)(speed*10));
		g2.drawString("Speed: " + speedShown, 566, panH - 45); //positioned near the bottom right
	}

	private void drawMap(double pX, double pY){
		
		// circle player tracker / dot

		int MAX_SIZE = 192; //constrain minimap to 170x170 square

		int mapImgWidth = minimap.getWidth(); 
 		int mapImgHeight = minimap.getHeight(); 
		
		int newMapImgWidth;
		int newMapImgHeight;

		double scaleFactor;

		//one side is always max size
		if (mapImgWidth >= mapImgHeight) {
    		scaleFactor = (double)MAX_SIZE / mapImgWidth;
		} else {
    		scaleFactor = (double)MAX_SIZE / mapImgHeight;
		}

		newMapImgWidth = (int)(mapImgWidth * scaleFactor);
		newMapImgHeight = (int)(mapImgHeight * scaleFactor);
		
		int mapX = 20; //20 margin from left side
		int mapY = panH - newMapImgHeight - 20; //20 margin from bottom
		g2.drawImage(minimap, mapX, mapY, newMapImgWidth, newMapImgHeight, null);

		//draw player dot
		//ground map is 8x the player pos. so ex: 1,1 in player position is 8,8 position in pixels
		//coordinates are swapped here as something goofy with the position object
		int newPosX = (int)((pY*8) * scaleFactor) + mapX; 
		int newPosY = (int)((pX*8) * scaleFactor) + mapY; 
		int dotDiameter = 11;

		g2.setColor(transparentRed);
		g2.fillOval(newPosX - (dotDiameter/2 + 1), newPosY - (dotDiameter/2 + 1), dotDiameter, dotDiameter);	//red dot with center at position relative to minimap
	}

	private void drawStartingTimer() {
		int timeSec = (int)(-timeElapsed/1000 % 60) + 1;//time shown in seconds

        g2.setFont(startingFont);
        

		String timeShown = String.format("%d", timeSec);
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, Renderer.ResolutionWidth, Renderer.ResolutionHeight);
		g2.setPaint(Color.WHITE);
		Rectangle2D timeBounds = startingFont.getStringBounds(timeShown, g2.getFontRenderContext());
        g2.drawString(timeShown, (Renderer.ResolutionWidth - (int)timeBounds.getWidth())/2, (Renderer.ResolutionHeight - (int)timeBounds.getHeight())/2); // positioned near the top right
	
		
	}

}
