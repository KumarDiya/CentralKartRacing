import java.awt.*;
import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapSelectionScreen extends Screen{

     //selection variables
    int boxX, boxY, boxWidth, boxHeight, spacing;

    String[] mapNames = {"Test"};
    String[] mapFolders = {"testMap"};

    Font font = new Font("Bahnschrift", Font.BOLD, 20);
    
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

        drawLeaderBoard(g2, "CentralKartRacing" + "\\" + mapFolders[selectedIndex] + "\\" + "leaderboard.txt");

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
        MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
        mainFrame.setSelectedMapName(mapNames[selectedIndex]);
        mainFrame.setSelectedMapFolder(mapFolders[selectedIndex]);

        switchScreen("loading");

        new Thread(() -> {
            mainFrame.game.stop();
            mainFrame.mainPanel.remove(mainFrame.game.getRenderer());
            Game newGame = new Game(mapNames[selectedIndex], mapFolders[selectedIndex]);

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
        
        final int MAXENTRIES = 14;
        
		
		try {
			in = new FileReader(data);
			readFile = new BufferedReader(in);
			
			while((line = readFile.readLine())!=null && names.size() < MAXENTRIES) {
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

        int yStart = 70;
        int xStart = 580;
        int ySpacing = 30;

        g2.drawString(mapNames[selectedIndex] + " Leaderboard", xStart, yStart - ySpacing);

        g2.setColor(Color.white);
        for (int i = 0; i < names.size(); i++) {
            if (i >= MAXENTRIES){
                break;
            }

            String name = names.get(i);
            int timeRaw = times.get(i);

            g2.setColor(Color.white);
            
            int timeMilli = (timeRaw % 1000)/10;//time shown in milliseconds; divides by 10 to show the first 2 digits rather than all 3
		    int timeSec = (timeRaw/1000 % 60);//time shown in seconds
		    int timeMin = (timeRaw/60000 % 60);

            String timeShown = String.format("%02d:%02d:%02d", timeMin, timeSec, timeMilli);


            int rankX = xStart;
            int nameX = rankX + 25;
            int timeX = nameX + 155; 

            g2.drawString((i + 1) + ".", rankX, yStart + (i * ySpacing));
            g2.drawString(name, nameX, yStart + (i * ySpacing));
            g2.setColor(Color.pink);
            g2.drawString(timeShown, timeX, yStart + (i * ySpacing));
            
        }
        



    }
}
