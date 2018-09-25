package mathLib.expression;

import java.util.Iterator;

import mathLib.expression.ConstantNode.DoubleNode;
import mathLib.expression.ConstantNode.IntegerNode;
import mathLib.expression.Node.NodeType;
import mathLib.operators.Operators;
import utils.SimpleLinkedList;
import utils.SimpleLinkedList.LinkedIterator;

public class Expression extends Operators<Expression>{
	
	private Node root;
	
	
	public Expression () {
		root = new IntegerNode(0);
	}
	
	private Expression (Node root) {
		this.root = root;
	}
	
	/**
	 * Parsers an expression
	 * @param expression
	 */
	public Expression (String expression) {
		root = parseNode(new ParserStatus(new Parser(expression), false, false));
	}
	
	@Override
	public Expression add(Expression num) {
		AddNode addNode = new AddNode();
		addNode.add(root.duplicate());
		addNode.add(num.root.duplicate());
		return new Expression(addNode);
	}

	@Override
	public Expression sub(Expression num) {
		AddNode addNode = new AddNode();
		addNode.add(root.duplicate());
		addNode.addInversed(num.root.duplicate());
		return new Expression(addNode);
	}

	@Override
	public Expression mult(Expression num) {
		MultNode multNode = new MultNode();
		multNode.add(root.duplicate());
		multNode.add(num.root.duplicate());
		return new Expression(multNode);
	}

	@Override
	public Expression div(Expression num) {
		MultNode multNode = new MultNode();
		multNode.add(root.duplicate());
		multNode.addInversed(num.root.duplicate());
		return new Expression(multNode);
	}

	@Override
	public Expression exp(Expression num) {
		ExponentNode exponentNode = new ExponentNode();
		exponentNode.add(root.duplicate());
		exponentNode.add(num.root.duplicate());
		return new Expression(exponentNode);
	}

	@Override
	public Expression sqrt() {
		FunctionNode sqrtNode = new FunctionNode("sqrt");
		sqrtNode.addParam(root.duplicate());
		return new Expression(sqrtNode);
	}

	@Override
	public Expression get1() {
		return new Expression(new IntegerNode(1));
	}

	@Override
	public Expression getn1() {
		return new Expression(new IntegerNode(-1));
	}

	@Override
	public Expression get0() {
		return new Expression(new IntegerNode(0));
	}

	@Override
	public Expression[] mkZeroArray(int size) {
		Expression[] array = new Expression[size];
		for(int i = 0; i < size; i++)
			array[i] = get0();
		return array;
	}

	@Override
	public Operators<Expression> dup() {
		return new Expression(root.duplicate());
	}
	
	@Override
	public Operators<Expression> op(Expression value) {
		this.root = value.root.duplicate();
		return this;
	}
	
	
	
	
	
	// parse methods
	
	private static Node parseNode(ParserStatus status) {
		Parser p = status.getParser();
		char c;
		
		byte op = 0;
		
		while ((c = p.get()) != Parser.END) {
			switch (c) {
			case '^':
				op = 2;
			case '*':
				op++;
			case '+':
				status.addNodeToOperator(op);
				op = 0;
				c = skipWhiteSpace(p);
				break;
				
			case '-':
				if(status.previousNode == null) {
					status.addNegate();
					c = skipWhiteSpace(p);
					break;
				}
				op = -1;
			case '/':
				op++;
				status.addNodeToOperator(op);
				status.setInversed();
				op = 0;
				c = skipWhiteSpace(p);
				break;
			case '(':
				status.setPreviousNode(parseNode(new ParserStatus(p, true, false)));
				c = skipWhiteSpace(p);
				break;
			case ')':
				if(status.isRecursive)
					return status.getRoot();
				else throw new ExpressionParseException(p);
			case ',':
				if(status.isFunctionParam)
					return status.getRoot();
				else throw new ExpressionParseException(p);
			default:
				if(Character.isAlphabetic(c))
					status.setPreviousNode(parseName(p));
				else if(Character.isDigit(c) || c == '.')
					status.setPreviousNode(parseNum(p));
				else throw new ExpressionParseException(p);
				break;
			}
		}
		if(status.isRecursive) throw new ExpressionParseException(p);
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
		String name = "" + p.get();
		
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
			if (p.get() == Parser.END) 
				throw new ExpressionParseException(p);
			hasAllParams = p.get() == ')';
		}
		
		skipWhiteSpace(p);
		
