package preferences;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import appUI.AppDialogs;


public class QASMPreferencesView extends AbstractPreferenceView{
	
	private JTextField field = new JTextField();
		
	public QASMPreferencesView() {
		super("QASM");
				
				
		getContent().setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.insets = new Insets(3, 5, 3, 5);
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel label = new JLabel("Set QASM Interpreter Location:");
		getContent().add(label, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		field.setPreferredSize(new Dimension(100, 25));
		getContent().add(field, gbc);
		gbc.weightx = 0;
		gbc.gridx = 1;
		gbc.gridy = 1;
		JButton button = new JButton("Browse");
		button.addActionListener(new FileBrowser());
		getContent().add(button, gbc);
	}
			
			
	private class FileBrowser implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setDialogTitle("Find QASM Interpreter Path");
			
			final int option1 = fileChooser.showDialog(getContent().getParent(), "Select");
			if(option1 == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if(file.exists() && file.isFile()) {
					field.setText(file.getAbsolutePath());
					field.setCaretPosition(0);
				}else {
					AppDialogs.fileIsntValid(getContent().getParent(), file);
				}
			}
		}
	}
	
	
	
	
	
	@Override
	protected void applyChanges() {
		AppPreferences.put("QASM", "Interpreter Location", field.getText().trim());
	}
		
	@Override
	protected void restoreToDefaults() {
		AppPreferences.put("QASM", "Interpreter Location", null);
		field.setText(AppPreferences.get("QASM", "Interpreter Location"));
	}
		
	@Override
	protected void importPreferences() {
		field.setText(AppPreferences.get("QASM", "Interpreter Location"));
		field.setCaretPosition(0);
	}
	
	
}