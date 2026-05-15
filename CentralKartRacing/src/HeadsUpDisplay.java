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

		Graphics2D g2;
        HeadsUpDisplay(Graphics2D g2, int panW, int panH, long timeStarted) {
			this.setPreferredSize(new Dimension(panW, panH));
			this.g2 = g2;
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
		drawLap();

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

	private void drawMap(){
		//constrain map image to a square area //maybe another jpanel
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