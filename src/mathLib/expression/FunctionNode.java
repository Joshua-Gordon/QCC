package mathLib.expression;

import utils.SimpleLinkedList;

public class FunctionNode extends Node {
	private SimpleLinkedList<Node> params;
	private String name;
	
	public FunctionNode (String name) {
		this(name, new SimpleLinkedList<>());
	}
	
	private FunctionNode (String name, SimpleLinkedList<Node> params) {
		this.params = params;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public SimpleLinkedList<Node> getParams() {
		return params;
	}
	
	public void addParam(Node node) {
		params.add(node);
	}
	
	@Override
	public NodeType getNodeType() {
		return NodeType.FUNC;
	}
	
	@Override
	public String toString() {
		return name + "()" + params.toString();
	}

	@Override
	public Node duplicate() {
		return new FunctionNode(name, duplicateNodes(params));
	}
}
