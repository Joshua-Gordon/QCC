package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.text.Font;

public class ResourceLoaderFX {
		
	private static final Hashtable<String, File> TEMP_FILES = new Hashtable<>();
	
	public static void loadFXResources() {
		loadAllJavaFXFonts();
	}
	
	private static void loadAllJavaFXFonts() {
		loadFont("mplus-2m-bold.ttf", 12);
		loadFont("VastShadow-Regular.ttf", 12);
		loadFont("KosugiMaru-Regular.ttf", 12);
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
	
	private static void loadFont(String fileName, int size) {
		URL url = ResourceLoader.class.getResource("/fonts/" + fileName);
		
		File f = null;
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Font.loadFont(stream, size);
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