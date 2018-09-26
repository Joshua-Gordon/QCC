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
	
	public Expression (String expression) {
		this(NodeParser.parseNode(expression));
	}
	
	private Expression (Node root) {
		this.root = root;
	}
	
	public Node getRoot() {
		return root;
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
				return "-" + toString(((NegateNode) node).getNegatedNode());
			}
			
			
			
			return "";
		case VAR:
			return ((VariableNode) node).getName();
		default:
			return "";
		}
	}	
}

