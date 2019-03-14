package appFX.appUI.appViews;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appUI.GateIcon;
import appFX.appUI.LatexNode;
import appFX.appUI.appViews.AppView.ViewListener;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.BasicModel;
import appFX.framework.gateModels.GateModel;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.customCollections.ImmutableArray;

public class BasicModelView extends AppView implements Initializable, ViewListener {
	
	public TextField name, symbol, registers, modelType;
	public ScrollPane description;
	public BorderPane iconSpace;
	public HBox parameters, definitionStatement, editBar;
	public VBox definition;
	public Button editButton, editNewButton;
	private boolean initialized = false;
	
	
	private BasicModel gm;
	
	public BasicModelView(BasicModel gm) {
		super("GateModelView.fxml", gm.getFormalName(), Layout.CENTER);
		this.gm = gm;
	}

	public void onButtonPress(ActionEvent e) {
		AppCommand.doAction(AppCommand.EDIT_GATE, gm.getFormalName());
	}
	
	public void onButton2Press(ActionEvent e) {
		AppCommand.doAction(AppCommand.EDIT_AS_NEW_GATE, gm.getFormalName());
	}
	
	@Override
	public boolean receive(Object source, String methodName, Object... args) {
		Project p = AppStatus.get().getFocusedProject();
		if(initialized && p.getCustomGates() == source) {

			if(methodName.equals("put")) {
				if(((GateModel)args[0]).getFormalName().equals(getName())) {
					setViewListener(null);
					closeView();
					return true;
				}
			} else if (methodName.equals("replace") || methodName.equals("remove")){
				if(args[0].equals(getName())) {
					setViewListener(null);
					closeView();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setViewListener(this);
		addToReceiveEventListener();
		initialized = true;
		updateDefinitionUI();
	}
	
	private void updateDefinitionUI() {
		name.setEditable(false);
		symbol.setEditable(false);
		registers.setEditable(false);
		modelType.setEditable(false);
		
		name.setText(gm.getName());
		symbol.setText(gm.getSymbol());
		
		Node n = new LatexNode(gm.getDescription(), 20);
		
		description.setContent(n);
		
		
		modelType.setText(gm.getGateModelType().toString());
		
		parameters.getChildren().clear();
		
		ImmutableArray<String> args = gm.getArguments();
		
		if(!args.isEmpty()) {
			String parametersLatex = "\\(" + args.get(0) + "\\)";
			for(int i = 1; i < args.size(); i++)
				parametersLatex += ", \\(" + args.get(i) + "\\)";
			
			parameters.getChildren().add(new LatexNode(parametersLatex, 20));
		}
		
		registers.setText(Integer.toString(gm.getNumberOfRegisters()));
		
		iconSpace.getChildren().clear();
		
		GateIcon gi = GateIcon.getGateIcon(gm);
		
		iconSpace.setCenter(gi.getView());
		
		
		definitionStatement.getChildren().clear();
		definition.getChildren().clear();
		
		switch (gm.getGateModelType()) {
		case HAMILTONIAN:
			String latex = "\\text{The model is specified by matrix designated by } \\( e ^ {Ht}\\) \\text{ where } \\( H \\) \\text{ is defined by:} ";
			definitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			definition.getChildren().add(new LatexNode("$$ H = " + gm.getLatex().get(0) + " $$", 20));
			
			break;
		case POVM:
			latex = "\\text{The model is specified by kraus matricies } \\( (k_1, k_2, k_3, ... k_n) \\) \\text{ where } \\( \\sum_{ i = 1 } ^ { n } k_i k_i ^ * = I \\) \\text{ : }";
			definitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			ImmutableArray<String> krausLatex = gm.getLatex();
			
			latex = "\\begin{eqnarray}";
			
			int i = 1;
			for(String l : krausLatex)
				latex += "k_" + (i++) + " = " + l + " \\\\";
			
			latex += "\\end{eqnarray}";
			
			definition.getChildren().add(new LatexNode(latex, 20));
			
			break;
		case UNIVERSAL:
			latex = "\\text{The model is specified by a universal gate } \\( U \\) \\text{ described by the matrix:}";
			definitionStatement.getChildren().add(new LatexNode(latex, 15));
			
			definition.getChildren().add(new LatexNode(" $$ U = " + gm.getLatex().get(0) + " $$ ", 20));
			
			break;
			
			default:
			break;
		}
		
		
		
		
		editButton.setDisable(gm.isPreset());
		editButton.setVisible(!gm.isPreset());
		editButton.setManaged(!gm.isPreset());
	}

	@Override
	public void viewChanged(boolean wasAdded) {
		if(!wasAdded)
			removeEventListener();
	}

}
