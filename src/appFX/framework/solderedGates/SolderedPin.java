package appFX.framework.solderedGates;

import java.io.Serializable;

public abstract class SolderedPin implements Serializable {
	private static final long serialVersionUID = 6996945385029339136L;
	
	private final SolderedGate gate;
	
	public SolderedPin (SolderedGate gate) {
		this.gate = gate;
	}
	
	public SolderedGate getSolderedGate() {
		return gate;
	}
}
