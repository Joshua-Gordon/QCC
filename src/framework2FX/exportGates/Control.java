package framework2FX.exportGates;

public class Control {
	public static final boolean CONTROL_TRUE = true;
	public static final boolean CONTROL_FALSE = false;
	
	private final int register;
	private final boolean controlStatus;
	
	public Control (int register, boolean controlStatus) {
		this.register = register;
		this.controlStatus = controlStatus;
	}
	
}
