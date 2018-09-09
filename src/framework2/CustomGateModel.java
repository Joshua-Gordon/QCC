package framework2;

import mathLib.Complex;
import mathLib.Matrix;

public class CustomGateModel extends AbstractGateModel{
	
	private static final long serialVersionUID = -1093117887117073147L;
	
	public CustomGateModel(String name, String symbol, int matSize, Matrix<Complex> matrix) {
		super(name, symbol, matrix);
	}
	
	@Override
	public GateModelType getGateModelType() {
		return GateModelType.CUSTOM_GATE;
	}

}
