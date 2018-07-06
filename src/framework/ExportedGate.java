package framework;

public class ExportedGate {
	private AbstractGate abstractGate;
	private int[] registers;
	private int height;
	
	
	public static void exportGates(CircuitBoard cb, ExportGatesRunnable egr) {
		ExportedGate eg;
		int y;
		for(int x = 0; x < cb.getColumns(); x++) {
			egr.nextColumnEvent(x);
			y = 0;
			while(y < cb.getRows()) {
				eg = new ExportedGate(cb, x, y);
				egr.gateExported(eg, x, y);
				y += eg.height;
			}
		}
	}
	
	private ExportedGate(CircuitBoard cb, int x, int y) {
		
		SolderedRegister sr = cb.getSolderedRegister(x, y);
		SolderedGate sg = sr.getSolderedGate();
		
		this.abstractGate = sg.getAbstractGate();
		this.registers = new int[sg.getExpectedNumberOfRegisters()];
		
		int registersFound = 0;
		int row = y;
		
		SolderedRegister curSr;
		while(registersFound < registers.length) {
			curSr = cb.getSolderedRegister(x, row);
			if(curSr.getSolderedGate().equals(sg)) {
				registers[curSr.getGateRegisterNumber()] = row;
				registersFound++;
			}
			row++;
		}
		this.height = row - y;
	}
	
	public AbstractGate getAbstractGate() {
		return abstractGate;
	}

	public int[] getRegisters() {
		return registers;
	}

	public int getHeight() {
		return height;
	}
}
