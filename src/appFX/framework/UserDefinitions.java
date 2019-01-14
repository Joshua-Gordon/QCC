package appFX.framework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseLeaf;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol;
import mathLib.Complex;
import mathLib.MathValue;
import mathLib.Matrix;
import mathLib.expression.Expression;
import mathLib.expression.Expression.EvaluateExpressionException;
import mathLib.expression.Expression.ExpressionParser;
import mathLib.expression.Expression.ExpressionParser.EquationParseException;
import mathLib.expression.Function;
import mathLib.expression.Function.LatexFormat;
import mathLib.expression.MathSet;
import mathLib.expression.Variable;
import mathLib.expression.Variable.ConcreteVariable;
import utils.customCollections.ImmutableArray;

public class UserDefinitions implements Serializable {
	private static final long serialVersionUID = 5627225884389883084L;







	public static GroupDefinition evaluateInput(CheckDefinitionRunnable runnable, String ... rawUserInput) throws DefinitionEvaluatorException {
		return evaluateInput(runnable, null, rawUserInput); 
	}
	
	
	public static GroupDefinition evaluateInput(CheckDefinitionRunnable runnable, String[] onlyVariables, String ... rawUserInput) throws DefinitionEvaluatorException {
		Definition[] definitions = new Definition[rawUserInput.length];
		
		int i = 0;
		for(String input : rawUserInput) {
			
			try {
				definitions[i] = evaluateInput(input);
				
				if(onlyVariables != null && definitions[i].hasArguments()) {
					for(String arg : ((ArgDefinition) definitions[i]).getArguments()) {
						boolean isContained = false;
						for(String var : onlyVariables) {
							if(arg.equals(var)) {
								isContained = true;
								break;
							}
						}
						if(!isContained)
							throw new DefinitionEvaluatorException("Variable \"" + arg + "\" is not defined", i);
					}
				}
			} catch (EquationParseException | EvaluateExpressionException e) {
				throw new DefinitionEvaluatorException(e.getMessage(), i);
			}  
			
			if(definitions[i].hasArguments())
				runnable.checkArgDefinition((ArgDefinition)definitions[i], i);
			else if (definitions[i].isMatrix())
				runnable.checkMatrixDefinition((MatrixDefinition)definitions[i], i);
			else
				runnable.checkScalarDefinition((ScalarDefinition)definitions[i], i);
			i++;
		}
		
		
		return new GroupDefinition(definitions);
	}
	
	
	
	
	
	
	public static Definition evaluateInput (String rawUserInput) throws EquationParseException, EvaluateExpressionException {
		return evaluateInput(rawUserInput, MathDefintions.GLOBAL_DEFINITIONS);
	}
	
	
	
	
	public static Definition evaluateInput (String rawUserInput, MathSet mathdefinitions) throws EquationParseException, EvaluateExpressionException {
		String userInputString = rawUserInput; 
		Expression userInputExpression = new Expression(userInputString);
		ExpressionTraits et = getExpressionTraits(userInputExpression, mathdefinitions);
		
		if(et.undefinedVariables.size() == 0)
			if(et.result instanceof Matrix<?>)
				return new MatrixDefinition(userInputString, et);
			else
				return new ScalarDefinition(userInputString, et);
		else
			return new ArgDefinition(userInputString, et);
	}
	
	public static interface CheckDefinitionRunnable {
		
		public void checkScalarDefinition(ScalarDefinition definition, int numDefinition) throws DefinitionEvaluatorException;
		public void checkMatrixDefinition(MatrixDefinition definition, int numDefinition) throws DefinitionEvaluatorException;
		public void checkArgDefinition(ArgDefinition definition, int numDefinition) throws DefinitionEvaluatorException;
		
	}
	
	
	@SuppressWarnings("serial")
	public static class DefinitionEvaluatorException extends Exception {
		private final int definitionNumber;
		
		public DefinitionEvaluatorException (String message, int definitionNumber) {
			super(message);
			this.definitionNumber = definitionNumber;
		}
		
