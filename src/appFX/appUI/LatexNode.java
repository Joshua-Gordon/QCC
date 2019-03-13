package appFX.appUI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class LatexNode extends ImageView {
	
	private float fontSize;
	private String textColor;
	
	
	public LatexNode() {
		this("");
	}
	
	public LatexNode(String latex) {
		this(latex, 20);
	}
	
	public LatexNode(String latex, float fontSize) {
		this(latex, fontSize, "#000000");
	}
	
	public LatexNode(String latex, float fontSize, String textColor) {
		this(latex, fontSize, textColor, "#00000000");
	}
	
	public LatexNode(String latex, float fontSize, String textColor, String backColor) {
		this.fontSize = fontSize;
		this.textColor = textColor;
		rerender(latex);
	}
	
	public void setLatex(String latex) {
		rerender(latex);
	}
	
	public synchronized void rerender(String latex) {
		Thread thread = new Thread(()-> {
			
			TeXFormula tf = new TeXFormula(latex) ;
	        TeXIcon ti = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize, 0, Color.decode(textColor));
	        BufferedImage bimg = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
	        Graphics2D g2d = (Graphics2D) bimg.getGraphics();
	        JLabel label = new JLabel();
	        ti.paintIcon(label, g2d, 0, 0);
	        Image image = SwingFXUtils.toFXImage(bimg, null);
	        Platform.runLater(()-> {
	        	setImage(image);
	        });
		});
		thread.start();
	}

}
