package appUI;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class GateChooserUI extends AbstractAppViewUI{
	private JTabbedPane tabPane;
	private Window w;
	
	public GateChooserUI(Window w) {
		super("Gates");
		this.w = w;
		setBackground(Color.LIGHT_GRAY);
		setLayout(new BorderLayout());
		tabPane = new JTabbedPane();
		tabPane.add("Common Gates", new JPanel());
		tabPane.add("Custom Gates", new JPanel());
		tabPane.add("Custom Oracles", new JPanel());
		add(tabPane, BorderLayout.CENTER);
		
	}
	
	@Override
	public void changeVisibility(boolean visible) {
		setVisible(visible);
		JSplitPane splitPane = w.getGateChooserSplitPane();
		if(visible) {
			splitPane.setDividerLocation(-100);
		}
		splitPane.setEnabled(visible);
	}

}
