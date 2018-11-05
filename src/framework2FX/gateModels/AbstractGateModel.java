package framework2FX.gateModels;

import java.io.Serializable;

import appUI.GateIcon;
import framework.AbstractGate.GateType;
import framework.CircuitBoard;
import framework.ExportedGate;
import framework.SolderedGate;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.MathSet;

/**
 * <b> IMMUTABLE </b>
 * <p>
 * This class creates a model for a gate that contains most generic types of information a certain quantum gate could have.
 * These properties include a name, general description, a {@link GateIcon}, a {@link GateType}, and a {@link Matrix}.
 * <p>
 * Any properties concerning locations on the board (including the qubits registers and height) are found in instances of {@link ExportedGate}.
 * due to optimization for boundary checking of the gates on the {@link CircuitBoard}.
 * <p>
 * Regardless, each {@link CircuitBoard} should have no more than one unique instance of this {@link AbstractGateModel} with in its list of gates.
 * <p>
 * When attached to a {@link CircuitBoard}, a {@link SolderedGate} is instantiated with a reference to this {@link AbstractGateModel}.
 * 
 * @author quantumresearch
 *
 */
public abstract class AbstractGateModel implements Serializable{
	private static final long serialVersionUID = -358713650794388405L;
	
	public static enum GateModelType {
		DEFAULT_GATE, CUSTOM_GATE, CUSTOM_ORACLE;
	}
	
	
	private final String description;
    private final String name;
    private final String symbol;
    
    
	
	public AbstractGateModel (String name, String description, String symbol) {
		this.description = description;
		this.name = name;
		this.symbol = symbol;
	}
	
	
	/**
	 * @return the type of gate
	 */
	public abstract GateModelType getGateModelType();
	
	
	
	/**
	 * @param mathDefinitions if this matrix is represented by an expression, then  should specify
	 * any variable values or function definitions
	 * @return
	 * the {@link Matrix} that represents this {@link AbstractGateModel}
	 */
	public abstract Matrix<Complex> getMatrix(MathSet mathDefinitions);
	
	
	/**
	 * @return
	 * whether or not the {@link Matrix} that this {@link AbstractGateModel} represents is multi-qubit.
	 * (determined by the size the {@link Matrix})
	 */
	public abstract boolean isMultiQubitGate();

	
	/**
	 * @return
	 * the number of qubits registers needed for this {@link AbstractGateModel}
	 */
	public abstract int getNumberOfRegisters();
	
	
	
	
	/**
	 * @return
	 * the name of this {@link AbstractGateModel}
	 */
	public String getName() {
		return name;
	}
	
    
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * @return
	 * the description of this {@link AbstractGateModel}
	 */
    public String getDescription() {
		return description;
    }

    
    @SuppressWarnings("serial")
	public static class InvalidGateModelMatrixException extends RuntimeException {
		public InvalidGateModelMatrixException (String reason) {
			super (reason);
		}
	}


    
}


