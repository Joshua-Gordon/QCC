package framework;

import java.io.Serializable;
import java.util.ArrayList;

import appUI.CustomGateConstructorUI;
import mathLib.Complex;
import mathLib.Matrix;


public class CustomGate extends AbstractGate implements Serializable {
	private static final long serialVersionUID = 8692251843065026400L;
	
	 //render the gate on the first index
	private ArrayList<Matrix<Complex>> matrixes = new ArrayList<>();

	
	
	public CustomGate(Matrix<Complex> mat) {
        matrixes.add(mat);
        setType(GateType.CUSTOM);
    }
    
    public CustomGate(String name, ArrayList<Matrix<Complex>> mats) {
        this.matrixes = mats;
        setType(GateType.CUSTOM);
        setName(name);
    }   
    
    public static void makeCustom() {
    	CustomGateConstructorUI window = new CustomGateConstructorUI(Main.getWindow().getFrame());
    	window.setVisible(true);
    	ArrayList<Matrix<Complex>> matrixes = window.getCustomMatrix();
    	if(matrixes != null) {
    		CustomGate mqg = new CustomGate(window.getGateName(), matrixes);
    		mqg.setDescription(window.getDescription());
    		mqg.setIcon(window.getIcon());
    		Main.getWindow().getSelectedBoard().getCustomGates().addElement(mqg);
    		Main.getWindow().getSelectedBoard().setUnsaved();
    	}
    }
    
    @Override
    public boolean isMultiQubitGate() {
    	if(matrixes.size() > 1)
    		return true;
    	return matrixes.get(0).getColumns() > 2;
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
