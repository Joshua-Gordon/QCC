package framework2FX.gateModels;

import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.Expression;
import mathLib.expression.MathSet;

public class ExpressionGateModel extends AbstractGateModel {
	private static final long serialVersionUID = -7193017988855705441L;
	
	private final Expression expression;
//	private final ModelData modelData;

	public ExpressionGateModel(String name, String description, String symbol, Expression expression) {
		super(name, description, symbol);
		this.expression = expression;
	}

	@Override
	public GateModelType getGateModelType() {
		return GateModelType.CUSTOM_GATE;
	}

	@Override
	public Matrix<Complex> getMatrix(MathSet mathDefinitions) {
		return null;
	}

	@Override
	public boolean isMultiQubitGate() {
		return false;
	}

	@Override
	public int getNumberOfRegisters() {
		return 0;
	}
}
