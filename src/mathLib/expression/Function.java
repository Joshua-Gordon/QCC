package mathLib.expression;

import java.util.Objects;

import mathLib.MathValue;
import mathLib.expression.Variable.ConcreteVariable;

public abstract class Function {
	
	private FunctionID id;
	
	public Function(String name, int numParams) {
		this.id = new FunctionID(name, numParams);
	}
	
	public Function(FunctionID functionID) {
		this.id = functionID;
	}
	
	public FunctionID getID() {
		return id;
	}
	
	public abstract MathValue compute(MathSet setDefinedBody, MathSet setGroup, Expression ... expressions);
	
	public static class ExpressionDefinedFunction extends Function {
		private final String[] params;
		private final Expression definition;
		
		public ExpressionDefinedFunction(String definition, String name, String ... params) {
			this(new Expression(definition), name, params);
		}
		
		public ExpressionDefinedFunction(Expression definition, String name, String ... params) {
			super(name, params.length);
			this.params = params;
			this.definition = definition;
		}
		
		@Override
		public MathValue compute(MathSet setDefinedBody, MathSet localSet, Expression ... expressions) {
			MathSet paramDefinitons = new MathSet(setDefinedBody);
			int i = 0;
			for(String param : params)
				paramDefinitons.addVariable(new ConcreteVariable(param, expressions[i++].compute(localSet)));
			return definition.compute(paramDefinitons);
		}
	}
	
	public static class ConcreteFunction extends Function {
		private final FunctionDefinition definition;
		
		public ConcreteFunction (String name, int numParams, FunctionDefinition definition) {
			super(name, numParams);
			this.definition = definition;
		}
		
		public MathValue compute(MathSet setDefinedBody, MathSet localSet, Expression ... expressions) {
			return definition.compute(setDefinedBody, localSet, expressions);
		}
	}
	
	public static interface FunctionDefinition {
		
		public MathValue compute(MathSet setDefinedBody, MathSet localSet, Expression ... expressions);
		
	}
	
	
	
	public static class FunctionID {
		private final String name;
		private final int numParams;
		
		public FunctionID (String name, int numParams) {
			this.name = name;
			this.numParams = numParams;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null || !(obj instanceof FunctionID))
				return false;
			FunctionID fh = (FunctionID) obj;
			return name.equals(fh.name) && numParams == fh.numParams;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(name, numParams);
		}
	}
}
