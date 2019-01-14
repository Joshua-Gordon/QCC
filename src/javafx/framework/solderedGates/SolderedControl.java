package javafx.framework.solderedGates;

public class SolderedControl extends SpacerPin {
	private static final long serialVersionUID = 292673113796147747L;
	
	private boolean controlStatus;
	
	public SolderedControl(SolderedGate gate, boolean controlStatus) {
		super(gate);
		this.controlStatus = controlStatus;
	}
	
	public boolean getControlStatus() {
		return controlStatus;
	}
}
