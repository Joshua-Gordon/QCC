package appUIFX.appViews;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import appUIFX.AppAlerts;
import appUIFX.AppFXMLComponent;
import appUIFX.LatexNode;
import framework2FX.AppCommand;
import framework2FX.AppStatus;
import framework2FX.Project;
import framework2FX.Project.ProjectHashtable;
import framework2FX.UserDefinitions.DefinitionEvaluatorException;
import framework2FX.gateModels.DefaultModel;
import framework2FX.gateModels.DefaultModel.DefaultModelType;
import framework2FX.gateModels.GateModel.NameTakenException;
import framework2FX.gateModels.GateModelFactory;
import framework2FX.gateModels.PresetGateType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.customCollections.ImmutableArray;

public class GateEditableView extends AppFXMLComponent implements Initializable, ChangeListener<DefaultModelType> {
	
	public TextField name, symbol;
	public ComboBox<DefaultModelType> modelType;
	public TextArea description;
	public HBox definitionStatement;
	public VBox definition;
	public Button addKrausButton, createButton;
	

	
	private DefaultModel gm;
	private String tempName = "";
	private Stage stage;
	private boolean editAsNewModel;
	private int krausSelected = -1;
	
	
	public static void openGateEditableView() {
		openGateEditableView(null, true);
	}
	
	public static void openGateEditableView(String name) {
		Stage primary = AppStatus.get().getPrimaryStage();
		
		Stage dialog = new Stage();
		dialog.setScene(new Scene((Parent) new GateEditableView(dialog, name).loadAsNode(), 650, 700));
		dialog.initOwner(primary);
		dialog.initModality(Modality.APPLICATION_MODAL);
		
		dialog.setTitle("Create new Gate");
		
		dialog.showAndWait();
	}
	
	public static void openGateEditableView(DefaultModel gm, boolean editAsNewModel) {
		Stage primary = AppStatus.get().getPrimaryStage();
		
		Stage dialog = new Stage();
		dialog.setScene(new Scene((Parent) new GateEditableView(dialog, gm, editAsNewModel).loadAsNode(), 650, 700));
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
	
	private GateEditableView (Stage stage, String name) {
		this(stage, null, true);
		this.tempName = name;
	}
	
	private GateEditableView (Stage stage, boolean editAsNewModel) {
		this(stage, null, editAsNewModel);
	}
	
	private GateEditableView(Stage stage, DefaultModel gm, boolean editAsNewModel) {
		super("GateEditableView.fxml");
		this.gm = gm;
		this.stage = stage;
		this.editAsNewModel = editAsNewModel;
	}

	public void buttonCancel(ActionEvent e) {
		stage.close();
	}
	
	public void buttonCreate(ActionEvent e) {
		
		
		DefaultModel gmNew = checkRequiredFields();
		
		
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
			pht.replace(gm.getFormalName(), gmNew);
		} else {
			if(pht.containsGateModel(gmNew.getFormalName())) {
				Optional<ButtonType> options = AppAlerts.showMessage(stage, "Override Gate Model " + gm.getName(), 
						"A Gate Model with the name \"" + gm.getName() + "\" already exists, "
								+ "do you want to override this gate model?"
								+ " All instances of the previous implementation of \""
								+ gm.getName() + "\" in this project will be removed.", AlertType.WARNING);
				if(options.get() == ButtonType.CANCEL)
					return;
			}
			
			pht.put(gmNew);
		}
		
		AppCommand.doAction(AppCommand.OPEN_GATE, gmNew.getFormalName());
		
		stage.close();
	}
	
	
	
	DefaultModel checkRequiredFields() {
		
		
		
		if(name.getText() == null) {
			name.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Unfilled prompts", "Name must be defined", AlertType.ERROR);
			return null;
		} else if(!name.getText().matches("[a-zA-Z][\\w]*")) {
			name.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Inproper name scheme", "Name must be a letter followed by letters, digits, or underscores", AlertType.ERROR);
			return null;
		} else {
			try {
				PresetGateType.checkName(name.getText());
				name.setStyle("-fx-background-color: #ffffff");
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
		} else if(!symbol.getText().matches("[a-zA-Z][\\w\\s]*")) {
			symbol.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, "Inproper symbol scheme", "Symbol must be a letter followed by letters, digits, or underscores", AlertType.ERROR);
			return null;
		} else {
			symbol.setStyle("-fx-background-color: #ffffff");
		}
		
		ObservableList<Node> children = definition.getChildren();
		String[] definitions = new String[children.size()];
		
		if(definitions.length == 0) {
			definition.setStyle("-fx-border-width: 3; -fx-border-color: #ff000033");
			AppAlerts.showMessage(stage, "Unfilled prompts", "At least one Kraus Matrix must be defined", AlertType.ERROR);
			return null;
		} else {
			definition.setStyle("-fx-border-width: 0; -fx-border-color: #ffffff");
		}
		
		int i = 0;
		for(Node n : children) {
			TextField  tf = ((TextField)((HBox)n).getChildren().get(1));
			if(tf.getText().equals("")) {
				tf.setStyle("-fx-background-color: #ff000033");
				AppAlerts.showMessage(stage, "Unfilled prompts", "definition can not be blank", AlertType.ERROR);
				return null;
			} else {
				tf.setStyle("-fx-background-color: #ffffff");
			}
			definitions[i++] = tf.getText();
		}
		
		DefaultModel gm = null;
		try {
			gm = GateModelFactory.makeGateModel(name.getText(), symbol.getText(), description.getText(), modelType.getValue(), definitions);
		} catch (DefinitionEvaluatorException e) {
			AppAlerts.showMessage(stage, "Definition error", e.getMessage(), AlertType.ERROR);
			HBox hbox = (HBox) children.get(e.getDefinitionNumber());
			hbox.getChildren().get(1).setStyle("-fx-background-color: #ff000033");
		}
		
		return gm;
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
		ObservableList<DefaultModelType> items = modelType.getItems();
		for(DefaultModelType gmt : DefaultModelType.values())
			items.add(gmt);
		modelType.valueProperty().addListener(this);
	}
	
