package framework;

import java.io.Serializable;

/**
 * This object is the physical connection between the {@link SolderedGate} and the {@link CircuitBoard}'s
 * component. A {@link SolderedRegister} can be thought of as the "pin" of the physical part of a classical chip attached to a breadboard, 
 * if the classical chip is represented by {@link SolderedGate} and the breadboard is represented by the {@link CircuitBoard}.
 * <p>
 * This class contains references to the {@link SolderedGate} and it is labeled with the {@link SolderedGate}'s "qubit pin" number
 * (aka <code> gateRegisterNumber </code>)
 * <p>
 * This has no reference to the location on the {@link CircuitBoard} (intentional), but the {@link CircuitBoard} has a reference to this 
 * {@link SolderedRegister}
 * 
 * @author quantumresearch
 *
 */
public class SolderedRegister implements Serializable{
	private static final long serialVersionUID = 126844024737787336L;
	
	private int gateRegisterNumber;
	private SolderedGate solderedGate;
	
	public SolderedRegister(SolderedGate solderedGate, int gateRegisterNumber) {
		this.gateRegisterNumber = gateRegisterNumber;
		this.solderedGate = solderedGate;
	}
	
	/**
	 * @return
	 * the {@link SolderedGate} that is connected with this {@link SolderedRegister}.
	 */
	public SolderedGate getSolderedGate() {
		return solderedGate;
	}
	
	/**
	 * @return
	 * the "pin" number or register number of this {@link SolderedRegister}
	 */
	public int getGateRegisterNumber() {
		return gateRegisterNumber;
	}
	
	
	/**
	 * @return
	 *  a {@link SolderedRegister} that has a {@link SolderedGate} of which is the Identity Gate
	 */
	public static SolderedRegister identity() {
		return new SolderedRegister(new SolderedGate(DefaultGate.DEFAULT_GATES.get("I")),0);
	}
}
