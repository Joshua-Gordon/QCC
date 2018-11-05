package mathLib.expression;

import java.util.Hashtable;

import mathLib.Complex;
import mathLib.MathValue;
import mathLib.Matrix;
import mathLib.expression.Function.FunctionID;

public class MathSet {
	private final MathSet enclosingSubSet;
	private Hashtable<FunctionID, Function> functionSet = new Hashtable<>();
	private Hashtable <String, Variable> variableTable = new Hashtable<>();
	
	public MathSet () {
		this(null);
	}
	
	public MathSet (MathSet enclosingSubSet) {
		this.enclosingSubSet = enclosingSubSet;
	}
	
	public void addVariable(Variable v) {
		variableTable.put(v.getName(), v);
	}
	
	public void removeVariable(Variable v) {
		variableTable.remove(v.getName());
	}
	
	public void addFunctionDefinition (Function function) {
		functionSet.put(function.getID(), function);
	}
	
	public void removeFunctionDefinition(Function function) {
		functionSet.remove(function.getID());
	}
	
	public MathSet getEnclosingSubSet () {
		return enclosingSubSet;
	}
	
	private Variable getVariable (String name) {
		Variable variable = variableTable.get(name);
		if(variable == null && enclosingSubSet != null)
			variable = enclosingSubSet.getVariable(name);
		return variable;
	}
	
	private Function getFunction (String name, int numParams) {
		Function function = functionSet.get(new FunctionID(name, numParams));
		if(function == null && enclosingSubSet != null)
			function = enclosingSubSet.getFunction(name, numParams);
		return function;
	}
	
	public MathValue computeFunction(String name, Expression ... params) {
		Function function = functionSet.get(new FunctionID(name, params.length));
		if(function == null)
			if(enclosingSubSet != null)
				return enclosingSubSet.computeFunction(this, name, params);
			else throw new FunctionNotDefinedException(name, params.length);
		return function.compute(this, this, params);
	}
	
	private MathValue computeFunction(MathSet localSet, String name, Expression ... params) {
		Function function = getFunction(name, params.length);
		if(function == null)
			throw new FunctionNotDefinedException(name, params.length);
		return function.compute(this, localSet, params);
	}
	
	public MathValue computeVariable (String name) {
		Variable variable = getVariable(name);
		if(variable == null)
			throw new VariableNotDefinedException(name);
		return variable.getValue(this);
	}
	
	@SuppressWarnings("serial")
	public static class FunctionNotDefinedException extends RuntimeException {
		private String functionName;
		private int numParams;
		
		public FunctionNotDefinedException (String functionName, int numParams) {
			super("Function \"" + functionName + "\" with " + numParams + " parameters is not defined.");
			this.functionName = functionName;
			this.numParams = numParams;
		}
		
		public String getFunctionName() {
			return functionName;
		}
		
		public int getNumParams() {
			return numParams;
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class VariableNotDefinedException extends RuntimeException {
		private String variableName;
		
		public VariableNotDefinedException (String variableName) {
			super("Variable \"" + variableName + "\" is not defined.");
			this.variableName = variableName;
		}
		
		public String getVariableName() {
			return variableName;
		}
	}
	
}
