package framework;

import java.io.Serializable;
import java.util.ArrayList;


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
	private int firstLocalRegister = 0, lastLocalRegister = 0;
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
		this.firstLocalRegister = firstLocalRegister;
		this.lastLocalRegister = lastLocalRegister;
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
		return firstLocalRegister;
	}

	/**
	 * sets the first local register number pertaining to this {@link SolderedGate}
	 * @param firstLocalRegister
	 */
	public void setFirstLocalRegister(int firstLocalRegister) {
		this.firstLocalRegister = firstLocalRegister;
	}

	/**
	 * @return
	 * returns the last local register number pertaining to this {@link SolderedGate}
	 */
	public int getLastLocalRegister() {
		return lastLocalRegister;
	}

	/**
	 * sets the last local register number pertaining to this {@link SolderedGate}
	 * @param lastLocalRegister
	 */
	public void setLastLocalRegister(int lastLocalRegister) {
		this.lastLocalRegister = lastLocalRegister;
	}
	
//	public ArrayList<SolderedRegister> getRegisters() {
//		ArrayList<SolderedRegister> regs = new ArrayList<>();
//		CircuitBoard cb = Main.getWindow().getSelectedBoard();
//		int column = -1;
//		for(int y = 0; y < cb.getRows(); ++y) {
//			for(int x = 0; x < cb.getColumns(); ++x) {
//				if(equals(cb.getSolderedGate(x,y))) {
//					column = x;
//				}
//			}
//		}
//		if(column == -1) {
//			//Gate not on board
//			return null;
//		}
//		for(int y = 0; y < cb.getColumns(); ++y) {
//			SolderedRegister sr = cb.getSolderedRegister(column,y);
//			if(equals(sr.getSolderedGate())) {
//				regs.add(sr);
//			}
//		}
//		return regs;
//	}

//	public void addControl(int reg, boolean control) {
//		controls.put(reg,control ? Control.TRUE : Control.FALSE);
//	}
//
//	public Control controlled(int reg) {
//		return controls.getOrDefault(reg,Control.NONE);
//	}

	@Override
	public String toString() {
		return abstractGate.getName();
	}

//	public static Matrix<Complex> makeControlledMatrix(Matrix<Complex> in, Control[] controls) {
//		Matrix<Complex> newMat = null, oldMat = in;
//		for(Control c : controls) {
//			newMat = new Matrix<>(Complex.ISQRT2(),oldMat.getColumns()<<1,oldMat.getRows()<<1).identity();
//			if(c.equals(Control.TRUE)) {
//				for(int y = oldMat.getRows(); y < newMat.getRows(); ++y) {
//					for(int x = oldMat.getColumns(); x < newMat.getColumns(); ++x) {
//						newMat.r(oldMat.v(x-oldMat.getColumns(),y-oldMat.getRows()),x,y);
//					}
//				}
//			} else if(c.equals(Control.FALSE)){
//				for(int y = 0; y < oldMat.getRows(); ++y) {
//					for(int x = 0; x < oldMat.getColumns(); ++x) {
//						newMat.r(oldMat.v(x,y),x,y);
//					}
//				}
//			}
//			oldMat = newMat;
//		}
//		return newMat;
//	}
}
