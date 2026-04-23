import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {
    private int TextureWidth;
    private int TextureHeight;

    public BufferedImage textureImage;
    public int[][] texture;

    public Texture (String imageFilePath) {
        TextureWidth = 64;
        TextureHeight = 64;
        loadTexture(imageFilePath, 64, 64);
    }

    public Texture (String imageFilePath, int width, int height) {
        TextureWidth = width;
        TextureHeight = height;
        loadTexture(imageFilePath, width, height);
    }

    private void loadTexture(String imageFilePath, int width, int height) {
        String filePath = "CentralKartRacing\\src\\" + imageFilePath + ".java";
        try {
            textureImage = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            System.out.println("A texture failed to load.");
        }
        for (int i = 0; i < TextureWidth; i++) {
            for (int j = 0; j < TextureHeight; j++) {
                texture[i][j] = textureImage.getRGB(i, j);
            }
        }
    }
}
