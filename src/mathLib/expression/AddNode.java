package mathLib.expression;

import utils.SimpleLinkedList;

public class AddNode extends OperatorNode {
	private SimpleLinkedList<Node> inversed;
	private SimpleLinkedList<Node> nodes;
	
	public AddNode () {
		super(ADD);
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
		return "ADD()" + nodes.toString() + ", SUB()" + inversed.toString();
	}
}
