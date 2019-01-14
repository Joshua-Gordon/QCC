package javafx.appUI.appViews.gateChooser;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.framework.AppCommand;
import javafx.framework.AppStatus;
import javafx.framework.Project;
import javafx.framework.gateModels.GateModel;

public class CustomOracleChooser extends AbstractGateChooser {
	private boolean initialized = false;
	
	public CustomOracleChooser() {
		super("Custom Oracles");
	}
	
	public void initializeGates() {
		Project p = AppStatus.get().getFocusedProject();
		if(p != null) {
			for(GateModel s : p.getCustomOracles().getGateModelIterable())
				addGateModel(s);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		button.setVisible(true);
		button.setText("Create Custom Oracle");
		initializeGates();
		initialized = true;
	}
	
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		AppStatus status = AppStatus.get();
		
		if(source == status && methodName.equals("setFocusedProject") && initialized) {
			removeAllGateModels();
			Project p = (Project) args[0];
			for(GateModel s : p.getCustomOracles().getGateModelIterable())
				addGateModel(s);
		}
		
		Project p = status.getFocusedProject();
		if(p != null && source == p.getCustomOracles() && initialized) {
			if(methodName.equals("put")) {
				GateModel replacement = (GateModel) args[0];
				removeGateModelByName(replacement.getName());
				addGateModel(replacement);
			} else if(methodName.equals("replace") ) {
				String name = (String) args[0];
				GateModel replacement = (GateModel) args[1];
				removeGateModelByName(name);
				
				String newName = replacement.getFormalName();
				if(!newName.equals(name))
					removeGateModelByName(newName);	
				addGateModel(replacement);
			} else if(methodName.equals("remove")) {
				String name = (String) args[0];
				removeGateModelByName(name);
			}
		}
		return false;
	}

	@Override
	public void buttonAction() {
		AppCommand.doAction(AppCommand.CREATE_ORACLE_GATE);
	}

}
