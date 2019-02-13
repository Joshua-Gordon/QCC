package appFX.framework.solderedGates;

public class SolderedRegister extends SolderedPin  {
	private static final long serialVersionUID = -4844461109723348256L;
	
	private int solderedGatePinNumber;
	
	public SolderedRegister(SolderedGate gate, int solderedGatePinNumber) {
		super(gate, true);
		this.solderedGatePinNumber = solderedGatePinNumber;
	}
	
	public int getSolderedGatePinNumber() {
		return solderedGatePinNumber;
	}
}
