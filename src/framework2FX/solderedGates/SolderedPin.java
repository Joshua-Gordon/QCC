package framework2FX.solderedGates;

public abstract class SolderedPin {
	private final SolderedGate gate;
	
	public SolderedPin (SolderedGate gate) {
		this.gate = gate;
	}
	
	public SolderedGate getSolderedGate() {
		return gate;
	}
}
