
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

public class MainMenuScreen extends Screen{

    int panW, panH;

    //BufferedImage for intro background
    BufferedImage mainMenuImg;

    Sound sound = new Sound();

    //selection box variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    String[] choice = {"Play", "Mute", "Quit"};

    Boolean muted = false;
    
    MainMenuScreen(Sound sound){
        super("testMainMenu.png");
        this.sound = sound;
        totalOptions = 3;
        boxX = 708 * Renderer.scalingFactor;
        boxY = 20 * Renderer.scalingFactor;
        boxWidth = 243 * Renderer.scalingFactor;
        boxHeight = 119 * Renderer.scalingFactor;
        spacing = 138 * Renderer.scalingFactor;
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
        if (choice[selectedIndex].equals( "Play")){
            switchScreen("player selection");
        }else if (choice[selectedIndex].equals("Mute")){
            //mute/unmute music
                System.out.println("toggle");
                if (!muted){
                    sound.stop();
                    muted = true;
                }else if (muted){
                    sound.play();
                    muted = false;
                }   
        }else{
            //exit game
            mainFrame.dispose();
            System.exit(0);

        }
    }


    
}
