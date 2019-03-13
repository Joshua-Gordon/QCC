package appFX.appUI;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.framework.AppStatus;
import appFX.framework.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.Project;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.CircuitBoardModel.RecursionException;
import appFX.framework.gateModels.GateModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.customCollections.ImmutableArray;

public class ParameterPrompt extends AppFXMLComponent implements Initializable{
	private final CircuitBoardModel cb;
	private final GateModel gm;
	private final Integer[] rows;
	private final int column;
	private final Stage stage;
	private boolean addedGateSuccessfully = false;
	
	@FXML
	private VBox parameters;
	@FXML
	private Button addGateButton;
	
	
	public ParameterPrompt (Project p, CircuitBoardModel cb, String gateName, Integer[] rows, int column) {
		super("ParameterPrompt.fxml");
		this.cb = cb;
		this.gm = p.getGateModel(gateName);
		this.rows = rows;
		this.column = column;
		stage = new Stage();
		stage.setTitle("Set parameters of " + gateName);
		stage.initOwner(AppStatus.get().getPrimaryStage());
		stage.initModality(Modality.APPLICATION_MODAL);
		loadNewScene(stage, 400, 500);
	}
	
	public void showAndWait() {
		stage.showAndWait();
	}
	
	
	@FXML
	private void addGate(ActionEvent ae) {
		String[] params = new String[gm.getArguments().size()];
		for(int i = 0; i < params.length; i++)
			params[i] = getInput(i);
		
		try {
			cb.placeGate(gm.getFormalName(), column, rows, params);
			addedGateSuccessfully = true;
		} catch (DefinitionEvaluatorException e) {
			AppAlerts.showMessage(stage, "Inproper Parameter Definition", e.getMessage(), AlertType.ERROR);
			int error = e.getDefinitionNumber();
			for(int i = 0; i < params.length; i++) {
				if(i == error)
					getTextField(i).setStyle("-fx-background-color: #ff000033");
				else
					getTextField(i).setStyle("");
			}
			return;
		} catch (RecursionException e2) {
			AppAlerts.showMessage(stage, "Recursion detected", e2.getMessage(), AlertType.ERROR);
		}
		stage.close();
	}
	
	
	public boolean addedGateSuccesfully() {
		return addedGateSuccessfully;
	}
	
	@FXML
	private void cancel(ActionEvent ae) {
		stage.close();
	}

	private TextField getTextField(int index) {
		HBox box = (HBox)parameters.getChildren().get(index);
		return (TextField) box.getChildren().get(1);
	}
	
	private String getInput(int index) {
		return getTextField(index).getText();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ImmutableArray<String> array = gm.getArguments();
		parameters.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5;");
		for(String s : array) {
			HBox box = new HBox();
			LatexNode ln = new LatexNode("\\(" + s + " = \\)");
			TextField tf = new TextField("0");
			HBox.setHgrow(tf, Priority.ALWAYS);
			box.setSpacing(5);
			box.setAlignment(Pos.CENTER_LEFT);
			box.getChildren().add(ln);
			box.getChildren().add(tf);
			parameters.getChildren().add(box);
		}
	}
	
}
