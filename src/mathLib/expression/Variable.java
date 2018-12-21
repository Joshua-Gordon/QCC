package mathLib.expression;

import mathLib.MathValue;
import mathLib.expression.Expression.EvaluateExpressionException;

public abstract class Variable {
	public static final String NONE = null;
	
	private final String name;
	private final String latexFormat;
	
	public Variable (String name, String latexFormat) {
		this.name = name;
		this.latexFormat = latexFormat;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLatexFormat() {
		return latexFormat;
	}
	
	public abstract MathValue getValue(MathSet group) throws EvaluateExpressionException;
	
	
	public static class ExpressionDefinedVariable extends Variable {
		private Expression definition;
		
		public ExpressionDefinedVariable(String name, String latexFormat, Expression definition) {
			super(name, latexFormat);
			this.definition = definition;
		}
		
		public ExpressionDefinedVariable(String name, Expression definition) {
			this(name, NONE, definition);
		}
		
		@Override
		public MathValue getValue(MathSet group) throws EvaluateExpressionException {
			return definition.compute(group);
		}
	}
	
	public static class ConcreteVariable extends Variable {
		private MathValue value;
		
		public ConcreteVariable(String name, String latexFormat, MathValue value) {
			super(name, latexFormat);
			this.value = value;
		}
		
		public ConcreteVariable(String name, MathValue value) {
			this(name, NONE, value);
		}

		@Override
		public MathValue getValue(MathSet group) {
			return value;
		}
		
	}
}
