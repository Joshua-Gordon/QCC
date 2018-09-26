package mathLib.expression;

import mathLib.expression.ConstantNode.DoubleNode;
import mathLib.expression.ConstantNode.IntegerNode;
import utils.SimpleLinkedList;
import utils.SimpleLinkedList.LinkedIterator;

public class NodeParser {
	
	
	static Node parseNode(String expression) {
		Parser p  = new Parser(expression);
		ParserStatus ps = new ParserStatus(p);
		return parseNode(ps);
	}
	
	@SuppressWarnings("serial")
	public static class ExpressionParseException extends RuntimeException {
		private final int indexOfError;
		
		public ExpressionParseException(Parser p) {
			super(p.getIndexedExpression() + " <- Error");
			this.indexOfError = p.getLastParsedIndex();
		}
		
		public int getIndexOfError() {
			return indexOfError;
		}
	}
	
	private static Node parseNode(ParserStatus status) {
		Parser parser = status.getParser();
		char c;
		
		byte op = 0;
		
		while ((c = parser.getNext()) != Parser.END) {
			switch (c) {
			case '^':
				op = 2;
			case '*':
				op++;
			case '+':
				status.addPrevNodeToCurOperNode(op);
				op = 0;
				c = skipWhiteSpace(parser);
				break;
				
			case '-':
				if(status.previousNode == null) {
					status.addNegate();
					c = skipWhiteSpace(parser);
					break;
				}
				op = -1;
			case '/':
				op++;
				status.addPrevNodeToCurOperNode(op);
				status.setInversed();
				op = 0;
				c = skipWhiteSpace(parser);
				break;
			case '(':
				status.setPreviousNode(parseNode(new ParserStatus(parser, true, false)));
				c = skipWhiteSpace(parser);
				break;
			case ')':
				if(status.isRecursive)
					return status.getRoot();
				else throw new ExpressionParseException(parser);
			case ',':
				if(status.isFunctionParam)
					return status.getRoot();
				else throw new ExpressionParseException(parser);
			default:
				if(Character.isAlphabetic(c))
					status.setPreviousNode(parseName(parser));
				else if(Character.isDigit(c) || c == '.')
					status.setPreviousNode(parseNum(parser));
				else throw new ExpressionParseException(parser);
				break;
			}
		}
		if(status.isRecursive) throw new ExpressionParseException(parser);
		return status.getRoot();
	}
	
	
	
	
	private static char skipWhiteSpace(Parser p) {
		char c;
		while((c = p.next()) != Parser.END)
			if(!Character.isWhitespace(c))
				break;
		return c;
	}
	
	
	
	
	private static Node parseName(Parser p) {
		String name = "" + p.getNext();
		
		char c;
		while ((c = p.next()) != Parser.END)
			if(Character.isAlphabetic(c) || Character.isDigit(c))
				name += c;
			else break;
		
		if(Character.isWhitespace(c))
			c = skipWhiteSpace(p);
		
		if(c != '(')
			return new VariableNode(name);
		
		return parseFunction(name, p);
	}
	
	
	
	
	private static FunctionNode parseFunction (String name, Parser p) {
		FunctionNode function = new FunctionNode(name);
		
		boolean hasAllParams = false;
		while (!hasAllParams) {
			function.addParam(parseNode(new ParserStatus(p, true, true)));
			if (p.getNext() == Parser.END) 
				throw new ExpressionParseException(p);
			hasAllParams = p.getNext() == ')';
		}
		
		skipWhiteSpace(p);
		
		return function;
	}
	
	
	
	
	
	private static Node parseNum (Parser p) {
		String value = "" + p.getNext();
		
		boolean foundDecimal = value.equals(".");
		
		char c;
		while ((c = p.next()) != Parser.END)
			if (Character.isDigit(c))
				value += c;
			else if (c == '.')
				if (foundDecimal)
					throw new ExpressionParseException(p);
				else
					value += '.';
			else break;
		
		if (value.equals("."))
			throw new ExpressionParseException(p);
		
		if (Character.isWhitespace(c))
			c = skipWhiteSpace(p);
		
		return foundDecimal ? new DoubleNode(value) : new IntegerNode(value);
	}
	
	
	
	
	
	
	

	
	private static class Parser {
		private String expression;
		private int index;
		public static char END = '\u0000';
		private char c;
		
