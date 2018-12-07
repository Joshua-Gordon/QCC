package appUIFX;

import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppStatus;
import framework2FX.gateModels.PresetGateType;

public class PresetGatesView extends AbstractGateChooser {
	
	public PresetGatesView() {
		super("Preset Gates");
	}

	public void initializeGates() {
		for(PresetGateType dg : PresetGateType.values())
			addSolderable(dg.getModel());
	}
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		setButtonVisible(false);
		initializeGates();
	}

	@Override
	public void buttonAction() {}


	@Override
	public void receive(Object source, String methodName, Object... args) {
		
	}
	
}
