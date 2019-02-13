package appSW.appUI.appTools;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import appSW.appUI.Window;
import utils.ResourceLoader;
import utils.ResourceLoader.SwingResources;

@SuppressWarnings("serial")
public class AppToolBar extends JToolBar{
	private ButtonGroup toolGroup = new ButtonGroup();
	private Window w;
	private Tool selected;
	
	public AppToolBar(Window w) {
		super();
		this.w = w;
		
		SwingResources sr = ResourceLoader.getSwingResources();
		
		
		setFloatable(false);
		
		selected = new SelectionTool(w, new ImageIcon(sr.SELECT));
		
//		Add Tools Here:
		addTool(selected);
		addTool(new SolderingTool(w, new ImageIcon(sr.SOLDER)));
		addTool(new EditTool(w, new ImageIcon(sr.EDIT)));
		
		addSeparator();
		addTool(new AddColumnTool(w, new ImageIcon(sr.ADD_COLUMN)));
		addTool(new RemoveColumnTool(w, new ImageIcon(sr.REMOVE_COLUMN)));
		addTool(new AddRowTool(w, new ImageIcon(sr.ADD_ROW)));
		addTool(new RemoveRowTool(w, new ImageIcon(sr.REMOVE_ROW)));
		
		
		((JToggleButton) getComponent(0)).setSelected(true);
	}
	
	private void addTool(Tool tool) {
		JToggleButton button = new JToggleButton(tool.getIcon());
		button.setToolTipText(tool.getName());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				Component comp = w.getDisplay();
				
				comp.removeMouseListener(selected);
				comp.removeMouseMotionListener(selected);
				comp.removeMouseWheelListener(selected);
				comp.removeKeyListener(selected);
				selected.onUnselected();
				
				selected = tool;
				
				comp.addMouseListener(tool);
				comp.addMouseMotionListener(tool);
				comp.addMouseWheelListener(tool);
				comp.addKeyListener(tool);
				tool.onSelected();
			}
		});
		toolGroup.add(button);
		add(button);
	}
	
	public void refreshSelectedTool() {
		if(selected != null) {
			selected.onUnselected();
			selected.onSelected();
		}
	}
	
	public void stopSelectedTool() {
		if(selected != null) {
			Component comp = w.getDisplay();
			comp.addMouseListener(selected);
			comp.addMouseMotionListener(selected);
			comp.addMouseWheelListener(selected);
			comp.addKeyListener(selected);
			selected.onSelected();
		}
	}
	
	public void restartTool() {
		if(selected != null) {
			Component comp = w.getDisplay();
			comp.removeMouseListener(selected);
			comp.removeMouseMotionListener(selected);
			comp.removeMouseWheelListener(selected);
			comp.removeKeyListener(selected);
			selected.onUnselected();
		}
	}
	
}
