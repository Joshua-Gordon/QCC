package mathLib.expression;

import mathLib.MathValue;

public abstract class Variable {
	private String name;
	
	
	public Variable (String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract MathValue getValue(MathSet group);
	
	
	public static class ExpressionDefinedVariable extends Variable {
		private Expression definition;
		
		public ExpressionDefinedVariable(String name, Expression definition) {
			super(name);
			this.definition = definition;
		}

		@Override
		public MathValue getValue(MathSet group) {
			return definition.compute(group);
		}
	}
	
	public static class ConcreteVariable extends Variable {
		private MathValue value;
		
		public ConcreteVariable(String name, MathValue value) {
			super(name);
			this.value = value;
		}

		@Override
		public MathValue getValue(MathSet group) {
			return value;
		}
		
	}
}
