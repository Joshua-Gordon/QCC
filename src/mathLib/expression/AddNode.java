package mathLib.expression;

import utils.SimpleLinkedList;
import utils.SimpleLinkedList.LinkedIterator;

public class AddNode extends OperatorNode {
	private SimpleLinkedList<Node> inversed;
	private SimpleLinkedList<Node> nodes;
	
	public AddNode () {
		this(new SimpleLinkedList<>(), new SimpleLinkedList<>());
	}
	
	private AddNode (SimpleLinkedList<Node> nodes, SimpleLinkedList<Node> inversed) {
		super(ADD);
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
		return "ADD()" + nodes.toString() + ", SUB()" + inversed.toString();
	}

	@Override
	public AddNode duplicate() {
		return new AddNode(duplicateNodes(nodes), duplicateNodes(inversed));
	}
	
}
