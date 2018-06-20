package utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

public class ResourceLoader {
	
	public static Font MPLUS;
	
	static {
		try {
			File fontFile = new File("res" + File.separator + "mplus-2m-bold.ttf");
			MPLUS = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(14f);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontFile));
		} catch (IOException e) {
		    e.printStackTrace();
		} catch(FontFormatException e) {
		    e.printStackTrace();
		}
	}
	
	
	
	
	
}
