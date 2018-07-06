package framework;

import java.io.Serializable;

import mathLib.Complex;
import mathLib.Matrix;
import utils.GateIcon;


/**
 * This class provides different ways to get / set Matrixes for different types of Gates
 * 
 * @author quantumresearch
 *
 */
public abstract class AbstractGate implements Serializable{
	private static final long serialVersionUID = -358713650794388405L;
	
	private static final double LOG10_2 = Math.log10(2);
	
	public static enum GateType{
        I,X,Y,Z,H, CUSTOM, MEASURE, CNOT, SWAP
    }
	
	

	private String description;
	private transient GateIcon icon = null;
    private String name;
    private GateType type;
    private int width = 1;
    
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
	
	public int getNumberOfRegisters() {
		Matrix<Complex> mat = getMatrix();
		return (int) Math.round(Math.log10(mat.getRows()) / LOG10_2);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	
	
}
