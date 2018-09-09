package framework2;

import java.io.Serializable;
import java.util.ArrayList;

import framework.AbstractGate;
import framework.CircuitBoard;
import framework.SolderedRegister;

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
	private int topLocalRegister = 0, bottomLocalRegister = 0;
	private ArrayList<Boolean> controls = new ArrayList<Boolean>();
	
	
	
//	protected enum Control{
//		NONE,TRUE,FALSE
//	}
//
//	private HashMap<Integer,Control> controls;
	
	/**
	 * 
	 * Creates a {@link SolderedGate} instance. <br>
	 * **A {@link SolderedGate} must specify it's first local register and the last local register.<br>
	 * Look at the example below for this concept.
	 * 
	 * <p>
	 * ie. This gate acts upon 3 global registers (1 3, 4) and the gate has 3 local registers (0, 1, 2)({@link CircuitBoard} registers)
	 * <p>
	 * <pre>
	 * |0> ---------------------
	 * |1> ------|2       |-----
	 * |2> ------|   GATE |-----
	 * |3> ------|0       |-----
	 * |4> ------|1       |-----
	 * |5> ---------------------
	 * </pre>
	 * *The <code>firstLocalRegister</code> is the Gate's top-most register which is "2" in the example above. <br>
	 * *The <code>lastLocalRegister</code> is the Gate's bottom most register which is "1" in the example above. <br>
	 * <p>
	 * If the gate is <b>NOT</b> a Multi-Qubit Gate, then the <code>firstLocalRegister</code> and the <code>lastLocalRegister</code>
	 * are both "0"
	 * 
	 * @param abstractGate
	 * @param firstLocalRegister
	 * @param lastLocalRegister
	 */
	public SolderedGate(AbstractGate abstractGate, int firstLocalRegister, int lastLocalRegister) {
		this.abstractGate = abstractGate;
		this.topLocalRegister = firstLocalRegister;
		this.bottomLocalRegister = lastLocalRegister;
//		this.controls = new HashMap<>();
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
		return abstractGate.getNumberOfRegisters() + controls.size();
	}

	/**
	 * @return
	 * the first local register number pertaining to this {@link SolderedGate}
	 */
	public int getFirstLocalRegister() {
		return topLocalRegister;
	}

	/**
	 * sets the first local register number pertaining to this {@link SolderedGate}
	 * @param firstLocalRegister
	 */
	public void setFirstLocalRegister(int firstLocalRegister) {
		this.topLocalRegister = firstLocalRegister;
	}

	/**
	 * @return
	 * returns the last local register number pertaining to this {@link SolderedGate}
	 */
	public int getLastLocalRegister() {
		return bottomLocalRegister;
	}

	/**
	 * sets the last local register number pertaining to this {@link SolderedGate}
	 * @param lastLocalRegister
	 */
	public void setLastLocalRegister(int lastLocalRegister) {
		this.bottomLocalRegister = lastLocalRegister;
	}
	
}
