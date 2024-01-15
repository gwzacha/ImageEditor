import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

public class ImageEditorPanel extends JPanel implements KeyListener {

    Color[][] pixels;
    final static int MAX_COLOR_VAL = 255;
    final int BLUR_RADIUS = 5;
    final double CONTRAST_FACTOR = 1.1;
    final double BLUE_FACTOR = 1.1;

    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            imageIn = ImageIO.read(new File("CITY.jpg"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    public void run() {
        // call your image-processing methods here OR call them from keyboard event
        // handling methods
        // pixels = flipHoriz(pixels);
        // pixels = flipVert(pixels);
        // pixels = grayscale(pixels);
        // pixels = vintage(pixels);
        // pixels = blur(pixels, BLUR_RADIUS);
        // pixels = contrast(pixels, CONTRAST_FACTOR);
        repaint();
    }

    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        // System.out.println("Loaded image: width: " +width + " height: " + height);
        return result;
    }

    public Color[][] flipHoriz(Color[][] oldImg) {
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] horizImg = new Color[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int flipCol = cols - 1 - c;
                horizImg[r][flipCol] = oldImg[r][c];
            }
        }
        return horizImg;
    }

    public Color[][] flipVert(Color[][] oldImg) {
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] vertImg = new Color[rows][cols];
        for (int r = 0; r < rows; r++) {
            int flipRow = rows - 1 - r;
            for (int c = 0; c < cols; c++) {
                vertImg[flipRow][c] = oldImg[r][c];
            }
        }
        return vertImg;
    }

    // applies a grayscale to the image.
    public Color[][] grayscale(Color[][] oldImg) {
        final int DIVISOR = 3;
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] grayImg = new Color[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color pixel = oldImg[r][c];
                int red = pixel.getRed();
                int green = pixel.getGreen();
                int blue = pixel.getBlue();
                int grayValue = (red + green + blue) / DIVISOR;

                Color gray = new Color(grayValue, grayValue, grayValue);
                grayImg[r][c] = gray;
            }
        }
        return grayImg;
    }

    public Color[][] blueTint(Color[][] oldImg, double blueFactor) {
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] blueImg = new Color[rows][cols];

        // Iterate each pixel and apply blue tint
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color pixel = oldImg[r][c];
                int newBlue = (int) (pixel.getBlue() * blueFactor);
                newBlue = Math.min(MAX_COLOR_VAL, Math.max(0, newBlue));
                blueImg[r][c] = new Color(pixel.getRed(), pixel.getGreen(), newBlue);
            }
        }
        return blueImg;
    }

    // applys a vintage effect to photo
    public static Color[][] vintage(Color[][] oldImg) {
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] vintageImg = new Color[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color pixel = oldImg[r][c];
                int red = pixel.getRed();
                int green = pixel.getGreen();
                int blue = pixel.getBlue();

                // values from wikipedia
                int vintageRed = (int) ((0.393 * red) + (0.769 * green) + (0.189 * blue));
                int vintageGreen = (int) ((0.349 * red) + (0.686 * green) + (0.168 * blue));
                int vintageBlue = (int) ((0.272 * red) + (0.534 * green) + (0.131 * blue));

                // Check values are in color range
                vintageRed = Math.min(MAX_COLOR_VAL, Math.max(0, vintageRed));
                vintageGreen = Math.min(MAX_COLOR_VAL, Math.max(0, vintageGreen));
                vintageBlue = Math.min(MAX_COLOR_VAL, Math.max(0, vintageBlue));
                vintageImg[r][c] = new Color(vintageRed, vintageGreen, vintageBlue);
            }
        }
        return vintageImg;
    }

    // changes contrast of the image
    public static Color[][] contrast(Color[][] oldImg, double factor) {
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] contrastImg = new Color[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Color pixel = oldImg[r][c];
                // centers the original value by - half of color range
                // then multiplys by contrast factor
                // then adds back half of the color range
                int red = (int) (factor * (pixel.getRed() - MAX_COLOR_VAL / 2) + MAX_COLOR_VAL / 2);
                int green = (int) (factor * (pixel.getGreen() - MAX_COLOR_VAL / 2) + MAX_COLOR_VAL / 2);
                int blue = (int) (factor * (pixel.getBlue() - MAX_COLOR_VAL / 2) + MAX_COLOR_VAL / 2);
                // makes sure new values are in rgb range
                red = Math.min(MAX_COLOR_VAL, Math.max(0, red));
                green = Math.min(MAX_COLOR_VAL, Math.max(0, green));
                blue = Math.min(MAX_COLOR_VAL, Math.max(0, blue));
                contrastImg[r][c] = new Color(red, green, blue);
            }
        }
        return contrastImg;
    }

    // adds blur effect to image
    // uses two helper methods
    public static Color[][] blur(Color[][] oldImg, int blurRadius) {
        int rows = oldImg.length;
        int cols = oldImg[0].length;
        Color[][] blurredImg = new Color[rows][cols];
        for (int r = blurRadius; r < rows - blurRadius; r++) {
            for (int c = blurRadius; c < cols - blurRadius; c++) {
                Color[] neighbors = calcSquareNeighbors(oldImg, r, c, blurRadius);
                Color avgColor = calcAvgColor(neighbors);
                blurredImg[r][c] = avgColor;
            }
        }
        return blurredImg;
    }

    // helper method
    public static Color[] calcSquareNeighbors(Color[][] pixels, int centerRow, int centerCol, int radius) {
        // Calc total number of pixels in the square neighborhood
        Color[] neighbors = new Color[(2 * radius + 1) * (2 * radius + 1)];
        int index = 0;
        // Negative sign for radius to do a full iteration over neighborhood of current
        // pixel
        for (int r = -radius; r <= radius; r++) {
            for (int c = -radius; c <= radius; c++) {
                neighbors[index++] = pixels[centerRow + r][centerCol + c];
            }
        }
        return neighbors;
    }

    // Helper method to calculate the average color of an array of colors
    // calculates the average color by using RGB values of each pixel
    public static Color calcAvgColor(Color[] colors) {
        int totalRed = 0;
        int totalGreen = 0;
        int totalBlue = 0;
        for (Color pixel : colors) {
            totalRed += pixel.getRed();
            totalGreen += pixel.getGreen();
            totalBlue += pixel.getBlue();
        }
        int size = colors.length;
        int avgRed = totalRed / size;
        int avgGreen = totalGreen / size;
        int avgBlue = totalBlue / size;
        avgRed = Math.min(MAX_COLOR_VAL, Math.max(0, avgRed));
        avgGreen = Math.min(MAX_COLOR_VAL, Math.max(0, avgGreen));
        avgBlue = Math.min(MAX_COLOR_VAL, Math.max(0, avgBlue));

        return new Color(avgRed, avgGreen, avgBlue);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // the last else if is to reset the image back to original
        // got the code from above in ImageEditorPanel
        char keyChar = e.getKeyChar();
        if (keyChar == 'h') {
            pixels = flipHoriz(pixels);
        } else if (keyChar == 'f') {
            pixels = flipVert(pixels);
        } else if (keyChar == 'g') {
            pixels = grayscale(pixels);
        } else if (keyChar == 'v') {
            pixels = vintage(pixels);
        } else if (keyChar == 'b') {
            pixels = blur(pixels, BLUR_RADIUS);
        } else if (keyChar == 'c') {
            pixels = contrast(pixels, CONTRAST_FACTOR);
        } else if (keyChar == 't') {
            pixels = blueTint(pixels, BLUE_FACTOR);
        } else if (keyChar == 'r') {
            BufferedImage imageIn = null;
            try {
                imageIn = ImageIO.read(new File("CITY.jpg"));
            } catch (IOException ex) {
                System.out.println(ex);
                System.exit(1);
            }
            pixels = makeColorArray(imageIn);
        }
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
