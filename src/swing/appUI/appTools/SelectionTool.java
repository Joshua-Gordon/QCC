package swing.appUI.appTools;

import javax.swing.ImageIcon;

import swing.appUI.Window;
import swing.framework.ExportGatesRunnable;
import swing.framework.ExportedGate;

public class SelectionTool extends Tool{

	public SelectionTool(Window w, ImageIcon icon) {
		super("Select Tool", w, icon);
	}

	@Override
	public void onSelected() {
		
	}

	@Override
	public void onUnselected() {
		
	}

}
