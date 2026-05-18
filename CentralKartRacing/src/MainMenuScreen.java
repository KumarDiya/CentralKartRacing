
import java.awt.*;
import java.awt.image.BufferedImage;

public class MainMenuScreen extends Screen{

    int panW, panH;
    //BufferedImage for intro background
    BufferedImage mainMenuImg;
    
    MainMenuScreen(){
        super(  "testMainMenu.PNG");
        totalOptions = 1;
    }

    

    @Override
    void drawContent(Graphics2D g2) {
        
    }

    @Override
    void navigate(int keyCode) {
    }

    @Override
    void confirmSelection() {
        switchScreen("player selection");
    }
}
