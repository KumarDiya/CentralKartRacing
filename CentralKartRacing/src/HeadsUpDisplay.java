import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class HeadsUpDisplay extends JPanel{
    //everything will be drawn on a jPanel that is on top of the main game 
        
	long timeElapsed;
	
	int panH;
	Color transparentWhite = new Color(255, 255, 255, 200); //used for the player dot 
	Font guiFont = new Font("Bahnschrift", Font.BOLD, (int)(40 * Renderer.scalingFactor));
	Font startingFont = new Font("Bahnschrift", Font.BOLD, (int)(70 * Renderer.scalingFactor));

	final int[][] TutorialScreenImgLocations;

	double maxSpeedNormal;

	BufferedImage tutorial1, tutorial2, tutorial3, tutorial4;

	Graphics2D g2;

	Map map;

	//Boost Bar Graphics
	final int y = 23 * Renderer.scalingFactor;
	final int height = 57 * Renderer.scalingFactor;
	final int maxFill = 226 * Renderer.scalingFactor; //width of the boost rectangle when full

	//Minimap Graphics
	BufferedImage  minimap;
	int MAX_SIZE = 192 * Renderer.scalingFactor; //constrain minimap to 170x170 square
	final int mapX, mapY;
	final double scaleFactor;

	Sound startSound = new Sound();
	

	HeadsUpDisplay(int panW, int panH, Map map) {
		this.map = map;

		if (map.getName() == "WindowsXP") minimap = loadImage("fakeGroundTexture.png");
		else minimap = loadImage("groundTexture.png");
		
		int mapImgWidth = minimap.getWidth();
		int mapImgHeight = minimap.getHeight();
		int newMapImgWidth;
		int newMapImgHeight;

		//one side is always max size
		if (mapImgWidth >= mapImgHeight) {
			scaleFactor = (double)MAX_SIZE / mapImgWidth;
		} else {
			scaleFactor = (double)MAX_SIZE / mapImgHeight;
		} 
		newMapImgWidth = (int)(mapImgWidth * scaleFactor);
		newMapImgHeight = (int)(mapImgHeight * scaleFactor);
		
		mapX = 20 * Renderer.scalingFactor; //20 margin from left side
		mapY = (panH - newMapImgHeight - 20 * Renderer.scalingFactor); //20 margin from bottom

		minimap = scaleImage(minimap, newMapImgWidth, newMapImgHeight);

		if (map.getName() == "Tutorial") {
			tutorial1 = scaleImage(loadImage("tutorialHudPopups/tutorial1.png"), 351 * Renderer.scalingFactor, 162 * Renderer.scalingFactor);
			tutorial2 = scaleImage(loadImage("tutorialHudPopups/tutorial2.png"), 428 * Renderer.scalingFactor, 198 * Renderer.scalingFactor);
			tutorial3 = scaleImage(loadImage("tutorialHudPopups/tutorial3.png"), 391 * Renderer.scalingFactor, 171 * Renderer.scalingFactor);
			tutorial4 = scaleImage(loadImage("tutorialHudPopups/tutorial4.png"), 394 * Renderer.scalingFactor, 156 * Renderer.scalingFactor);
		} else {
			tutorial1 = tutorial2 = tutorial3 = tutorial4 = null;
		}

		int[][] tempTutorialScreenImgLocations = {{54 * Renderer.scalingFactor, 14 * Renderer.scalingFactor, 36 * Renderer.scalingFactor, 40 * Renderer.scalingFactor}, {108 * Renderer.scalingFactor, 91 * Renderer.scalingFactor, 107 * Renderer.scalingFactor, 86 * Renderer.scalingFactor}};
		TutorialScreenImgLocations = tempTutorialScreenImgLocations;

		startSound.setFile(4);

		this.setPreferredSize(new Dimension(panW, panH));
		this.panH = panH;
		this.setOpaque(false);//enable transparency
	}

	private BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        try {
            URL file = this.getClass().getResource(map.getMapResourcePath() + filename);
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

	public void drawTutorialHUD(int lap, int checkpoint, double posX, double posY, double currentF, double maxF, double playerSpeed) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f)); //make everything translucent
		if (timeElapsed < 0) {
			drawStartingTimer();
		} else {
			drawTimer();
			drawBoostBar(currentF, maxF);
			drawLap(lap);
			drawMap(posX, posY);
			drawSpeedometer(playerSpeed);
			drawTutorial(checkpoint);
		}
    }

	public void drawTutorial(int checkpoint){
		switch(checkpoint){
			case 0: 
				g2.drawImage(tutorial1, TutorialScreenImgLocations[0][0], TutorialScreenImgLocations[1][0], null);
				break;
			case 1: 
				g2.drawImage(tutorial2, TutorialScreenImgLocations[0][1], TutorialScreenImgLocations[1][1], null);
				break;
			case 2: 
				g2.drawImage(tutorial3, TutorialScreenImgLocations[0][2], TutorialScreenImgLocations[1][2], null);
				break;
			case 3: 
				g2.drawImage(tutorial4, TutorialScreenImgLocations[0][3], TutorialScreenImgLocations[1][3], null);
				break;
		}
	}

	private void drawLap(int l){
		g2.setFont(guiFont);
        g2.setPaint(Color.white);
		g2.drawString("Lap " + String.valueOf(l), (int)(50 * Renderer.scalingFactor), (int)(60 * Renderer.scalingFactor)); //positioned near the top left
	}
    
    private void drawBoostBar(double currentF, double maxF){
		int x = (int)((Renderer.WindowWidth - maxFill)/2); //positioned at the top middle

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
		
        g2.drawString(timeShown, 740*Renderer.scalingFactor, 60*Renderer.scalingFactor); // positioned near the top right
    }	

	private void drawSpeedometer(double speed) { //simple speedometer 

		g2.setFont(guiFont);
		g2.setPaint(Color.white);

		// int height = (int)(MAXHEIGHT * (speed/maxSpeedNormal));

		String speedShown = String.format("%d", (int)(speed*10));
		g2.drawString("Speed: " + speedShown, 680*Renderer.scalingFactor, (panH - 70)); //positioned near the bottom right
	}

	private void drawMap(double pX, double pY){
		
		// circle player tracker / dot
		g2.drawImage(minimap, mapX, mapY, null);

		//draw player dot
		//ground map is 8x the player pos. so ex: 1,1 in player position is 8,8 position in pixels
		//coordinates are swapped here as something goofy with the position object
		int newPosX = (int)((pY * 8) * scaleFactor) + mapX; 
		int newPosY = (int)((pX * 8) * scaleFactor) + mapY; 
		int dotDiameter = 12 * Renderer.scalingFactor;

		g2.setColor(transparentWhite);
		g2.fillOval(newPosX - (dotDiameter/2 + 1), newPosY - (dotDiameter/2 + 1), dotDiameter, dotDiameter);	//red dot with center at position relative to minimap
	}

	private void drawStartingTimer() {
		int timeSec = (int)(-timeElapsed/1000 % 60) + 1;//time shown in seconds

        g2.setFont(startingFont);

		//play 321 go sound
        if (timeSec <= 3) startSound.play();

		String timeShown = String.format("%d", timeSec);
		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, Renderer.ResolutionWidth * Renderer.scalingFactor, Renderer.ResolutionHeight * Renderer.scalingFactor);
		g2.setPaint(Color.WHITE);
		Rectangle2D timeBounds = startingFont.getStringBounds(timeShown, g2.getFontRenderContext());
        g2.drawString(timeShown, (Renderer.ResolutionWidth * Renderer.scalingFactor - (int)timeBounds.getWidth())/2, (Renderer.ResolutionHeight * Renderer.scalingFactor - (int)timeBounds.getHeight())/2); // positioned near the top right
	
		
	}

	public static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        
        Graphics2D g2d = resizedImage.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        
        g2d.dispose();
        
        return resizedImage;
    }

}