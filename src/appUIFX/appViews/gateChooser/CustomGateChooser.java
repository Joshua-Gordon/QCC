package appUIFX.appViews.gateChooser;

import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppCommand;
import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.gateModels.GateModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CustomGateChooser extends AbstractGateChooser implements ChangeListener<Boolean>{
	private boolean initialized = false;
	
	public CustomGateChooser() {
		super("Custom Gates");
	}
	
	public void initializeGates() {
		Project p = AppStatus.get().getFocusedProject();
		if(p != null) {
			for(GateModel s : p.getCustomGates().getGateModelIterable())
				addGateModel(s);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		button.setVisible(true);
		button.setText("Create Custom Gate");
		button.pressedProperty().addListener(this);
		initializeGates();
		initialized = true;
	}
	// TODO : FIX Immediately
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		AppStatus status = AppStatus.get();
		
		if(source == status && methodName.equals("setFocusedProject") && initialized) {
			removeAllGateModels();
			Project p = (Project) args[0];
			for(GateModel s : p.getCustomGates().getGateModelIterable())
				addGateModel(s);
		}
		
		Project p = status.getFocusedProject();
		if(p != null && source == p.getCustomGates() && initialized) {
			if(methodName.equals("put")) {
				GateModel replacement = (GateModel) args[0];
				removeGateModelByName(replacement.getName());
				addGateModel(replacement);
			}
			
			if(methodName.equals("replace") ) {
				String name = (String) args[0];
				GateModel replacement = (GateModel) args[1];
				removeGateModelByName(name);
				putGateModel(replacement);
			}
			
			if(methodName.equals("remove")) {
				String name = (String) args[0];
				removeGateModelByName(name);
			}
		}
		return false;
	}

	@Override
	public void buttonAction() {
		AppCommand.doAction(AppCommand.CREATE_DEFAULT_GATE);
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		
	}

}
