package appFX.framework.solderedGates;

import java.io.Serializable;

public abstract class SolderedPin implements Serializable {
	private static final long serialVersionUID = 6996945385029339136L;
	
	private final SolderedGate gate;
	private final boolean isWithinBody;
	
	public SolderedPin (SolderedGate gate, boolean isWithinBody) {
		this.gate = gate;
		this.isWithinBody = isWithinBody;
	}
	
	public SolderedGate getSolderedGate() {
		return gate;
	}
	
	public boolean isWithinBody() {
		return isWithinBody;
	}
}
