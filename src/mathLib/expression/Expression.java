package mathLib.expression;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import framework2FX.MathDefintions;
import language.compiler.LexicalAnalyzer;
import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseLeaf;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol;
import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.Token;
import mathLib.Complex;
import mathLib.MathValue;
import mathLib.Matrix;
import mathLib.expression.Variable.ConcreteVariable;
import utils.customCollections.Pair;

public class Expression implements Serializable {
	private static final long serialVersionUID = 6406307424607474858L;
	
	
	/**
	 * returns the variable's name that this expression represents. 
	 * If this expression doesn't contains a single variable name,
	 * then an {@link IllegalArgumentException} is thrown
	 * @param expression the variable name
	 * @return the variable's name that this expression represents
	 */
	public static String getVariableFrom(Expression expression) {
		ParseNode pn;
		ParseBranch pb;
		pn = expression.tree.getRoot();
		while(!pn.isLeaf()) {
			pb = (ParseBranch) pn;
			if(pb.getChildren().size() == 1)
				pn = pb.getChildren().getFirst();
			else throw new IllegalArgumentException("Expression must only contain one variable");
		}
		if(pn.getProductionSymbol() == ExpressionParser.NAME) {
			return ((ParseLeaf) pn).getValue();
		} else throw new IllegalArgumentException("Expression must only contain one variable");
	}
	
	public static void main(String args[]) {
		Expression expr = new Expression("-[1, 0]");
		System.out.println(expr.treeString() + "\n");
		System.out.println(expr.compute(MathDefintions.GLOBAL_DEFINITIONS));
	}
	
	
	private ParseTree tree;
	
	private Expression (ParseTree tree) {
		this.tree = tree;
	}
	
	public Expression (String expression) {
		ExpressionParser ep = new ExpressionParser(expression);
		tree = ep.parse();
	}
	
	public String treeString () {
		return tree.toString();
	}
	
	public MathValue compute(MathSet mathDefinitions) {
		return evalExpr((ParseBranch)tree.getRoot(), mathDefinitions);
	}
	
	private MathValue evalExpr(ParseBranch pb, MathSet mathDefinitions) {
		Iterator<ParseNode> terms = pb.getChildren().iterator();
		
		MathValue current = evalTerm((ParseBranch)terms.next(), mathDefinitions);
		ParseNode pn;
		while(terms.hasNext()) {
			pn = terms.next();
			if(pn.getProductionSymbol() == ExpressionParser.ADD)
				current = MathValue.add(current, evalTerm((ParseBranch) terms.next(), mathDefinitions));
			else if(pn.getProductionSymbol() == ExpressionParser.SUB_NEG)
				current = MathValue.sub(current, evalTerm((ParseBranch) terms.next(), mathDefinitions));
			else 
				current = MathValue.xOR(current, evalTerm((ParseBranch) terms.next(), mathDefinitions));
		}
		return current;
	}
	
	private MathValue evalTerm(ParseBranch pb, MathSet mathDefinitions) {
		Iterator<ParseNode> pows = pb.getChildren().iterator();
		
		MathValue current = evalPow((ParseBranch) pows.next(), mathDefinitions);
		ParseNode pn;
		while(pows.hasNext()) {
			pn = pows.next();
			if(pn.getProductionSymbol() == ExpressionParser.MULT)
				current = MathValue.mult(current, evalPow((ParseBranch) pows.next(), mathDefinitions));
			else if(pn.getProductionSymbol() == ExpressionParser.DIV)
				current = MathValue.div(current, evalPow((ParseBranch) pows.next(), mathDefinitions));
			else
				current = MathValue.tensor(current, evalPow((ParseBranch) pows.next(), mathDefinitions));
		}
		return current;
	}


	private MathValue evalPow(ParseBranch pb, MathSet mathDefinitions) {
		Iterator<ParseNode> pows = pb.getChildren().iterator();
		
		ParseNode pn = pows.next();
		
		MathValue current;
		if(pn.getProductionSymbol() == ExpressionParser.SUB_NEG)
			current = MathValue.neg(evalPow((ParseBranch) pows.next(), mathDefinitions));
		else
			current = evalValue((ParseBranch) pn, mathDefinitions);
		
		while(pows.hasNext()) {
			pn = pows.next();
			if(pn.getProductionSymbol() == ExpressionParser.EXP) {
				pn = pows.next();
				if(pn.getProductionSymbol() == ExpressionParser.SUB_NEG)
					current = MathValue.pow(current, MathValue.neg(evalPow((ParseBranch) pows.next(), mathDefinitions)));
				else
					current = MathValue.pow(current, evalValue((ParseBranch) pn, mathDefinitions));
			}
		}
		return current;
	}
	
