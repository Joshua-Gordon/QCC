package framework;

import appUI.GateIcon;
import mathLib.Complex;
import mathLib.Matrix;


/**
 * This class provides different ways to get / set Matrixes for different types of Gates
 * 
 * @author quantumresearch
 *
 */
public abstract class AbstractGate {
	
	public static enum GateType{
        I,X,Y,Z,H, CUSTOM, MEASURE, CNOT, SWAP
    }

	private String description;
	private transient GateIcon icon = null;
    private String name;
    private GateType type;
    public int length;
    
    
	private Matrix<Complex> matrix;
	
	public Matrix<Complex> getMatrix(){
		return matrix;
	}
	
	public void setMatrix(Matrix<Complex> matrix) {
		this.matrix = matrix;
	}
	
	public boolean isMultiQubitGate() {
    	return matrix.getColumns() > 2;
    }
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public void loadIcon() {
    	icon = new GateIcon(getName(), isMultiQubitGate());
    }
	
	public GateIcon getIcon() {
		return icon;
	}

	public void setIcon(GateIcon icon) {
		this.icon = icon;
	}
    
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public GateType getType() {
		return type;
	}

	public void setType(GateType type) {
		this.type = type;
	}
	
	
}
