package framework2FX.exportGates;

import java.util.HashSet;
import java.util.stream.Stream;

import framework2FX.CircuitBoard;
import mathLib.expression.MathSet;

public abstract class Exportable {
	private final int column;
	
	public Exportable(int column) {
		this.column = column;
	}
	
	public abstract boolean isCircuitBoard();
	public abstract Stream<Exportable> exportCircuitBoard();
	public abstract ExportedGate exportGate();
	
	
	public int getColumn () {
		return column;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	static class ExportableCircuitBoard extends Exportable {
		private final CircuitBoard cb;
		private final MathSet mathDefinitions;
		private final int[] registers;
		private final HashSet<Control> controls;
		
		
		public ExportableCircuitBoard(int column, CircuitBoard cb, MathSet mathDefinitions, int[] registers, HashSet<Control> controls) {
			super(column);
			
			this.cb = cb;
			this.mathDefinitions = mathDefinitions;
			this.registers = registers;
			this.controls = controls;
		}

		@Override
		public boolean isCircuitBoard() {
			return true;
		}

		@Override
		public Stream<Exportable> exportCircuitBoard() {
			return GateManager.exportGatesRecursively(cb, mathDefinitions, registers, controls);
		}

		@Override
		public ExportedGate exportGate() {
			throw new RuntimeException("This can not be exported into a single gate as it contains a CircuitBoard instance");
		}
	}
	
	static class ExportableGate extends Exportable {
		private final ExportedGate eg;
		
		public ExportableGate(int column, ExportedGate eg) {
			super(column);
			
			this.eg = eg;
		}
		
		@Override
		public boolean isCircuitBoard() {
			return false;
		}
		
		@Override
		public Stream<Exportable> exportCircuitBoard() {
			throw new RuntimeException("This can not be exported into as multiple gates as it does not contain a CircuitBoard instance");
		}
		
		@Override
		public ExportedGate exportGate() {
			return eg;
		}
		
	}
	
}



