package framework;

import java.io.Serializable;

public class SolderedRegister implements Serializable{
	private static final long serialVersionUID = 126844024737787336L;
	
	private int gateRegisterNumber;
	private SolderedGate solderedGate;
	
	public SolderedRegister(SolderedGate solderedGate, int gateRegisterNumber) {
		this.gateRegisterNumber = gateRegisterNumber;
		this.solderedGate = solderedGate;
	}
	
	public SolderedGate getSolderedGate() {
		return solderedGate;
	}
	
	public int getGateRegisterNumber() {
		return gateRegisterNumber;
	}

	public static SolderedRegister identity() {
		return new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()),0);
	}
}
