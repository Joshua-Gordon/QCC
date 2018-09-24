package mathLib.expression;

import utils.SimpleLinkedList;

public class MultNode extends OperatorNode {
	private SimpleLinkedList<Node> inversed;
	private SimpleLinkedList<Node> nodes;
	
	public MultNode() {
		super(MULT);
		this.inversed = new SimpleLinkedList<>();
		this.nodes = new SimpleLinkedList<>();
	}
	
	public SimpleLinkedList<Node> getNodes(){
		return nodes;
	}
	
	public SimpleLinkedList<Node> getInversedNodes(){
		return inversed;
	}
	
	@Override
	public void addInversed(Node node) {
		inversed.add(node);
	}

	@Override
	public void add(Node node) {
		nodes.add(node);
	}
	
	@Override
	public String toString() {
		return "MULT()" + nodes.toString() + ", DIV()" + inversed.toString();
	}
}