	private MathValue evalValue(ParseBranch pb, MathSet mathDefinitions) {
		Iterator<ParseNode> parts = pb.getChildren().iterator();
		ParseNode pn = parts.next();
		ProductionSymbol ps = pn.getProductionSymbol();
		
		if(ps == ExpressionParser.OBRA) {
			ParseBranch matrixParams = (ParseBranch) parts.next();
			int rows = (matrixParams.getChildren().size() + 1) / 2;
			
			Iterator<ParseNode> row = matrixParams.getChildren().iterator();
			ParseBranch columnParams = (ParseBranch) row.next();
			int columns = (columnParams.getChildren().size() + 1) / 2;
			
			Complex[] matrixComps = new Complex[columns * rows];
			
			try {
				fillMatrixArray(matrixComps, 0, columnParams, mathDefinitions);
			
				int i = 0;
				while(row.hasNext()) {
					row.next(); // skip ;
					i+=columns;
					columnParams = (ParseBranch) row.next();
					if((columnParams.getChildren().size() + 1) / 2 != columns)
						throw new EvaluateExpressionException("Matrix cannot have different length columns");
					fillMatrixArray(matrixComps, i, columnParams, mathDefinitions);
				}
			} catch (ClassCastException e) {
				throw new EvaluateExpressionException("Can not have a matrix inside another matrix");
			}
			
			return new Matrix<Complex>(rows, columns, matrixComps);
			
		} else if (ps == ExpressionParser.OPAR) {
			return evalExpr((ParseBranch) parts.next(), mathDefinitions);
		} else if (ps == ExpressionParser.NUM) {
			return new Complex(Double.parseDouble(((ParseLeaf)pn).getValue()), 0);
		} else {
			String funcVarName = ((ParseLeaf) pn).getValue();
			
			if(pb.getChildren().size() == 1) {
				return mathDefinitions.computeVariable(funcVarName);
			} else {
				parts.next(); // skip (
				ParseBranch params = (ParseBranch) parts.next();
				int numParams = (params.getChildren().size() + 1) / 2;
				Expression[] paramList = new Expression[numParams];
				
				fillParamArray(paramList, 0, params, mathDefinitions);
				
				return mathDefinitions.computeFunction(funcVarName, paramList);
			}
		}
	}
	
