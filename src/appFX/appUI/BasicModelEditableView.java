package appFX.appUI;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.Project;
import appFX.framework.Project.ProjectHashtable;
import appFX.framework.gateModels.BasicModel;
import appFX.framework.gateModels.BasicModel.BasicModelType;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.GateModel.NameTakenException;
import appFX.framework.gateModels.PresetGateType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.customCollections.ImmutableArray;

public class BasicModelEditableView extends AppFXMLComponent implements Initializable, ChangeListener<BasicModelType> {
	
	public TextField name, symbol;
	public ComboBox<BasicModelType> modelType;
	public TextArea description;
	public HBox definitionStatement;
	public VBox definition, parameters;
	public Button addKrausButton, createButton;
	
	private BasicModel gm;
	private String tempName = "";
	private Stage stage;
	private boolean editAsNewModel;
	private ListSelectionPaneWrapper krausListSelection;
	private ListSelectionPaneWrapper parameterSelection;
	
	
	
	
	public static void createNewGate() {
		openGateEditableView(null, true);
	}
	
	
	
	
	
	public static void createNewGate(String name) {
		Stage primary = AppStatus.get().getPrimaryStage();
		
		Stage dialog = new Stage();
		dialog.setScene(new Scene((Parent) new BasicModelEditableView(dialog, name).loadAsNode(), 650, 700));
		dialog.initOwner(primary);
		dialog.initModality(Modality.APPLICATION_MODAL);
		
		dialog.setTitle("Create new Gate");
		
		dialog.showAndWait();
	}
	
	
	
	
	
	
	
	public static void editGate(String name) {
		openGateEditableView(name, false);
	}
	
	
	
	
	
	public static void editAsNewGate(String name) {
		openGateEditableView(name, true);
	}
	
	
	
	
	
