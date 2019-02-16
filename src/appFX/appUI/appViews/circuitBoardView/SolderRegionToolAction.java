package appFX.appUI.appViews.circuitBoardView;

import java.util.ArrayList;

import appFX.appUI.AppAlerts;
import appFX.appUI.ParameterPrompt;
import appFX.appUI.appViews.gateChooser.AbstractGateChooser;
import appFX.framework.AppStatus;
import appFX.framework.UserDefinitions.DefinitionEvaluatorException;
import appFX.framework.gateModels.CircuitBoardModel.RecursionException;
import appFX.framework.gateModels.GateModel;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import utils.customCollections.ImmutableArray;

public class SolderRegionToolAction  implements ToolAction {
	private Integer[] regs;
	private ArrayList<NumberRegion> regDisps;
	private int currentReg = -1;
	private int lastReg = -1;
	private int selectedColumn = -1;
	private final CircuitBoardView view;
	
	public SolderRegionToolAction(CircuitBoardView view) {
		this.view = view;
	}

	@Override
	public void buttonPressed(int row, int column) {
		
		if(selectedColumn != -1 && selectedColumn != column)
			reset();
		selectedColumn = column;
		
		
		GateModel gm = getSelectedModel();
		if(gm != null) { 
			
			if(regs == null) {
				regs = new Integer[gm.getNumberOfRegisters()];
				regDisps = new ArrayList<NumberRegion>(gm.getNumberOfRegisters() - 1);
				lastReg = 1;
				currentReg = 0;
			}
			
			NumberRegion numberRegion = new NumberRegion(row, column); 
			regDisps.add(numberRegion);
			view.circuitBoardPane.getChildren().add(numberRegion);
			
			regs[currentReg] = row;
			currentReg = lastReg;
			lastReg++;
			
			if(lastReg == regs.length + 1) {
				ImmutableArray<String> args = gm.getArguments();
				if(args.size() > 0) {
					ParameterPrompt pp = new ParameterPrompt(view.getProject(), view.getCircuitBoard(), gm.getFormalName(), regs, selectedColumn);
					pp.showAndWait();
				} else {
					try {
						view.getCircuitBoard().placeGate(gm.getFormalName(), selectedColumn, regs);
					} catch (DefinitionEvaluatorException e) {
						e.printStackTrace();
					} catch(RecursionException e2) {
						AppAlerts.showMessage(AppStatus.get().getPrimaryStage(),
								"Recursion detected", e2.getMessage(), AlertType.ERROR);
					}
				}
				reset();
			}
		}
	}


	@Override
	public void reset() {
		currentReg = -1;
		selectedColumn = -1;
		lastReg = -1;
		
		if(regDisps != null) {
			for(NumberRegion nr : regDisps)
				view.circuitBoardPane.getChildren().remove(nr);
		}
		
		regDisps = null;
		regs = null;
	}
	
	public GateModel getSelectedModel() {
		return AbstractGateChooser.getSelected();
	}
	
	
	private class NumberRegion extends BorderPane {
		private int reg = currentReg;
		private Label label;
		
		private NumberRegion(int row, int column) {
			label = new Label(Integer.toString(reg));
			setCenter(label);
			setStyle("-fx-background-color: #BDBDBD;");
			GridPane.setConstraints(this, column + 1, row);
			
			setOnMouseClicked((e) -> {
				regs[currentReg] = GridPane.getRowIndex(this);
				int temp = currentReg;
				currentReg = reg;
				reg = temp;
				label.setText(Integer.toString(reg));
			});
		}
	}


	@Override
	public boolean isCursorDisplayed() {
		return true;
	}

}