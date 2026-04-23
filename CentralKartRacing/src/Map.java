import java.io.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Map {
    private String mapFolder;
    private String name;

    private int mapWidth;
    private int mapHeight;

    public int[][] wallMap;
    public int[][] groundMap;

    public Texture groundTexture;
    public Texture skyTexture;

    public Texture[] wallTextures;
    public Texture[] spriteTextures;

    private int numSprites;

    public Sprite[] sprites;

    final int groundMapScale = 8;
    final String wallMapFile = "wallMap.txt";
    final String groundMapFile = "groundMap.png";
    final String spriteMapFile = "spriteMap.txt";

    public Map (String name, String mapFolder){
        this.name = name;
        this.mapFolder = "CentralKartRacing\\" + mapFolder + "\\";
        loadWallMap();
        loadGroundMap();
    }

    

    private void loadWallMap() {
        File wallMapPath = new File(mapFolder + wallMapFile);
        try {
            FileReader r = new FileReader(wallMapPath);
            BufferedReader reader = new BufferedReader(r);
            mapWidth = reader.readLine().length();
            mapHeight = 1;
            while (reader.readLine() != null){
                mapHeight++;
            }
            reader.close();
            r.close();
            r = new FileReader(wallMapPath);
            reader = new BufferedReader(r);
            wallMap = new int[mapWidth][mapHeight];
            for (int x = 0; x < mapWidth; x++){
                for (int y = 0; y < mapHeight; y++){
                    wallMap[x][y] = reader.read() - 48;
                }
                reader.readLine();
            }
            reader.close();
            r.close();

        } catch (IOException e) {
            System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } 
    }

    private void loadGroundMap () {
        File groundMapPath = new File(mapFolder + groundMapFile);
        BufferedImage groundMapImage;
        try {
            groundMapImage = ImageIO.read(groundMapPath);
            if (groundMapImage.getWidth() != mapWidth * groundMapScale || groundMapImage.getHeight() != mapHeight * groundMapScale) {
                System.out.printf("Ground Map size: %d x %d\nMap size: %d x %d\n", groundMapImage.getWidth(), groundMapImage.getHeight(), mapWidth, mapHeight);
                throw new GroundMapSizeException();
            }
            groundMap = new int[mapWidth * groundMapScale][mapHeight * groundMapScale];
            for (int x = 0; x < groundMap.length; x++) {
                for (int y = 0; y < groundMap[0].length; y++) {
                    if (groundMapImage.getRGB(x, y) == 0){
                        groundMap[x][y] = 0;
                    } else {
                        groundMap[x][y] = 1;
                    }
                }
            }

        } catch (IOException e) {
            System.out.printf("An error loading the groundMap for the map \"%s\" occurred.\n", name);
        } catch (GroundMapSizeException e) {
            System.out.printf("The groundMap file is the wrong size for the map \"%s\".\n", name);
        }
    }

    private void loadSprites (String spritesFile) {
        File spriteMapPath = new File(mapFolder + spriteMapFile);
        try {
            FileReader r = new FileReader(spriteMapPath);
            BufferedReader reader = new BufferedReader(r);
            numSprites = 0;
            while (reader.readLine() != null){
                numSprites++;
            }
            reader.close();
            r.close();
            r = new FileReader(spriteMapPath);
            reader = new BufferedReader(r);
            wallMap = new int[mapWidth][mapHeight];
            for (int x = 0; x < mapWidth; x++){
                for (int y = 0; y < mapHeight; y++){
                    wallMap[x][y] = reader.read() - 48;
                }
                reader.readLine();
            }
            reader.close();
            r.close();

        } catch (IOException e) {
            System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } 

    }

    class GroundMapSizeException extends Exception {
        public GroundMapSizeException() {}
    }

}

