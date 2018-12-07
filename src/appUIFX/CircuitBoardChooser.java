package appUIFX;

import java.net.URL;
import java.util.ResourceBundle;

import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.solderedGates.Solderable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class CircuitBoardChooser extends AbstractGateChooser implements ChangeListener<Boolean> {
	private boolean initialized = false;
	
	public CircuitBoardChooser() {
		super("Circuit Boards");
	}
	
	public void initializeGates() {
		Project p = AppStatus.get().getFocusedProject();
		if(p != null) {
			for(Solderable s : p.getSubCircuits().valueIterable())
				addSolderable(s);
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
	
	@Override
	public void receive(Object source, String methodName, Object... args) {
		if(source == AppStatus.get() && methodName == "setFocusedProject" && initialized) {
			removeAllSolderables();
			Project p = (Project) args[0];
			for(Solderable s : p.getSubCircuits().valueIterable())
				addSolderable(s);
		}
	}

	@Override
	public void buttonAction() {
		
	}

	@Override
	public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
		
	}

}
