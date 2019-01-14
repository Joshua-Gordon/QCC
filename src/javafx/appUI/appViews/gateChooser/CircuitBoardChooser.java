package javafx.appUI.appViews.gateChooser;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.framework.AppCommand;
import javafx.framework.AppStatus;
import javafx.framework.Project;
import javafx.framework.gateModels.GateModel;

public class CircuitBoardChooser extends AbstractGateChooser {
	private boolean initialized = false;
	
	public CircuitBoardChooser() {
		super("Circuit Boards");
	}
	
	public void initializeGates() {
		Project p = AppStatus.get().getFocusedProject();
		if(p != null) {
			for(GateModel s : p.getCircuitBoardModels().getGateModelIterable())
				addGateModel(s);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		button.setVisible(true);
		button.setText("Create Circuit Board");
		initializeGates();
		initialized = true;
	}
	
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		AppStatus status = AppStatus.get();
		
		if(source == status && methodName == "setFocusedProject" && initialized) {
			removeAllGateModels();
			Project p = (Project) args[0];
			for(GateModel s : p.getCircuitBoardModels().getGateModelIterable())
				addGateModel(s);
		}
		
		Project p = status.getFocusedProject();
		if(p != null && source == p.getCircuitBoardModels() && initialized) {
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
		AppCommand.doAction(AppCommand.CREATE_CIRCUIT_BOARD);
	}

}
