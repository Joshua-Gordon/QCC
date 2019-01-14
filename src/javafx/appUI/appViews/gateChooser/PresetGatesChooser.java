package javafx.appUI.appViews.gateChooser;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.framework.gateModels.PresetGateType;

public class PresetGatesChooser extends AbstractGateChooser {
	
	public PresetGatesChooser() {
		super("Preset Gates");
	}

	public void initializeGates() {
		for(PresetGateType dg : PresetGateType.values())
			addGateModel(dg.getModel());
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		setButtonVisible(false);
		initializeGates();
	}

	@Override
	public void buttonAction() {}


	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		
		return false;
	}
	
}
