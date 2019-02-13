package appSW.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import utils.AppDialogs;

public abstract class AbstractPreferenceView implements ActionListener{
	
	private final String TITLE;
	
	private JPanel content = new JPanel();
	private JPanel completeView = new JPanel();
	
	protected abstract void applyChanges();
	protected abstract void restoreToDefaults();
	protected abstract void importPreferences();
	
	public AbstractPreferenceView(String title) {
		super();
		this.TITLE = title;
		completeView.setLayout(new BorderLayout());
		JLabel label = new JLabel();
		label.setText(TITLE + " Preferences");
		label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 8));
		label.setBackground(Color.LIGHT_GRAY);
		label.setOpaque(true);
		completeView.add(label, BorderLayout.NORTH);
		completeView.add(new JScrollPane(content), BorderLayout.CENTER);
		content.setOpaque(true);
		content.setBackground(new Color(225, 225, 225));
		completeView.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		JPanel bottom = new JPanel();
		SpringLayout layout = new SpringLayout();
		bottom.setLayout(layout);
		
		JButton button1 = new JButton("Apply Changes");
		button1.addActionListener(this);
		bottom.add(button1);
		JButton button2= new JButton("Restore To Defaults");
		button2.addActionListener(this);
		bottom.add(button2);
		layout.putConstraint(SpringLayout.EAST, button1, -3, SpringLayout.EAST, bottom);
		layout.putConstraint(SpringLayout.EAST, button2, -3, SpringLayout.WEST, button1);
		layout.putConstraint(SpringLayout.NORTH, button1, 3, SpringLayout.NORTH, bottom);
		layout.putConstraint(SpringLayout.NORTH, button2, 3, SpringLayout.NORTH, bottom);
		bottom.setPreferredSize(new Dimension(200, 50));
		
		completeView.add(bottom, BorderLayout.SOUTH);
	}
	
	@Override
	public String toString() {
		return TITLE;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
		case "Apply Changes":
			if(AppDialogs.changePreferences(completeView) == 0)
				applyChanges();
			break;
		case "Restore To Defaults":
			if(AppDialogs.restoreToDefaults(completeView, TITLE + " Preferences") == 0)
				restoreToDefaults();
			break;
		}
	}
	
	public JPanel getContent() {
		return content;
	}
	
	public JPanel getViewToRender() {
		return completeView;
	}
}
