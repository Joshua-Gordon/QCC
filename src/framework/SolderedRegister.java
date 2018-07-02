package framework;

import java.io.Serializable;

public class SolderedRegister implements Serializable{
	private static final long serialVersionUID = 126844024737787336L;
	
	private int gateRegister;
	private SolderedGate solderedGate;
	
	public SolderedRegister(SolderedGate solderedGate, int gateRegister) {
		this.gateRegister = gateRegister;
		this.solderedGate = solderedGate;
	}
	
	public SolderedGate getSolderedGate() {
		return solderedGate;
	}
	
	public int getGateRegister() {
		return gateRegister;
	}	
}
