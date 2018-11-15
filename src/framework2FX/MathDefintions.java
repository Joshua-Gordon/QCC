package framework2FX;

import java.util.HashSet;

import mathLib.Complex;
import mathLib.MathValue;
import mathLib.expression.Expression;
import mathLib.expression.Function.ConcreteFunction;
import mathLib.expression.Function.ExpressionDefinedFunction;
import mathLib.expression.Function.LatexFormat;
import mathLib.expression.MathSet;
import mathLib.expression.Variable.ConcreteVariable;

public class MathDefintions {
	public static final MathSet GLOBAL_DEFINITIONS =  new MathSet();
	
	
	
	
	
	
	static {
		// mathematical expressions and variables defined by java code:
		addConcreteDefinitions();
		
		
		// Expression defined functions and constants are defined here:
		
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ExpressionDefinedFunction("n^.5", "sqrt", 
				new LatexFormat(" { \\sqrt{ ", LatexFormat.insertParam(0), " } } "), "n"));
		
		
	}
	
	
	
	
	
	
	
	public static void addConcreteDefinitions () {
		// Concrete Definitons
		GLOBAL_DEFINITIONS.addVariable(new ConcreteVariable("i", Complex.I()));
		GLOBAL_DEFINITIONS.addVariable(new ConcreteVariable("e", Complex.e()));
		GLOBAL_DEFINITIONS.addVariable(new ConcreteVariable("pi", "\\pi", Complex.pi()));
		
		
		
		// this is used for "sum" and "prod" function definitions since the 2nd parameter is expected to just be a variable name
		final HashSet<Integer> variableParamIndexes = new HashSet<>();
		variableParamIndexes.add(1);
		
		
		
		
		
		// sum ( f(var) , var , min,  max) =  f(min) + f(min + 1) + ... + f(max)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("sum", 4, variableParamIndexes, 
				new LatexFormat(" { \\sum_{ ", LatexFormat.insertParam(1), " = ", LatexFormat.insertParam(2),
						" } ^ { ", LatexFormat.insertParam(3) , " } ( ", LatexFormat.insertParam(0), " ) } ") ,
				
				(global, local, args)-> {
			String variable = Expression.getVariableFrom(args[1]);
			MathValue arg2 = args[2].compute(local);
			MathValue arg3 = args[3].compute(local);
			MathValue sum = Complex.ZERO();
			
			MathSet mg = new MathSet(local);
			
			long min = MathValue.getInteger(arg2);
			long max = MathValue.getInteger(arg3);
			for (long i = min ; i <= max; i++) {
				mg.addVariable(new ConcreteVariable(variable, new Complex(i, 0)));
				sum = MathValue.add(sum, args[0].compute(mg));
			}
			return sum;
		}));
		
		
		
		
		// prod ( f(var) , var , min,  max) =  f(min) * f(min + 1) * ... * f(max)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("prod", 4, variableParamIndexes,
				new LatexFormat(" { \\prod_{ ", LatexFormat.insertParam(1), " = ", LatexFormat.insertParam(2),
						" } ^ { ", LatexFormat.insertParam(3) , " } ( ", LatexFormat.insertParam(0), " ) } ") ,
				
				(global, local, args)-> {
			String variable = Expression.getVariableFrom(args[1]);
			MathValue arg2 = args[2].compute(local);
			MathValue arg3 = args[3].compute(local);
			MathValue prod = Complex.ONE();
			
			MathSet mg = new MathSet(local);
			
			long min = MathValue.getInteger(arg2);
			long max = MathValue.getInteger(arg3);
			for (long i = min ; i <= max; i++) {
				mg.addVariable(new ConcreteVariable(variable, new Complex(i, 0)));
				prod = MathValue.mult(prod, args[0].compute(mg));
			}
			return prod;
		}));
		
		
		
		
		// exp(x) = e^x
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("exp", 1, 
				new LatexFormat(" { e ^ { ", LatexFormat.insertParam(0), " } } ") ,
				
				(global, local, args) -> {
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Complex)) throw new IllegalArgumentException("Function not defined for Matrix inputs");
			return ((Complex) mv).exponentiated();
		}));
	}

}
