package mathLib.expression;

import utils.SimpleLinkedList;

public class ExponentNode extends OperatorNode {
	private SimpleLinkedList<Node> nodes;
	
	public ExponentNode () {
		super(EXPO);
		this.nodes = new SimpleLinkedList<>();
	}

	public SimpleLinkedList<Node> getNodes(){
		return nodes;
	}
	
	@Override
	public void addInversed(Node node) {}

	@Override
	public void add(Node node) {
		nodes.add(node);
	}
	
	@Override
	public String toString() {
		return "EXPO()" + nodes.toString();
	}
}