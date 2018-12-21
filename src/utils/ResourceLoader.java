package utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

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
	
	static {
		MPLUS = loadFont("mplus-2m-bold.ttf", 12);
		VAST_SHADOW = loadFont("VastShadow-Regular.ttf", 12).deriveFont(35);
		loadFont("KosugiMaru-Regular.ttf", 12);
		SOLDER = loadImage("solderIcon.png");
		EDIT = loadImage("editIcon.png");
		SELECT = loadImage("selectIcon.png");
		ADD_ROW = loadImage("addRowIcon.png");
		ADD_COLUMN = loadImage("addColumnIcon.png");
		REMOVE_ROW = loadImage("removeRowIcon.png");
		REMOVE_COLUMN = loadImage("removeColumnIcon.png");
		
	}
	
	public static String getHTMLString (String filename) throws IOException, URISyntaxException {
		URL url = ResourceLoader.class.getResource("/html/" + filename);
		byte[] encoded = Files.readAllBytes(Paths.get(url.toURI()));
		Charset ascii = Charset.forName("US-ASCII");
		return new String(encoded, ascii);
	}
	
	public static Node loadFXML(String filename) throws IOException {
		return loadFXMLLoader(filename).load();
	}
	
	public static FXMLLoader loadFXMLLoader(String filename) {
		return new FXMLLoader(ResourceLoader.class.getResource("/fxml/" + filename));
	}
	
	
	private static BufferedImage loadImage(String fileName) {
		BufferedImage bi = null;
		try{
			URL url = ResourceLoader.class.getResource("/icons/" + fileName);
			File iconFile = new File(url.toURI());
			FileInputStream fis = new FileInputStream(iconFile);
			bi = ImageIO.read(fis);
		}catch(IOException ie) {
			ie.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return bi;
	}
	
	
	private static Font loadFont(String fileName, int size) {
		Font font = null;
		try {
			URL url = ResourceLoader.class.getResource("/fonts/" + fileName);
			File fontFile = new File(url.toURI());
			font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(size);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));
		    javafx.scene.text.Font.loadFont(url.toExternalForm(), size);
		} catch (IOException e) {
		    e.printStackTrace();
		} catch(FontFormatException e) {
		    e.printStackTrace();
		} catch (URISyntaxException e) {
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
