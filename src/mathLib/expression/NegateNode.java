package mathLib.expression;

public class NegateNode extends OperatorNode {
	private Node toNegate;
	
	public NegateNode () {
		super(NEG);
		this.toNegate = null;
	}
	
	@Override
	public NodeType getNodeType() {
		return NodeType.OP;
	}

	@Override
	public void add(Node node) {
		this.toNegate = node;
	}
	
	public Node getNegatedNode() {
		return toNegate;
	}
	
	@Override
	public void addInversed(Node node) {}
	
	@Override
	public String toString() {
		return "NEG()" + toNegate.toString();
	}
}
