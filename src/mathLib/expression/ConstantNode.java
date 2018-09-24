package mathLib.expression;

public abstract class ConstantNode<T extends Number> extends Node {
	private T value;
	
	public ConstantNode(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}
	
	@Override
	public NodeType getNodeType() {
		return NodeType.CONST;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	public static class IntegerNode extends ConstantNode<Integer> {
		public IntegerNode(Integer value) {
			super(value);
		}
		public IntegerNode(String value) {
			super(Integer.parseInt(value));
		}
		@Override
		public Node duplicate() {
			return new IntegerNode(getValue());
		}
	}
	
	public static class DoubleNode extends ConstantNode<Double> {
		public DoubleNode(Double value) {
			super(value);
		}
		public DoubleNode(String value) {
			super(Double.parseDouble(value));
		}
		
		@Override
		public Node duplicate() {
			return new DoubleNode(getValue());
		}
	}
}
