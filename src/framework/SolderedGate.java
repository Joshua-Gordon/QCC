package framework;

import java.io.Serializable;

import mathLib.Complex;
import mathLib.Matrix;


/**
 * This class contains a reference to the {@link AbstractGate} and could be though of as the physical part of {@link AbstractGate} on the 
 * {@link CircuitBoard}. All {@link SolderedRegister}'s on the board that are apart of this {@link SolderedGate} have a reference
 * to this instance.
 * 
 * @author quantumresearch
 * 
 */
public class SolderedGate implements Serializable{
	private static final long serialVersionUID = 2595030500395644473L;
	
	private AbstractGate abstractGate;
	
	public SolderedGate(AbstractGate abstractGate) {
		this.abstractGate = abstractGate;
	}

	/**
	 * @return
	 * whether or not this gate is multi-qubit gate
	 */
	public boolean isMultiQubit() {
		return abstractGate.isMultiQubitGate();
	}

	/**
	 * @return
	 * the {@link AbstractGate} that is associated with this {@link SolderedGate}
	 */
	public AbstractGate getAbstractGate() {
		return abstractGate;
	}
	
	/**
	 * @return
	 * the expected number of {@link SolderedRegister}'s attached to this {@link SolderedGate}
	 */
	public int getExpectedNumberOfRegisters() {
		return abstractGate.getNumberOfRegisters();
	}
	
	
}
