import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public abstract class Screen extends JPanel implements KeyListener{

    //each screen will have its own KeyListener
    //each screen will utilize WASD as a "joystick" and u for confirmation/switch to next screen

    //the image to store background image and its corresponding filename
    BufferedImage bgImg;
    int panW, panH;

    //each screen will have a selected index in the array of options, and the total number of options
    int selectedIndex, totalOptions;
    

    /**
     * constructor
     * @param bgImgFileName   the file name of the background image
     */
    public Screen(String bgImgFileName){
        bgImg = loadImage(bgImgFileName);
        this.setSize(new Dimension(panW, panH));
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        //draw background image
        if (bgImg != null) {
            g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), null);
        }
        drawContent(g2);

    }

    //each screen must implement the following abstract methods: drawContent, navigate, confirmSelection
    abstract void drawContent(Graphics2D g2);
    abstract void navigate(int keyCode);
    abstract void confirmSelection();


    private BufferedImage loadImage(String filename) {
        BufferedImage image = null;
        try {
            
            File file = new File("CentralKartRacing\\testScreenImages\\" + filename);
            
            image = ImageIO.read(file);
            
            if (image != null) {
                System.out.println("Image loaded successfully!");
            }
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Image failed to load: " + filename, "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return image;
    }


    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D -> {
                //move whatever the screen desires, then repaint
                navigate(key);
                repaint();
            }
            case KeyEvent.VK_U -> {
                //AKA switch the screen, then repaint
                confirmSelection();
                repaint();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * switches the screen to the next
     * @param screenName   the screen to switch to
     */
    public void switchScreen(String screenName){
        MainFrame mainFrame = (MainFrame)SwingUtilities.getWindowAncestor(this);
        mainFrame.switchToScreen(screenName);
    }

    
}
