package framework2FX;


public class ExportedGate {	
	private final AbstractGateModel gateModel;
	private final int[] gateRegisters;
	private final int[] gateOpenControls;
	private final int[] gateClosedControls;
	
	public ExportedGate(AbstractGateModel gateModel, int[] gateRegisters, int[] gateOpenControls, int[] gateClosedControls) {
		this.gateModel = gateModel;
		this.gateRegisters = gateRegisters;
		this.gateOpenControls = gateOpenControls;
		this.gateClosedControls = gateClosedControls;
	}
}
