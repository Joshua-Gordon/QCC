package appFX.appUI.appViews.circuitBoardView;

import appFX.framework.gateModels.PresetGateType;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import utils.customCollections.Pair;

public class ControlToolAction extends ToolAction {
	private final boolean controlType;
	private final CircuitBoardView cbv;
	private int rowSel, colSel;
	private ControlSelectRegion region = null;
	private SolderedGate currSG;
	
	public ControlToolAction(CircuitBoardView cbv, boolean controlType) {
		super(true);
		this.cbv = cbv;
		this.controlType = controlType;
	}
	
	@Override
	public void buttonPressed(int row, int column) {
		SolderedPin sp = cbv.getCircuitBoard().getSolderPinAt(row, column);
		SolderedGate sg = sp.getSolderedGate();
		if(currSG == null) {
			if(PresetGateType.isIdentity(sg.getGateModelFormalName()))
				return;
			
			currSG = sg;
			rowSel = row;
			colSel = column;
			
			Pair<Integer, Integer> bounds = cbv.getCircuitBoard().getSolderedGateBodyBounds(row, column);
			
			region = new ControlSelectRegion(bounds.first(), column, bounds.second() - bounds.first());
			ObservableList<Node> nodes = cbv.circuitBoardPane.getChildren();
			nodes.add(nodes.size() - 1, region);
		} else {
			if(colSel != column) {
				reset();
				return;
			}
			if(sg == currSG && sp instanceof SolderedRegister)
				return;
			
			cbv.getCircuitBoard().placeControl(row, rowSel, column, controlType);
			
			reset();
		}
	}
	
	@Override
	public void reset() {
		rowSel = -1;
		colSel = -1;
		if(region != null)
			cbv.circuitBoardPane.getChildren().remove(region);
		region = null;
		currSG = null;
	}
	
	private class ControlSelectRegion extends Region {
		
		public ControlSelectRegion (int row, int column, int height) {
			setStyle("-fx-border-color: blue;\n"
	                + "-fx-border-width: 2;\n"
	                + "-fx-border-style: dashed;\n");
			
			GridPane.setConstraints(this, column + 1, row, 1, height);
		}
		
		
	}

	@Override
	public boolean isCursorDisplayed() {
		return true;
	}
}
