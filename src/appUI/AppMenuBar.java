package appUI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import preferences.AppPreferences;

@SuppressWarnings("serial")
public class AppMenuBar extends JMenuBar {
	
	private Window window;
	
	public AppMenuBar(Window window) {
		super();
		this.window = window;
		
		JMenu menu, subMenu, subSubMenu;
		
		menu = new JMenu("File");
		menu.add(mkItem("New Circuit"));
		menu.add(mkItem("Open Circuit"));
		menu.add(mkItem("Save Circuit as"));
		menu.add(mkItem("Save"));
		menu.addSeparator();
			subMenu = new JMenu("Export to");
			subMenu.add(mkItem("QUIL"));
			subMenu.add(mkItem("QASM"));
			subMenu.add(mkItem("Quipper"));
		menu.add(subMenu);
		menu.addSeparator();
		menu.add(mkItem("Preferences"));
		add(menu);
		
		menu = new JMenu("Edit");
			subMenu = new JMenu("Set Gate");
			subMenu.add(mkItem("Hadamard"));
				subSubMenu = new JMenu("Pauli Gates");
				subSubMenu.add(mkItem("I"));
				subSubMenu.add(mkItem("X"));
				subSubMenu.add(mkItem("Y"));
				subSubMenu.add(mkItem("Z"));
			subMenu.add(subSubMenu);
			subMenu.add(mkItem("Measure"));
			subMenu.add(mkItem("CNot"));
			subMenu.add(mkItem("Swap"));
		menu.add(subMenu);
		menu.addSeparator();
		menu.add(mkItem("Add Row"));
		menu.add(mkItem("Add Column"));
		menu.add(mkItem("Remove Last Row"));
		menu.add(mkItem("Remove Last Column"));
		add(menu);
		menu = new JMenu("Run");
			subMenu = new JMenu("Language");
			subMenu.add(mkItem("Run QUIL"));
			subMenu.add(mkItem("Run QASM"));
		menu.add(subMenu);
		add(menu);
		
		menu = new JMenu("View");
		menu.add(mkViewItem(window.getConsole()));
		add(menu);
		
	}
	
	private JMenuItem mkItem(String label) {
		JMenuItem item = new JMenuItem(label);
		if(AppPreferences.prefExists("Action Commands", label)) {
			int key = AppPreferences.getInt("Action Commands", label);
			item.setMnemonic(key);
			item.setAccelerator(KeyStroke.getKeyStroke(key, KeyEvent.CTRL_DOWN_MASK));
		}
		item.addActionListener(window.getKeyboard());
		return item;
	}
	
	private JMenuItem mkViewItem(AbstractAppViewUI view){
		JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem(view.getName());
		checkBox.setSelected(view.isVisible());
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.changeVisibility(checkBox.isSelected());
			}
		});
		return checkBox;
	}
	
	
	
}
