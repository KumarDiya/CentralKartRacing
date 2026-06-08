/**
 * Renderer.java
 * Justin Zhou
 * The class that handles all rendering in the main game. 
 */


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Renderer extends JPanel implements KeyListener{
    //General screen variables
    public int Width;                   //The final width of the JPanel (screen).
    public int Height;                  //The final height of the JPanel (screen).
    public static int ResolutionWidth = 848;   //The width of the resolution for the game to be rendered in.
    public static int ResolutionHeight = 477;  //The height of the resolution for the game to be rendered in.

    final static double StandardFOV = 82.7;
    static double FOV = StandardFOV;

    BufferedImage frameA = new BufferedImage(ResolutionWidth, ResolutionHeight, BufferedImage.TYPE_INT_RGB);
    int[] frameBufferA = ((DataBufferInt) frameA.getRaster().getDataBuffer()).getData();
    private volatile BufferedImage activeFrame = frameA;
    BufferedImage frameB = new BufferedImage(ResolutionWidth, ResolutionHeight, BufferedImage.TYPE_INT_RGB);
    int[] frameBufferB = ((DataBufferInt) frameB.getRaster().getDataBuffer()).getData();

    //paused boolean
    boolean paused;

    //Map and Player
    Map map;
    Player player;
    
    
    //Skybox variables
    private double skyPixelsPerRevolution;  //The number of pixels the skybox needs to stretch to to cover one revolution of the player's FOV.
    private double angBetweenRays;             //The angle between two rays casted by the raycaster.
    private double skyStepX;                //The amount to step by between pixels on the skybox when rendering.

    //Sprite variables
    private double[] zBuffer;               //An array holding the distance to walls for casted rays from the player. Used to determine when to render sprites.

    //Constants
    public static final int DarkerNumber = Integer.parseInt("011111110111111101111111", 2); //Bitmask to make colors darker. Makes use of the bitwise 'and' bitshift operator (fun!)
    public static double CameraDistance = 2;  //The distance the camera will follow the player at.

    //Key Pressed Booleans
    public boolean wPressed, sPressed, aPressed, dPressed, uPressed, iPressed;

    public static final int TargetFrameRate = 60;
    public static final double TargetFrameTime = (double) 1 / TargetFrameRate; // 1/Target Frame Rate

    public HeadsUpDisplay HUD;

    /**
     * Renderer constructor. extra param added: selectedPlayer
     */
    public Renderer (Map map, Player player) {

        this.map = map;
        this.player = player;

        paused = false;

        skyPixelsPerRevolution = map.skyTexture.getWidth() / (2 * Math.PI);
        angBetweenRays = Math.atan2(player.unRotatedPlane.y, player.direction.x) * 2 / ResolutionWidth;
        skyStepX = map.skyTexture.getWidth()/(2*Math.PI/angBetweenRays);

        zBuffer = new double[ResolutionWidth];

        HUD = new HeadsUpDisplay(ResolutionWidth, ResolutionHeight, map);

        wPressed = sPressed = aPressed = dPressed = uPressed = iPressed = false;

        //set up panel
        this.setPreferredSize(new Dimension(ResolutionWidth, ResolutionHeight));
        this.setFocusable(true);
        this.addKeyListener(this);
    }
    

    /**
     * Renders a frame, setting frame to the final rendered frame.
     * @param map       The map to be rendered.
     * @param player    The player in the map to be rendered.
     */
    public synchronized void render(){

        //Frame buffer chooser
        BufferedImage inactiveFrame;
        int[] frameBuffer;
        if (frameA == activeFrame) {
            inactiveFrame = frameB;
            frameBuffer = frameBufferB;
        } else {
            inactiveFrame = frameA;
            frameBuffer = frameBufferA;
        }

        //The camera position
        Vector cameraPos = getCameraPos();

        //Render Floor
        Vector rayDirMin = new Vector(player.direction.x - player.plane.x, player.direction.y - player.plane.y);
        Vector rayDirMax = new Vector(player.direction.x + player.plane.x, player.direction.y + player.plane.y);

        for (int y = ResolutionHeight/2; y < ResolutionHeight; y++) {

            int p = y - ResolutionHeight / 2;
            double posZ = 0.5 * ResolutionHeight;
            double rowDistance = posZ / p;
            
            Vector floorStep = new Vector(rowDistance * (rayDirMax.x - rayDirMin.x) / ResolutionWidth, rowDistance * (rayDirMax.y - rayDirMin.y) / ResolutionWidth);
            Vector floor = new Vector(cameraPos.x + rowDistance * rayDirMin.x, cameraPos.y + rowDistance * rayDirMin.y);

            for (int x = 0; x < ResolutionWidth; x++){
                VectorInt cell = new VectorInt((int)(floor.x), (int)(floor.y));
                VectorInt fcTexture = new VectorInt(map.groundTexture.getHeight() - 1 - Math.abs((int)(map.groundTexture.getHeight() * (floor.y / map.getHeight() - cell.y)) % (map.groundTexture.getHeight())), map.groundTexture.getWidth() - 1 - Math.abs((int)(map.groundTexture.getWidth() * (floor.x / map.getWidth() - cell.x)) % (map.groundTexture.getWidth())));
                // VectorInt fcTexture = new VectorInt(Math.abs((int)(map.groundTexture.getHeight() * (floor.y / map.getHeight() - cell.y)) & (map.groundTexture.getHeight() - 1)), Math.abs((int)(map.groundTexture.getWidth() * (floor.x / map.getWidth() - cell.x)) & (map.groundTexture.getHeight() - 1)));

                floor.x += floorStep.x;
                floor.y += floorStep.y;

                int color;
                color = map.groundTextureInUse.texture[fcTexture.x][fcTexture.y];
                frameBuffer[y * ResolutionWidth + x] = color;
            }
        }

        //The current angle the player is facing.
        double dirAng = Math.atan2(player.direction.y, player.direction.x);

        //Render Walls and Sky
        for (int x = 0; x < ResolutionWidth; x++){
            //Calculate ray position and direction
            double cameraX = 2 * x / (double) ResolutionWidth - 1;
            Vector rayDir = player.direction.addVec(player.plane.scalMult(cameraX));

            //Variable initialization for DDA
            VectorInt mapSquare = new VectorInt((int)cameraPos.x, (int)cameraPos.y);
            Vector sideDist = new Vector();
            Vector deltaDist = new Vector();
            double perpWallDist;
            VectorInt step =  new VectorInt();
            int hit = 0;
            boolean side = false; //NS = true, EW = false;

            //Calculate step and initial sideDist
            if (rayDir.x == 0){
                deltaDist.x = Double.POSITIVE_INFINITY;
            } else {
                deltaDist.x = Math.abs(1/rayDir.x);
            }
            if (rayDir.y == 0){
                deltaDist.y = Double.POSITIVE_INFINITY;
            } else {
                deltaDist.y = Math.abs(1/rayDir.y);
            }

            if (rayDir.x < 0) {
                step.x = -1;
                sideDist.x = (cameraPos.x - mapSquare.x) * deltaDist.x;
            } else {
                step.x = 1;
                sideDist.x = (mapSquare.x + 1.0 - cameraPos.x) * deltaDist.x;
            }
            if (rayDir.y < 0) {
                step.y = -1;
                sideDist.y = (cameraPos.y - mapSquare.y) * deltaDist.y;
            } else {
                step.y = 1;
                sideDist.y = (mapSquare.y + 1.0 - cameraPos.y) * deltaDist.y;
            }

            //Perform DDA
            while (hit == 0){
                if (sideDist.x < sideDist.y) {
                    sideDist.x += deltaDist.x;
                    mapSquare.x += step.x;
                    side = false;
                } else {
                    sideDist.y += deltaDist.y;
                    mapSquare.y += step.y;
                    side = true;
                }
                if (map.wallMap[mapSquare.x][mapSquare.y] > 0) hit = 1;
            }

            if (!side) perpWallDist = sideDist.x - deltaDist.x;
            else perpWallDist = sideDist.y - deltaDist.y;

            //Calculate line's parameters to draw on screen
            int lineHeight = (int)(ResolutionHeight / perpWallDist);
            int drawStart, drawEnd;

            drawStart = (-lineHeight + ResolutionHeight) / 2;
            if (drawStart < 0) drawStart = 0;
            drawEnd = (lineHeight + ResolutionHeight) / 2;
            if (drawEnd >= ResolutionHeight) drawEnd = ResolutionHeight - 1;
            
            //Wall Texture Calculations
            int texNum = map.wallMap[mapSquare.x][mapSquare.y] - 1;

            double wallX;
            if (!side) wallX = cameraPos.y + perpWallDist * rayDir.y;
            else wallX = cameraPos.x + perpWallDist * rayDir.x;
            wallX -= Math.floor(wallX);

            int texX = (int)(wallX * (double)Texture.DefaultSize);
            if (!side && rayDir.x > 0) {
                texX = Texture.DefaultSize - texX - 1;
            } 
            if (side && rayDir.y < 0) {
                texX = Texture.DefaultSize - texX - 1;
            }

            double texStep = (double)Texture.DefaultSize / lineHeight;
            double texPos = (drawStart - ResolutionHeight/2 + lineHeight/2) * texStep;

            //Render Wall
            for (int y = drawStart; y < drawEnd; y++) {
                int texY = (int)texPos & (Texture.DefaultSize - 1);
                texPos += texStep;
                int color = map.wallTextures[texNum].texture[texX][texY];
                if (side) color = (color >> 1) & DarkerNumber;
                frameBuffer[y * ResolutionWidth + x] = color;
            }

            //Render Sky
            int skyX = (int)(dirAng * skyPixelsPerRevolution - x * skyStepX);
            if (skyX < 0) skyX += map.skyTexture.getWidth();
            else if (skyX >= map.skyTexture.getWidth()) skyX -= map.skyTexture.getWidth();

            for (int y = 0; y < drawStart; y++) {
                int color = map.skyTexture.texture[skyX][y];
                frameBuffer[y * ResolutionWidth + x] = color;
            }

            //Set ZBuffer for sprite rendering
            zBuffer[x] = perpWallDist;
        }

        //Sprite Rendering
        player.DBsprite.setXY(player.pos.addVec(player.direction.scalMult(-0.5)));
        player.sprite.setXY(player.pos);
        
        player.sprite.setXY(player.pos);
        player.DBsprite.setXY(player.pos);
        
        map.sprites[map.sprites.length - 1] = player.DBsprite;
        map.sprites[map.sprites.length - 2] = player.sprite;
        
        int[] spriteOrder = new int[map.getNumSprites()];
        double[] spriteDistance = new double[map.getNumSprites()];

        for (int i = 0; i < map.getNumSprites(); i++){
            spriteOrder[i] = i;
            spriteDistance[i] = ((cameraPos.x - map.sprites[i].position.x)*(cameraPos.x - map.sprites[i].position.x) + (cameraPos.y - map.sprites[i].position.y)*(cameraPos.y - map.sprites[i].position.y));
        }

        Sprite.sortSprites(spriteOrder, spriteDistance, map.getNumSprites());

        for (int i = 0; i < map.getNumSprites(); i++){
            Vector spriteCamPos = new Vector(map.sprites[spriteOrder[i]].position.x - cameraPos.x, map.sprites[spriteOrder[i]].position.y - cameraPos.y);
            //transform sprite with the inverse camera matrix
            // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
            // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
            // [ planeY   dirY ]                                          [ -planeY  planeX ]
            double invDet = 1.0/(player.plane.x * player.direction.y - player.direction.x * player.plane.y);
            Vector transform = new Vector(invDet * (player.direction.y * spriteCamPos.x - player.direction.x * spriteCamPos.y), invDet * (-player.plane.y * spriteCamPos.x + player.plane.x * spriteCamPos.y));
            int spriteScreenX = (int)Math.round((ResolutionWidth/2)*(1 + transform.x/transform.y));

            //calculate height of the sprite on screen
            int spriteHeight = Math.abs((int)(ResolutionHeight / transform.y)); //using 'transformY' instead of the real distance prevents fisheye
            //calculate lowest and highest pixel to fill in current stripe
            int drawStartY = -spriteHeight / 2 + ResolutionHeight / 2;
           
            if(drawStartY < 0) drawStartY = 0;
            int drawEndY = spriteHeight / 2 + ResolutionHeight / 2;
            if(drawEndY >= ResolutionHeight) drawEndY = ResolutionHeight - 1;

           

            //calculate width of the sprite
            int spriteWidth = Math.abs((int)(ResolutionHeight / transform.y));
            int drawStartX = -spriteWidth / 2 + spriteScreenX;
            if(drawStartX < 0) drawStartX = 0;
            int drawEndX = spriteWidth / 2 + spriteScreenX;
            if(drawEndX >= ResolutionWidth) drawEndX = ResolutionWidth - 1;

            //loop through every vertical stripe of the sprite on screen
            for(int stripe = drawStartX; stripe < drawEndX; stripe++) {
                int texX = (int)(256 * (long)(stripe - (-spriteWidth / 2 + spriteScreenX)) * Texture.DefaultSize / spriteWidth) / 256;
                //the conditions in the if are:
                //1) it's in front of camera plane so you don't see things behind you
                //2) it's on the screen (left)
                //3) it's on the screen (right)
                //4) ZBuffer, with perpendicular distance
                if(transform.y > 0 && stripe > 0 && stripe < ResolutionWidth && transform.y < zBuffer[stripe]) {
                    for(int y = drawStartY; y < drawEndY; y++){ //for every pixel of the current stripe
                        int d = (y) * 256 - ResolutionHeight * 128 + spriteHeight * 128; //256 and 128 factors to avoid floats
                        int texY = (int)((((long) d * Texture.DefaultSize) / spriteHeight) / 256);
                        if (texY < 0) texY = 0;
                        int color;
                        Texture spriteTexture = null;
                        Sprite sprite = map.sprites[spriteOrder[i]];

                        if (sprite == player.sprite) {
                            spriteTexture = player.characterTextures[player.sprite.texture];
                        } else if (sprite == player.DBsprite) {
                            spriteTexture = player.DBTextures[player.DBsprite.texture];
                        } else if (map.spriteTextures != null && map.spriteTextures.length > 0) {
                            spriteTexture = map.spriteTextures[sprite.texture];
                        }

                        if (spriteTexture != null) {
                            color = spriteTexture.texture[texX][texY];
                            if ((color & 0x00FFFFFF) != 0) {
                                frameBuffer[y * ResolutionWidth + stripe] = color;
                            }
                        }
                        /**
                         * Texture spriteTexture = map.spriteTextures[spriteOrder[i]];
                        if (spriteTexture != null) {
                            color = spriteTexture.texture[texX][texY];
                            if ((color & 0x00FFFFFF) != 0) {
                                frameBuffer[y * ResolutionWidth + stripe] = color;
                            }
                        }
                         */
                        
                    }
                }
            }
        }
        activeFrame = inactiveFrame;
    }

   
    /**
     * Gets the current camera position by casting a ray backwards from where the player is facing and checking for collision.
     * @param map
     * @param player
     * @return
     */
    private Vector getCameraPos() {

        //Potential future FOV effects
        // if (player.speed > player.MAX_SPEED) {
        //     CameraDistance = 2 + (player.speed - player.MAX_SPEED)/player.MAX_SPEED;
        // } else {
        //     CameraDistance = 2;
        // }

        //Camera Collision Detection (prevents camera from going through walls when close to them)
        Vector cameraPos;
        Vector cameraDir = player.direction.scalMult(-1);

        VectorInt mapSquare = new VectorInt((int)player.pos.x, (int)player.pos.y);
        Vector sideDist = new Vector();
        Vector deltaDist = new Vector();
        VectorInt step =  new VectorInt();
        int hit = 0;
        double perpWallDist, cameraMult;
        boolean side = false;
        double cameraMag = cameraDir.getMagnitude();

        if (cameraDir.x == 0){
            deltaDist.x = Double.POSITIVE_INFINITY;
        } else {
            deltaDist.x = Math.abs(cameraMag/cameraDir.x);
        }
        if (cameraDir.y == 0){
            deltaDist.y = Double.POSITIVE_INFINITY;
        } else {
            deltaDist.y = Math.abs(cameraMag/cameraDir.y);
        }

        if (cameraDir.x < 0) {
            step.x = -1;
            sideDist.x = (player.pos.x - mapSquare.x) * deltaDist.x;
        } else {
            step.x = 1;
            sideDist.x = (mapSquare.x + 1.0 - player.pos.x) * deltaDist.x;
        }
        if (cameraDir.y < 0) {
            step.y = -1;
            sideDist.y = (player.pos.y - mapSquare.y) * deltaDist.y;
        } else {
            step.y = 1;
            sideDist.y = (mapSquare.y + 1.0 - player.pos.y) * deltaDist.y;
        }

        while (hit == 0){
            if (sideDist.x < sideDist.y) {
                sideDist.x += deltaDist.x;
                mapSquare.x += step.x;
                side = true;
            } else {
                sideDist.y += deltaDist.y;
                mapSquare.y += step.y;
                side = false;
            }
            
            if (map.wallMap[mapSquare.x][mapSquare.y] > 0) {
                hit = 1;
                //System.out.print(map.wallMap[mapSquare.x][mapSquare.y] + ", ");
                //System.out.println(mapSquare.x + ", " + mapSquare.y);
            }
        }

        if(side) perpWallDist = (sideDist.x - deltaDist.x);
        else perpWallDist = (sideDist.y - deltaDist.y);

        if (perpWallDist > CameraDistance) cameraMult = CameraDistance;
        else cameraMult = perpWallDist - 0.01;
    
        //System.out.println(perpWallDist);
        cameraPos = player.pos.addVec(cameraDir.scalMult(cameraMult));

        return cameraPos;
    }

    /**
     * Draws the current frame to the screen.
     */
    public synchronized void requestRepaint() {
        this.repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        BufferedImage framePin;
        synchronized (this) {
            framePin = activeFrame;
        }
        g.drawImage(framePin, 0, 0, null);
        Graphics2D g2 = (Graphics2D)g;
        HUD.setG2(g2);
        HUD.drawHUD(player.getLap(), player.pos.x, player.pos.y, player.currentFuel, player.MAXFUEL, player.speed);
        

        /*if (paused){
            
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            
        }*/
    }

    public boolean wDown () {
        return wPressed;
    }

    public boolean sDown() {
        return sPressed;
    }

    public boolean aDown() {
        return aPressed;
    }

    public boolean dDown() {
        return dPressed;
    }

    public boolean uDown() {
        return uPressed;
    }

    public boolean iDown(){
        return iPressed;
    }

    public boolean[] getControlsDown() {
        boolean[] controls = {wPressed, sPressed, aPressed, dPressed};
        return controls;
    }

    public void checkGameFinish(){
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            mainFrame.switchToScreen("finish");
        
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            wPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            sPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            aPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            dPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_U) {
            uPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_I) {
            iPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_K) {
            
            if(!paused){
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
                paused = true;
                mainFrame.switchToScreen("pause");
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            wPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            sPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            aPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            dPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_U) {
            uPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_I) {
            iPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * determines index of the player texture the user chooses
     * @param character
     */
    public void setPlayerCharacter(String character) {
        int textureIndex = 3;
        if (character.equals("Blonde Guy")) {
            textureIndex = 0;
        } else if (character.equals("Jeff")) {
            textureIndex = 1;
        } else if (character.equals("Po")) {
            textureIndex = 2;
        }

        player.sprite.texture = textureIndex;
        System.out.println("Player texture set to index: " + textureIndex);
    }

    
}