package framework2FX.solderedGates;

public class SolderedRegister extends SolderedPin {

	private int solderedGatePinNumber;
	
	public SolderedRegister(SolderedGate gate, int solderedGatePinNumber) {
		super(gate);
		this.solderedGatePinNumber = solderedGatePinNumber;
	}
	
	public int getSolderedGatePinNumber() {
		return solderedGatePinNumber;
	}

	@Override
	public boolean isWithinBody() {
		return true;
	}
}
