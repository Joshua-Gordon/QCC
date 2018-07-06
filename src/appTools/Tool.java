package appTools;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import appUI.Window;

public abstract class Tool extends MouseAdapter implements KeyListener{
	protected Window window;
	protected ImageIcon icon; 
	
	public Tool(Window window, ImageIcon icon) {
		this.window = window;
		this.icon = icon;
	}
	
	public abstract void onSelected();
	public abstract void onUnselected();
	
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {};
	
	public ImageIcon getIcon() {
		return icon;
	}
}
