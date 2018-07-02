package appUI;


import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

@SuppressWarnings("serial")
public class AppToolBar extends JToolBar{
	private JToggleButton[] tools = new JToggleButton[2];
	private ButtonGroup toolGroup = new ButtonGroup();
	
	
	public AppToolBar() {
		super();
		tools[0] = new JToggleButton("Editor");
		tools[1] = new JToggleButton("Move");
		
		toolGroup.add(tools[0]);
		toolGroup.add(tools[1]);
		
		
		
		add(tools[0]);
		add(tools[1]);
		setFloatable(false);
	}
	
}
