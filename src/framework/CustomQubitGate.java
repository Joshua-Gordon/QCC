package framework;

import java.util.ArrayList;

import appUI.CustomGateConstructorUI;
import appUI.GateIcon;
import mathLib.Complex;
import mathLib.Matrix;


public class CustomQubitGate extends DefaultGate {
	private static final long serialVersionUID = 8692251843065026400L;
	
	
	 //render the gate on the first index
	private ArrayList<Matrix<Complex>> matrixes = new ArrayList<>();
	private String description;
	private transient GateIcon icon;

	
	
	public CustomQubitGate(Matrix<Complex> mat, GateType gt) {
        super(null, gt);
        matrixes.add(mat);
    }
    
    public CustomQubitGate(String name, ArrayList<Matrix<Complex>> mats) {
        super(null, DefaultGate.GateType.CUSTOM);
        this.matrixes = mats;
        setName(name);
    }
    
    
    
    public static void makeCustom() {
    	CustomGateConstructorUI window = new CustomGateConstructorUI(Main.w.getFrame());
    	window.setVisible(true);
    	ArrayList<Matrix<Complex>> matrixes = window.getCustomMatrix();
    	if(matrixes != null) {
    		CustomQubitGate mqg = new CustomQubitGate(window.getGateName(), matrixes);
    		mqg.setDescription(window.getDescription());
    		mqg.setIcon(window.getIcon());
    		Main.cb.customGates.add(mqg);
    	}
    }
    
    
    public void loadIcon() {
    	icon = new GateIcon(getName(), isMultiQubitGate());
    }
    
    public boolean isMultiQubitGate() {
    	if(matrixes.size() > 1)
    		return true;
    	return matrixes.get(0).getColumns() > 2;
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

	
	
	
	
	@Override
    public Matrix<Complex> getMatrix() {
    	Matrix<Complex> mat = matrixes.get(0);
    	for(int i = 1; i < matrixes.size(); i++)
    		mat = mat.kronecker(matrixes.get(i));
    	return mat;
    }
    
    @Override
    public void setMatrix(Matrix<Complex> matrix) {
    	if(matrix != null) {
    		matrixes.clear();
        	matrixes.add(matrix);
    	}
    }

}
