
import java.awt.*;
import javax.swing.SwingUtilities;


public class PlayerSelectionScreen extends Screen{

    //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    //array storing player names
    String[] playerNames = {"Jeff", "Po", "Blonde Guy"};

    int panW, panH;
    
    PlayerSelectionScreen(){
        super("testPlayerSelect.png");
        totalOptions = 3;
        boxX = 0 * Renderer.scalingFactor;
        boxY = 140 * Renderer.scalingFactor;
        boxWidth = 110 * Renderer.scalingFactor;
        boxHeight = 300 * Renderer.scalingFactor;
        spacing = 350 * Renderer.scalingFactor;
    }


    @Override
    void drawContent(Graphics2D g2) {
        //draw selection box outline
        int currentBoxX = boxX + boxWidth + (selectedIndex * spacing);
        g2.setColor(new Color(255, 255, 0, 150));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(currentBoxX, boxY, boxWidth, boxHeight);

        //fill with translucent yellow
        g2.setColor(new Color(255, 255, 0, 50));
        g2.fillRect(currentBoxX, boxY, boxWidth, boxHeight);
    }

    @Override
    void navigate(int keyCode) {
        switch(keyCode){
            case java.awt.event.KeyEvent.VK_A -> {
                selectedIndex --;
                if (selectedIndex < 0){
                    selectedIndex = totalOptions - 1;
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_D -> {
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
        System.out.println("Selected player: " + playerNames[selectedIndex]);

        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        mainFrame.setSelectedPlayer(playerNames[selectedIndex]);
        switchScreen("map selection");
    }
}