	private void fillParamArray(Expression[] paramList, int offset, ParseBranch params, MathSet mathDefinitions) {
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		
		paramList[offset] = new Expression(new ParseTree(iterator.next()));
		
		int i = offset;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			paramList[++i] = new Expression(new ParseTree(iterator.next()));
		}
	}
	
	private void fillMatrixArray(Complex[] paramList, int offset, ParseBranch params, MathSet mathDefinitions) throws ClassCastException {
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		paramList[offset] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions);
		
		int i = offset;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			paramList[++i] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions);
		}
	}


	@SuppressWarnings("serial")
	public static class EvaluateExpressionException extends RuntimeException {
		public EvaluateExpressionException(String message) {
			super(message);
		}
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public ExpressionTraits getExpressionTraits (MathSet mathDefinitions, String ... undefinedVariables) {
		MathSet dummyVariableSet = new MathSet(mathDefinitions);
		
		ExpressionTraits et = new ExpressionTraits();
		
		for(String var : undefinedVariables) {
			dummyVariableSet.addVariable(new ConcreteVariable(var, Complex.ZERO()));
			et.addUndefinedVariable(var);
		}
		
		StringBuilder latexBuilder = new StringBuilder();
		MathValue v = evalExpr ((ParseBranch)tree.getRoot(), dummyVariableSet, et, latexBuilder);
		et.setLatexString(latexBuilder.toString());
		
		if(v instanceof Matrix<?>) {
			Matrix<Complex> matrix = (Matrix<Complex>) v;
			et.setRows(matrix.getRows());
			et.setColumns(matrix.getColumns());
			et.setMatrix(true);
		} else {
			et.setMatrix(false);
		}
		return et;
	}
	
	
	
	
	private MathValue evalExpr(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) {
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
	
	private MathValue evalTerm(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) {
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
				latex = " { " + latex + " \\over " + numerators.get(0);
				for(int i = 1; i < numerators.size(); i++)
					sb.append(" \\cdot " + numerators.get(i));
				latex += " } ";
			}
		} else {
			if(!denominators.isEmpty()) {
				latex = " { 1 \\over " + numerators.get(0);
				for(int i = 1; i < numerators.size(); i++)
					sb.append(" \\cdot " + numerators.get(i));
				latex += " } ";
			}
		}
		
		sb.append(latex);
		
		for(String s : matrixProducts)
			sb.append(s);
		
		return current;
	}


	private MathValue evalPow(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) {
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
	
	private MathValue evalValue(ParseBranch pb, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) {
		Iterator<ParseNode> parts = pb.getChildren().iterator();
		ParseNode pn = parts.next();
		ProductionSymbol ps = pn.getProductionSymbol();
		// TODO: HERE
		if(ps == ExpressionParser.OBRA) {
			ParseBranch matrixParams = (ParseBranch) parts.next();
			int rows = (matrixParams.getChildren().size() + 1) / 2;
			
			Iterator<ParseNode> row = matrixParams.getChildren().iterator();
			ParseBranch columnParams = (ParseBranch) row.next();
			int columns = (columnParams.getChildren().size() + 1) / 2;
			
			Complex[] matrixComps = new Complex[columns * rows];
			
			try {
				fillMatrixArray(matrixComps, 0, columnParams, mathDefinitions);
			
				int i = 0;
				while(row.hasNext()) {
					row.next(); // skip ;
					i+=columns;
					columnParams = (ParseBranch) row.next();
					if((columnParams.getChildren().size() + 1) / 2 != columns)
						throw new EvaluateExpressionException("Matrix cannot have different length columns");
					fillMatrixArray(matrixComps, i, columnParams, mathDefinitions);
				}
			} catch (ClassCastException e) {
				throw new EvaluateExpressionException("Can not have a matrix inside another matrix");
			}
			
			
			
			return new Matrix<Complex>(rows, columns, matrixComps);
			
		} else if (ps == ExpressionParser.OPAR) {
			return evalExpr((ParseBranch) parts.next(), mathDefinitions);
		} else if (ps == ExpressionParser.NUM) {
			return new Complex(Double.parseDouble(((ParseLeaf)pn).getValue()), 0);
		} else {
			String funcVarName = ((ParseLeaf) pn).getValue();
			
			if(pb.getChildren().size() == 1) {
				return mathDefinitions.computeVariable(funcVarName);
			} else {
				parts.next(); // skip (
				ParseBranch params = (ParseBranch) parts.next();
				int numParams = (params.getChildren().size() + 1) / 2;
				Expression[] paramList = new Expression[numParams];
				
				fillParamArray(paramList, 0, params, mathDefinitions);
				
				return mathDefinitions.computeFunction(funcVarName, paramList);
			}
		}
	}
	
	
	private void fillMatrixArray(Complex[] paramList, int offset, ParseBranch params, MathSet mathDefinitions, ExpressionTraits traits, StringBuilder sb) throws ClassCastException {
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		paramList[offset] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions);
		
		int i = offset;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			paramList[++i] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions);
		}
	}
	
	
	
	
	
	
	
	
	// starting nonterminal : expr
	// expr -> expr + term | expr - term | term
	// term -> term pow | term * pow | term / pow | pow
	// pow -> pow ^ value |  value
	// value -> ( expr ) | NUM | NAME | NAME ( params )
	// params -> expr , FUNC_PARAM | expr
	
	
	// get rid of left recursion and added negation, matrix, tensor, and xor
	// starting nonterminal : expr
	// expr -> term expr1
	// expr1 -> + term expr1 | - term expr1 | (+) expr1 | eps
	// term -> pow term1
	// term1 -> * pow term1 | / pow term1 | (x) term1 | noNeg term1 | eps
	// pow -> noNeg | neg
	// noNeg -> value pow1
	// neg -> - pow
	// pow1 -> ^ value pow1 | ^ neg | eps
	// value -> ( expr ) | [ matrixParams ]  | NUM | NAME | NAME ( params )
	// matrixParams -> params ; matrixParams | params
	// params -> expr , params | expr
	
	public static class ExpressionParser {
		private static final Token NAME 	= new Token();  // used for variable and function names
		private static final Token NUM		= new Token();	// any positive real decimal or integer
		private static final Token OBRA 	= new Token();	// [
		private static final Token CBRA 	= new Token();	// ]
		private static final Token OPAR 	= new Token();	// (
		private static final Token CPAR 	= new Token();	// )
		private static final Token COM 		= new Token();	// ,
		private static final Token SCOM 	= new Token();	// ;
		private static final Token ADD 		= new Token();	// +
		private static final Token SUB_NEG 	= new Token();	// -
		private static final Token MULT 	= new Token();	// *
		private static final Token EXP 		= new Token();	// ^
		private static final Token DIV 		= new Token();	// /
		private static final Token SPACE 	= new Token();	// any white spce character
		private static final Token TENSOR	= new Token();	// tensor product (x)
		private static final Token XOR		= new Token();	// addtion modulo 2 or exclusive OR (+)
		private static final NonTerminal EXPR 			= new NonTerminal("expr");			
		private static final NonTerminal TERM 			= new NonTerminal("term"); 
		private static final NonTerminal POW 			= new NonTerminal("pow");
		private static final NonTerminal VALUE 			= new NonTerminal("value");
		private static final NonTerminal MATRIX_PARAMS 	= new NonTerminal("matrixParams");
		private static final NonTerminal PARAM 			= new NonTerminal("param");
		
		private static final LexicalAnalyzer EXPRESSION_LEXER = new LexicalAnalyzer(
					new Pair<String, Token>("[a-zA-Z_]\\w*",	NAME),
					new Pair<String, Token>("\\d+(\\.\\d*)?|\\d*\\.\\d+", 	NUM),
					new Pair<String, Token>("\\[", 				OBRA),
					new Pair<String, Token>("\\]", 				CBRA),
					new Pair<String, Token>("\\(", 				OPAR),
					new Pair<String, Token>("\\)", 				CPAR),
					new Pair<String, Token>(";", 				SCOM),
					new Pair<String, Token>(",", 				COM),
					new Pair<String, Token>("\\+", 				ADD),
					new Pair<String, Token>("-", 				SUB_NEG),
					new Pair<String, Token>("\\*", 				MULT),
					new Pair<String, Token>("^", 				EXP),
					new Pair<String, Token>("/", 				DIV),
					new Pair<String, Token>("\\(x\\)", 			TENSOR),
					new Pair<String, Token>("\\(\\+\\)", 			XOR),
					new Pair<String, Token>("\\s+", 			SPACE));
		
		private Iterator<Pair<Token, String>> iterator;
		
		private Token pushBack = null;
		private String lexeme = null;
		private Token lookAhead;
		private String lexemeAhead;
		
		public ExpressionParser(String string) {
			this.iterator = EXPRESSION_LEXER.getTokenStream(string).filter(ExpressionParser::filterWhiteSpace).iterator();
		}
		
		private static boolean filterWhiteSpace(Pair<Token, String> o) {
			if(o == null)
				return true;
			if(o.first() == SPACE)
				return false;
			return true;
		}
		
		public ExpressionParser(BufferedReader br) {
			this.iterator = EXPRESSION_LEXER.getTokenStream(br).iterator();
		}
		
		public ParseTree parse() {
			ParseTree pt = new ParseTree(expr());
			if(getNext())
				error();
			return pt;
		}
		
		private ParseBranch expr() {
			ParseBranch pb = new ParseBranch(EXPR);
			pb.addChild(term());
			expr1(pb);
			return pb;
		}
		
		private void expr1(ParseBranch pb) {
			if(!getNext())
				return;
			if(lookAhead == ADD || lookAhead == SUB_NEG || lookAhead == XOR) {
				addTokenToBranch(pb);
				pb.addChild(term());
				expr1(pb);
			} else {
				pushBack();
			}
		}

		private ParseBranch term() {
			ParseBranch pb = new ParseBranch(TERM);
			pb.addChild(pow());
			term1(pb);
			return pb;
		}
		
		private void term1(ParseBranch pb) {
			if (!getNext())
				return;
			if (lookAhead == MULT || lookAhead == DIV || lookAhead == TENSOR) {
				addTokenToBranch(pb);
				pb.addChild(pow());
				term1(pb);
			} else if(lookAhead == NAME || lookAhead == NUM || 
					  lookAhead == OPAR || lookAhead == OBRA) {
				pb.addChild(new ParseLeaf(MULT, "*"));
				pushBack();
				pb.addChild(pow());
				term1(pb);
			} else {
				pushBack();
			}
		}
		
		private ParseBranch pow() {
			ParseBranch pb = new ParseBranch(POW);
			if(mustRead() == SUB_NEG) {
				addTokenToBranch(pb);
				pb.addChild(pow());
			} else {
				pushBack();
				pb.addChild(value());
				pow1(pb);
			}
			return pb;
		}
		
		private void pow1(ParseBranch pb) {
			if(!getNext())
				return;
			if(lookAhead == EXP) {
				addTokenToBranch(pb);
				
				if(mustRead() == SUB_NEG) {
					addTokenToBranch(pb);
					pb.addChild(pow());
				} else {
					pushBack();
					pb.addChild(value());
					pow1(pb);
				}
			} else {
				pushBack();
			}
		}
		
		private ParseNode value() {
			ParseBranch pb = new ParseBranch(VALUE);
			
			if(!getNext())
				error();
			if(lookAhead == OPAR) {
				addTokenToBranch(pb);
				pb.addChild(expr());
				match(CPAR);		
				addTokenToBranch(pb);
			} else if (lookAhead == OBRA){
				addTokenToBranch(pb);
				pb.addChild(matrixParams());
				match(CBRA);
				addTokenToBranch(pb);
			}else if(lookAhead == NUM) {
				pb.addChild(getLeafFromToken());
			} else if(lookAhead == NAME) {
				addTokenToBranch(pb);
				if(getNext() && lookAhead == OPAR) {
					addTokenToBranch(pb);
					pb.addChild(params());
					match(CPAR);
					addTokenToBranch(pb);
				} else {
					pushBack();
				}
			} else {
				error();
			}
			
			
			return pb;
		}
		
		private ParseBranch matrixParams() {
			ParseBranch pb = new ParseBranch(MATRIX_PARAMS);
			pb.addChild(params());
			
			while(getNext() && lookAhead == SCOM) {
				addTokenToBranch(pb);
				pb.addChild(params());
			}
			
			pushBack();
			
			return pb;
		}
		
		private ParseBranch params() {
			ParseBranch pb = new ParseBranch(PARAM);
			pb.addChild(expr());
			
			while(getNext() && lookAhead == COM) {
				addTokenToBranch(pb);
				pb.addChild(expr());
			}
			
			pushBack();
			
			return pb;
		}
		
		
		private ParseLeaf getLeafFromToken() {
			return new ParseLeaf(lookAhead, lexemeAhead);
		}
		
		private void addTokenToBranch(ParseBranch pb) {
			pb.addChild(new ParseLeaf(lookAhead, lexemeAhead));
		}
		
		private void pushBack() {
			if(pushBack == null) {
				pushBack = lookAhead;
				lexeme = lexemeAhead;
			}
		}
		
		private void match(Token token) {
			if(!getNext() || lookAhead != token)
				throw new ExpressionParseException();
		}
		
		private void error() {
			throw new ExpressionParseException();
		}
		
		private Token mustRead() {
			if(!getNext())
				error();
			return lookAhead;
		}
		
		private boolean getNext() {
			if(pushBack != null) {
				lookAhead = pushBack;
				lexemeAhead = lexeme;
				pushBack = null;
				return true;
			}
			
			Pair<Token, String> next = iterator.next();
			
			if(next == null) {
				lookAhead = null;
				lexemeAhead = null;
				return false;
			} else {
				lookAhead = next.first();
				lexemeAhead = next.second();
				return true;
			}
		}
		
		
		@SuppressWarnings("serial")
		public class ExpressionParseException extends RuntimeException {
			
			public ExpressionParseException () {
				super ("Equation could not be parse due to syntax");
			}
		}
		
	}
	
}
