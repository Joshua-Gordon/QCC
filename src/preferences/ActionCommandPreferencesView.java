package preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import appUI.AppDialogs;
import appUI.AppMenuBar;
import framework.Main;

@SuppressWarnings("serial")
public class ActionCommandPreferencesView extends AbstractPreferenceView{

	private GridBagConstraints gbc = new GridBagConstraints();
	
	private ArrayList<JLabel> labels = new ArrayList<>();
	private ArrayList<JTextField> fields = new ArrayList<>();
	private ArrayList<JButton> buttons = new ArrayList<>();
	private ArrayList<Integer> keycodes = new ArrayList<>();
	private ArrayList<Integer> keyIndexModified = new ArrayList<>(); 
	
	public ActionCommandPreferencesView() {
		super("Action Commands");
		content.setLayout(new GridBagLayout());
		gbc.insets = new Insets(4, 7, 7, 0);
		gbc.fill= GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel label = new JLabel("<html><u><b>Keyboard Shortcuts</b></u></html>");
		content.add(label, gbc);
		gbc.gridy++;
		
		addTableItem("Open Circuit");
		addTableItem("Save Circuit as");
		addTableItem("Save");
		addTableItem("QUIL");
		addTableItem("QASM");
		addTableItem("Quipper");
		addTableItem("Preferences");
		addTableItem("Hadamard");
		addTableItem("I");
		addTableItem("X");
		addTableItem("Y");
		addTableItem("Z");
		addTableItem("Measure");
		addTableItem("CNot");
		addTableItem("Swap");
		addTableItem("Add Row");
		addTableItem("Add Column");
		addTableItem("Remove Last Row");
		addTableItem("Remove Last Column");
		addTableItem("Run QUIL");
		addTableItem("Run QASM");
		
		
	}

	@Override
	protected void applyChanges() {
		for(Integer index : keyIndexModified) {
			AppPreferences.putInt("Action Commands", labels.get(index).getText().trim(), keycodes.get(index));
		}
		keyIndexModified.clear();
		Main.w.getFrame().setJMenuBar(new AppMenuBar(Main.w));
		Main.w.getFrame().validate();
	}

	@Override
	protected void restoreToDefaults() {
		keyIndexModified.clear();
		for(int i = 0; i < labels.size(); i++) {
			String command = labels.get(i).getText().trim();
			AppPreferences.put("Action Commands", command, null);
			int keyCode = AppPreferences.getInt("Action Commands", command);
			keycodes.set(i, keyCode);
			fields.get(i).setText(getKeyText(keyCode));
		}
		Main.w.getFrame().setJMenuBar(new AppMenuBar(Main.w));
		Main.w.getFrame().validate();
	}

	@Override
	protected void importPreferences() {
		keyIndexModified.clear();
		keycodes.clear();
		for(int i = 0; i < labels.size(); i++) {
			JLabel label = labels.get(i);
			keycodes.add(AppPreferences.getInt( "Action Commands", label.getText().trim()));
			fields.get(i).setText(getKeyText(keycodes.get(i)));
			fields.get(i).setCaretPosition(0);
		}
	}
	
	
	private void addTableItem(String actionCommand) {
		gbc.insets = new Insets(2, 5, 2, 0);
		gbc.weightx = 1;
		JLabel label = new JLabel(" " + actionCommand);
		label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		label.setOpaque(true);
		labels.add(label);
		content.add(label, gbc);
		gbc.insets = new Insets(2, 0, 2, 0);
		gbc.gridx++;
		gbc.weightx = .3;
		JTextField field = new JTextField();
		field.setPreferredSize(new Dimension(90, 25));
		field.setEditable(false);
		fields.add(field);
		content.add(field, gbc);
		gbc.insets = new Insets(2, 0, 2, 5);
		gbc.gridx++;
		gbc.weightx = 0;
		JButton button = new JButton("Change");
		button.addActionListener(new ButtonActionListener(buttons.size()));
		buttons.add(button);
		content.add(button, gbc);
		gbc.gridx -= 2;
		gbc.gridy++;
	}
	
	
	private String getKeyText(int keycode) {
		return KeyEvent.getModifiersExText(KeyEvent.SHIFT_DOWN_MASK) + " + "
				+ KeyEvent.getKeyText(keycode);
	}
	
	private boolean isAKeyModifier(int keyCode) {
		return keyCode == KeyEvent.VK_SHIFT ||
				keyCode == KeyEvent.VK_ALT ||
				keyCode == KeyEvent.VK_CONTROL;
	}
	
	private class ButtonActionListener implements ActionListener{
		private int keyIndex;
		public ButtonActionListener(int keyIndex) {
			this.keyIndex = keyIndex;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			KeyChooserDialog dialog = new KeyChooserDialog(getParent(), keyIndex);
			dialog.setVisible(true);
		}
		
	}
	
	
	private class KeyChooserDialog extends JDialog implements KeyListener{
		
		private int keyIndex;
		private Component parent;
		
		private KeyChooserDialog(Component parent, int keyIndex) {
			super(JOptionPane.getFrameForComponent(parent));
			this.keyIndex = keyIndex;
			this.parent = parent;
			setModal(true);
			setSize(new Dimension(120, 80));
			setLayout(new BorderLayout());
			JLabel label = new JLabel("  Press a Key");
			add(label, BorderLayout.CENTER);
			setLocationRelativeTo(parent);
			addKeyListener(this);
		}
		@Override
		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyCode();
			int keycodePresent = keycodes.get(keyIndex);
			if(keycode == keycodePresent){
				this.dispose();
			}else if(keycodes.contains(keycode)){
				AppDialogs.keyCodeUsed(this);
			}else if(isAKeyModifier(keycode)){
				AppDialogs.keyCodeNotValid(this);
			}else {
				keyIndexModified.add(keyIndex);
				keycodes.set(keyIndex, keycode);
				fields.get(keyIndex).setText(getKeyText(keycode));
				fields.get(keyIndex).validate();
				this.dispose();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}
		@Override
		public void keyTyped(KeyEvent e) {}
		
	}
}
