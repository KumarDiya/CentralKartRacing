
import java.awt.CardLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame{
    
    //main parent panel
    JPanel mainPanel;

    //main menu screen
    MainMenuScreen mainMenuScreen;

    //character selection screen
    PlayerSelectionScreen playerSelectionScreen;

    //map selection screen
    MapSelectionScreen mapSelectionScreen;

    //game screen
    Game game;

    //paused screen
    PausedScreen pausedScreen;

    //card layout object
    CardLayout cardLayout;

    //keep track of current screen
    String currentScreen;

    //keep track of selected player
    private String selectedPlayer;


    public static void main (String[]args){

        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                new MainFrame();
            }
        });
    }

    /**
     * constructor
     */
    MainFrame(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        this.add(mainPanel);

        //create instance of and add main menu screen
        mainMenuScreen = new MainMenuScreen();
        mainPanel.add(mainMenuScreen, "main menu");
        currentScreen = "main menu";

        //create instance of and add player selection screen
        playerSelectionScreen = new PlayerSelectionScreen();
        mainPanel.add(playerSelectionScreen, "player selection");

        //create instance of and add map selection screen
        mapSelectionScreen = new MapSelectionScreen();
        mainPanel.add(mapSelectionScreen, "map selection");

        //create instance of and add renderer screen
        game = new Game();
        mainPanel.add(game.getRenderer(), "game");

        pausedScreen = new PausedScreen();
        mainPanel.add(pausedScreen, "pause");

        this.pack();

        this.setVisible(true);

    }

    /**
     * set selected player
     * @param player   the player that is selected by the user
     */
    public void setSelectedPlayer(String player){
        this.selectedPlayer = player;
    }

    /**
     * get the selected player
     * @return   the player that is selected by the user
     */
    public String getSelectedPlayer(){
        return this.selectedPlayer;
    }

    /**
     * get the current displayed screen in the cardLayout
     * @return
     */
    public String getCurrentScreen(){
        return currentScreen;
    }

    public Game getGame() {
        return game;
    }

    /**
     * switches the screen in front in the cardLayout
     * @param screenName   the name of the screen to switch to
     */
    public void switchToScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
        currentScreen = screenName;

        //note to self for later: remember to request focus for each screen!

        switch(screenName){
            case "main menu" -> {
                mainMenuScreen.requestFocusInWindow();
                game.getRenderer().setFocusable(false);
            }
            case "player selection" -> {
                playerSelectionScreen.requestFocusInWindow();
                game.getRenderer().setFocusable(false);
            }
            case "map selection" -> {
                mapSelectionScreen.requestFocusInWindow();
                game.getRenderer().setFocusable(false);
            }
            case "game" -> {
                game.setPlayerCharacter(selectedPlayer); //call method to set player, which determines the displayed player image
                //I don't know the different between these focus methods so I'll add all three to TRIPLE make sure it is in focus
                game.getRenderer().requestFocusInWindow();
                game.getRenderer().setFocusable(true);
                game.getRenderer().requestFocus();
                //start the game!
                game.start();
            }
            case "pause" -> {
                System.out.println("Switching to paused screen");
                pausedScreen.setFocusable(true);
                pausedScreen.requestFocus();
                pausedScreen.requestFocusInWindow();
                game.getRenderer().setFocusable(false);
            }

            
        }
        
        
    }
    


}
