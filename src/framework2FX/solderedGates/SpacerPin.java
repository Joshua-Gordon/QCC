package framework2FX.solderedGates;

public class SpacerPin extends SolderedPin {
	
	private final boolean spacerWithinGateBody;
	
	public SpacerPin(SolderedGate gate, boolean spacerWithinGateBody) {
		super(gate);
		this.spacerWithinGateBody = spacerWithinGateBody;
	}
	
	public boolean isSpacerWithinGateBody () {
		return spacerWithinGateBody;
	}

}
