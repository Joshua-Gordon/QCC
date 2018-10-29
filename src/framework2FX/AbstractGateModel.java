package framework2FX;

import java.io.Serializable;

import appUI.GateIcon;
import framework.CircuitBoard;
import framework.ExportedGate;
import framework.SolderedGate;
import mathLib.Complex;
import mathLib.Matrix;

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
	
	private static final double LOG10_2 = Math.log10(2);
	
	public static enum GateModelType {
		DEFAULT_GATE, CUSTOM_GATE, CUSTOM_ORACLE;
	}
	
	
	private final String description;
    private final String name;
    private final String symbol;
	private final Matrix<Complex> matrix;
	
	
	public AbstractGateModel(String description, String name, String symbol, Matrix<Complex> matrix) {
		this.description = description;
		this.name = name;
		this.symbol = symbol;
		this.matrix = matrix;
		
	}
	
	public AbstractGateModel(String description, String name, String symbol, int matSize, Complex ... matElements) {
		this(description, name, symbol, new Matrix<Complex>(matSize, matSize, matElements));
	}
	
	public AbstractGateModel(String name, String symbol, Matrix<Complex> matrix) {
		this("", name, symbol, matrix);
	}
	
	public AbstractGateModel(String name, String symbol, int matSize, Complex ... matElements) {
		this("", name, symbol, matSize, matElements);
	}
	
	
	
	public abstract GateModelType getGateModelType();
	
	
	
	/**
	 * @return
	 * the {@link Matrix} that represents this {@link AbstractGateModel}
	 */
	public Matrix<Complex> getMatrix(){
		return matrix;
	}
	
	
	/**
	 * @return
	 * whether or not the {@link Matrix} that this {@link AbstractGateModel} represents is multi-qubit.
	 * (determined by the size the {@link Matrix})
	 */
	public boolean isMultiQubitGate() {
    	return matrix.getColumns() > 2;
    }

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

	
	/**
	 * @return
	 * the calculated number of qubits registers needed for this {@link AbstractGateModel}
	 */
	public int getNumberOfRegisters() {
		Matrix<Complex> mat = getMatrix();
		return (int) Math.round(Math.log10(mat.getRows()) / LOG10_2);
	}


}
