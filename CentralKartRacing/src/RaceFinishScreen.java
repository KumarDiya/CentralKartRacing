
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;


public class RaceFinishScreen extends Screen{

    String userName;

    //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacingX, spacingY;

    int totalOptionsX, totalOptionsY;

    int selectedIndexX, selectedIndexY;

    Font font = new Font("Bahnschrift", Font.BOLD, 60);

    //the keyboard
    String[][] choice = {
    {"A", "B", "C", "D", "E", "F", "G"},
    {"H", "I", "J", "K", "L", "M", "N"},
    {"O", "P", "Q", "R", "S", "T", "U"},
    {"V", "W", "X", "Y", "Z", "BACKSPACE", "ENTER"}
    };   
    
    /**
     * constructor
     */
    RaceFinishScreen(){
        super("finishScreen.png");
        totalOptionsX = 7;
        totalOptionsY = 4;
        boxX = 182 * Renderer.scalingFactor; //the box location
        boxY = 271 * Renderer.scalingFactor;
        boxWidth = 68 * Renderer.scalingFactor;
        boxHeight = 68 * Renderer.scalingFactor;
        spacingY = 65 * Renderer.scalingFactor;
        spacingX = 85 * Renderer.scalingFactor;
        selectedIndexX = selectedIndexY = 0;
        userName = "";
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PausedScreen got key: " + e.getKeyCode());  // DEBUG
        super.keyPressed(e);  // Call parent
    }
    @Override
    void drawContent(Graphics2D g2) { //draws selection box
        //draw selection box outline
        int currentBoxY = boxY + (selectedIndexY * spacingY);
        int currentBoxX = boxX + (selectedIndexX * spacingX);
        g2.setColor(new Color(255, 255, 0, 150));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(currentBoxX, currentBoxY, boxWidth, boxHeight);

        //fill with translucent yellow
        g2.setColor(new Color(255, 255, 0, 50));
        g2.fillRect(currentBoxX, currentBoxY, boxWidth, boxHeight);

        //draw the player's input
        g2.setColor(Color.WHITE);
        g2.setFont(font);
        g2.drawString("Name: " + userName, 57, 226);

        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        Game game = mainFrame.getGame();
        
        long timeElapsed = game.getFinishTime();
        int timeMilli = (int)(timeElapsed % 1000)/10;//time shown in milliseconds; divides by 10 to show the first 2 digits rather than all 3
		int timeSec = (int)(timeElapsed/1000 % 60);//time shown in seconds
		int timeMin = (int)(timeElapsed/60000 % 60);

		String timeShown = String.format("Time: " + "%02d:%02d:%02d", timeMin, timeSec, timeMilli);
		
        g2.drawString(timeShown, 57, 85);
    }

    @Override
    void navigate(int keyCode) {
        switch(keyCode){
            case java.awt.event.KeyEvent.VK_W -> {
                selectedIndexY --;
                if (selectedIndexY < 0){
                    selectedIndexY = totalOptionsY - 1;
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_S -> {
                selectedIndexY ++;
                if (selectedIndexY > totalOptionsY - 1){
                    selectedIndexY = 0;
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_A -> {
                selectedIndexX --;
                if (selectedIndexX < 0){
                    selectedIndexX = totalOptionsX - 1; //loop around
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_D -> {
                selectedIndexX ++;
                if (selectedIndexX > totalOptionsX - 1){
                    selectedIndexX = 0;
                }
                this.repaint();
            }
        }
    }


    @Override
    void confirmSelection() {
        System.out.println("Choice: " + choice[selectedIndexY][selectedIndexX]);
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);

        if (choice[selectedIndexY][selectedIndexX].equals("ENTER")){
            Game game = mainFrame.getGame();
            game.stop();
            if (userName.equals("")) userName = "ANONYMOUS";
            game.logFinish(userName);
            game.getRenderer().paused = false;
            switchScreen("main menu"); //go back to main menu
            userName = "";
            boxX = 182 * Renderer.scalingFactor; 
            boxY = 272 * Renderer.scalingFactor;
            
        } else if (choice[selectedIndexY][selectedIndexX].equals("BACKSPACE")){
            if (userName.length() > 0){
                userName = userName.substring(0, userName.length() - 1); //subtract last letter
            }
        } else if (userName.length() >= 10){
            return; //keeps max length at 10
        } else {
            userName += choice[selectedIndexY][selectedIndexX]; //add the letter to the name
        }
    }
}
