package mathLib.expression;

import utils.SimpleLinkedList;

public class FunctionNode extends Node {
	private SimpleLinkedList<Node> params;
	private String name;
	
	public FunctionNode (String name) {
		this.params = new SimpleLinkedList<>();
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
}
