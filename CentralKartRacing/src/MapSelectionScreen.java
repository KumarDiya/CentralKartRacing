import java.awt.*;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MapSelectionScreen extends Screen{

     //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacingX, spacingY;

    //Leaderboard variables
    int yStart = 80 * Renderer.scalingFactor;
    int xStart = 657 * Renderer.scalingFactor;
    int ySpacing = 34 * Renderer.scalingFactor;
    int rankX = xStart;
    int nameX = rankX + 28 * Renderer.scalingFactor;
    int timeX = nameX + 175 * Renderer.scalingFactor; 

    String[] mapNames = {"Test", "Sunset", "Windows95", "Mall"};
    String[] mapFolders = {"testMap", "sunsetMap", "windows95Map", "mallMap"};

    Font font = new Font("Bahnschrift", Font.BOLD, 20);

    int totalOptionsX, totalOptionsY;
    int selectedIndexX, selectedIndexY;
    
    MapSelectionScreen(){
        super("testMapSelect.png");
        totalOptionsX = 2;
        totalOptionsY = 2;
        selectedIndexX = 0;
        selectedIndexY = 0;
        boxX = 65 * Renderer.scalingFactor;
        boxY = 120 * Renderer.scalingFactor;
        boxWidth = 226 * Renderer.scalingFactor;
        boxHeight = 130 * Renderer.scalingFactor;
        spacingX = 283 * Renderer.scalingFactor;
        spacingY = 170 * Renderer.scalingFactor;
    }


    @Override
    void drawContent(Graphics2D g2) {
        //draw selection box outline
        int currentBoxY = boxY + (selectedIndexY * spacingY);
        int currentBoxX = boxX + (selectedIndexX * spacingX);
        g2.setColor(new Color(255, 255, 0, 150));
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(currentBoxX, currentBoxY, boxWidth, boxHeight);

        drawLeaderBoard(g2, "CentralKartRacing" + "\\" + mapFolders[selectedIndex] + "\\" + "leaderboard.txt");

        //fill with translucent yellow
        g2.setColor(new Color(255, 255, 0, 50));
        g2.fillRect(currentBoxX, currentBoxY, boxWidth, boxHeight);
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
                if (selectedIndexX < 0){
                    selectedIndexX = totalOptionsX - 1; //loop around
                }
                this.repaint();
            }
            case java.awt.event.KeyEvent.VK_D -> {
                selectedIndexX ++;
                if (selectedIndexX > totalOptionsX - 1){
                    selectedIndexX = 0;
                }
                this.repaint();
            }
        }
        selectedIndex = selectedIndexY * totalOptionsY + selectedIndexX;
    }

    @Override
    void confirmSelection() {

        System.out.println("Selected Map: " + mapNames[selectedIndex]);
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        mainFrame.setSelectedMapName(mapNames[selectedIndex]);
        mainFrame.setSelectedMapFolder(mapFolders[selectedIndex]);

        switchScreen("loading");

        new Thread(() -> {
            mainFrame.game.stop();
            mainFrame.mainPanel.remove(mainFrame.game.getRenderer());
            Game newGame = new Game(mapNames[selectedIndex], mapFolders[selectedIndex], MainFrame.getSelectedPlayerIndex());

            SwingUtilities.invokeLater(() -> {
                mainFrame.game = newGame;
                mainFrame.mainPanel.add(mainFrame.game.getRenderer(), "game");
                mainFrame.mainPanel.revalidate();
                mainFrame.mainPanel.repaint();
                mainFrame.switchToScreen("game");
            });
        }).start();
        
    }

    private void drawLeaderBoard(Graphics2D g2, String leaderboardTXT) {
        //load data from file
        File data = new File(leaderboardTXT);
		FileReader in;
		BufferedReader readFile;
		String line;

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> times = new ArrayList<>();
        
        final int MaxEntries = 14;
        
		
		try {
			in = new FileReader(data);
			readFile = new BufferedReader(in);
			
			while((line = readFile.readLine())!=null && names.size() < MaxEntries) {
                String[] dataParts = line.split(" ");
                
                names.add(dataParts[0]);
                times.add(Integer.parseInt(dataParts[1]));
				
			}

		readFile.close();	
		in.close();	
		}catch(IOException e) {
			System.out.println("Problem with reading file " + e.getMessage());
		}

        //display leaderboard
        g2.setFont(font);
        g2.setColor(Color.yellow);

        g2.drawString(mapNames[selectedIndex] + " Leaderboard", xStart, yStart - ySpacing);

        g2.setColor(Color.white);
        for (int i = 0; i < names.size(); i++) {
            if (i >= MaxEntries){
                break;
            }

            String name = names.get(i);
            int timeRaw = times.get(i);

            g2.setColor(Color.white);
            
            int timeMilli = (timeRaw % 1000)/10;//time shown in milliseconds; divides by 10 to show the first 2 digits rather than all 3
		    int timeSec = (timeRaw/1000 % 60);//time shown in seconds
		    int timeMin = (timeRaw/60000 % 60);

            String timeShown = String.format("%02d:%02d:%02d", timeMin, timeSec, timeMilli);

            g2.drawString((i + 1) + ".", rankX, yStart + (i * ySpacing));
            g2.drawString(name, nameX, yStart + (i * ySpacing));
            g2.setColor(Color.pink);
            g2.drawString(timeShown, timeX, yStart + (i * ySpacing));
            
        }
    }
}