		public int getDefinitionNumber () {
			return definitionNumber;
		}
	}
	
	
	
	public static abstract class Definition implements Serializable {
		private static final long serialVersionUID = -1749477646803650121L;
		
		private final String rawUserInput;
		private final String latexRepresentation;
		
		Definition (String rawUserInput, ExpressionTraits et) {
			this.rawUserInput = rawUserInput;
			this.latexRepresentation = et.latexString;
		}
		
		
		public abstract boolean hasArguments();
		public abstract boolean isMatrix();
		
		
		public String getRawUserInput() {
			return rawUserInput;
		}
		
		public String getLatexRepresentation() {
			return latexRepresentation;
		}	
	}
	
	
	
	
	public static class ScalarDefinition extends Definition {
		private static final long serialVersionUID = 2084584343957581263L;
		
		private final Complex value;
		
		ScalarDefinition(String rawUserInput, ExpressionTraits et) {
			super(rawUserInput, et);
			this.value = (Complex) et.result;
		}
		
		public Complex getValue () {
			return value;
		}
		
		@Override
		public boolean hasArguments() {
			return false;
		}

		@Override
		public boolean isMatrix() {
			return false;
		}
		
	}
	
	
	
	
	
	
	public static class MatrixDefinition extends Definition {
		private static final long serialVersionUID = -1751452868227716197L;
		
		private final Matrix<Complex> matrix;
		
		@SuppressWarnings("unchecked")
		MatrixDefinition(String rawUserInput, ExpressionTraits et) {
			super(rawUserInput, et);
			this.matrix = (Matrix<Complex>) et.result;
		}

		public Matrix<Complex> getMatrix() {
			return matrix;
		}
		
		@Override
		public boolean hasArguments() {
			return false;
		}

		@Override
		public boolean isMatrix() {
			return true;
		}
		
	}
	
	
	
	
	
	public static class ArgDefinition extends Definition {
		private static final long serialVersionUID = -2552282272041934364L;
		
		private final boolean isMatrix;
		private final int rows, columns;
		private final ImmutableArray<String> arguments;
		private final Expression definition;
		
		@SuppressWarnings("unchecked")
		ArgDefinition (String rawUserInput, ExpressionTraits et) {
			super(rawUserInput, et);
			this.isMatrix = et.result instanceof Matrix<?>;
			this.definition = et.expression;
			
			if(isMatrix) {
				Matrix<Complex> m = (Matrix<Complex>) et.result;
				this.rows = m.getRows();
				this.columns = m.getColumns();
			} else {
				this.rows = -1;
				this.columns = -1;
			}
			HashSet<String> argSet = et.undefinedVariables;
			String[] argList = new String[argSet.size()];
			argSet.toArray(argList);
			
			this.arguments = new ImmutableArray<>(argList);
			
		}
		
		@Override
		public boolean hasArguments() {
			return true;
		}

		public ImmutableArray<String> getArguments() {
			return arguments;
		}
		
		@Override
		public boolean isMatrix() {
			return isMatrix;
		}

		public int getRows() {
			return rows;
		}

		public int getColumns() {
			return columns;
		}
		
	}
	
	
	
	public static class GroupDefinition implements Serializable {
		private static final long serialVersionUID = 6568397205909656782L;
		
		private final ImmutableArray<String> arguments;
		private final ImmutableArray<String> rawUserInput;
		private final ImmutableArray<String> latexRepresentations;
		private final ImmutableArray<MathObject> definitions;
		
