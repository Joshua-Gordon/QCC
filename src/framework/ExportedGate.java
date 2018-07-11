package framework;


/**
 * This class is used to receive gate properties on the {@link CircuitBoard} 
 * that one would not get from {@link CircuitBoard} methods. <p>
 * 
 * When instantiated (privately through this class), ExportedGate contains the {@link AbstractGate}
 * associated with {@link SolderedGate} or {@link SolderedRegister}, as well as all the {@link AbstractGate}'s
 * {@link Matrix}'s qubit registers on the board that it applies to. It also contains how many vertical grid spaces
 * the {@link SolderedGate} instance takes up.
 * 
 * @author quantumresearch
 *
 */
public class ExportedGate {
	private AbstractGate abstractGate;
	private int[] registers;
	private int height;
	
	
	/**
	 * This method scans through each gate Row by Row within Column by Column <b> in order </b><br>
	 * on the Specified {@link CircuitBoard}.<br>
	 * 
	 * To apply an action to each gate through the scan, one must implements and pass through a<br>
	 * {@link ExportGatesRunnable}. <br>
	 * 
	 * 
	 * 
	 * @param cb
	 * The {@link CircuitBoard} evaluated
	 * @param egr
	 * The {@link ExportGatesRunnable} that gives the actions on each gate scanned
	 */
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
			egr.columnEndEvent(x);
		}
	}
	
	private ExportedGate(CircuitBoard cb, int x, int y) {
		
		SolderedRegister sr = cb.getSolderedRegister(x, y);
		SolderedGate sg = sr.getSolderedGate();
		
		this.abstractGate = sg.getAbstractGate();
		this.registers = new int[sg.getExpectedNumberOfRegisters()];
		
		int lastLocalReg = sg.getLastLocalRegister();
		int curLocalReg;
		
		int row = y;
		
		SolderedRegister curSr;
		while(row < cb.getRows() || cb.throwGateBoundsException(sg)) {
			curSr = cb.getSolderedRegister(x, row);
			if(curSr.getSolderedGate().equals(sg)) {
				curLocalReg = curSr.getLocalRegisterNumber();
				registers[curLocalReg] = row;
				if(curLocalReg == lastLocalReg)
					break;
			}
			row++;
		}
		this.height = row - y + 1;
	}
	
	/**
	 * @return 
	 * the {@link AbstractGate associated with the {@link SolderedGate}
	 */
	public AbstractGate getAbstractGate() {
		return abstractGate;
	}

	/**
	 * @return
	 * the list of qubit board registers associated with the {@link SolderedGate}
	 */
	public int[] getRegisters() {
		return registers;
	}

	/**
	 * @return
	 * the amount of vertical grid spaces the {@link SolderedGate} takes up
	 */
	public int getHeight() {
		return height;
	}
}
