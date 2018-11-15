package mathLib.expression;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import mathLib.MathValue;
import mathLib.expression.Variable.ConcreteVariable;

public abstract class Function {
	private static HashSet<Integer>  NONE = null;
	
	private final FunctionID id;
	private final LatexFormat format;
	private final HashSet<Integer> variableParamIndexes;
	
	public Function(String name, int numParams, HashSet<Integer> variableParamIndexes, LatexFormat format) {
		this(new FunctionID(name, numParams), variableParamIndexes, format);
	}
	
	public Function(FunctionID functionID, HashSet<Integer> variableParamIndexes, LatexFormat format){
		this.id = functionID;
		this.format = format;
		this.variableParamIndexes = variableParamIndexes;
	}
	
	public String getName () {
		return id.name;
	}
	
	public int numArgs() {
		return id.numArgs;
	}
	
	FunctionID getID() {
		return id;
	}
	
	public LatexFormat getLatexFormat() {
		return format;
	}
	
	public boolean isVariableParam(int paramIndex) {
		return variableParamIndexes != null && variableParamIndexes.contains(paramIndex);
	}
	
	public abstract MathValue compute(MathSet setDefinedBody, MathSet setGroup, Expression ... expressions);
	
	public static class ExpressionDefinedFunction extends Function {
		private final String[] params;
		private final Expression definition;
		
		public ExpressionDefinedFunction(String definition, String name, String ... params) {
			this(definition, name, LatexFormat.NONE, params);
		}
		
		public ExpressionDefinedFunction(String definition, String name, LatexFormat format, String ... params) {
			this(definition, name, NONE, format, params);
		}
		
		public ExpressionDefinedFunction(String definition, String name, HashSet<Integer> variableParamIndexes, LatexFormat format, String ... params) {
			super(name, params.length, variableParamIndexes, format);
			this.params = params;
			this.definition = new Expression(definition);
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
			this(name, numParams, LatexFormat.NONE, definition);
		}
		
		public ConcreteFunction (String name, int numParams, LatexFormat format, FunctionDefinition definition) {
			this(name, numParams, NONE, format, definition);
		}
		
		public ConcreteFunction (String name, int numParams, HashSet<Integer> variableParamIndexes, LatexFormat format, FunctionDefinition definition) {
			super(name, numParams, variableParamIndexes, format);
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
		private final int numArgs;
		
		public FunctionID (String name, int numParams) {
			this.name = name;
			this.numArgs = numParams;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null || !(obj instanceof FunctionID))
				return false;
			FunctionID fh = (FunctionID) obj;
			return name.equals(fh.name) && numArgs == fh.numArgs;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(name, numArgs);
		}
	}
	
	public static class LatexFormat implements Iterable<Object>{
		public static final LatexFormat NONE = null;
		
		private final Object[] formatArray;
		
		public static Integer insertParam(int index) {
			return index;
		}
		
		public static boolean isParam(Object formatPart) {
			return formatPart instanceof Integer;
		}
		
		public static int getParamNumber(Object formatPart) {
			if(!isParam(formatPart))
				throw new IllegalArgumentException("String must be an param");
			return (Integer) formatPart;
		}
		
		public LatexFormat (Object ... formatArray) {
			this.formatArray = formatArray;
		}

		@Override
		public Iterator<Object> iterator() {
			return new LatexFormatIterator();
		}
		
		public int size() {
			return formatArray.length;
		}
		
		public class LatexFormatIterator implements Iterator<Object> {
			private int index = -1;
			
			@Override
			public boolean hasNext() {
				return index + 1 < size();
			}
			
			@Override
			public Object next() {
				return formatArray[++index];
			}
			
		}
	}
}
