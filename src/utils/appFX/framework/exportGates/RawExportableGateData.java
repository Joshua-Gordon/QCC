package appFX.framework.exportGates;

import java.util.Hashtable;
import java.util.LinkedList;

import appFX.framework.solderedGates.SolderedGate;

public class RawExportableGateData {
	private final SolderedGate sg;
	private final Hashtable<Integer, Integer> registers;
	private final LinkedList<Control> controls;
	private final LinkedList<Integer> underneathIdentityGates;
	private final int gateRowSpaceStart, gateRowSpaceEnd, gateRowBodyStart, gateRowBodyEnd, column;
	
	public RawExportableGateData(SolderedGate sg, Hashtable<Integer, Integer> registers, LinkedList<Control> controls,
			LinkedList<Integer> underneathIdentityGates, int gateRowSpaceStart, int gateRowSpaceEnd, int gateRowBodyStart,
			int gateRowBodyEnd, int column) {
		this.sg = sg;
		this.registers = registers;
		this.controls = controls;
		this.underneathIdentityGates = underneathIdentityGates;
		this.gateRowSpaceStart = gateRowSpaceStart;
		this.gateRowSpaceEnd = gateRowSpaceEnd;
		this.gateRowBodyStart = gateRowBodyStart;
		this.gateRowBodyEnd = gateRowBodyEnd;
		this.column = column;
	}

	public SolderedGate getSolderedGate() {
		return sg;
	}

	public Hashtable<Integer, Integer> getRegisters() {
		return registers;
	}

	public LinkedList<Control> getControls() {
		return controls;
	}

	public LinkedList<Integer> getUnderneathIdentityGates () {
		return underneathIdentityGates;
	}
	
	public int getGateRowSpaceStart() {
		return gateRowSpaceStart;
	}

	public int getGateRowSpaceEnd() {
		return gateRowSpaceEnd;
	}

	public int getGateRowBodyStart() {
		return gateRowBodyStart;
	}

	public int getGateRowBodyEnd() {
		return gateRowBodyEnd;
	}

	public int getColumn() {
		return column;
	}
}
