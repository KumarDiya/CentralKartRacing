import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Map {
    private String mapFolder; //The folder all map files are found in.
    private String name;

    private int mapWidth;
    private int mapHeight;

    public static int[][] wallMap; //The map determining the location of walls.
    public int[][] groundMap; //The map determining the ground materials; this will be an integer multiple of wallMap, determined by groundMapScale.
    final int groundMapScale = 8; //The upscale factor of groundmap to wallMap.

    private int numSprites; //The number of sprites.
    public Sprite[] sprites; //The sprites used in the level.

    public Texture groundTexture; //The texture used for the ground.
    public Texture skyTexture; //The texture used for the skybox.

    public Texture[] wallTextures; //The textures of the walls, index determined by order of placement in wallTextures.txt.
    public Texture[] spriteTextures; //The textures of sprites, index determined by order of placement in spriteTextures.txt.
    public Texture[] groundTextures; //The textures of the ground, index determined by order of placement in groundTexture.txt

    //Constants used for file access. 
    final String wallMapFile = "wallMap.txt";
    final String groundMapFile = "groundMap.png";
    final String spriteMapFile = "spriteMap.txt";
    final String wallTextureFile = "wallTextures.txt";
    final String groundTextureFile = "groundTextures.txt";
    final String skyTextureFile = "skyTextures.txt";
    final String spriteTextureFile = "spriteTextures.txt";

    final String wallTextureFolder = "wallTextures";
    final String groundTextureFolder = "groundTextures";
    final String skyTextureFolder = "skyTextures";
    final String spriteTextureFolder = "spriteTextures";

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

    public int getNumSprites() {
        return numSprites;
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
     * Loads the groundMap from groundMap.png. (Png, as it's easier to visuallize)
     */
    private void loadGroundMap() {
        //Gets the full filepath for the groundMap.
        File groundMapPath = new File(mapFolder + groundMapFile);
        BufferedImage groundMapImage; //The image representing the groundMap.

        //Reads and loads the groundMap from an image. We use an image because it's more visually intuitive to draw out a groundmap this way.
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
            //Error handling for IO errors.
            System.out.printf("An error loading the groundMap for the map \"%s\" occurred.\n", name);
        } catch (GroundMapSizeException e) {
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
            while (reader.readLine() != null){
                numSprites++;
            }
            reader.close();
            r.close();
            r = new FileReader(spriteMapPath);
            reader = new BufferedReader(r);
            sprites = new Sprite[numSprites];
            for (int i = 0; i < numSprites; i++) {
                String currentSprite = reader.readLine();
                String[] spriteInfo = currentSprite.split(" ");
                sprites[i] = new Sprite(Double.parseDouble(spriteInfo[0]), Double.parseDouble(spriteInfo[1]), Integer.parseInt(spriteInfo[2]));
            }
            reader.close();
            r.close();

        } catch (IOException e) {
            //Error handling for IO errors.
            System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            //Error handling for if the spriteMap contains an unparseable character.
            System.out.printf("The spriteFile for the map \"%s\" contained an unparseable number.", name);
        }
    }

    /**
     * Loads the wall textures in the wallTextures folder using wallTextures.txt as a guide.
     */
    private void loadWallTextures() {
        //Gets the full filepath for the wallTextures.
        File wallTexturePath = new File(mapFolder + wallTextureFile);

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
            System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            System.out.printf("The spriteFile for the map \"%s\" contained an unparseable number.", name);
        }
    }
    
    /**
     * Loads the ground textures in the groundTexture folder using groundTexture.txt as a guide
     */
    private void loadGroundTexture(){
        File groundTexturePath = new File(mapFolder + groundTextureFile);
    	try {
            FileReader r = new FileReader(groundTexturePath);
            BufferedReader reader = new BufferedReader(r);
            int numGroundTextures = 0;
            while (reader.readLine() != null){
                numGroundTextures ++;
            }
            reader.close();
            r.close();
            r = new FileReader(groundTexturePath);
            reader = new BufferedReader(r);
            for (int i = 0; i < numGroundTextures; i++){
                String groundTextureFile = mapFolder + groundTextureFolder + "\\" + reader.readLine();
                groundTextures[i] = new Texture(groundTextureFile);
            }

            reader.close();
            r.close();

        } catch (IOException e) {
             System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            System.out.printf("The spriteFile for the map \"%s\" contained an unparseable number.", name);
        
        }
    }

    /**
     * Loads the sky textures in the skyTexture folder using skyTexture.txt as a guide
     */
    private void loadSkyTexture(){
        File skyTexturePath = new File(mapFolder + skyTextureFile);
    	try {
            FileReader r = new FileReader(skyTexturePath);
            BufferedReader reader = new BufferedReader(r);
            int numSkyTextures = 0;
            while (reader.readLine() != null){
                numSkyTextures ++;
            }
            reader.close();
            r.close();
            r = new FileReader(skyTexturePath);
            reader = new BufferedReader(r);
            for (int i = 0; i < numSkyTextures; i++){
                String skyTextureFile = mapFolder + skyTextureFolder + "\\" + reader.readLine();
                groundTextures[i] = new Texture(skyTextureFile);
            }

            reader.close();
            r.close();

        } catch (IOException e) {
             System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            System.out.printf("The spriteFile for the map \"%s\" contained an unparseable number.", name);
        
        }
    }

    private void loadSpriteTexture(){
        File spriteTexturePath = new File(mapFolder + spriteTextureFile);
    	try {
            FileReader r = new FileReader(spriteTexturePath);
            BufferedReader reader = new BufferedReader(r);
            int numSpriteTextures = 0;
            while (reader.readLine() != null){
                numSpriteTextures ++;
            }
            reader.close();
            r.close();
            r = new FileReader(spriteTexturePath);
            reader = new BufferedReader(r);
            for (int i = 0; i < numSpriteTextures; i++){
                String spriteTextureFile = mapFolder + spriteTextureFolder + "\\" + reader.readLine();
                groundTextures[i] = new Texture(spriteTextureFile);
            }

            reader.close();
            r.close();

        } catch (IOException e) {
             System.out.printf("An error loading the wallMap for the map \"%s\" occurred.\n", name);
        } catch (NumberFormatException e) {
            System.out.printf("The spriteFile for the map \"%s\" contained an unparseable number.", name);
        
        }
    }


    class GroundMapSizeException extends Exception {
        public GroundMapSizeException() {}
    }

}

