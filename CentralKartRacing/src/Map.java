import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Map {
    private final String mapFolder; //The folder all map files are found in.
    private final String name;

    private int mapWidth;
    private int mapHeight;

    private Vector startPosition;
    private Vector startDirection;

    public int[][] wallMap; //The map determining the location of walls.
    public int[][] groundMap; //The map determining the ground materials; this will be an integer multiple of wallMap, determined by groundMapScale.
    final int groundMapScale = 8; //The upscale factor of groundMap to wallMap.

    private int numSprites; //The number of sprites.
    public Sprite[] sprites; //The sprites used in the level.
    private int numSpriteCollisions;
    public CollisionBox[] spriteCollisions;

    public Texture groundTexture; //The original texture used for the ground.
    public Texture groundTextureInUse; //The texture after being modified by drifting.
    public HashMap<Vector, Integer> darkeningValues = new HashMap<Vector, Integer>(); //The groundTexture positions to modify, and by how much.

    final int groundTextureScale = 8;
    public Texture skyTexture; //The texture used for the skybox. The theoretical ideal texture size should be 3447px by resolutionWidth/2.
    final int skyTextureWidth = 3447, skyTextureHeight = 250;

    public Texture[] wallTextures; //The textures of the walls, index determined by order of placement in wallTextures.txt.
    public Texture[] spriteTextures; //The textures of sprites, index determined by order of placement in spriteTextures.txt.
    private int numCheckpoints;
    public CollisionBox[] checkpoints; //The checkpoints of the map, used for lap determination.

    //Constants used for file access. 
    private final String wallMapFile = "wallMap.txt";
    private final String groundMapFile = "groundMap.png";
    private final String spriteMapFile = "spriteMap.txt";
    private final String wallTexturesFile = "wallTextures.txt";
    private final String groundTextureFile = "groundTexture.png";
    private final String skyTextureFile = "skyTexture.png";
    private final String spriteTexturesFile = "spriteTextures.txt";
    private final String checkpointsFile = "checkpoints.txt";
    private final String leaderboardFile = "leaderboard.txt";
    private final String startInfoFile = "startingInfo.txt";

    private final String wallTextureFolder = "wallTextures";
    private final String spriteTextureFolder = "spriteTextures";

    //Constants used for groundMap color setting
    private final int WallColor = new Color(0, 0, 0).getRGB();
    private final int RoadColor = new Color(28, 27, 27).getRGB();
    private final int GrassColor = new Color(86, 147, 64).getRGB();
    private final int SandColor = new Color(200, 196, 121).getRGB();

    /**
     * Map constructor.
     * @param name      The name of the map.
     * @param mapFolder The folder the map is located in.
     */
    public Map (String name, String mapFolder){
        this.name = name;
        this.mapFolder = "CentralKartRacing\\" + mapFolder + "\\";
        loadWallMap();
        loadGroundMap();
        loadSpriteMap();
        loadWallTextures();
        loadGroundTexture();
        loadSkyTexture();
        loadSpriteTextures();
        loadCheckpoints();
        loadStartingInfo();
    }

    public String getMapFolder() {
        return mapFolder;
    }

    /**
     * Gets the width of the map.
     * @return  The width of the map.
     */
    public int getWidth() {
        return mapWidth;
    }

    /**
     * Gets the height of the map.
     * @return  The height of the map.
     */
    public int getHeight() {
        return mapHeight;
    }

    public Vector getStartingPos() {
        return startPosition;
    }

    public Vector getStartingDir() {
        return startDirection;
    }

    public int getNumSprites() {
        return sprites.length;
    }

    public int getNumSpriteCollisions() {
        return spriteCollisions.length;
    }

    public int getNumCheckpoints() {
        return numCheckpoints;
    }

    public void modifyGroundTexture(int timeElapsed) {
        ArrayList<Vector> toBeRemoved = new ArrayList<Vector>();
        for (Vector darkenedDriftPosition : darkeningValues.keySet()) {
            int lifetime = darkeningValues.get(darkenedDriftPosition);
            VectorInt groundTexturePositions = new VectorInt((int)(darkenedDriftPosition.y * groundTextureScale), (int)(darkenedDriftPosition.x * groundTextureScale));
            if (lifetime == 0) {
                groundTextureInUse.texture[groundTexturePositions.x][groundTexturePositions.y] = (groundTexture.texture[groundTexturePositions.x][groundTexturePositions.y] >> 1) & Renderer.DarkerNumber;
            } else if (lifetime + timeElapsed > 5000) {
                groundTextureInUse.texture[groundTexturePositions.x][groundTexturePositions.y] = groundTexture.texture[groundTexturePositions.x][groundTexturePositions.y];
                toBeRemoved.add(darkenedDriftPosition);
            }
            darkeningValues.replace(darkenedDriftPosition, lifetime + timeElapsed);
        }
        for (Vector toRemove : toBeRemoved) {
            darkeningValues.remove(toRemove);
        }
    }

    /**
     * Loads the wallMap from wallMap.txt
     */
    private void loadWallMap() {
        //Gets the full filepath for the wallMap.
        File wallMapPath = new File(mapFolder + wallMapFile);
        //Reads and loads the wallMap to the array, determining its width and height in the process.
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
            //Error handling, with specificity.
            System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } 
    }

    /**
     * Loads the groundMap from groundMap.png. (Png, as it's easier to visualize)
     */
    private void loadGroundMap() {
        //Gets the full filepath for the groundMap.
        File groundMapPath = new File(mapFolder + groundMapFile);
        BufferedImage groundMapImage; //The image representing the groundMap.

        //Reads and loads the groundMap from an image. We use an image because it's more visually intuitive to draw out a groundMap this way.
        try {
            groundMapImage = ImageIO.read(groundMapPath);
            if (groundMapImage.getWidth() != mapWidth * groundMapScale || groundMapImage.getHeight() != mapHeight * groundMapScale) {
                System.out.printf("Ground Map size: %d x %d\nMap size: %d x %d\n", groundMapImage.getWidth(), groundMapImage.getHeight(), mapWidth, mapHeight);
                throw new WrongSizeException();
            }
            groundMap = new int[mapWidth * groundMapScale][mapHeight * groundMapScale];
            for (int x = 0; x < groundMap.length; x++) {
                for (int y = 0; y < groundMap[0].length; y++) {
                    int groundRGB = groundMapImage.getRGB(y, x);
                    if (groundRGB == WallColor){
                        groundMap[x][y] = 0;
                    } else if (groundRGB == RoadColor) {
                        groundMap[x][y] = 1;
                    } else if (groundRGB == GrassColor) {
                        groundMap[x][y] = 2;
                    } else if (groundRGB == SandColor) {
                        groundMap[x][y] = 3;
                    } else {
                        System.out.println("A color used in the groundMap is undefined.");
                    }
                }
            }

        } catch (IOException e) {
            //Error handling for IO errors.
            System.out.printf("An error loading the groundMap for the map \"%s\" occurred.\n", name);
        } catch (WrongSizeException e) {
            //A SPECIAL ERROR for when the groundMap size doesn't line up with what it's supposed to for the real map.
            System.out.printf("The groundMap file is the wrong size for the map \"%s\".\n", name);
        }
    }

    /**
     * Loads the spriteMap from spriteMap.txt
     */
    private void loadSpriteMap() {
        //Gets the full filepath for the spriteMap.
        File spriteMapPath = new File(mapFolder + spriteMapFile);

        //Loads the spriteMap, determining the number of sprites in the process.
        try {
            FileReader r = new FileReader(spriteMapPath);
            BufferedReader reader = new BufferedReader(r);
            numSprites = 0;
            String temp = reader.readLine();
            while (temp != null){
                numSprites++;
                if (temp.split(" ").length == 5) {
                    numSpriteCollisions++;
                }
                temp = reader.readLine();
            }
            reader.close();
            r.close();
            r = new FileReader(spriteMapPath);
            reader = new BufferedReader(r);
            sprites = new Sprite[numSprites + 2]; // +2 for the player and db sprites
            spriteCollisions = new CollisionBox[numSpriteCollisions];
            int spriteCollisionsCounter = 0;
            for (int i = 0; i < numSprites; i++) {
                String currentSprite = reader.readLine();
                String[] spriteInfo = currentSprite.split(" ");
                sprites[i] = new Sprite(Double.parseDouble(spriteInfo[1]), Double.parseDouble(spriteInfo[0]), Integer.parseInt(spriteInfo[2]));
                if (spriteInfo.length == 5) {
                    double width = Double.parseDouble(spriteInfo[3]), height = Double.parseDouble(spriteInfo[4]);
                    spriteCollisions[spriteCollisionsCounter] = new CollisionBox(sprites[i].position.x - width/2, sprites[i].position.y - height/2, width, height);
                    spriteCollisionsCounter++;
                }
            }
            reader.close();
            r.close();

        } catch (IOException e) {
            //Error handling for IO errors.
            System.out.printf("An error loading the spriteMap for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            //Error handling for if the spriteMap contains an un-parseable character.
            System.out.printf("The spriteFile for the map \"%s\" contained an un-parseable number.", name);
        }
    }

    /**
     * Loads the wall textures in the wallTextures folder using wallTextures.txt as a guide.
     */
    private void loadWallTextures() {
        //Gets the full filepath for the wallTextures.
        File wallTexturePath = new File(mapFolder + wallTexturesFile);

        //Loads all the wallTextures from the files specified using wallTextures.txt.
        try {
            FileReader r = new FileReader(wallTexturePath);
            BufferedReader reader = new BufferedReader(r);
            int numWallTextures = 0;
            while (reader.readLine() != null){
                numWallTextures++;
            }
            reader.close();
            r.close();
            r = new FileReader(wallTexturePath);
            reader = new BufferedReader(r);
            wallTextures = new Texture[numWallTextures];
            for (int i = 0; i < numWallTextures; i++) {
                String wallTextureFile = mapFolder + wallTextureFolder + "\\" + reader.readLine();
                wallTextures[i] = new Texture(wallTextureFile);
            }
            reader.close();
            r.close();

        } catch (IOException e) {
            //Error handling for IO errors.
            System.out.printf("An error loading the wallTextures for the map \"%s\" occurred.\n", name);
        } 
    }
    
    /**
     * Loads the ground textures in the groundTexture folder using groundTexture.txt as a guide
     */
    private void loadGroundTexture(){
        try {
            String groundTexturePath =  mapFolder + groundTextureFile;
            groundTexture = new Texture(groundTexturePath);
            groundTextureInUse = new Texture(groundTexturePath);
            if (groundTexture.getWidth() != mapWidth * groundTextureScale || groundTexture.getHeight() != mapHeight * groundTextureScale){
                throw new WrongSizeException();
            }
            
        } catch (WrongSizeException e) {
            System.out.printf("The groundTexture is the wrong size for the map \"%s\".\n", name);
        }
    }

    /**
     * Loads the sky textures in the groundSkyTextures folder
     */
    private void loadSkyTexture(){
        String skyTexturePath = mapFolder + skyTextureFile;
        skyTexture = new Texture(skyTexturePath, skyTextureWidth, skyTextureHeight);
    }

    private void loadSpriteTextures(){
        File spriteTexturePath = new File(mapFolder + spriteTexturesFile);
    	try {
            FileReader r = new FileReader(spriteTexturePath);
            BufferedReader reader = new BufferedReader(r);
            int numSpriteTextures = 0;
            while (reader.readLine() != null){
                numSpriteTextures++;
            }
            reader.close();
            r.close();
            r = new FileReader(spriteTexturePath);
            reader = new BufferedReader(r);
            spriteTextures = new Texture[numSpriteTextures + 1]; // + 1 for the player sprite
            for (int i = 0; i < numSpriteTextures; i++){
                String spriteTextureFile = mapFolder + spriteTextureFolder + "\\" + reader.readLine();
                spriteTextures[i] = new Texture(spriteTextureFile);
            }

            reader.close();
            r.close();

        } catch (IOException e) {
             System.out.printf("An error loading the spriteTextures for the map \"%s\" occurred.\n", name);
        } 
    }

    private void loadCheckpoints(){
        //Gets the full filepath for the spriteMap.
        File checkpointsPath = new File(mapFolder + checkpointsFile);

        //Loads the spriteMap, determining the number of sprites in the process.
        try {
            FileReader r = new FileReader(checkpointsPath);
            BufferedReader reader = new BufferedReader(r);
            numCheckpoints = 0;
            while (reader.readLine() != null){
                numCheckpoints++;
            }
            reader.close();
            r.close();
            r = new FileReader(checkpointsPath);
            reader = new BufferedReader(r);
            checkpoints = new CollisionBox[numCheckpoints];
            for (int i = 0; i < numCheckpoints; i++) {
                String currentCheckpoint = reader.readLine();
                String[] checkpointInfo = currentCheckpoint.split(" ");
                checkpoints[i] = new CollisionBox(Double.parseDouble(checkpointInfo[1]), Double.parseDouble(checkpointInfo[0]), Double.parseDouble(checkpointInfo[3]), Double.parseDouble(checkpointInfo[2]));
            }
            reader.close();
            r.close();

        } catch (IOException e) {
            //Error handling for IO errors.
            System.out.printf("An error loading the checkpoints for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            //Error handling for if the spriteMap contains an un-parseable character.
            System.out.printf("The checkpointsFile for the map \"%s\" contained an un-parseable number.", name);
        }
    }

    public void logLeaderboard(String name, long time) {
        File leaderboardPath = new File(mapFolder + leaderboardFile);

        try {
            FileReader r = new FileReader(leaderboardPath);
            BufferedReader reader = new BufferedReader(r);
            String currentLeaderboardValue = reader.readLine();
            HashMap<Long, String> leaderboard = new HashMap<Long, String>();
            ArrayList<Long> times = new ArrayList<Long>();

            for (int i = 0; i < 10 && currentLeaderboardValue != null; i++) {
                String[] splitValues = currentLeaderboardValue.split(" ");
                Long currentTime = Long.parseLong(splitValues[1]);
                leaderboard.put(currentTime, splitValues[0]);
                times.add(currentTime);
                currentLeaderboardValue = reader.readLine();
            }

            leaderboard.put(time, name);
            times.add(time);

            reader.close();
            r.close();

            times.sort(new Comparator<Long>() {
                public int compare(Long a, Long b) {
                    return (int)(a - b);
                }
            });

            FileWriter w = new FileWriter(leaderboardPath);
            BufferedWriter writer = new BufferedWriter(w);

            for (int i = 0; i < times.size() && i < 10; i++) {
                writer.write(leaderboard.get(times.get(i))  + " " + times.get(i));
                writer.newLine();
            }

            writer.close();
            w.close();

        } catch (IOException e) {
            System.out.printf("An error occurred while logging the checkpoints for the map \"%s\".\n", name);
        }
    }

    public void loadStartingInfo() {
        File startingInfoPath = new File(mapFolder + startInfoFile);
        try {
            FileReader r = new FileReader(startingInfoPath);
            BufferedReader reader = new BufferedReader(r);
            
            String[] line = reader.readLine().split(" ");
            startPosition = new Vector(Double.parseDouble(line[0]), Double.parseDouble(line[1]));
            line = reader.readLine().split(" ");
            startDirection = new Vector(Double.parseDouble(line[0]), Double.parseDouble(line[1]));

            reader.close();
            r.close();

        } catch (IOException e) {
             System.out.printf("An error loading the starting information for the map \"%s\" occurred.\n", name);
        } 

    }

    class WrongSizeException extends Exception {
        public WrongSizeException() {}
    }

}

