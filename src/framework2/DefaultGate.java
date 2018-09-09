package framework2;

import mathLib.Complex;
import mathLib.Matrix;

public enum DefaultGate {
	
	
	
	Identity (new DefaultGateModel("Identity", "I", 2,
			Complex.ONE(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE())),
	
	Hadamard (new DefaultGateModel("Hadamard", "H", 2,
			Complex.ONE(), Complex.ONE(),
            Complex.ONE(), Complex.ONE().negative())),
	
	Pauli_x (new DefaultGateModel("Pauli x", "X", 2,
			Complex.ZERO(), Complex.ONE(),
            Complex.ONE(), Complex.ZERO())),
	
	Pauli_y (new DefaultGateModel("Pauli y", "Y", 2,
			Complex.ZERO(), Complex.I().negative(),
            Complex.I(), Complex.ZERO())),
	
	Pauli_z (new DefaultGateModel("Pauli z", "Z", 2,
			Complex.ZERO(), Complex.I().negative(),
            Complex.I(), Complex.ZERO())),
	
	Phase (new DefaultGateModel("Phase", "S", 2,
			Complex.ONE(), Complex.ZERO(),
    		Complex.ZERO(), Complex.I())),
	
	Pi_on_8 (new DefaultGateModel("Pi/8", "T", 2,
			Complex.ONE(), Complex.ZERO(),
    		Complex.ZERO(), (Complex.ONE().add(Complex.I())).mult(Complex.ISQRT2()))),
	
	Swap (new DefaultGateModel("Swap", "", 4,
			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE())),
	
	Cnot (new DefaultGateModel("Cnot", "", 4,
			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
            Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO())),
	
	Toffoli (new DefaultGateModel("Toffoli", "", 8,
			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
            Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO())),
	
	Measurement (new DefaultGateModel("Measurement", "M", null)),
	
	
	
	
	;
	
	
	
	private final AbstractGateModel gateModel;
	
	private DefaultGate(DefaultGateModel gateModel) {
		this.gateModel = gateModel;
		gateModel.setDefaultGate(this);
	}
	
	public AbstractGateModel getModel() {
		return gateModel;
	}
	
	
	public static final class DefaultGateModel extends AbstractGateModel {
		private static final long serialVersionUID = 5201159952522714585L;
		
		private DefaultGate defaultGate;
		
		private DefaultGateModel(String description, String name, String symbol, Matrix<Complex> matrix) {
			super(description, name, symbol, matrix);
		}
		
		private DefaultGateModel(String description, String name, String symbol, int matSize, Complex ... matElements) {
			super(description, name, symbol, matSize, matElements);
		}
		
		private DefaultGateModel(String name, String symbol, Matrix<Complex> matrix) {
			super("", name, symbol, matrix);
		}
		
		private DefaultGateModel(String name, String symbol, int matSize, Complex ... matElements) {
			super("", name, symbol, matSize, matElements);
		}
		
		private void setDefaultGate(DefaultGate defaultGate) {
			this.defaultGate = defaultGate;
		}
		
		
		
		
		
		public DefaultGate getDefaultGate() {
			return defaultGate;
		}
		
		@Override
		public GateModelType getGateModelType() {
			return GateModelType.DEFAULT_GATE;
		}
		
	}
}
