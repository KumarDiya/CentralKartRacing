import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer extends JFrame{
    public static DrawingPanel panel;
    public static BufferedImage frame;
    public static int width;
    public static int height;

    public Renderer () {
        panel = new DrawingPanel();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.add(panel);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        width = this.getWidth();
        height = this.getHeight();
        frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void render(Map m, Player p){
        
    }

    public void drawFrame(BufferedImage frame) {
        panel.repaint();
    }

    private class DrawingPanel extends JPanel {
        DrawingPanel() {}

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(frame, 0, 0, null);
        }
    }
}
