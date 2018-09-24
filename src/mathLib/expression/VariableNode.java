package mathLib.expression;

public class VariableNode extends Node {
	private String name;
	
	public VariableNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public NodeType getNodeType() {
		return NodeType.VAR;
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public Node duplicate() {
		return new VariableNode(name);
	}
}
