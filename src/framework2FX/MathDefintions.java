package framework2FX;

import mathLib.Complex;
import mathLib.MathValue;
import mathLib.expression.Expression;
import mathLib.expression.Function.ConcreteFunction;
import mathLib.expression.Function.ExpressionDefinedFunction;
import mathLib.expression.MathSet;
import mathLib.expression.Variable.ConcreteVariable;

public class MathDefintions {
	public static final MathSet GLOBAL_DEFINITIONS =  new MathSet();
	
	static {
		// Concrete Definitons
		GLOBAL_DEFINITIONS.addVariable(new ConcreteVariable("i", Complex.I()));
		GLOBAL_DEFINITIONS.addVariable(new ConcreteVariable("e", Complex.e()));
		GLOBAL_DEFINITIONS.addVariable(new ConcreteVariable("pi", Complex.pi()));
		
		
		
		// sum ( f(var) , var , min,  max) =  f(min) + f(min + 1) + ... + f(max)
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("sum", 4, (global, local, args)-> {
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
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("prod", 4, (global, local, args)-> {
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
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ConcreteFunction("exp", 1, (global, local, args) -> {
			MathValue mv = args[0].compute(local);
			if(!(mv instanceof Complex)) throw new IllegalArgumentException("Function not defined for Matrix inputs");
			return ((Complex) mv).exponentiated();
		}));
		
		// Expression Definitions
		
		GLOBAL_DEFINITIONS.addFunctionDefinition(new ExpressionDefinedFunction("n^.5", "sqrt", "n"));
		
		
	}

}