		public GroupDefinition (Definition ... definitions) {
			HashSet<String> argumentSet = new HashSet<>();
			
			String[] userDefs = new String[definitions.length];
			String[] latexStrings = new String[definitions.length];
			MathObject[] defs = new MathObject[definitions.length];
			
			int i = 0;
			for(Definition def : definitions) {
				userDefs[i] = def.rawUserInput;
				latexStrings[i] = def.latexRepresentation;
				
				if(def.hasArguments()) {
					ArgDefinition argDef = (ArgDefinition) def;
					for(String arg : argDef.arguments)
						argumentSet.add(arg);
					defs[i] = new ArgObject(argDef.definition, argDef.isMatrix, argDef.rows, argDef.columns);
				} else if (def.isMatrix()){
					defs[i] = new MatrixObject(((MatrixDefinition)def).matrix);
				} else {
					defs[i] = new ScalarObject(((ScalarDefinition)def).value);
				}
				
				i++;
			}
			
			String[] args = new String[argumentSet.size()];
			i = 0;
			for(String s : argumentSet)
				args[i++] = s;
			
			this.arguments = new ImmutableArray<>(args);
			this.rawUserInput = new ImmutableArray<>(userDefs);
			this.latexRepresentations = new ImmutableArray<>(latexStrings);
			this.definitions = new ImmutableArray<>(defs);
		}
		
		public ImmutableArray<String> getArguments(){
			return arguments;
		}
		
		public ImmutableArray<String> getRawUserInput(){
			return rawUserInput;
		}
		
		public ImmutableArray<String> getLatexRepresentations(){
			return latexRepresentations;
		}
		
		public ImmutableArray<MathObject> getMathDefinitions(){
			return definitions;
		}
		
		public int getSize() {
			return definitions.size();
		}
	}
	
	
	public static abstract interface MathObject extends Serializable {
		
		public abstract boolean hasArguments ();
		public abstract boolean isMatrix();
		
	}
	
	
	public static class ScalarObject implements MathObject {
		private static final long serialVersionUID = 6189604891964648829L;
		
		private final Complex scalar;
		
		ScalarObject (Complex scalar) {
			this.scalar = scalar;
		}
		
		public Complex getScalar(){
			return scalar;
		}
		
		@Override
		public boolean hasArguments() {
			return false;
		}

		@Override
		public boolean isMatrix() {
			return false;
		}
		
	}
	
	
	public static class MatrixObject implements MathObject {
		private static final long serialVersionUID = 712029353060012634L;
		
		private final Matrix<Complex> matrix;
		
		
		MatrixObject (Matrix<Complex> matrix) {
			this.matrix = matrix;
		}
		
		
		public Matrix<Complex> getMatrix() {
			return matrix;
		}
		
		@Override
		public boolean hasArguments() {
			return false;
		}

		@Override
		public boolean isMatrix() {
			return true;
		}
	}
	
	public static class ArgObject implements MathObject {
		private static final long serialVersionUID = -8836456210671603780L;
		
		private final Expression definition;
		private final boolean isMatrix;
		private final int rows, columns;
		
		ArgObject(Expression definition, boolean isMatrix, int rows, int columns) {
			this.definition = definition;
			this.isMatrix = isMatrix;
			this.rows = rows;
			this.columns = columns;
		}
		
		@Override
		public boolean hasArguments() {
			return false;
		}

		@Override
		public boolean isMatrix() {
			return isMatrix;
		}
		
		public Expression getDefinition () {
			return definition;
		}
		
		public int getRows() {
			return rows;
		}
		
		public int getColumns() {
			return columns;
		}
		
		
	}
	
	

