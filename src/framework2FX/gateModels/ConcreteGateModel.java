package framework2FX.gateModels;

import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.MathSet;

public class ConcreteGateModel extends AbstractGateModel {
	private static final long serialVersionUID = 7379046930854321915L;
	
	private Matrix<Complex> matrix;

	public ConcreteGateModel(String name, String symbol, Matrix<Complex> matrix) {
		this(name, symbol, "", matrix);
	}
	
	public ConcreteGateModel(String name, String symbol, String description, int matSize, Complex ... matElements) {
		this(name, symbol, description, new Matrix<>(matSize, matSize, matElements));
	}
	
	public ConcreteGateModel(String name, String symbol, String description, Matrix<Complex> matrix) {
		super(name, symbol, description);
		checkSize(matrix);
		this.matrix = matrix;
	}

	@Override
	public GateModelType getGateModelType() {
		return GateModelType.CUSTOM_GATE;
	}
	
	// attribute set ignored
	@Override
	public Matrix<Complex> getMatrix(MathSet mathDefinitons) {
		return matrix;
	}

	@Override
	public boolean isMultiQubitGate() {
		return matrix.getRows() > 2;
	}

	@Override
	public int getNumberOfRegisters() {
		int i = 0;
		int size = matrix.getRows();
		while(((size >> (++i)) | 1) != 1);
		return i - 1;
	}
	
	private static void checkSize(Matrix<Complex> mat) {
		if(mat.getRows() != mat.getColumns())
			throw new InvalidGateModelMatrixException("Number of rows must be equal to number of Columns");
		
		int size = mat.getRows();
		if((size & (size - 1)) != 0 || size < 2)
			throw new InvalidGateModelMatrixException("The matrix size must greater than 1 and be some power of 2");
	}

}
