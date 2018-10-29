package mathLib;

import java.io.BufferedReader;
import java.util.Iterator;

import language.compiler.LexicalAnalyzer;
import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseLeaf;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.Token;
import language.finiteAutomata.DFA;
import language.finiteAutomata.NFA;
import language.finiteAutomata.RegularExpression;
import utils.customCollections.Pair;

public class Expression {
	private ParseTree tree;
	
	public Expression (String expression) {
		ExpressionParser ep = new ExpressionParser(expression);
		tree = ep.parse();
	}
	
	public String treeString () {
		return tree.toString();
	}
	
	// starting nonterminal : expr
	// expr -> expr + term | expr - term | term
	// term -> term pow | term * pow | term / pow | pow
	// pow -> pow ^ value |  value
	// value -> ( expr ) | NUM | NAME | NAME ( params )
	// params -> expr , FUNC_PARAM | expr
	
	
	// get rid of left recursion and added negation
	// starting nonterminal : expr
	// expr -> term expr1
	// expr1 -> + term expr1 | - term expr1 | eps
	// term -> pow term1
	// term1 -> * pow term1 | / pow term1 | noNeg term1 | eps
	// pow -> noNeg | neg
	// noNeg -> value pow1
	// neg -> - pow
	// pow1 -> ^ value pow1 | ^ neg | eps
	// value -> ( expr ) | NUM | NAME | func
	// func ->  NAME ( params )
	// params -> expr , params | expr
	
	public static class ExpressionParser {
		private static final Token NAME 	= new Token();
		private static final Token NUM		= new Token();
		private static final Token OPAR 	= new Token();
		private static final Token CPAR 	= new Token();
		private static final Token COM 		= new Token();
		private static final Token ADD 		= new Token();
		private static final Token SUB_NEG 	= new Token();
		private static final Token MULT 	= new Token();
		private static final Token EXP 		= new Token();
		private static final Token DIV 		= new Token();
		private static final Token SPACE 	= new Token();
		private static final NonTerminal EXPR = new NonTerminal("expr");
		private static final NonTerminal TERM = new NonTerminal("term");
		private static final NonTerminal POW = new NonTerminal("pow");
		private static final NonTerminal VALUE = new NonTerminal("value");
		private static final NonTerminal PARAM = new NonTerminal("param");
		
		private static final LexicalAnalyzer EXPRESSION_LEXER = new LexicalAnalyzer(
					new Pair<String, Token>("[a-zA-Z_]\\w*",NAME),
					new Pair<String, Token>("\\d+(\\.\\d+)?", NUM),
					new Pair<String, Token>("\\(", 			OPAR),
					new Pair<String, Token>("\\)", 			CPAR),
					new Pair<String, Token>(",", 			COM),
					new Pair<String, Token>("\\+", 			ADD),
					new Pair<String, Token>("-", 			SUB_NEG),
					new Pair<String, Token>("\\*", 			MULT),
					new Pair<String, Token>("^", 			EXP),
					new Pair<String, Token>("/", 			DIV),
					new Pair<String, Token>("\\s+", 		SPACE));
		
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
			if(lookAhead == ADD) {
				addTokenToBranch(pb);
				pb.addChild(term());
				expr1(pb);
			} else if (lookAhead == SUB_NEG) {
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
			if(!getNext())
				return;
			if(lookAhead == MULT) {
				addTokenToBranch(pb);
				pb.addChild(pow());
				term1(pb);
			} else if (lookAhead == DIV) {
				addTokenToBranch(pb);
				pb.addChild(pow());
				term1(pb);
			} else if(lookAhead == NAME || lookAhead == NUM) {
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
				pb.addChild(value());
				pow1(pb);
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
			} else if(lookAhead == NUM) {
				return getLeafFromToken();
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
