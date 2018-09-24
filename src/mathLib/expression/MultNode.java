package mathLib.expression;

import utils.SimpleLinkedList;

public class MultNode extends OperatorNode {
	private SimpleLinkedList<Node> inversed;
	private SimpleLinkedList<Node> nodes;
	
	public MultNode () {
		this(new SimpleLinkedList<>(), new SimpleLinkedList<>());
	}
	
	private MultNode (SimpleLinkedList<Node> nodes, SimpleLinkedList<Node> inversed) {
		super(MULT);
		this.inversed = inversed;
		this.nodes = nodes;
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

	@Override
	public Node duplicate() {
		return new MultNode(duplicateNodes(nodes), duplicateNodes(inversed));
	}
}
