package appFX.appUI;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.Project.ProjectHashtable;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.gateModels.GateModel.NameTakenException;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CircuitBoardPropertiesView extends AppFXMLComponent implements Initializable {
	
	public TextField name, symbol;
	public TextArea description;
	public VBox parameters;
	public Button createButton;
	
	private ParameterList parameterList;
	
	private Stage stage;
	private boolean editAsNew;
	private CircuitBoardModel cb;
	private String boardName;
	
	
	
	public static void createNewGate() {
		openCircuitBoardPropertiesEditableView(null, true);
	}
	
	
	
	public static void createNewGate(String name) {
		Stage primary = AppStatus.get().getPrimaryStage();
		
		Stage dialog = new Stage();
		dialog.setScene(new Scene((Parent) new CircuitBoardPropertiesView(dialog, name).loadAsNode(), 650, 700));
		dialog.initOwner(primary);
		dialog.initModality(Modality.APPLICATION_MODAL);
		
		dialog.setTitle("Create new Gate");
		
		dialog.showAndWait();
	}
	
	
	
	
	public static void editGate(String name) {
		openCircuitBoardPropertiesEditableView(name, false);
	}
	
	
	public static void editAsNewGate(String name) {
		openCircuitBoardPropertiesEditableView(name, true);
	}
	
	
	private static void openCircuitBoardPropertiesEditableView(String name, boolean editAsNewModel) { 
		Stage primary = AppStatus.get().getPrimaryStage();
		Project p = AppStatus.get().getFocusedProject();
		CircuitBoardModel gm = name == null ? null : (CircuitBoardModel) p.getGateModel(name);
		
		
		
		Stage dialog = new Stage();
		dialog.setScene(new Scene((Parent) new CircuitBoardPropertiesView(dialog, gm, editAsNewModel).loadAsNode(), 650, 700));
		dialog.initOwner(primary);
		dialog.initModality(Modality.APPLICATION_MODAL);
		
		if(gm == null)
			dialog.setTitle("Create new Gate");
		else if(editAsNewModel)
			dialog.setTitle("Coping " + gm.getName() + " to new Gate");
		else
			dialog.setTitle("Changing " + gm.getName() + " Gate");
		
		dialog.showAndWait();
	}
	
	public CircuitBoardPropertiesView(Stage stage, String name) {
		super("CircuitBoardProperties.fxml");
		this.stage = stage;
		this.cb = null;
		this.editAsNew = true;
		this.boardName = name;
	}
	
	public CircuitBoardPropertiesView(Stage stage, CircuitBoardModel cb, boolean editAsNew) {
		super("CircuitBoardProperties.fxml");
		this.stage = stage;
		this.cb = cb;
		this.editAsNew = editAsNew;
		this.boardName = null;
	}
	
	
	public void addParameter(ActionEvent ae) {
		addParameter("");
	}
	
	public void buttonCreate(ActionEvent ae) {
		CircuitBoardModel newModel = checkRequiredFields();
		
		if(newModel == null)
			return;
		
		Project p = AppStatus.get().getFocusedProject();
		ProjectHashtable pht = p.getCircuitBoardModels();
		
		if (!editAsNew && cb != null) {
			Optional<ButtonType> options = AppAlerts.showMessage(stage, "Apply changes to " + cb.getName(), 
					"Are you sure you want to change circuit board model \"" + cb.getName() + "\"? "
							+ "All instances of the previous circuit board model in this project will be changed to the new implementation.", AlertType.WARNING);
			if(options.get() == ButtonType.CANCEL)
				return;
			if(!newModel.getName().equals(cb.getName()) && p.containsGateModel(newModel.getFormalName())) {
				options = AppAlerts.showMessage(stage, "Override circuit board model " + newModel.getName(), 
						"A circuit board model with the name \"" + newModel.getName() + "\" already exists, "
								+ "do you want to override this circuit board model?"
								+ " All instances of the previous implementation of \""
								+ newModel.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(options.get() == ButtonType.CANCEL)
					return;
			}
			
			if(options.get() == ButtonType.CANCEL)
				return;
			
			pht.replace(cb.getFormalName(), newModel);
		} else {
			if(pht.containsGateModel(newModel.getFormalName())) {
				Optional<ButtonType> options = AppAlerts.showMessage(stage, "Override circuit board model " + newModel.getName(), 
						"A circuit board model with the name \"" + newModel.getName() + "\" already exists, "
								+ "do you want to override this circuit board model?"
								+ " All instances of the previous implementation of \""
								+ newModel.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(options.get() == ButtonType.CANCEL)
					return;
			}
			
			pht.put(newModel);
		}
		
		AppCommand.doAction(AppCommand.OPEN_GATE, newModel.getFormalName());
		
		stage.close();
	}
	
	
	
	
	
	private CircuitBoardModel checkRequiredFields() {
		
		if(name.getText() == null) {
			name.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Unfilled prompts", "Name must be defined", AlertType.ERROR);
			return null;
		} else if(!name.getText().matches(GateModel.NAME_REGEX)) {
			name.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Inproper name scheme", GateModel.IMPROPER_NAME_SCHEME_MSG, AlertType.ERROR);
			return null;
		} else {
			try {
				PresetGateType.checkName(name.getText());
				name.setStyle("");
			} catch (NameTakenException e) {
				AppAlerts.showMessage(stage, "The name chosen is exclusive to a Preset Gate", e.getMessage(), AlertType.ERROR);
				name.setStyle("-fx-background-color: #ff000033");
				return null;
			}
		}
		
		if(symbol.getText() == null) {
			symbol.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Unfilled prompts", "Symbol must be defined", AlertType.ERROR);
			return null;
		} else if(!symbol.getText().matches(GateModel.SYMBOL_REGEX)) {
			symbol.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Inproper symbol scheme", GateModel.IMPROPER_SYMBOL_SCHEME_MSG, AlertType.ERROR);
			return null;
		} else {
			symbol.setStyle("");
		}
		
		String[] parameters = new String[parameterList.size()];
		
		int i = 0;
		for(Node n : parameterList) {
			TextField  tf = (TextField) n;
			if(tf.getText().equals("")) {
				tf.setStyle("-fx-background-color: #ff000033");
				AppAlerts.showMessage(stage, "Unfilled prompts", "Parameter name can not be blank", AlertType.ERROR);
				return null;
			} else if(!tf.getText().matches(GateModel.PARAMETER_REGEX)) {
				AppAlerts.showMessage(stage, "Inproper parameter scheme", GateModel.IMPROPER_PARAMETER_SCHEME_MSG, AlertType.ERROR);
				tf.setStyle("-fx-background-color: #ff000033");
				return null;
			} else {
				tf.setStyle("");
			}
			parameters[i++] = tf.getText();
		}
		
		return cb == null? new CircuitBoardModel(name.getText(), symbol.getText(), description.getText(), 5, 5, parameters) :
			cb.createDeepCopyToNewName(name.getText(), symbol.getText(), description.getText(), parameters);
	}
	
	
	
	
	public void buttonCancel(ActionEvent ae) {
		stage.close();
	}
	
	public void addParameter(String parameter) {
		TextField tf = new TextField();
		tf.setText(parameter);
		VBox.setVgrow(tf, Priority.ALWAYS);
		parameterList.addElementToEnd(tf);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		parameterList = new ParameterList();
		
		if(cb != null) {
			name.setText(cb.getName());
			symbol.setText(cb.getSymbol());
			description.setText(cb.getDescription());
			
			if(!editAsNew)
				createButton.setText("Change Properties");
			
			for(String arg : cb.getArguments())
				parameterList.addElementToEnd(new TextField(arg));
		} else {
			if(boardName != null)
				name.setText(boardName);
		}
	}
	
	
	private class ParameterList extends ListSelectionPaneWrapper {

		public ParameterList() {
			super(parameters);
		}

		@Override
		protected boolean whenElementIsRemoved(int index, Node n) {
			return true;
		}

		@Override
		protected boolean whenElementIsAdded(int index, Node n) {
			return true;
		}

		@Override
		protected boolean whenElementMoves(int indexFirst, int indexNext) {
			return true;
		}

		@Override
		protected boolean whenCleared() {
			return true;
		}
		
	}
	
}
