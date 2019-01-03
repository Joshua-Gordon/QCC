package framework;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ListModel;

import appUI.CircuitBoardRenderContext;
import appUI.CustomGateConstructorUI;
import mathLib.Complex;
import mathLib.Matrix;
import utils.ResourceLoader;

/**
 * This class is an extension of the {@link AbstractGate} class.
 * Custom Gates are sometimes created as kronecker product of multiple matrices.
 * Thus, the <code> getMatrix() </code> super method is override to kronecker the products out then return 
 * the product of the matrixes.
 * 
 * 
 * @author quantumresearch
 *
 */
public class CustomGate extends AbstractGate implements Serializable {
	private static final long serialVersionUID = 8692251843065026400L;
	
	private ArrayList<Matrix<Complex>> matrixes = new ArrayList<>();
	
	public CustomGate(Matrix<Complex> mat) {
        matrixes.add(mat);
        setType(GateType.OTHER);
    }
    
    public CustomGate(String name, ArrayList<Matrix<Complex>> mats) {
        this.matrixes = mats;
        setType(GateType.OTHER);
        setName(name);
    }   
    
    
    /**
     * Creates a Prompt that allows the user to make a {@link CustomGate}.
     * If the user successfully creates a {@link CustomGate}, then the {@link CustomGate} will
     * be added to the currently selected {@link CircuitBoard}'s {@link CustomGate} {@link ListModel}.
     */
    public static void makeCustom() {
    	final Font VAST_SHADOW = ResourceLoader.getSwingResources().VAST_SHADOW;
    	
    	CustomGateConstructorUI window = new CustomGateConstructorUI(Main.getWindow().getFrame());
    	window.setVisible(true);
    	ArrayList<Matrix<Complex>> matrixes = window.getCustomMatrix();
    	if(matrixes != null) {
    		CustomGate cg = new CustomGate(window.getGateName(), matrixes);
    		cg.setDescription(window.getDescription());
    		cg.setIcon(window.getIcon());
    		
    		Rectangle2D rect = CircuitBoardRenderContext.getStringBounds(VAST_SHADOW, cg.getName());
    		int offset = (int) (2 * CircuitBoardRenderContext.REGISTER_NUM_PADDING + 
    				CircuitBoardRenderContext.getStringBounds(VAST_SHADOW, Integer.toString(cg.getNumberOfRegisters() - 1)).getWidth());
    		int width = (int)Math.ceil((double) (rect.getWidth() + offset)/ (double) CircuitBoardRenderContext.GATE_PIXEL_SIZE);
    		cg.setWidth(width);
    		
    		Main.getWindow().getSelectedBoard().getCustomGates().addElement(cg);
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

    @Override
	public int getNumberOfRegisters() {
    	
    	if(matrixes.size() > 1)
    		return matrixes.size();
    	else
    		return (int) Math.round(Math.log10(matrixes.get(0).getRows()) / AbstractGate.LOG10_2);
	}

}
