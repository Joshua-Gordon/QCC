package appFX.framework;

import java.util.HashSet;

import mathLib.Complex;
import mathLib.MathValue;
import mathLib.Matrix;
import mathLib.expression.Expression;
import mathLib.expression.Expression.ExpressionParser.EquationParseException;
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
		
		
		try {
		
		// Expression defined functions and constants are defined here:
		
		
			GLOBAL_DEFINITIONS.addFunctionDefinition(new ExpressionDefinedFunction("n^.5", "sqrt", 
					new LatexFormat(" { \\sqrt{ ", LatexFormat.insertParam(0), " } } "), "n"));
			
			
			
			
		} catch (EquationParseException epe) {
			epe.printStackTrace();
			System.exit(1);
		}
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
			if(!(mv instanceof Complex)) 
				throw new IllegalArgumentException("Function not defined for Matrix inputs");
			
			return ((Complex) mv).exponentiated();
		}));
		
		
		// cos(x)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("cos", 1, (global, local, args) -> {
					
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Complex)) 
				throw new IllegalArgumentException("Function not defined for Matrix inputs");
			
			Complex c = (Complex) mv;
			if(!MathValue.fuzzyEquals(c.getImaginary(), 0)) {
				return new Complex(Math.cos(c.getReal()), 0);
			} else {
				double x = c.getReal();
				double y = c.getImaginary();
				return new Complex(Math.cos(x) * Math.cosh(y), - Math.sin(x) * Math.sinh(y));
			}
		}));
		
		// sin(x)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("sin", 1, (global, local, args) -> {
							
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Complex)) 
				throw new IllegalArgumentException("Function not defined for Matrix inputs");
			
			Complex c = (Complex) mv;
			if(!MathValue.fuzzyEquals(c.getImaginary(), 0)) {
				return new Complex(Math.sin(c.getReal()), 0);
			} else {
				double x = c.getReal();
				double y = c.getImaginary();
				return new Complex(Math.sin(x) * Math.cosh(y), Math.cos(x) * Math.sinh(y));
			}
		}));
		
		// tan(x)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("tan", 1, (global, local, args) -> {
							
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Complex)) 
				throw new IllegalArgumentException("Function not defined for Matrix inputs");
			
			Complex c = (Complex) mv;
			if(!MathValue.fuzzyEquals(c.getImaginary(), 0)) {
				return new Complex(Math.tan(c.getReal()), 0);
			} else {
				double x = c.getReal();
				double y = c.getImaginary();
				
				Complex first = new Complex(Math.sin(2 * x), Math.sinh(2 * y));
				Complex second = new Complex(Math.cos(2 * x) + Math.cosh(2 * y), 0);				
				return first.div(second);
			}
		}));
		
		
		// trace(x)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("trace", 1, (global, local, args) -> {
									
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Matrix)) 
				throw new IllegalArgumentException("Function not defined for non-Matrix inputs");
			
			@SuppressWarnings("unchecked")
			Matrix<Complex> mat = (Matrix<Complex>) mv;
			if(mat.getRows() != mat.getColumns()) 
				throw new IllegalArgumentException("Function input is not defined for non-square matrixes");
			
			Complex sum = Complex.ZERO();
			for(int i = 0; i < mat.getRows(); i++)
				sum = sum.add(mat.v(i, i));
			
			return sum;
		}));
		
		
		// abs(x) = |x|
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("abs", 1, 
				new LatexFormat(" { \\lvert ", LatexFormat.insertParam(0) , " \\rvert } "), 
				(global, local, args) -> {
			
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Complex)) 
				throw new IllegalArgumentException("Function not defined for non-Complex inputs");
			
			Complex value = (Complex) mv;
			
			return new Complex(value.abs(), 0);
		}));
		
	}

}
