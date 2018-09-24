package mathLib.expression;

public abstract class Node {
	
	public static enum NodeType {
		OP, FUNC, VAR, CONST;
	}
	
	public abstract NodeType getNodeType();
}
