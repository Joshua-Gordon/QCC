package javafx.framework.exportGates;

public class Control {
	public static final boolean CONTROL_TRUE = true;
	public static final boolean CONTROL_FALSE = false;
	
	private final int register;
	private final boolean controlStatus;
	
	public Control (int register, boolean controlStatus) {
		this.register = register;
		this.controlStatus = controlStatus;
	}

	public int getRegister() {
		return register;
	}

	public boolean getControlStatus() {
		return controlStatus;
	}
	
}
