package framework2FX.gateModels;

import framework2FX.MathDefintions;
import framework2FX.gateModels.AbstractGateModel.InvalidGateModelMatrixException;
import mathLib.Complex;
import mathLib.MathValue;
import mathLib.Matrix;
import mathLib.expression.Expression;

public enum DefaultGateModel {
	
	Identity (new DefaultMatrixGateModel("Identity", "I", 2,
			Complex.ONE(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE())),
	
	Hadamard (new DefaultMatrixGateModel("Hadamard", "H", 2,
			Complex.ONE(), Complex.ONE(),
            Complex.ONE(), Complex.ONE().negative())),
	
	Pauli_x (new DefaultMatrixGateModel("Pauli x", "X", 2,
			Complex.ZERO(), Complex.ONE(),
            Complex.ONE(), Complex.ZERO())),
	
	Pauli_y (new DefaultMatrixGateModel("Pauli y", "Y", 2,
			Complex.ZERO(), Complex.I().negative(),
            Complex.I(), Complex.ZERO())),
	
	Pauli_z (new DefaultMatrixGateModel("Pauli z", "Z", 2,
			Complex.ZERO(), Complex.I().negative(),
            Complex.I(), Complex.ZERO())),
	
	Phase (new DefaultMatrixGateModel("Phase", "S", 2,
			Complex.ONE(), Complex.ZERO(),
    		Complex.ZERO(), Complex.I())),
	
	Pi_on_8 (new DefaultMatrixGateModel("Pi/8", "T", 2,
			Complex.ONE(), Complex.ZERO(),
    		Complex.ZERO(), (Complex.ONE().add(Complex.I())).mult(Complex.ISQRT2()))),
	
	Swap (new DefaultMatrixGateModel("Swap", "", 4,
			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE())),
	
	Cnot (new DefaultMatrixGateModel("Cnot", "", 4,
			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
            Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO())),
	
	Toffoli (new DefaultMatrixGateModel("Toffoli", "", 8,
			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO())),
	
	Measurement (new DefaultMatrixGateModel("Measurement", "M")),
	
	
	
	
	;
	
	
	
	private final AbstractGateModel gateModel;
	
	private DefaultGateModel(DefaultMatrixGateModel gateModel) {
		this.gateModel = gateModel;
		gateModel.setDefaultGate(this);
	}
	
	public AbstractGateModel getModel() {
		return gateModel;
	}
	
	
	public static final class DefaultMatrixGateModel extends ConcreteGateModel {
		private static final long serialVersionUID = 5201159952522714585L;
		
		private DefaultGateModel defaultGate;
		
		private DefaultMatrixGateModel(String name, String symbol) {
			super(name, symbol, new Matrix<>(2, 2, Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO()));
		}
		
		private DefaultMatrixGateModel(String name, String symbol, String expression) {
			this(name, symbol, "", expression);
		}
		
		private DefaultMatrixGateModel(String name, String symbol, String description, String expression) {
			super(name, symbol, description, expressionToMatrix(expression));
		}
		
		
		private DefaultMatrixGateModel(String name, String symbol, String description, Matrix<Complex> matrix) {
			super(name, symbol, description, matrix);
		}
		
		private DefaultMatrixGateModel(String name, String symbol, String description, int matSize, Complex ... matElements) {
			super(name, symbol, description, matSize, matElements);
		}
		
		private DefaultMatrixGateModel(String name, String symbol, Matrix<Complex> matrix) {
			super(name, symbol, "", matrix);
		}
		
		private DefaultMatrixGateModel(String name, String symbol, int matSize, Complex ... matElements) {
			super(name, symbol, "", matSize, matElements);
		}
		
		private void setDefaultGate(DefaultGateModel defaultGate) {
			this.defaultGate = defaultGate;
		}
		
		public DefaultGateModel getDefaultGate() {
			return defaultGate;
		}
		
		@Override
		public GateModelType getGateModelType() {
			return GateModelType.DEFAULT_GATE;
		}
	
	}
	
	
	@SuppressWarnings("unchecked")
	private static Matrix<Complex> expressionToMatrix(String expression) {
		Expression e = new Expression(expression);
		MathValue mathO =  e.compute(MathDefintions.GLOBAL_DEFINITIONS);
		if(mathO instanceof Matrix<?>)
			return (Matrix<Complex>) mathO;
		else throw new InvalidGateModelMatrixException("The expression is scalar");
	}
}
