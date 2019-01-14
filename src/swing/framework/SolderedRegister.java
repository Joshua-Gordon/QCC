package swing.framework;

import java.io.Serializable;
import java.util.ArrayList;

import mathLib.Matrix;

/**
 * This object is the physical connection between the {@link SolderedGate} and the {@link CircuitBoard}'s
 * component. A {@link SolderedRegister} can be thought of as the "pin" of the physical part of a classical chip attached to a breadboard, 
 * if the classical chip is represented by {@link SolderedGate} and the breadboard is represented by the {@link CircuitBoard}.
 * <p>
 * This class contains references to the {@link SolderedGate} and it is labeled with the {@link SolderedGate}'s "qubit pin" number
 * (aka <code> localRegisterNumber </code>)
 * <p>
 * This has no reference to the location on the {@link CircuitBoard} (intentional), but the {@link CircuitBoard} has a reference to this 
 * {@link SolderedRegister}
 * 
 * @author quantumresearch
 *
 */
public class SolderedRegister implements Serializable{
	private static final long serialVersionUID = 126844024737787336L;
	
	private final int localRegisterNumber;
	private final SolderedGate solderedGate;
	
	
	
	/**
	 * Creates a {@link SolderedRegister} of a given {@link SolderedGate} and local register number. Look at the example below for more information.
	 * 
	 * <p>
	 * ie. This {@link SolderedGate} (named "GATE") acts upon 3 global registers (1 3, 4) and has 3 local registers (0, 1, 2)({@link CircuitBoard} registers)
	 * <p>
	 * <pre>
	 * |0> ---------------------
	 * |1> ------|2       |-----
	 * |2> ------|   GATE |-----
	 * |3> ------|0       |-----
	 * |4> ------|1       |-----
	 * |5> ---------------------
	 * </pre>
	 * 
	 * The {@link SolderedGate} "Gate" has a reference to an {@link AbstractGate}, which contain's the following {@link Matrix} 
	 * (represented as a kronecker product):
	 * <pre>
	 * |0   1|<sub> </sub>|1   0|<sub> </sub>|0  -i|<sub> </sub>
	 * |1   0|<sub>i</sub>|0  -1|<sub>j</sub>|i   0|<sub>k</sub>  
	 * </pre>
	 * 
	 * 
	 * In the {@link CircuitBoard} the double {@link ArrayList} of {@link SolderedRegister}s can be thought of as a grid of {@link SolderedRegister}s
	 * like the following:
	 * 
	 * <pre>
	 * [I][I]    [I]    [I]
	 * [I][I][ GATE  2 ][I]
	 * [I][I]    [I]    [I]
	 * [I][I][ GATE  0 ][I]
	 * [I][I][ GATE  1 ][I]
	 * [I][I]    [I]    [I]
	 * </pre> 
	 * As seen above, there are three instances of {@link SolderedRegister}s in the column 2 (third column) each referring to the SolderedGate "GATE".
	 * On the row 1 (second row), the {@link SolderedRegister} is stored with local register "2"; meaning that when the board is exported, the 
	 * row that contains this {@link SolderedRegister} will effect the the third {@link Matrix} in "GATE".
	 * <br>
	 * If we apply this logic to the row 3, and 4 (the fourth and fifth row),
	 * the {@link SolderedGate} "Gate" now can be represented by the following Matrix when exported off the board:
	 * <pre>
	 * |0   1|<sub> </sub>|1   0|<sub> </sub>|0  -i|<sub> </sub>
	 * |1   0|<sub>3</sub>|0  -1|<sub>4</sub>|i   0|<sub>1</sub>  
	 * </pre>
	 * 
	 * <i>Note: This logic above also applies if the given matrix is a permutation matrix</i>
	 * <p>
	 * @See 
	 * {@link ExportedGate}
	 * {@link AbstractGate}
	 * {@link SolderedGate}
	 * 
	 * 
	 * @param solderedGate
	 * @param localRegisterNumber
	 */
	public SolderedRegister(SolderedGate solderedGate, int localRegisterNumber) {
		this.localRegisterNumber = localRegisterNumber;
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
	public int getLocalRegisterNumber() {
		return localRegisterNumber;
	}
	
	
	/**
	 * @return
	 *  a {@link SolderedRegister} that has a {@link SolderedGate} of which is the Identity Gate
	 */
	public static SolderedRegister identity() {
		return new SolderedRegister(new SolderedGate(DefaultGate.DEFAULT_GATES.get("I"), 0, 0),0);
	}

	@Override
	public String toString() {
		return "Register " + localRegisterNumber + " for " + solderedGate.toString();
	}
}