	private static ExpressionTraits getExpressionTraits (Expression e, MathSet mathDefinitions) throws EvaluateExpressionException {
		MathSet dummyVariableSet = new MathSet(mathDefinitions);
		
		ExpressionTraits et = new ExpressionTraits(e);
		
		StringBuilder latexBuilder = new StringBuilder();
		
		try {
			
			MathValue v = evalExpr ((ParseBranch) e.getTree().getRoot(), dummyVariableSet, et, latexBuilder);
			et.latexString = latexBuilder.toString();
			et.result = v;
			
			return et;
			
		} catch (EvaluateExpressionException e1) {
			throw e1;
		} catch (Exception e2) {
			throw new EvaluateExpressionException(e2.getMessage());
		}
	}
	
	
	
	
	private static MathValue evalExpr(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) throws EvaluateExpressionException {
		Iterator<ParseNode> terms = pb.getChildren().iterator();
		
		MathValue current = evalTerm((ParseBranch) terms.next(), mathDefinitions, traits, sb);
		ParseNode pn;
		StringBuilder nextTerm;
		while(terms.hasNext()) {
			pn = terms.next();
			nextTerm = new StringBuilder();
			if(pn.getProductionSymbol() == ExpressionParser.ADD) {
				sb.append(" + ");
				current = MathValue.add(current, evalTerm((ParseBranch) terms.next(), mathDefinitions, traits, nextTerm));
				sb.append(nextTerm.toString());
			} else if(pn.getProductionSymbol() == ExpressionParser.SUB_NEG) {
				sb.append(" - ");
				current = MathValue.sub(current, evalTerm((ParseBranch) terms.next(), mathDefinitions, traits, nextTerm));
				sb.append(nextTerm.toString());
			} else {
				sb.append(" \\oplus ");
				current = MathValue.xOR(current, evalTerm((ParseBranch) terms.next(), mathDefinitions, traits, nextTerm));
				sb.append(nextTerm.toString());
			}
		}
		return current;
	}
	
	private static MathValue evalTerm(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) throws EvaluateExpressionException {
		Iterator<ParseNode> pows = pb.getChildren().iterator();
		

		StringBuilder nextTerm = new StringBuilder();
		ArrayList<String> matrixProducts = new ArrayList<>();
		ArrayList<String> numerators = new ArrayList<>();
		ArrayList<String> denominators = new ArrayList<>();
		MathValue current = evalPow((ParseBranch) pows.next(), mathDefinitions, traits, nextTerm);
		if(current instanceof Matrix<?>)
			matrixProducts.add(nextTerm.toString());
		else
			numerators.add(nextTerm.toString());
		
		ParseNode pn;
		while(pows.hasNext()) {
			pn = pows.next();
			nextTerm = new StringBuilder();
			if(pn.getProductionSymbol() == ExpressionParser.MULT) {
				current = MathValue.mult(current, evalPow((ParseBranch) pows.next(), mathDefinitions, traits, nextTerm));
				if(current instanceof Matrix<?>)
					matrixProducts.add(nextTerm.toString());
				else
					numerators.add(nextTerm.toString());
			} else if(pn.getProductionSymbol() == ExpressionParser.DIV) {
				current = MathValue.div(current, evalPow((ParseBranch) pows.next(), mathDefinitions, traits, nextTerm));
				denominators.add(nextTerm.toString());
			} else {
				current = MathValue.tensor(current, evalPow((ParseBranch) pows.next(), mathDefinitions, traits, nextTerm));
				matrixProducts.add(" \\otimes ");
				matrixProducts.add(nextTerm.toString());
			}
		}
		
		
		String latex = "";
		if(!numerators.isEmpty()) {
			latex += numerators.get(0);
			for(int i = 1; i < numerators.size(); i++)
				latex += " \\cdot " + numerators.get(i);
			
			if(!denominators.isEmpty()) {
				latex = " { " + latex + " \\over " + denominators.get(0);
				for(int i = 1; i < denominators.size(); i++)
					sb.append(" \\cdot " + denominators.get(i));
				latex += " } ";
			}
		} else {
			if(!denominators.isEmpty()) {
				latex = " { 1 \\over " + denominators.get(0);
				for(int i = 1; i < denominators.size(); i++)
					sb.append(" \\cdot " + denominators.get(i));
				latex += " } ";
			}
		}
		
		sb.append(latex);
		
		for(String s : matrixProducts)
			sb.append(s);
		
		return current;
	}


