import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
    private int TextureWidth;
    private int TextureHeight;

    public BufferedImage textureImage;
    public int[][] texture;

    public static final int DefaultSize = 64; 

    public Texture (String imageFilePath) {
        TextureWidth = DefaultSize;
        TextureHeight = DefaultSize;
        loadTexture(imageFilePath, DefaultSize, DefaultSize);
    }

    public Texture (String imageFilePath, int width, int height) {
        TextureWidth = width;
        TextureHeight = height;
        loadTexture(imageFilePath, width, height);
    }

    public int getWidth() {
        return TextureWidth;
    }

    public int getHeight() {
        return TextureHeight;
    }

    private void loadTexture(String imageFilePath, int width, int height) {
        try {
            texture = new int[TextureWidth][TextureHeight];
            textureImage = ImageIO.read(new File(imageFilePath));
            for (int i = 0; i < TextureWidth; i++) {
                for (int j = 0; j < TextureHeight; j++) {
                    texture[i][j] = textureImage.getRGB(i, j);
                }
            }
        } catch (IOException e) {
            System.out.println("A texture failed to load.");
        }
        
    }
}
