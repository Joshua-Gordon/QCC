package mathLib.expression;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.Iterator;

import language.compiler.LexicalAnalyzer;
import language.compiler.LexicalAnalyzer.LexemeNotRecognizedException;
import language.compiler.LexicalAnalyzer.LexicalAnaylizerIOException;
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
import mathLib.expression.Expression.ExpressionParser.EquationParseException;
import utils.customCollections.Pair;

public class Expression implements Serializable {
	private static final long serialVersionUID = 6406307424607474858L;
	
	private ParseTree tree;
	
	
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
	
	
	
	
	
	
	
	public Expression (String expression) throws EquationParseException {
		ExpressionParser ep = new ExpressionParser(expression);
		tree = ep.parse();
	}
	
	
	public Expression (ParseTree tree) {
		this.tree = tree;
	}
	
	
	
	
	public String treeString () {
		return tree.toString();
	}
	
	public ParseTree getTree() {
		return tree;
	}
	
	public MathValue compute(MathSet mathDefinitions) throws EvaluateExpressionException {
		try {
			return evalExpr((ParseBranch)tree.getRoot(), mathDefinitions);
		} catch (EvaluateExpressionException e) {
			throw e;
		} catch (Exception e) {
			throw new EvaluateExpressionException(e.getMessage());
		} 
	}
	
	
	
	
	
	
	
	private MathValue evalExpr(ParseBranch pb, MathSet mathDefinitions) throws EvaluateExpressionException {
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
	
	private MathValue evalTerm(ParseBranch pb, MathSet mathDefinitions) throws EvaluateExpressionException {
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


	private MathValue evalPow(ParseBranch pb, MathSet mathDefinitions) throws EvaluateExpressionException {
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
	
	private MathValue evalValue(ParseBranch pb, MathSet mathDefinitions) throws EvaluateExpressionException {
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
				
				fillParamArray(paramList, params, mathDefinitions);
				
				return mathDefinitions.computeFunction(funcVarName, paramList);
			}
		}
	}
	
	private void fillParamArray(Expression[] paramList, ParseBranch params, MathSet mathDefinitions) {
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		
		paramList[0] = new Expression(new ParseTree(iterator.next()));
		
		int i = 0;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			paramList[++i] = new Expression(new ParseTree(iterator.next()));
		}
	}
	
	private void fillMatrixArray(Complex[] paramList, int offset, ParseBranch params, MathSet mathDefinitions) throws ClassCastException, EvaluateExpressionException {
		Iterator<ParseNode> iterator = params.getChildren().iterator();
		paramList[offset] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions);
		
		int i = offset;
		while(iterator.hasNext()) {
			iterator.next(); // skip ,
			paramList[++i] = (Complex) evalExpr((ParseBranch) iterator.next(), mathDefinitions);
		}
	}
	
	@SuppressWarnings("serial")
	public static class EvaluateExpressionException extends Exception {
		public EvaluateExpressionException (String message) {
			super(message);
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
		public static final Token NAME 		= new Token();  // used for variable and function names
		public static final Token NUM		= new Token();	// any positive real decimal or integer
		public static final Token OBRA 		= new Token();	// [
		public static final Token CBRA 		= new Token();	// ]
		public static final Token OPAR 		= new Token();	// (
		public static final Token CPAR 		= new Token();	// )
		public static final Token COM 		= new Token();	// ,
		public static final Token SCOM 		= new Token();	// ;
		public static final Token ADD 		= new Token();	// +
		public static final Token SUB_NEG 	= new Token();	// -
		public static final Token MULT 		= new Token();	// *
		public static final Token EXP 		= new Token();	// ^
		public static final Token DIV 		= new Token();	// /
		public static final Token SPACE 	= new Token();	// any white spce character
		public static final Token TENSOR	= new Token();	// tensor product (x)
		public static final Token XOR		= new Token();	// addtion modulo 2 or exclusive OR (+)
		public static final NonTerminal EXPR 			= new NonTerminal("expr");			
		public static final NonTerminal TERM 			= new NonTerminal("term"); 
		public static final NonTerminal POW 			= new NonTerminal("pow");
		public static final NonTerminal VALUE 			= new NonTerminal("value");
		public static final NonTerminal MATRIX_PARAMS 	= new NonTerminal("matrixParams");
		public static final NonTerminal PARAM 			= new NonTerminal("param");
		
		private static final LexicalAnalyzer EXPRESSION_LEXER = new LexicalAnalyzer(
					new Pair<String, Token>("\\\\?[a-zA-Z_]\\w*",	NAME),
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
			this.iterator = EXPRESSION_LEXER.getTokenStream(string).filter((o) -> o.first() != SPACE).iterator();
		}
				
		public ExpressionParser(BufferedReader br) {
			this.iterator = EXPRESSION_LEXER.getTokenStream(br).filter((o) -> o.first() != SPACE).iterator();
		}
		
		public ParseTree parse() throws EquationParseException {
			try {
				ParseTree pt = new ParseTree(expr());
				if(getNext())
					error();
				return pt;
			} catch (LexemeNotRecognizedException | LexicalAnaylizerIOException | NoSuchParseException e) {
				throw new EquationParseException(e.getMessage());
			}
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
				throw new NoSuchParseException();
		}
		
		private void error() {
			throw new NoSuchParseException();
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
			
			if(iterator.hasNext()) {
				Pair<Token, String> next = iterator.next();
				lookAhead = next.first();
				lexemeAhead = next.second();
				return true;
			} else {
				lookAhead = null;
				lexemeAhead = null;
				return false;
			}
		}
		
		
		@SuppressWarnings("serial")
		public class NoSuchParseException extends RuntimeException {
			
			private NoSuchParseException () {
				super("Equation could not be parse due to syntax");
			}
		}
		
		@SuppressWarnings("serial")
		public class EquationParseException extends Exception {
			private EquationParseException(String message) {
				super(message);
			}
		}
	}
}
