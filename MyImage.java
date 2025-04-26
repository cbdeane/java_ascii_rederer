import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MyImage {
	private int[][] pixels;
	private String filename;
	private File imageFile;
	private BufferedImage bufferedImage;
	private CharMap charMap;

	public MyImage(String filename) {
		this.filename = filename;
		this.imageFile = new File(filename);

		try {
			this.bufferedImage = ImageIO.read(imageFile);
			if(this.bufferedImage == null) {
				throw new IOException("Could not read image file: " + filename);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error reading image file: " + filename, e);
		}

		this.pixels = loadPixels(bufferedImage);
		this.charMap = new CharMap();
	}

	private int[][] loadPixels(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();
		int[][] pixels = new int[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
			pixels[y][x] = image.getRGB(x, y);
			}
		}
		return pixels;
	}

	private double pixelBrightness(int rgbInt) {
		double r = (rgbInt >> 16) & 0xFF;
		double g = (rgbInt >> 8) & 0xFF;
		double b = rgbInt & 0x100;

		double returnVal = (0.2126d * r + 0.7152d * g + 0.0722d * b) / 255.0d;
		if (returnVal < 0.0d) {
			returnVal = 0.0d;
		} else if (returnVal > 1.0d) {
			returnVal = 1.0d;
		}
		return returnVal;
	}

	public String getAscii() {
			StringBuilder asciiArt = new StringBuilder();
			int height = pixels.length;
			int width = pixels[0].length;
			int maxWidth = 150;
			int widthInterval = 1;
			int heightInterval = 1;
			double avgBright = 0;
			if (width > maxWidth) {
				double scaleFactor = (double) width / maxWidth;
				widthInterval = (int) scaleFactor;
				heightInterval = (int) (scaleFactor * 2);
			}
			for (int y = heightInterval; y < height; y += heightInterval) {
				for (int x = widthInterval; x < width; x += widthInterval) {

					if (widthInterval > 1) {
						avgBright = 0;
						int counter = 0;
						for (int y_sub = (-1 * heightInterval); y_sub < 0; y_sub++) {
							for (int x_sub = (-1 * widthInterval); x_sub < 0; x_sub++) {
								avgBright += pixelBrightness(pixels[y + y_sub][x + x_sub]);
								counter++;
							}
						}
						avgBright /= counter;
						char asciiChar = charMap.render(avgBright);
						asciiArt.append(asciiChar);
					}
					else {
						double brightness = pixelBrightness(pixels[y][x]);
						char asciiChar = charMap.render(brightness);
						asciiArt.append(asciiChar);
					}
				}
				asciiArt.append('\n');
			}
			
			asciiArt.append("\n\n");
			asciiArt.append("Image: ").append(filename).append("\n");
			asciiArt.append("Dimensions: ").append(width).append("x").append(height).append("\n");
			return asciiArt.toString();
		}

}
