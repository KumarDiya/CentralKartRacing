
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;


public class RaceFinishScreen extends Screen{
    String userName;

    //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    int totalOptionsX, totalOptionsY;

    int selectedIndexX, selectedIndexY;

    //the keyboard
    String[][] choice = {
    {"A", "B", "C", "D", "E", "F", "G"},
    {"H", "I", "J", "K", "L", "M", "N"},
    {"O", "P", "Q", "R", "S", "T", "U"},
    {"V", "W", "X", "Y", "Z", "ENTER", "ENTER"}
    };
    
    /**
     * constructor
     */
    RaceFinishScreen(){
        super("");
        totalOptionsX = 7;
        totalOptionsY = 4;
        boxX = 375; //the box location
        boxY = 220;
        boxWidth = 50;
        boxHeight = 50;
        spacing = 40;
        selectedIndexX = selectedIndexY = 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PausedScreen got key: " + e.getKeyCode());  // DEBUG
        super.keyPressed(e);  // Call parent
    }
    @Override
    void drawContent(Graphics2D g2) { //draws selection box
        //draw selection box outline
        int currentBoxY = boxY + (selectedIndexY * spacing);
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
                if (selectedIndexX > totalOptionsX - 1){
                    selectedIndexX = 0;
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_D -> {
                selectedIndexX --;
                if (selectedIndexX < 0){
                    selectedIndexX = totalOptionsX - 1;
                }
                this.repaint();
            }
        }
    }


    @Override
    void confirmSelection() {
        System.out.println("Choice: " + choice[selectedIndexX][selectedIndexY]);
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);

        if (choice[selectedIndexX][selectedIndexY].equals("ENTER")){
            Game game = mainFrame.getGame();
            game.stop();
            game.getRenderer().paused = false;
            switchScreen("main menu");
            
        }
        
    }
}
