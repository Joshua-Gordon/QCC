package mathLib;

import java.util.ArrayList;

public class Expression {
	
	/**
	 * is Leaf
	 */
	public static final byte L =       0b0;
	/**
	 * This is a tree type
	 */
	public static final byte T =       0b1;
	/**
	 * These are operators types
	 */
	public static final byte A =      0b10;
	public static final byte N =     0b110;
	public static final byte M =    0b1000;
	public static final byte E =   0b10000;
	public static final byte F =  0b100010;
	/**
	 * reverse operator
	 */
	public static final byte R = 0b1000000;
	
	
	private static final Node A_N = new Node(A);
	private static final Node S_N = new Node((byte) (N));
	private static final Node M_N = new Node(M);
	private static final Node D_N = new Node((byte) (M + R));
	private static final Node E_N = new Node(E);
	
	
	
	private Tree root;
	
	public Expression(String expression) {
		root = new Tree();
		root.parse(expression, 0, expression.length());
	}
	
	
	public static Node getNode(final String expression, ParseIndex pi) {
		String name = "";
		char c;
		
		while(pi.i < pi.end) {
			c = expression.charAt(pi.i);
			if(Character.isAlphabetic(c) || c == '_')
				name += c;
			else
				break;
			pi.inc();
		}
		
		boolean isFunction = false;
		while(pi.i < pi.end) {
			c = expression.charAt(pi.i);
			if(!Character.isWhitespace(c)) {
				isFunction = c == '(';
				break;
			}
			pi.inc();
		}
		if(isFunction)
			 return Function.getFunction(name, expression, pi);
		else 
			return new Leaf(name, true);
	}
	
	
	
	public static class ParseIndex {
		public int i;
		public final int end;
		
		public ParseIndex(int start, int end) {
			this.i = start;
			this.end = end;
		}
		
		public void inc() { i++; }
	}
	
	public static class Node {
		protected byte type;
		
		public Node (byte type) {
			this.type = type;
		}
		
		public boolean isTree() {
			return ((type) & 1) != 0;
		}
		
		public boolean isLeaf() {
			return type == 0;
		}
		
		public boolean isOperator() {
			return !isTree() && !isLeaf();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	public static class Tree extends Node {
		ArrayList<Node> children;
		
		public Tree() {
			super(T);
		}
		
		public void parse(final String expression, int start, int end) {
			ArrayList<Node> adders = null, multipliers = null, exponents = null;
			
			Node negate = null;
			Node previousNode = null, currentNode;
			ParseIndex pi = new ParseIndex(start, end);
			
			while(pi.i < pi.end)
				if((previousNode = getCurrentNodeAtLoc(expression, pi)) != null)
					break;
			
			if(previousNode == null || previousNode.isOperator())
				throw new ExpressionParseException(expression, pi);
						
			while(pi.i < pi.end) {
				currentNode = getCurrentNodeAtLoc(expression, pi);
				if((currentNode = getCurrentNodeAtLoc(expression, pi)) != null) {
//					if() {
//						I was last here !
//					}
				}
				previousNode = currentNode;
			}
		}
		
		
		public static Node getCurrentNodeAtLoc(String expression, ParseIndex pi) {
			char c = expression.charAt(pi.i);
			
			Node next;
			if(Character.isWhitespace(c)) next = null;
			else if(c == '(') next = parseParenthesis(expression, pi);
			else if(Character.isAlphabetic(c) || c == '_') next = getNode(expression, pi);
			else if(Character.isDigit(c) || c == '.') next = Leaf.getNumber(expression, pi);
			else if(c == '+') next = A_N;
			else if(c == '-') next = S_N;
			else if(c == '*') next = M_N;
			else if(c == '/') next = D_N;
			else if(c == '^') next = E_N;
			else throw new ExpressionParseException(expression, pi);
			
			pi.inc();
			return next;
		}
		
		public static Tree parseParenthesis(String expression, ParseIndex pi) {
			pi.inc();
			char c;
			int recursionIn = 1;
			int startParamIndex = pi.i;
			
			while(pi.i < pi.end) {
				c = expression.charAt(pi.i);
				if(c == '(')
					recursionIn++;
				else if(c == ')')
					if(--recursionIn == 0)
						break;
				pi.inc();
			}
			if(recursionIn == 0) {
				Tree tree = new Tree();
				// TODO: parse this on another thread
				tree.parse(expression, startParamIndex, pi.i);
				
				return tree;
			} else
				throw new ExpressionParseException(expression, pi);
		}
		
		/**
		 * Can use this function once
		 */
		public void setOperator(byte operator) {
			this.type += operator;
		}
	}
	
	
	
	
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class ExpressionParseException extends RuntimeException {
		
		public ExpressionParseException(String expression, ParseIndex pi) {
			super(String.format("Could not parse equation:\n%s",
					expression.substring(0, pi.i)) + " <- Error");
		}
		
	}
	
	
	
	
	
	
	
	
	
	public static class Function extends Node {
		private String name;
		private ArrayList<Node> params;
		
		public Function(String name, ArrayList<Node> params) {
			super((byte) (F + T));
			this.params = params;
		}
		
		public static Function getFunction(String name, final String expression, ParseIndex pi) {
			ArrayList<Node> params = new ArrayList<Node>();
			
			pi.inc();
			char c;
			int recursionIn = 1;
			int startParamIndex = pi.i;
			
			while(pi.i < pi.end) {
				c = expression.charAt(pi.i);
				if(c == '(')
					recursionIn++;
				else if(c == ')')
					if(--recursionIn == 0)
						break;
				else if(c == ',' && recursionIn == 1) {
					Tree tree = new Tree();
					params.add(tree);
					
					// TODO: parse this on another thread
					tree.parse(expression, startParamIndex, pi.i);
					
					startParamIndex = pi.i + 1;
				}
				pi.inc();
			}
			
			if(recursionIn == 0) {
				Tree tree = new Tree();
				params.add(tree);
				
				// TODO: parse this on another thread
				tree.parse(expression, startParamIndex, pi.i);
			}else
				throw new ExpressionParseException(expression, pi);
			
			return new Function(name, params);
		}
		
	}
	
	public static class PureOperator extends Node {
		public PureOperator(byte type) {
			super(type);
		}
	}
	
	public static class Leaf extends Node {
		String value;
		boolean isVariable;
		
		public Leaf(String value, boolean isVariable) {
			super(L);
			this.value = value;
			this.isVariable = isVariable;
		}
		
		public static Leaf getNumber(String expression, ParseIndex pi) {
			boolean hitDecimal = false;
			
			String number = "";
			
			char c;
			while(pi.i < pi.end) {
				c = expression.charAt(pi.i);
				if(c == '.')
					if(hitDecimal)
						throw new ExpressionParseException(expression, pi);
					else {
						hitDecimal = true;
						number += c;
					}
				else if (Character.isDigit(c)) number += c;
				else break;
				pi.inc();
			}
			
			if(number.endsWith("."))
				number.substring(0, number.length() - 1);
			if(number.equals("")) throw new ExpressionParseException(expression, pi);
			
			return new Leaf(number, false);
		}
	}
	
	
}













