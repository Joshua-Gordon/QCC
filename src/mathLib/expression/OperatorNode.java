package mathLib.expression;

public abstract class OperatorNode extends Node{
	private byte opType;
	public static final byte ADD  = 0;
	public static final byte MULT = 1;
	public static final byte NEG  = 2;
	public static final byte EXPO = 3;
	
	public OperatorNode(byte opType) {
		this.opType = opType;
	}
	
	public static OperatorNode getOperator(byte opType) {
		switch (opType){
		case ADD:
			return new AddNode();
		case MULT:
			return new MultNode();
		case NEG:
			return new NegateNode();
		case EXPO:
		default:
			return new ExponentNode();
		}
	}
	
	public byte getOpType() {
		return opType;
	}
	
	@Override
	public NodeType getNodeType() {
		return NodeType.OP;
	}
	
	public abstract void add(Node node);		
	public abstract void addInversed(Node node);
	
	public byte comparePrecendenceTo(byte opType) {
		if(this.opType > opType) return -1;
		if(this.opType < opType) return  1;
		return 0;
	}
}
