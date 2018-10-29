package framework;

import java.io.Serializable;

import appUI.GateIcon;
import mathLib.Complex;
import mathLib.Matrix;


/**
 * 
 * This class creates a model for a gate that contains most generic types of information a certain quantum gate could have.
 * These properties include a name, general description, a {@link GateIcon}, a {@link GateType}, and a {@link Matrix}.
 * <p>
 * Any properties concerning locations on the board (including the qubits registers and height) are found in instances of {@link ExportedGate}.
 * due to optimization for boundary checking of the gates on the {@link CircuitBoard}.
 * <p>
 * Regardless, each {@link CircuitBoard} should have no more than one unique instance of this {@link AbstractGate} with in its list of gates.
 * <p>
 * When attached to a {@link CircuitBoard}, a {@link SolderedGate} is instantiated with a reference to this {@link AbstractGate}.
 * 
 * @author quantumresearch
 *
 */
public abstract class AbstractGate implements Serializable{
	private static final long serialVersionUID = -358713650794388405L;
	
	protected static final double LOG10_2 = Math.log10(2);
	
	public static enum GateType{
        I,X,Y,Z,H,S,T, OTHER, MEASURE, CNOT, SWAP, TOFFOLI
    }
	
	private String description;
	private transient GateIcon icon = null;
    private String name;
    private GateType type;
    private int width = 1;
    
	private Matrix<Complex> matrix;
	
	/**
	 * @return
	 * the {@link Matrix} that represents this {@link AbstractGate}
	 */
	public Matrix<Complex> getMatrix(){
		return matrix;
	}
	
	/**
	 * Sets the {@link Matrix} that represents this {@link AbstractGate}
	 * @param matrix
	 */
	public void setMatrix(Matrix<Complex> matrix) {
		this.matrix = matrix;
	}
	
	/**
	 * @return
	 * whether or not the {@link Matrix} that this {@link AbstractGate} represents is multi-qubit.
	 * (determined by the size the {@link Matrix})
	 */
	public boolean isMultiQubitGate() {
    	return matrix.getColumns() > 2;
    }

	/**
	 * @return
	 * the name of this {@link AbstractGate}
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of this {@link AbstractGate}
	 * <br>
	 * <b>NOTE:</b> if this is changed, one must resize the width of this abstract gate for rendering purposes.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Loads a {@link GateIcon} for this {@link AbstractGate}. This is the default procedure for loading
	 * {@link GateIcon}s as it just generates an image with the name. 
	 */
	public void loadIcon() {
    	icon = new GateIcon(getName(), isMultiQubitGate());
    }
	
	/**
	 * Loads a {@link GateIcon} for this {@link AbstractGate}. This is another procedure for loading
	 * {@link GateIcon}s; it just generates an image with the specified name. 
	 * @param name
	 */
	public void loadIcon(String name) {
    	icon = new GateIcon(name, isMultiQubitGate());
	}
	
	/**
	 * @return
	 * the {@link GateIcon} associated with this {@link AbstractGate}
	 */
	public GateIcon getIcon() {
		return icon;
	}

	/**
	 * Sets the {@link GateIcon} associated with this {@link AbstractGate}
	 * @param icon
	 */
	public void setIcon(GateIcon icon) {
		this.icon = icon;
	}
    
	/**
	 * @return
	 * the description of this {@link AbstractGate}
	 */
    public String getDescription() {
		return description;
	}

    /**
     * sets the description of this {@link AbstractGate}
     * @param description
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return
	 * the {@link GateType} of this {@link AbstractGate}
	 */
	public GateType getType() {
		return type;
	}

	/**
	 * Sets the {@link GateType} of this {@link AbstractGate}
	 * @param type
	 */
	public void setType(GateType type) {
		this.type = type;
	}
	
	/**
	 * @return
	 * the calculated number of qubits registers needed for this {@link AbstractGate}
	 */
	public int getNumberOfRegisters() {
		Matrix<Complex> mat = getMatrix();
		return (int) Math.round(Math.log10(mat.getRows()) / LOG10_2);
	}

	/**
	 * @return
	 * the grid width taken up by this {@link AbstractGate} (Used for rendering purposes)
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the grid width taken up by this {@link AbstractGate} (Used for rendering purposes)
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
	}


}
