import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer extends JFrame{
    private DrawingPanel panel;
    private BufferedImage frame;
    private int[] screenArr = ((DataBufferInt) frame.getRaster().getDataBuffer()).getData();
    public int Width;
    public int Height;
    public int ResolutionWidth = 750;
    public int ResolutionHeight = 500;
    
    private double skyPixelsPerRevolution;
    private double angBetwRays;
    private double skyStepX;
    private double[] zBuffer;

    //Bitmask to make colors darker for y-side walls and floor
    public static final int DarkerNumber = Integer.parseInt("011111110111111101111111", 2); 

    public Renderer () {
        panel = new DrawingPanel();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.add(panel);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Width = this.getWidth();
        Height = this.getHeight();
        frame = new BufferedImage(Width, Height, BufferedImage.TYPE_INT_RGB);
    }

    public void renderSetup(Map map, Player player) {
        skyPixelsPerRevolution = map.skyTexture.getWidth() / (2 * Math.PI);
        angBetwRays = Math.atan2(player.plane.y, -player.direction.x) * 2 / ResolutionWidth;
        skyStepX = map.skyTexture.getWidth()/(2*Math.PI/angBetwRays);

        zBuffer = new double[map.getNumSprites()];
    }

    public void render(Map map, Player player){

        Vector cameraPos = getCameraPos(map, player);

        //Render Floor
        double dirAng = Math.atan2(player.direction.y, player.direction.x);
        Vector rayDir0 = new Vector(player.direction.x - player.plane.x, player.direction.y - player.plane.y);
        Vector rayDir1 = new Vector(player.direction.x + player.plane.x, player.direction.y + player.plane.y);

        for (int y = ResolutionHeight/2; y < ResolutionHeight; y++) {

            int p = y - ResolutionHeight / 2;
            double posZ = 0.5 * ResolutionHeight;
            double rowDistance = posZ / p;
            
            Vector floorStep = new Vector(rowDistance * (rayDir1.x - rayDir0.x) / ResolutionWidth, rowDistance * (rayDir1.y - rayDir0.y) / ResolutionWidth);
            Vector floor = new Vector(cameraPos.x + rowDistance * rayDir0.x, cameraPos.y + rowDistance * rayDir0.y);

            for (int x = 0; x < ResolutionWidth; x++){
                VectorInt cell = new VectorInt((int)(floor.x), (int)(floor.y));
                VectorInt fcTexture = new VectorInt(Math.abs((int)(map.groundTexture.getWidth() * (floor.y / map.getWidth() - cell.y)) % (map.groundTexture.getWidth())), Math.abs((int)(map.groundTexture.getHeight() * (floor.x / map.getHeight() - cell.x)) % (map.groundTexture.getHeight())));

                floor.x += floorStep.x;
                floor.y += floorStep.y;

                int color;
                color = map.groundTexture.texture[fcTexture.x][fcTexture.y];
                color = (color >> 1) & DarkerNumber;
                screenArr[y * ResolutionWidth + x] = color;
            }
        }

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
            //if (!side) perpWallDist = sideDist.x;
            //else perpWallDist = sideDist.y;

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
                screenArr[y * ResolutionWidth + x] = color;
            }

            //Render Sky
            int skyX = (int)(dirAng * skyPixelsPerRevolution - x * skyStepX);
            if (skyX < 0) skyX += map.skyTexture.getWidth();
            else if (skyX >= map.skyTexture.getWidth()) skyX -= map.skyTexture.getWidth();

            for (int y = 0; y < drawStart; y++) {
                int color = map.skyTexture.texture[skyX][y];
                screenArr[y * ResolutionWidth + x] = color;
            }

            //Set ZBuffer for sprite rendering
            zBuffer[x] = perpWallDist;
        }

        //Sprite Rendering
        map.sprites[19].setXY(player.pos);

        int[] spriteOrder = new int[map.getNumSprites()];
        double[] spriteDistance = new double[map.getNumSprites()];

        for (int i = 0; i < map.getNumSprites(); i++){
            spriteOrder[i] = i;
            spriteDistance[i] = ((cameraPos.x - map.sprites[i].position.x)*(cameraPos.x - map.sprites[i].position.x) + (cameraPos.y - map.sprites[i].position.y)*(cameraPos.y - map.sprites[i].position.y));
        }

        SpriteTesting.sortSprites(spriteOrder, spriteDistance, map.getNumSprites());

        for (int i = 0; i < map.getNumSprites(); i++){
            Vector spriteCamPos = new Vector(map.sprites[spriteOrder[i]].position.x - cameraPos.x, map.sprites[spriteOrder[i]].position.y - cameraPos.y);
            //transform sprite with the inverse camera matrix
            // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
            // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
            // [ planeY   dirY ]                                          [ -planeY  planeX ]
            double invDet = 1.0/(player.plane.x * player.direction.y - player.direction.x * player.plane.y);
            Vector transform = new Vector(invDet * (player.direction.y * spriteCamPos.x - player.direction.x * spriteCamPos.y), invDet * (-player.plane.y * spriteCamPos.x + player.plane.x * spriteCamPos.y));
            int spriteScreenX = (int)((ResolutionWidth/2)*(1 + transform.x/transform.y));

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
                if(transform.y > 0 && transform.y < zBuffer[stripe]) {
                    for(int y = drawStartY; y < drawEndY; y++){ //for every pixel of the current stripe
                        int d = (y) * 256 - ResolutionHeight * 128 + spriteHeight * 128; //256 and 128 factors to avoid floats
                        int texY = (int)((((long) d * Texture.DefaultSize) / spriteHeight) / 256);
                        int color;
                        color = map.spriteTextures[map.sprites[spriteOrder[i]].texture].texture[texX][texY]; //get current color from the texture
                        if((color & 0x00FFFFFF) != 0) screenArr[y * ResolutionWidth + stripe] = color; //paint pixel if it isn't black, black is the invisible color
                        
                    }
                }
            }
        }
    }
    
    public Vector getCameraPos(Map map, Player player) {
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
            //System.out.println(mapSquare.x + ", " + mapSquare.y);
            if (map.groundMap[mapSquare.x][mapSquare.y] > 0) hit = 1;
        }

        if(side) perpWallDist = (sideDist.x - deltaDist.x);
        else perpWallDist = (sideDist.y - deltaDist.y);

        if (perpWallDist > 2) cameraMult = 2;
        else cameraMult = perpWallDist - 0.01;
    
        cameraPos = player.pos.addVec(cameraDir.scalMult(cameraMult));

        return cameraPos;
    }

    public void drawFrame() {
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
