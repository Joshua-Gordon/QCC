import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class AppMenuBar extends JMenuBar {
	
	private Window window;
	
	public AppMenuBar(Window window) {
		super();
		this.window = window;
		
		JMenu menu, subMenu, subSubMenu;
		
		menu = new JMenu("File");
		menu.add(addItem("Open Circuit", KeyEvent.VK_O));
		menu.add(addItem("Save Circuit as", KeyEvent.VK_A));
		menu.add(addItem("Save", KeyEvent.VK_S));
		menu.addSeparator();
			subMenu = new JMenu("Export to");
			subMenu.add(addItem("QUIL", KeyEvent.VK_ENTER));
			subMenu.add(addItem("QASM", KeyEvent.VK_Q));
			subMenu.add(addItem("Quipper", KeyEvent.VK_P));
		menu.add(subMenu);
		add(menu);
		
		menu = new JMenu("Edit");
			subMenu = new JMenu("Set Gate");
			subMenu.add(addItem("Hadamard", KeyEvent.VK_H));
				subSubMenu = new JMenu("Pauli Gates");
				subSubMenu.add(addItem("I", KeyEvent.VK_I));
				subSubMenu.add(addItem("X", KeyEvent.VK_X));
				subSubMenu.add(addItem("Y", KeyEvent.VK_Y));
				subSubMenu.add(addItem("Z", KeyEvent.VK_Z));
			subMenu.add(subSubMenu);
			subMenu.add(addItem("Measure", KeyEvent.VK_M));
			subMenu.add(addItem("CNot", KeyEvent.VK_C));
			subMenu.add(addItem("Swap", KeyEvent.VK_W));
		menu.add(subMenu);
		menu.addSeparator();
		menu.add(addItem("Add Row", KeyEvent.VK_DOWN));
		menu.add(addItem("Add Column", KeyEvent.VK_RIGHT));
		menu.add(addItem("Remove Last Row", KeyEvent.VK_UP));
		menu.add(addItem("Remove Last Column", KeyEvent.VK_LEFT));
		add(menu);
		menu = new JMenu("Run");
			subMenu = new JMenu("Language");
			subMenu.add(addItem("Run QUIL",KeyEvent.VK_R));
		menu.add(subMenu);
		add(menu);
	}
	
	private JMenuItem addItem(String label, int mnemonic) {
		JMenuItem item = new JMenuItem(label, mnemonic);
		item.setAccelerator(KeyStroke.getKeyStroke(mnemonic, KeyEvent.CTRL_DOWN_MASK));
		item.addActionListener(window.getKeyboard());
		return item;
	}
	
}