	private static MathValue evalPow(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) throws EvaluateExpressionException {
		Iterator<ParseNode> pows = pb.getChildren().iterator();
		
		ParseNode pn = pows.next();
		StringBuilder nextPow = new StringBuilder();
		ArrayList<String> powLatex = new ArrayList<>();
		
		MathValue current;
		if(pn.getProductionSymbol() == ExpressionParser.SUB_NEG) {
			current =  MathValue.neg(evalPow((ParseBranch) pows.next(), mathDefinitions, traits, nextPow));
			powLatex.add(" - " + nextPow.toString());
		} else {
			current = evalValue((ParseBranch) pn, mathDefinitions, traits, nextPow);
			powLatex.add(nextPow.toString());
		}
			
		while(pows.hasNext()) {
			pn = pows.next();
			nextPow = new StringBuilder();
			if(pn.getProductionSymbol() == ExpressionParser.EXP) {
				pn = pows.next();
				if(pn.getProductionSymbol() == ExpressionParser.SUB_NEG) {
					current = MathValue.pow(current, MathValue.neg(evalPow((ParseBranch) pows.next(), mathDefinitions, traits, nextPow)));
					powLatex.add(" - " + nextPow.toString());
				} else {
					current = MathValue.pow(current, evalValue((ParseBranch) pn, mathDefinitions, traits, nextPow));
					powLatex.add(nextPow.toString());
				}
			}
		}
		
		String latex = powLatex.get(0);
		for(int i  = 1; i < powLatex.size(); i++)
			latex = " { " + latex + " } ^ { " + powLatex.get(i) + " } ";
		sb.append(latex);
		
		
		return current;
	}
	
	
	
	private static MathValue evalValue(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) throws EvaluateExpressionException {
		Iterator<ParseNode> parts = pb.getChildren().iterator();
		ParseNode pn = parts.next();
		ProductionSymbol ps = pn.getProductionSymbol();
		
		StringBuilder temp;
		
		if(ps == ExpressionParser.OBRA) {
			sb.append(" { \\begin{bmatrix} ");
			
			ParseBranch matrixParams = (ParseBranch) parts.next();
			int rows = (matrixParams.getChildren().size() + 1) / 2;
			
			Iterator<ParseNode> row = matrixParams.getChildren().iterator();
			ParseBranch columnParams = (ParseBranch) row.next();
			int columns = (columnParams.getChildren().size() + 1) / 2;
			
			Complex[] matrixComps = new Complex[columns * rows];
			
			try {
				temp = new StringBuilder();
				fillMatrixArray(matrixComps, 0, columnParams, mathDefinitions, traits, temp);
				sb.append(temp);
				
				int i = 0;
				while(row.hasNext()) {
					row.next(); // skip ;
					i+=columns;
					columnParams = (ParseBranch) row.next();
					if((columnParams.getChildren().size() + 1) / 2 != columns)
						throw new EvaluateExpressionException("Matrix cannot have different length columns");
					
					temp = new StringBuilder();
					fillMatrixArray(matrixComps, i, columnParams, mathDefinitions, traits, temp);
					sb.append(" \\\\ ").append(temp);
					
				}
			} catch (ClassCastException e) {
				throw new EvaluateExpressionException("Can not have a matrix inside another matrix");
			}
			
			sb.append(" \\end{bmatrix} } ");
			
			return new Matrix<Complex>(rows, columns, matrixComps);
			
		} else if (ps == ExpressionParser.OPAR) {
			temp = new StringBuilder();
			MathValue value = evalExpr((ParseBranch) parts.next(), mathDefinitions, traits, temp);
			sb.append(" ( ").append(temp).append(" ) "); 
			return value;
		} else if (ps == ExpressionParser.NUM) {
			String value = ((ParseLeaf)pn).getValue();
			
			sb.append(" " + value + " ");
			
			return new Complex(Double.parseDouble(value), 0);
		} else {
			String funcVarName = ((ParseLeaf) pn).getValue();
			
			if(pb.getChildren().size() == 1) {
				Variable v;
				
				if(mathDefinitions.getVariable(funcVarName) == null) {
					traits.undefinedVariables.add(funcVarName);
					v = new ConcreteVariable(funcVarName, Complex.ZERO());
					mathDefinitions.addVariable(v);
				} else {
					v = mathDefinitions.getVariable(funcVarName);
				}
				
				String s;
				if(v.getLatexFormat() == Variable.NONE)
					s = funcVarName;
				else
					s = v.getLatexFormat();
				
				
				sb.append(" \\mathit{" + s + "} ");
				
				return mathDefinitions.computeVariable(funcVarName);
			} else {
				parts.next(); // skip (
				ParseBranch params = (ParseBranch) parts.next();
				int numParams = (params.getChildren().size() + 1) / 2;
				
				Expression[] paramList = new Expression[numParams];
				String[] latexParams = new String[numParams];
				
				Function f = mathDefinitions.getFunction(funcVarName, numParams);
				
				if(f == null)
					throw new EvaluateExpressionException("Function \"" + funcVarName + "\" with " + numParams + " arguments is not defined.");

				
				fillParamArray(f, paramList, params, mathDefinitions, traits, latexParams);
				
				sb.append(functionToLatex(f, latexParams));
				
				
				return mathDefinitions.computeFunction(funcVarName, paramList);
			}
		}
	}
	