	private void setUI() {
		if(!editAsNewModel && gm != null)
			createButton.setText("Apply Changes");
		
		if(gm != null) {
			name.setText(gm.getName());
			symbol.setText(gm.getSymbol());
			description.setText(gm.getDescription());
			
			modelType.getSelectionModel().select(gm.getGateModelType());
			
			setDefinitionUI(gm.getGateModelType(), gm);
		} else {
			name.setText(tempName);
			modelType.getSelectionModel().select(DefaultModelType.UNIVERSAL);
			setDefinitionUI(DefaultModelType.UNIVERSAL, null);
		}
	}
	
	private void setDefinitionUI(DefaultModelType gmt, DefaultModel gm) {
		definitionStatement.getChildren().clear();
		definition.getChildren().clear();
		
		switch (gmt) {
		case HAMILTONIAN:
			addKrausButton.setDisable(true);
			addKrausButton.setVisible(false);
			
			String latex = "The model is specified by matrix designated by \\( e ^ {Ht}\\) where \\( H \\) is defined by: ";
			definitionStatement.getChildren().add(new LatexNode(latex, .7f));
			
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
			krausSelected = -1;
			
			latex = "The model is specified by kraus matricies \\( (k_1, k_2, k_3, ... k_n) \\) where \\( \\sum_{ i = 1 } ^ { n } k_i k_i ^ * = I \\) :";
			definitionStatement.getChildren().add(new LatexNode(latex, .7f));
			
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
			
			latex = "The model is specified by a universal gate \\( U \\) described by the matrix: ";
			definitionStatement.getChildren().add(new LatexNode(latex, .7f));
			
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

		KrausView vk = new KrausView(size, input);
		
		childs.add(vk);
	}
	
	private class KrausView extends HBox{
		
		KrausView(int number, String input) {
			LatexNode lv = new LatexNode("$$ k_{" + number + "} = $$", .7f, "#00000000", "#000000");
			TextField inputText = new TextField(input);
			Button button = new Button("x");
			
			ObservableList<Node> children = getChildren();
			
			children.addAll(lv, inputText, button);
			
			button.setOnAction(new KrausRemovedEvent());
			
			
			setSpacing(5);
			setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(inputText, Priority.ALWAYS);
			setPadding(new Insets(5, 5, 5, 5));
			addEventFilter(MouseEvent.DRAG_DETECTED, new KrausSelectedEvent());
			addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED, new KrausDroppedEvent());
			addEventFilter(MouseDragEvent.MOUSE_DRAG_ENTERED, (e) -> {
				int index = definition.getChildren().indexOf(this);
				if(index != krausSelected) {
					if(index < krausSelected)
						setStyle("-fx-border-width: 2 0 0 0; -fx-border-color: #00FF00;");
					else
						setStyle("-fx-border-width: 0 0 2 0; -fx-border-color: #00FF00;");
				}
			});
			addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED, (e) -> {
				setStyle("-fx-border-insets: 0 0 0 0; -fx-border-color: #00000000;");
			});
			
		}
		
		void setLatex(int number) {
			getLatexNode().setLatex("$$ k_{" + number + "} = $$");
		}
		
		KrausView get() {
			return this;
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
		
		private class KrausRemovedEvent implements EventHandler<ActionEvent> {

			@Override
			public void handle(ActionEvent event) {
				ObservableList<Node> krausViews = definition.getChildren();
				
				int i = 0;
				while(i != krausViews.size()) {
					Node kv = krausViews.get(i++);
					if(kv == get()) {
						definition.getChildren().remove(i - 1);
						krausViews.remove(get());
						break;
					}
				}
				i--;
				while(i != krausViews.size()) {
					KrausView kv = (KrausView) krausViews.get(i++);
					kv.setLatex(i-1);
				}
			}
			
		}
		
		private class KrausSelectedEvent implements EventHandler<MouseEvent> {
			
			@Override
			public void handle(MouseEvent event) {
				KrausView dragging = get();
				krausSelected = definition.getChildren().indexOf(dragging);
				dragging.startFullDrag();
			}
			
		}
		
		private class KrausDroppedEvent implements EventHandler<MouseEvent> {
			@Override
			public void handle(MouseEvent event) {
				ObservableList<Node> krausViews = definition.getChildren();
				
				KrausView kv = get();
				int insertIndex = krausViews.indexOf(kv);
				if(insertIndex == krausSelected || krausSelected == -1) {
					krausSelected = -1;
					return;
				}
				
				String selected = ((KrausView) krausViews.get(krausSelected)).getString();
				
				int i = 0;
				for(; i < krausViews.size(); i++) {
					if(i == insertIndex || i == krausSelected)
						break;
				}
				
				if(i == krausSelected) {
					KrausView next = (KrausView) krausViews.get(i);
					for(; i != insertIndex; i++) {
						KrausView cur = next;
						next = (KrausView) krausViews.get(i + 1);
						cur.setString(next.getString());
					}
					next.setString(selected);
				} else if(i == insertIndex) {
					for(; i < krausViews.size() && i <= krausSelected; i++) {
						KrausView cur = (KrausView) krausViews.get(i);
						String temp = cur.getString();
						cur.setString(selected);
						selected = temp;
					}
				}
			}
		}
		
	}


	@Override
	public void changed(ObservableValue<? extends DefaultModelType> observable, DefaultModelType oldValue,
			DefaultModelType newValue) {
		setDefinitionUI(newValue, null);
	}
	
}