		private Parser(String expression) {
			this.expression = expression;
			this.index = 0;
		}
		
		private char next() {
			if(index == expression.length()) {
				c = END;
				return END;
			}
			c = expression.charAt(index++);
			return c;
		}
		
		private String getIndexedExpression() {
			return expression.substring(0, index);
		}
		
		private char getNext() {
			return c;
		}
		
		private int getLastParsedIndex() {
			return index;
		}
	}
	
	
	
	
	
	
	private static class OperatorStatus {
		private OperatorNode op;
		private boolean isInversed;
		
		private OperatorStatus(OperatorNode op) {
			this.op = op;
			this.isInversed = false;
		}
		
		private OperatorNode getOperatorNode() {
			return op;
		}
	}
	
	
	
	
	
	private static class ParserStatus {
		private SimpleLinkedList<OperatorStatus> operatorHierarchy;
		private boolean isRecursive;
		private boolean isFunctionParam;
		private Node previousNode;
		private Parser parser;
		
		private ParserStatus(Parser parser) {
			this(parser, false, false);
		}	
		
		
		private ParserStatus (Parser parser, boolean isRecursive, boolean isFunctionalParam) {
			this.operatorHierarchy = new SimpleLinkedList<>();
			this.parser = parser;
			this.isRecursive = isRecursive;
			this.isFunctionParam = isFunctionalParam;
			getFirstNode();
		}
		
		
		
		
		private void getFirstNode() {
			char c = skipWhiteSpace(parser);
			
			if(Character.isAlphabetic(c))
				previousNode = parseName(parser);
			else if(Character.isDigit(c) || c == '.')
				previousNode = parseNum(parser);
			else if(c == '(') { 
				previousNode = parseNode(new ParserStatus(parser, true, false));
				skipWhiteSpace(parser);
			} else if(c == '-') {
				addNegate();
				getFirstNode();
			} else throw new ExpressionParseException(parser);	
		}
		
		
		
		
		private void addNegate() {
			operatorHierarchy.add(new OperatorStatus(new NegateNode()));
		}
		
		private void setInversed() {
			operatorHierarchy.get().isInversed = true;
		}
		
		private void addPrevNodeToCurOperNode(byte opType) {
			if(previousNode == null)
				throw new ExpressionParseException(parser);
			
			OperatorNode opNode = null;
			byte scenario = -1;
			for(OperatorStatus curOpStat : operatorHierarchy) {
				opNode = curOpStat.getOperatorNode();
				
				scenario = opNode.comparePrecendenceTo(opType);
				if(scenario != -1)
					break;
				if(curOpStat.isInversed)
					opNode.addInversed(previousNode);
				else
					opNode.add(previousNode);
				
				previousNode = opNode;
				operatorHierarchy.pop();
			}
			
			if(scenario != 0) {
				opNode = OperatorNode.getOperator(opType);
				operatorHierarchy.add(new OperatorStatus(opNode));				
			}
			
			OperatorStatus finalOpStat = operatorHierarchy.get();
			
			if(finalOpStat.isInversed) {
				opNode.addInversed(previousNode);
				finalOpStat.isInversed = false;
			} else
				opNode.add(previousNode);
			previousNode = null;
		}
		
		private void setPreviousNode(Node node) {
			if(previousNode == null)
				previousNode = node;
			else {
				addPrevNodeToCurOperNode(OperatorNode.MULT);
				previousNode = node;
			}
		}
		
		private Parser getParser() {
			return parser;
		}
		
		private Node getRoot() {
			if(previousNode == null)
				throw new ExpressionParseException(parser);
			
			OperatorNode opNode = null;
			for(OperatorStatus curOpStat : operatorHierarchy) {
				opNode = curOpStat.getOperatorNode();
				if(curOpStat.isInversed)
					opNode.addInversed(previousNode);
				else
					opNode.add(previousNode);
				previousNode = opNode;
			}
			
			return previousNode;
		}
		
	}
}
