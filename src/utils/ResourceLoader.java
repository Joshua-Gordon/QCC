package utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class ResourceLoader {
	
	public static Font MPLUS;
	public static Font VAST_SHADOW;
	public static BufferedImage SOLDER;
	public static BufferedImage EDIT;
	public static BufferedImage SELECT;
	
	public static BufferedImage ADD_ROW;
	public static BufferedImage ADD_COLUMN;
	public static BufferedImage REMOVE_ROW;
	public static BufferedImage REMOVE_COLUMN;
	
	private static final Hashtable<String, File> TEMP_FILES = new Hashtable<>();
	public static final String TEMP_FILE_URL = "res" + File.separator + "tempFiles";
	
	static {
		MPLUS = loadFont("mplus-2m-bold.ttf");
		VAST_SHADOW = loadFont("VastShadow-Regular.ttf").deriveFont(35f);
		SOLDER = scaleTo(loadImage("solderIcon.png"), 30, 30);
		EDIT = addPadding(scaleTo(loadImage("editIcon.png"), 20, 20), 5);
		SELECT = addPadding(scaleTo(loadImage("selectIcon.png"), 22, 22), 4);
		ADD_ROW = scaleTo(loadImage("addRowIcon.png"), 30, 30);
		ADD_COLUMN = scaleTo(loadImage("addColumnIcon.png"), 30, 30);
		REMOVE_ROW = scaleTo(loadImage("removeRowIcon.png"), 30, 30);
		REMOVE_COLUMN = scaleTo(loadImage("removeColumnIcon.png"), 30, 30);
		
	}
	
	private static BufferedImage addPadding(BufferedImage bi, int padding) {
		BufferedImage temp  = new BufferedImage(bi.getWidth() + 2*padding, bi.getHeight() + 2*padding, bi.getType());
		AffineTransform transform = new AffineTransform();
		transform.translate(padding, padding);
		Graphics2D g2d = (Graphics2D) temp.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(bi, transform, null);
		return temp;
	}
	
	private static BufferedImage scaleTo(BufferedImage bi, int width, int height) {
		BufferedImage temp  = new BufferedImage(width, height, bi.getType());
		AffineTransform transform = new AffineTransform();
		transform.scale((double) width / (double) bi.getWidth(), (double) height / (double)bi.getHeight());
		Graphics2D g2d = (Graphics2D) temp.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(bi, transform, null);
		return temp;
	}
	
	private static BufferedImage loadImage(String fileName) {
		BufferedImage bi = null;
		File iconFile = new File("res" + File.separator + "icons" + File.separator + fileName);
		try{
			FileInputStream fis = new FileInputStream(iconFile);
			bi = ImageIO.read(fis);
		}catch(IOException ie) {
			ie.printStackTrace();
		}
		return bi;
	}
	
	
	private static Font loadFont(String fileName) {
		Font font = null;
		try {
			File fontFile = new File("res" + File.separator + "fonts" + File.separator + fileName);
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(12f);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));
		} catch (IOException e) {
		    e.printStackTrace();
		} catch(FontFormatException e) {
		    e.printStackTrace();
		}
		return font;
	}
	
	/**
	 * 
	 * Returns the Temporary file with the name "fileName"
	 * <p>
	 * Note that the file with the name "fileName " <br>
	 * must be created under the addTempFile() method first
	 * 
	 * @param fileName
	 * @return Temporary File
	 */
	public static File getTempFile(String fileName) {
		return TEMP_FILES.get(fileName);
	}
	
	/**
	 * 
	 * Creates a Temporary File under the name "fileName" that
	 * deletes itself after program execution
	 * <p>
	 * If file with "fileName" already exists, then the file will be replaced
	 * with a new File
	 * 
	 * 
	 * @param fileName
	 * @return Temporary File
	 */
	public static File addTempFile(String fileName) {
		String[] parts = getPrefixSuffix(fileName);
		
		if(TEMP_FILES.containsKey(fileName)) {
			File file = TEMP_FILES.get(fileName);
			file.delete();
		}
		
		File tempFile = null;
		try {
			tempFile = File.createTempFile(parts[0], parts[1]);
			tempFile.deleteOnExit();
			TEMP_FILES.put(fileName, tempFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	
	
	/**
	 * Removes Temporary File with the name "fileName"
	 * <p>
	 * Note that the file with the name "fileName " <br>
	 * must be created under the addTempFile() method first
	 * @param fileName
	 */
	public static void removeTempFile(String fileName) {
		File file = TEMP_FILES.get(fileName);
		file.delete();
		TEMP_FILES.remove(fileName);
	}
	
	
	private static String[] getPrefixSuffix(String fileName) {
		String[] parts = fileName.split(".");
		if(parts.length > 1) {
			String prefix = parts[0];
			for(int i = 0; i < parts.length - 1; i++)
				prefix += "." + parts[i];
			String suffix = "." + parts[parts.length - 1];
			parts = new String[]{prefix, suffix};
		}else {
			parts = new String[] {fileName, ""};
		}
		return parts;
	}
}
