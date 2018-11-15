package framework2FX.solderedGates;

public class SolderedControl extends SolderedPin {
	
	private boolean controlStatus;
	
	public SolderedControl(SolderedGate gate, boolean controlStatus) {
		super(gate);
		this.controlStatus = controlStatus;
	}
	
	
	public boolean getControlStatus() {
		return controlStatus;
	}
}
