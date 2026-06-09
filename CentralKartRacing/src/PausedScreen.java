
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;


public class PausedScreen extends Screen{

    //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    //array storing player names
    String[] choice = {"Resume", "Quit"};

    int panW, panH;
    
    /**
     * constructor
     */
    PausedScreen(){
        super("testPaused.png");
        totalOptions = 2;
        boxX = 375 * Renderer.scalingFactor;
        boxY = 220 * Renderer.scalingFactor;
        boxWidth = 100 * Renderer.scalingFactor;
        boxHeight = 30 * Renderer.scalingFactor;
        spacing = 67 * Renderer.scalingFactor;

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PausedScreen got key: " + e.getKeyCode());  // DEBUG
        super.keyPressed(e);  // Call parent
    }
    @Override
    void drawContent(Graphics2D g2) {
        //draw selection box outline
        int currentBoxY = boxY + (selectedIndex * spacing);
        g2.setColor(new Color(255, 255, 0, 150));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(boxX, currentBoxY, boxWidth, boxHeight);

        //fill with translucent yellow
        g2.setColor(new Color(255, 255, 0, 50));
        g2.fillRect(boxX, currentBoxY, boxWidth, boxHeight);
    }

    @Override
    void navigate(int keyCode) {
        switch(keyCode){
            case java.awt.event.KeyEvent.VK_W -> {
                selectedIndex --;
                if (selectedIndex < 0){
                    selectedIndex = totalOptions - 1;
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_S -> {
                selectedIndex ++;
                if (selectedIndex > totalOptions - 1){
                    selectedIndex = 0;
                }
                this.repaint();
            }
        }
    }


    @Override
    void confirmSelection() {
        System.out.println("Choice: " + choice[selectedIndex]);
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);

        if (choice[selectedIndex].equals( "Quit")){
            Game game = mainFrame.getGame();
            game.stop();
            game.getRenderer().paused = false;
            //need to restart the game (add code later)
            switchScreen("main menu");
            
        }else{
            Game game = mainFrame.getGame();
            game.getRenderer().paused = false;//change to unpaused state
            switchScreen("game");
        }
        
    }
}
