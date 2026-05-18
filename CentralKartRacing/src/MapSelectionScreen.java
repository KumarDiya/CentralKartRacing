import java.awt.*;
public class MapSelectionScreen extends Screen{

     //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    String[] mapNames = {"Test Map"};
    
    MapSelectionScreen(){
        super("testMapSelect.png");
        totalOptions = 1;
        boxX = 57;
        boxY = 106;
        boxWidth = 200;
        boxHeight = 115;
        spacing = 250;
    }


    @Override
    void drawContent(Graphics2D g2) {
         //draw selection box outline
        int currentBoxX = boxX + (selectedIndex * spacing);
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
        System.out.println("Selected Map: " + mapNames[selectedIndex]);
        switchScreen("game");
    }
}
