package framework2FX.solderedGates;

public class SolderedControl extends SpacerPin {
	
	private boolean controlStatus;
	
	public SolderedControl(SolderedGate gate, boolean isWithinGate, boolean controlStatus) {
		super(gate, isWithinGate);
		this.controlStatus = controlStatus;
	}
	
	
	public boolean getControlStatus() {
		return controlStatus;
	}
}
