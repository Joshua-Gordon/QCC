package appUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import utils.ResourceLoader;

@SuppressWarnings("serial")
public class ConsoleUI extends AbstractAppViewUI{
	
	private JTextPane console = new JTextPane();
	private Window w;
	
	public ConsoleUI(Window w) {
		super("Console");
		this.w = w;
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 10, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		JLabel label = new JLabel(getName());
		add(label, gbc);
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 10, 3, 10);
		gbc.gridx = 0;
		gbc.gridy = 1;
		console.setEditable(false);
		console.setFont(ResourceLoader.MPLUS);
		console.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane container = new JScrollPane(console);
		container.setPreferredSize(new Dimension(200, 100));
		container.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		add(container, gbc);
		JButton button = new JButton("Clear");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 5, 10);
		add(button, gbc);
		
	}
	
	public void clear() {
		Document d = console.getDocument();
		try {
			d.remove(0, d.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void println(Object o) {
		consoleWrite(o.toString() + "\n", Color.BLACK);
	}
	
	public void print(Object o) {
		consoleWrite(o.toString(), Color.BLACK);
	}
	
	public void printlnErr(Object o) {
		consoleWrite(o.toString() + "\n", Color.RED);
	}
	
	public void printErr(Object o) {
		consoleWrite(o.toString(), Color.RED);
	}
	
	private void consoleWrite(String msg, Color c){
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        Document d = console.getDocument();
        int len = d.getLength();
        try {
			d.insertString(len, msg, aset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
        console.setCaretPosition(d.getLength());
    }

	@Override
	public void changeVisibility(boolean visible) {
		setVisible(visible);
		JSplitPane splitPane = w.getConsoleSplitPane();
		splitPane.setEnabled(visible);
	}
	
	
}
