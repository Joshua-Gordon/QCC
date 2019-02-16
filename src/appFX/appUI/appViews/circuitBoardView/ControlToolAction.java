package appFX.appUI.appViews.circuitBoardView;

import appFX.framework.gateModels.PresetGateType;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class ControlToolAction implements ToolAction {
	private final boolean controlType;
	private final CircuitBoardView cbv;
	private int rowSel, colSel;
	private ControlSelectRegion region = null;
	private SolderedGate currSG;
	
	public ControlToolAction(CircuitBoardView cbv, boolean controlType) {
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
			region = new ControlSelectRegion(row, column, 1);
			cbv.circuitBoardPane.getChildren().add(region);
		} else {
			if(colSel != column)
				reset();
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
	                + "-fx-border-insets: 5;\n"
	                + "-fx-border-width: 3;\n"
	                + "-fx-border-style: dashed;\n");
			
			GridPane.setConstraints(this, column + 1, row, height, 1);
		}
		
		
	}

	@Override
	public boolean isCursorDisplayed() {
		return true;
	}
}
