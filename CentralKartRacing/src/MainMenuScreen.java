
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class MainMenuScreen extends Screen{

    int panW, panH;

    //BufferedImages for the mute button background
    BufferedImage soundOn, soundOff;
    
    Sound sound = new Sound();

    //selection box variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    String[] choice = {"Play", "Quit", "Mute"};

    Boolean muted = false, previousMuted = false;
    
    MainMenuScreen(Sound sound){
        super("mainMenu.png");
        this.sound = sound;
        soundOn = Renderer.scaleImage(loadImage("soundOpenMainMenu.png"), Renderer.WindowWidth, Renderer.WindowHeight);
        soundOff = Renderer.scaleImage(loadImage("soundClosedMainMenu.png"), Renderer.WindowWidth, Renderer.WindowHeight);
        totalOptions = 3;
        boxX = 710 * Renderer.scalingFactor;
        boxY = 20 * Renderer.scalingFactor;
        boxWidth = 240 * Renderer.scalingFactor;
        boxHeight = 118 * Renderer.scalingFactor;
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
        g2.setColor(new Color(255, 255, 0, 150));
        g2.setStroke(new BasicStroke(3));
        int currentBoxY = boxY + (selectedIndex * spacing);
        if (selectedIndex == 2) {
            g2.drawRect(879 * Renderer.scalingFactor, 461 * Renderer.scalingFactor, 58 * Renderer.scalingFactor, 58 * Renderer.scalingFactor);
            //fill with translucent yellow
            g2.setColor(new Color(255, 255, 0, 50));
            g2.fillRect(879 * Renderer.scalingFactor, 461 * Renderer.scalingFactor, 58 * Renderer.scalingFactor, 58 * Renderer.scalingFactor);
        } else {
            g2.drawRect(boxX, currentBoxY, boxWidth, boxHeight);
            //fill with translucent yellow
            g2.setColor(new Color(255, 255, 0, 50));
            g2.fillRect(boxX, currentBoxY, boxWidth, boxHeight);
        }

        if (muted){
            g2.drawImage(soundOff, 0, 0, null);
        } else {
            g2.drawImage(soundOn, 0, 0, null);
        }
    }


    @Override
    void navigate(int keyCode) {
        switch(keyCode){
            case java.awt.event.KeyEvent.VK_W -> {
                selectedIndex--;
                if (selectedIndex < 0){
                    selectedIndex = totalOptions - 1;
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_S -> {
                selectedIndex++;
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
                    sound.loop();
                    muted = false;
                }   
        }else{
            //exit game
            mainFrame.dispose();
            System.exit(0);

        }
    }

    private BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        try {
            URL url = this.getClass().getResource("/assets/ScreenImages/" + filename);
            image = ImageIO.read(url);
            
            if (image != null) {
                System.out.println("Image loaded successfully!");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Image failed to load: " + filename, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return image;
    }
    
}