	private static void openGateEditableView(String name, boolean editAsNewModel) { 
		Stage primary = AppStatus.get().getPrimaryStage();
		Project p = AppStatus.get().getFocusedProject();
		BasicModel gm = name == null ? null : (BasicModel) p.getGateModel(name);
		
		
		
		Stage dialog = new Stage();
		dialog.setScene(new Scene((Parent) new BasicModelEditableView(dialog, gm, editAsNewModel).loadAsNode(), 650, 700));
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
	
	
	
	
	
	
	private BasicModelEditableView (Stage stage, String name) {
		this(stage, null, true);
		this.tempName = name;
	}
	
	
	
	
	
	private BasicModelEditableView (Stage stage, boolean editAsNewModel) {
		this(stage, null, editAsNewModel);
	}
	
	
	
	
	
	
	private BasicModelEditableView(Stage stage, BasicModel gm, boolean editAsNewModel) {
		super("GateEditableView.fxml");
		this.gm = gm;
		this.stage = stage;
		this.editAsNewModel = editAsNewModel;
	}

	
	
	
	
	
	public void buttonCancel(ActionEvent e) {
		stage.close();
	}
	
	
	
	
	
	
	public void buttonCreate(ActionEvent e) {
		BasicModel gmNew = checkRequiredFields();
		
		if(gmNew == null)
			return;
		
		Project p = AppStatus.get().getFocusedProject();
		ProjectHashtable pht = p.getCustomGates();
		
		if (!editAsNewModel && gm != null) {
			Optional<ButtonType> options = AppAlerts.showMessage(stage, "Apply changes to " + gm.getName(), 
					"Are you sure you want to change gate model \"" + gm.getName() + "\"? "
							+ "All instances of the previous gate model in this project will be changed to the new implementation.", AlertType.WARNING);
			if(options.get() == ButtonType.CANCEL)
				return;
			if(!gmNew.getName().equals(gm.getName()) && p.containsGateModel(gmNew.getFormalName())) {
				options = AppAlerts.showMessage(stage, "Override Gate Model " + gmNew.getName(), 
						"A Gate Model with the name \"" + gmNew.getName() + "\" already exists, "
								+ "do you want to override this gate model?"
								+ " All instances of the previous implementation of \""
								+ gmNew.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(options.get() == ButtonType.CANCEL)
					return;
			}
			if(options.get() == ButtonType.CANCEL)
				return;
			
			pht.replace(gm.getFormalName(), gmNew);
		} else {
			if(pht.containsGateModel(gmNew.getFormalName())) {
				Optional<ButtonType> options = AppAlerts.showMessage(stage, "Override Gate Model " + gmNew.getName(), 
						"A Gate Model with the name \"" + gmNew.getName() + "\" already exists, "
								+ "do you want to override this gate model?"
								+ " All instances of the previous implementation of \""
								+ gmNew.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(options.get() == ButtonType.CANCEL)
					return;
			}
			
			pht.put(gmNew);
		}
		
		AppCommand.doAction(AppCommand.OPEN_GATE, gmNew.getFormalName());
		
		stage.close();
	}
	
	
	
	
	
	
	
	private BasicModel checkRequiredFields() {

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
		
		
		Iterable<Node> children = modelType.getValue() == BasicModelType.POVM ? krausListSelection : definition.getChildren();
		
		String[] definitions = new String[definition.getChildren().size()];
		
		if(definitions.length == 0) {
			definition.setStyle("-fx-border-width: 3; -fx-border-color: #ff000033");
			AppAlerts.showMessage(stage, "Unfilled prompts", "At least one Kraus Matrix must be defined", AlertType.ERROR);
			return null;
		} else {
			definition.setStyle("");
		}
		
		
		int i = 0;
		for(Node n : children) {
			TextField  tf = ((TextField)((HBox)n).getChildren().get(1));
			if(tf.getText().equals("")) {
				tf.setStyle("-fx-background-color: #ff000033");
				AppAlerts.showMessage(stage, "Unfilled prompts", "definition can not be blank", AlertType.ERROR);
				return null;
			} else {
				tf.setStyle("");
			}
			definitions[i++] = tf.getText();
		}
		
		String[] paramTemp = new String[parameterSelection.size()];
		
		i = 0;
		for(Node n : parameterSelection) {
			TextField tf = (TextField) n;
			
			String param = tf.getText();
			
			if(param.equals("")) {
				tf.setStyle("-fx-background-color: #ff000033");
				AppAlerts.showMessage(stage, "Unfilled prompts", "Parameter name can not be blank", AlertType.ERROR);
				return null;
			} else if(!param.matches(GateModel.PARAMETER_REGEX)) {
				AppAlerts.showMessage(stage, "Inproper parameter scheme", GateModel.IMPROPER_PARAMETER_SCHEME_MSG, AlertType.ERROR);
				tf.setStyle("-fx-background-color: #ff000033");
				return null;
			} else {
				
				for(int j = 0; j < i; j++) {
					if(param.equals(paramTemp[j])) {
						AppAlerts.showMessage(stage, "Inproper parameter scheme", "There are more than one parameter with the same name", AlertType.ERROR);
						tf.setStyle("-fx-background-color: #ff000033");
						return null;
					}
				}
				
				
				tf.setStyle("");
			}
			
			paramTemp[i++] = tf.getText();
		}
		
		BasicModel gm = null;
		try {
			gm = new BasicModel(name.getText(), symbol.getText(), description.getText(), paramTemp, modelType.getValue(), definitions);
		} catch (DefinitionEvaluatorException e) {
			AppAlerts.showMessage(stage, "Definition error", e.getMessage(), AlertType.ERROR);
			HBox hbox = (HBox) definition.getChildren().get(e.getDefinitionNumber());
			hbox.getChildren().get(1).setStyle("-fx-background-color: #ff000033");
		}
		
		return gm;
	}
	
	
	public void addParameter(ActionEvent ae) {
		addParameter("");
	}
	
	private void addParameter(String name) {
		parameterSelection.addElementToEnd(new TextField(name));
	}
	
	
	
	public void addKrausMatrix(ActionEvent e) {
		addKrausView("");
	}

	
	
	
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setUp();
		setUI();
	}
	
	
	
	
	
	private void setUp() {
		ObservableList<BasicModelType> items = modelType.getItems();
		for(BasicModelType gmt : BasicModelType.values())
			items.add(gmt);
		modelType.valueProperty().addListener(this);
	}
	
	
	
	
	
	
	private void setUI() {
		if(!editAsNewModel && gm != null)
			createButton.setText("Apply Changes");
		
		krausListSelection = new KrausSelection();
		parameterSelection = new ParameterList();
		
		if(gm != null) {
			name.setText(gm.getName());
			symbol.setText(gm.getSymbol());
			description.setText(gm.getDescription());
			
			modelType.getSelectionModel().select(gm.getGateModelType());
			
			for(String param : gm.getArguments())
				addParameter(param);
			
			setDefinitionUI(gm.getGateModelType(), gm);
		} else {
			name.setText(tempName);
			modelType.getSelectionModel().select(BasicModelType.UNIVERSAL);
			setDefinitionUI(BasicModelType.UNIVERSAL, null);
		}
	}
	
	
	
	
	
	
	
	private void setDefinitionUI(BasicModelType gmt, BasicModel gm) {
		definitionStatement.getChildren().clear();
		definition.getChildren().clear();
		
		switch (gmt) {
		case HAMILTONIAN:
			addKrausButton.setDisable(true);
			addKrausButton.setVisible(false);
			
			String latex = "\\text{The model is specified by matrix designated by } \\( e ^ {Ht}\\) \\text{ where } \\( H \\) \\text{ is defined by:} ";
			definitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			Node n = new LatexNode(" $$ H = $$ ");
			TextField tf = new TextField();
			HBox hbox = new HBox(n, tf);
			hbox.setSpacing(5);
			hbox.setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(tf, Priority.ALWAYS);
			definition.getChildren().add(hbox);
			
			if(gm != null)
				tf.setText(gm.getUserInput().get(0));
			
			
			break;
		case POVM:
			addKrausButton.setDisable(false);
			addKrausButton.setVisible(true);
			
			latex = "\\text{The model is specified by kraus matricies } \\( (k_1, k_2, k_3, ... k_n) \\) \\text{ where } \\( \\sum_{ i = 1 } ^ { n } k_i k_i ^ * = I \\) \\text{ : }";
			definitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			if(gm != null) {
				ImmutableArray<String> inputMatrixes = gm.getUserInput();
				for(String inputMat : inputMatrixes)
					addKrausView(inputMat);
			} else {
				addKrausView("");
			}
			
			break;
		case UNIVERSAL:
			addKrausButton.setDisable(true);
			addKrausButton.setVisible(false);
			
			latex = "\\text{The model is specified by a universal gate } \\( U \\) \\text{ described by the matrix:} ";
			definitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			n = new LatexNode(" $$ U = $$ ");
			tf = new TextField();
			hbox = new HBox(n, tf);
			hbox.setSpacing(5);
			hbox.setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(tf, Priority.ALWAYS);
			
			definition.getChildren().add(hbox);
			
			if(gm != null)
				tf.setText(gm.getUserInput().get(0));
			
			break;
			
			default:
			break;
		}
	}
	
	
	
	
	private void addKrausView(String input) {
		
		ObservableList<Node> childs = definition.getChildren();
		int size = childs.size();
		
		krausListSelection.addElementToEnd(new KrausView(size, input));
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
	
	
	
	private class KrausSelection extends ListSelectionPaneWrapper {
		
		public KrausSelection() {
			super(definition);
		}
		
		@Override
		protected boolean whenElementIsRemoved(int index, Node n) {
			while(++index != krausListSelection.size()) {
				KrausView kv = (KrausView) krausListSelection.getElement(index);
				kv.setLatex(index - 1);
			}
			return true;
		}
		
		@Override
		protected boolean whenElementIsAdded(int index, Node n) {
			return true;
		}
		
		@Override
		protected boolean whenElementMoves(int indexFirst, int indexNext) {
			String selected = ((KrausView) krausListSelection.getElement(indexFirst)).getString();
			
			int i = 0;
			for(; i < krausListSelection.size(); i++) {
				if(i == indexNext || i == indexFirst)
					break;
			}
			
			if(i == indexFirst) {
				KrausView next = (KrausView) krausListSelection.getElement(i);
				for(; i != indexNext; i++) {
					KrausView cur = next;
					next = (KrausView) krausListSelection.getElement(i + 1);
					cur.setString(next.getString());
				}
				next.setString(selected);
			} else if(i == indexNext) {
				for(; i < krausListSelection.size() && i <= indexFirst; i++) {
					KrausView cur = (KrausView) krausListSelection.getElement(i);
					String temp = cur.getString();
					cur.setString(selected);
					selected = temp;
				}
			}
			
			return false;
		}

		@Override
		protected boolean whenCleared() {
			return true;
		}
	}
	
	
	
	
	
	private class KrausView extends HBox {
		KrausView(int number, String input) {
			LatexNode lv = new LatexNode("$$ k_{" + number + "} = $$", 20, "#00000000", "#000000");
			TextField inputText = new TextField(input);
			ObservableList<Node> children = getChildren();
			children.addAll(lv, inputText);
			
			setSpacing(5);
			setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(inputText, Priority.ALWAYS);
		}
		
		void setLatex(int number) {
			getLatexNode().setLatex("$$ k_{" + number + "} = $$");
		}
		
		LatexNode getLatexNode() {
			return (LatexNode) getChildren().get(0);
		}
		
		String getString() {
			return ((TextField)getChildren().get(1)).getText();
		}
		
		void setString(String s) {
			((TextField)getChildren().get(1)).setText(s);
		}
	}




	@Override
	public void changed(ObservableValue<? extends BasicModelType> observable, BasicModelType oldValue,
			BasicModelType newValue) {
		setDefinitionUI(newValue, null);
	}
	
}