		return function;
	}
	
	
	
	private static Node parseNum (Parser p) {
		String value = "" + p.get();
		
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
	
	
	
	
	
	
	

	
	public static class Parser {
		private String expression;
		private int index;
		public static char END = '\u0000';
		private char c;
		
		public Parser(String expression) {
			this.expression = expression;
			this.index = 0;
		}
		
		public char next() {
			if(index == expression.length()) {
				c = END;
				return END;
			}
			c = expression.charAt(index++);
			return c;
		}
		
		public String getIndexedExpression() {
			return expression.substring(0, index);
		}
		
		public char get() {
			return c;
		}
		
		public int getLastParsedIndex() {
			return index;
		}
		
		public boolean isAtStart() {
			return index == 0;
		}
	}
	
	
	
	
	private static class OperatorStatus {
		private OperatorNode op;
		private boolean isInversed;
		
		public OperatorStatus(OperatorNode op) {
			this.op = op;
			this.isInversed = false;
		}
		
		public OperatorNode getOperatorNode() {
			return op;
		}
		
		@Override
		public String toString() {
			switch(op.getOpType()) {
			case OperatorNode.ADD:
				return "ADD";
			case OperatorNode.MULT:
				return "MULT";
			case OperatorNode.EXPO:
				return "EXPO";
			case OperatorNode.NEG:
			default:
				return "NEG";
			} 
		}
	}
	
	
	private static class ParserStatus {
		private SimpleLinkedList<OperatorStatus> operatorHierarchy;
		private boolean isRecursive;
		private boolean isFunctionParam;
		private Node previousNode;
		private Parser parser;
		
		public ParserStatus (Parser parser, boolean isRecursive, boolean isFunctionalParam) {
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
		
		public void addNegate() {
			operatorHierarchy.add(new OperatorStatus(new NegateNode()));
		}
		
		public void setInversed() {
			operatorHierarchy.get().isInversed = true;
		}
		
		public void addNodeToOperator(byte opType) {
			if(previousNode == null)
				throw new ExpressionParseException(parser);
			
			LinkedIterator<OperatorStatus> opStats = operatorHierarchy.iterator();
			
			OperatorStatus curOpStat = null;
			OperatorNode opNode = null;
			byte scenario = -1;
			while(opStats.hasNext()) {
				curOpStat = opStats.next();
				opNode = curOpStat.getOperatorNode();
				
				scenario = opNode.compareTo(opType);
				if(scenario != -1)
					break;
				if(curOpStat.isInversed) {
					opNode.addInversed(previousNode);
				} else
					opNode.add(previousNode);
				previousNode = opNode;
				operatorHierarchy = opStats.getCurrentLinkedList();
			}
			
			if(scenario != 0) {
				opNode = OperatorNode.getOperator(opType);
				curOpStat = new OperatorStatus(opNode);
				operatorHierarchy.add(curOpStat);				
			}
			
			if(curOpStat.isInversed) {
				opNode.addInversed(previousNode);
				curOpStat.isInversed = false;
			} else
				opNode.add(previousNode);
			previousNode = null;
		}
		
		public void setPreviousNode(Node node) {
			if(previousNode == null) {
				previousNode = node;
			} else {
				addNodeToOperator(OperatorNode.MULT);
				previousNode = node;
			}
		}
		
		public Parser getParser() {
			return parser;
		}
		
		public Node getRoot() {
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
	
	
	@SuppressWarnings("serial")
	public static class ExpressionParseException extends RuntimeException {
		public ExpressionParseException(Parser p) {
			super(p.getIndexedExpression() + " <- Error");
		}
	}
	
	
	
	
	
	
	
	
	// string methods
	
	
	@Override
	public String toString() {
		return toString(root);
	}
	
	public void printTree() {
		System.out.println(treeString(root, 1));
	}
	
	private static String treeString(Node node, int numTab) {
		NodeType type = node.getNodeType();
		
		String indent = "";
		String indentMinusOne = "";
		for(int i = 0; i < numTab; i++)
			indent += "\t";
		for(int i = 0; i < numTab - 1; i++)
			indentMinusOne += "\t";
		
		String s = "";
		
		
		switch(type) {
		case CONST:
			s += ((ConstantNode<?>) node).getValue();
			break;
		case FUNC:
			FunctionNode funcNode = ((FunctionNode) node);
			
			s += funcNode.getName() + "() \n";
			for(Node child : funcNode.getParams())
				s += indent + treeString(child, numTab + 1) + "\n";
			
			
			break;
		case OP:
			byte opType = ((OperatorNode) node).getOpType();			
			switch (opType) {
			case OperatorNode.ADD:
				AddNode addNode = ((AddNode) node);
				
				s += "ADD() \n";
				for(Node child : addNode.getNodes())
					s += indent + treeString(child, numTab + 1) + "\n";
				s += indentMinusOne + "SUB() \n";
				for(Node child : addNode.getInversedNodes())
					s += indent + treeString(child, numTab + 1) + "\n";
				
				break;
			case OperatorNode.EXPO:
				ExponentNode expoNode = ((ExponentNode) node);
				
				s += "EXPO() \n";
				for(Node child : expoNode.getNodes())
					s += indent + treeString(child, numTab + 1) + "\n";
				break;
			case OperatorNode.MULT:
				MultNode multNode = ((MultNode) node);
				
				s += "MULT() \n";
				for(Node child : multNode.getNodes())
					s += indent + treeString(child, numTab + 1) + "\n";
				s += indentMinusOne + "DIV() \n";
				for(Node child : multNode.getInversedNodes())
					s += indent + treeString(child, numTab + 1) + "\n";
				
				break;
			case OperatorNode.NEG:
			default:
				NegateNode negNode = ((NegateNode) node);
				
				s += "NEG() \n";
				s += indent + treeString(negNode.getNegatedNode(), numTab + 1) +  '\n';
				break;
			}
			break;
		case VAR:
		default:
			s += ((VariableNode) node).getName();
			break;
		
		}
		
		
		return s;
	}
	
	
	private static String toString(Node node) {
		String s;
		switch(node.getNodeType()) {
		case CONST:
			return ((ConstantNode<?>) node).getValue().toString();
		
		case FUNC:
			FunctionNode fn = (FunctionNode) node;
			Iterator<Node> iterator = fn.getParams().iterator();
			Node first = iterator.next();
			
			s = toString(first) + ")";
			
			while(iterator.hasNext())
				s = toString(iterator.next()) + ", " + s;
			s = fn.getName() + "(" + s;
			return s;
			
		case OP:
			byte type = ((OperatorNode) node).getOpType();
			
			if(type == OperatorNode.ADD) {
				AddNode addNode = (AddNode) node;
				Iterator<Node> nodes = addNode.getNodes().iterator();
				
				s = "";
				if(nodes.hasNext()) {
					s = toString(nodes.next());
					while(nodes.hasNext())
						s = toString(nodes.next()) + " + " + s;
				}
				
				Iterator<Node> nodesInv = addNode.getInversedNodes().iterator();
				String s2 = "";
				if(nodesInv.hasNext()) {
					s2 = toString(nodesInv.next());
					if(nodesInv.hasNext()) {
						s2 = toString(nodesInv.next()) + " + " + s2;
						while(nodesInv.hasNext())
							s2 = toString(nodesInv.next()) + " + " + s2;
						s2 = '(' + s2 + ')';
					}
					s2 = " - " + s2;
				}
				
				return s + s2;
				
			} else if(type == OperatorNode.MULT) {
				MultNode multNode = (MultNode) node;
				Iterator<Node> nodes = multNode.getNodes().iterator();
				Node temp;
				s = "1";
				if(nodes.hasNext()) {
					temp = nodes.next();
					if(temp.getNodeType() == NodeType.OP && ((OperatorNode) temp).getOpType() == OperatorNode.ADD)
						s = "(" + toString(temp) + ")";
					else
						s = toString(temp);
					while(nodes.hasNext()) {
						temp = nodes.next();
						if(temp.getNodeType() == NodeType.OP && ((OperatorNode) temp).getOpType() == OperatorNode.ADD)
							s = "(" + toString(temp) + ") * " + s;
						else
							s = toString(temp) + " * " + s;
					}
				}
				
				Iterator<Node> nodesInv = multNode.getInversedNodes().iterator();
				String s2 = "";
				if(nodesInv.hasNext()) {
					temp = nodesInv.next();
					if(temp.getNodeType() == NodeType.OP && ((OperatorNode) temp).getOpType() == OperatorNode.ADD)
						s2 = "(" + toString(temp) + ")";
					else
						s2 = toString(temp);
					if(nodesInv.hasNext()) {
						temp = nodesInv.next();
						if(temp.getNodeType() == NodeType.OP && ((OperatorNode) temp).getOpType() == OperatorNode.ADD)
							s2 = "(" + toString(temp) + ") * " +  s2;
						else
							s2 = toString(temp) + " * " +  s2;
						while(nodesInv.hasNext()){
							temp = nodesInv.next();
							if(temp.getNodeType() == NodeType.OP && ((OperatorNode) temp).getOpType() == OperatorNode.ADD)
								s2 = "(" + toString(temp) + ") * " +  s2;
							else
								s2 = toString(temp) + " * " +  s2;
						}
						s2 = '(' + s2 + ')';
					}
					s2 = " / " + s2;
				}
				
				return s + s2;
			} else if(type == OperatorNode.EXPO) {
				ExponentNode expoNode = (ExponentNode) node;
				Iterator<Node> nodes = expoNode.getNodes().iterator();
				Node temp;
				s = "";
				if(nodes.hasNext()) {
					temp = nodes.next();
					if(temp.getNodeType() == NodeType.OP)
						s = "(" + toString(temp) + ")";
					else
						s = toString(temp);
					while(nodes.hasNext()) {
						temp = nodes.next();
						if(temp.getNodeType() == NodeType.OP)
							s = "(" + toString(temp) + ") ^ " + s;
						else
							s = toString(temp) + " ^ " + s;
					}
				}
				
				return s;
			} else if(type == OperatorNode.NEG) {
				return " -" + toString(((NegateNode) node).getNegatedNode());
			}
			
			
			
			return "";
		case VAR:
			return ((VariableNode) node).getName();
		default:
			return "";
		}
	}	
}