	private static String functionToLatex (Function f, String[] latexParams) {
		LatexFormat lf = f.getLatexFormat();
		String temp = "";
		
		if(lf != LatexFormat.NONE) {
			int paramIndex;
			
			for(Object latexPart : lf) {
				if(LatexFormat.isParam(latexPart)) {
					paramIndex = LatexFormat.getParamNumber(latexPart);
					temp += latexParams[paramIndex];
				} else {
					temp += latexPart;
				}
			}
			return temp;
		} else {
			temp += "\\mathit{ " + f.getName() + " } ( " + latexParams[0];
			
			for(int i = 1; i < latexParams.length; i++)
				temp += " , " + latexParams[i];
			
			temp += " ) ";
			
			return temp;
		}
	}
	
	private static void fillParamArray(Function function, Expression[] paramList, ParseBranch params, 
			MathSet mathDefinitions, ExpressionTraits traits, String[] latexParams) throws EvaluateExpressionException {
		
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		
		MathSet variableParamSet = new MathSet(mathDefinitions); 
		MathSet parameterSet;
		StringBuilder temp;
		
		paramList[0] = new Expression(new ParseTree(iterator.next()));
		
		if(function.isVariableParam(0))
			variableParamSet.addVariable(new ConcreteVariable(Expression.getVariableFrom(paramList[0]), Complex.ZERO()));
			
		parameterSet = new MathSet(variableParamSet);
		temp = new StringBuilder();
		evalExpr((ParseBranch) paramList[0].getTree().getRoot(), parameterSet, traits, temp);
		latexParams[0] = temp.toString();
		mathDefinitions.addToSet(parameterSet);
		
		
		int i = 0;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			paramList[++i] = new Expression(new ParseTree(iterator.next()));
			
			if(function.isVariableParam(i))
				variableParamSet.addVariable(new ConcreteVariable(Expression.getVariableFrom(paramList[i]), Complex.ZERO()));
			
			parameterSet = new MathSet(variableParamSet);
			temp = new StringBuilder();
			evalExpr((ParseBranch) paramList[i].getTree().getRoot(), parameterSet, traits, temp);
			latexParams[i] = temp.toString();
			mathDefinitions.addToSet(parameterSet);
		}
	}
	
	private static void fillMatrixArray(Complex[] paramList, int offset, ParseBranch params, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) throws ClassCastException, EvaluateExpressionException {
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		
		StringBuilder temp = new StringBuilder();
		
		paramList[offset] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions, traits, temp);
		sb.append(temp);
		
		int i = offset;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			temp = new StringBuilder();
			paramList[++i] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions, traits, temp);
			sb.append(" & ").append(temp);
		}
	}
	
	
	
	
	
	
	
	private static class ExpressionTraits {
		private HashSet<String> undefinedVariables = new HashSet<>();
		private MathValue result;
		private String latexString = "";
		private Expression expression;
		
		
		private ExpressionTraits(Expression expression) {
			this.expression = expression;
		}
	}
}
