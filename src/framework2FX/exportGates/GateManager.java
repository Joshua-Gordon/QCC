package framework2FX.exportGates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Stream;

import framework2FX.CircuitBoard;
import framework2FX.MathDefintions;
import framework2FX.Project;
import framework2FX.UserDefinitions.ArgObject;
import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MathObject;
import framework2FX.UserDefinitions.MatrixObject;
import framework2FX.UserDefinitions.ScalarObject;
import framework2FX.exportGates.Exportable.ExportableCircuitBoard;
import framework2FX.exportGates.Exportable.ExportableGate;
import framework2FX.gateModels.GateModel;
import framework2FX.solderedGates.SolderedControl;
import framework2FX.solderedGates.SolderedGate;
import framework2FX.solderedGates.SolderedPin;
import framework2FX.solderedGates.SolderedRegister;
import framework2FX.solderedGates.SpacerPin;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.MathSet;
import mathLib.expression.Variable.ConcreteVariable;
import sun.net.www.content.text.plain;
import utils.customCollections.ImmutableArray;

public class GateManager {
	
	public static Stream<ExportedGate> exportGates(Project p) {
		return exportGates(p.getTopLevelBoard());
	}
	
	public static Stream<ExportedGate> exportGates(CircuitBoard cb) {
		Stream<ExportedGate> stream = Stream.generate(new ExportEveryGateSupplier(cb));
		return stream.takeWhile(x -> x != null);
	}
	
	public static Stream<Exportable> exportGatesRecursively(Project p) {
		return exportGatesRecursively(p.getTopLevelBoard());
	}
	
	public static Stream<Exportable> exportGatesRecursively(CircuitBoard cb) {
		Stream<Exportable> stream = Stream.generate(new ExportGatesRecusivelySupplier(cb));
		return stream.takeWhile(x -> x != null);
	}
	
	static Stream<Exportable> exportGatesRecursively(CircuitBoard cb, MathSet mathDefinitions, int[] registers, HashSet<Control> controls) {
		ExportStatus es = new ExportStatus(cb, mathDefinitions, registers, controls);
		Stream<Exportable> stream = Stream.generate(new ExportGatesRecusivelySupplier(es));
		return stream.takeWhile(x -> x != null);
	}
	
	
	
	
	public static void scanSolderedGatesOnBoard (CircuitBoard cb, SolderedGateRunnable sgr) {
		int row = 0;
		int column = 0;
		
		SolderedGate prev = null;
		SolderedGate sg;
		
		while (column != cb.getColumns()) {
			sgr.columnEndEvent(column);
			while (row != cb.getRows()) {
				sg = cb.getGateAt(row, column);
				
				if(sg != prev)
					sgr.action(sg);
				
				prev = sg;
				
				row++;
			}
			sgr.columnEndEvent(column++);
		}
	}
	
	
	public static interface SolderedGateRunnable {
		public void action(SolderedGate sg);
		public default void columnStartEvent(int column) {}
		public default void columnEndEvent(int column) {}
	}
	
	
	
	
	
	
	private static class ExportStatus {
		CircuitBoard cb;
		MathSet mathDefinitions;
		int[] registers;
		HashSet<Control> controls;
		
		int row = 0;
		int column = 0;
		
		public ExportStatus(CircuitBoard cb, MathSet mathDefinitions, int[] registers, HashSet<Control> controls) {
			this.cb = cb;
			this.mathDefinitions = mathDefinitions;
			this.registers = registers;
			this.controls = controls;
		}
	}
	
	
	
	private static class ExportGatesRecusivelySupplier implements Supplier<Exportable> {
		
		ExportStatus current;
		
		boolean finshedExporting = false;
		
		SolderedPin sp;
		SolderedGate sg;
		HashSet<Control> localControls = null;
		int[] localRegisters = null;
		int curReg = 0;
		
		ExportGatesRecusivelySupplier(ExportStatus es) {
			current = es;
		}
		
		ExportGatesRecusivelySupplier(CircuitBoard cb) {
			this(new ExportStatus(cb, MathDefintions.GLOBAL_DEFINITIONS, null, new HashSet<>()));
		}
		
		@Override
		public Exportable get() {
			if(finshedExporting)
				return null;
			return getNextGate();
		}
		
		private Exportable getNextGate() {
			
			SolderedGate nextGate = getGate();
			
			if(nextGate != sg) {
				sg = nextGate;
				localRegisters = new int[sg.getGateModel().getNumberOfRegisters()];
				localControls = new HashSet<>(current.controls);
				curReg = 0;
			}
			
			sp = getPin();
			
			addToExportGate();
			
			while(increment() && sg == (sg = getGate())) {
				sp = getPin();
				boolean isSpacer = !addToExportGate();
				if(isSpacer)
					return new ExportableGate(current.column, ExportedGate.mkIdentAt(current.row));
			}
			
			// get back the last soldered gate
			sg = sp.getSolderedGate();
			
			if(curReg != localRegisters.length)
				throw new RuntimeException("The gate given does not have all its registers defined");
			
			
			// finally export gate to stream
			
			return exportGateToStream();
		}
		
		
		
		@SuppressWarnings("unchecked")
		private Exportable exportGateToStream() {
			MathSet localDefinitions = new MathSet(MathDefintions.GLOBAL_DEFINITIONS);
			
			ImmutableArray<String> params = sg.getGateModel().getArguments();
			GroupDefinition grp = sg.getParameterSet();
			Complex[] parameters = new Complex[params.size()];
			Complex c = null;
			
			int i = 0;
			for(MathObject mo : grp.getMathDefinitions()) {
				if(mo.isMatrix())
					throw new RuntimeException("Can not pass a matrix as an parameter");
				
				if(mo.hasArguments())
					c = (Complex) ((ArgObject) mo).getDefinition().compute(current.mathDefinitions);
				else
					c = (Complex) ((ScalarObject) mo).getScalar();
				
				localDefinitions.addVariable(new ConcreteVariable(params.get(i), c));
				parameters[i] = c;
				
				i++;
			}
			
			
			int column = current.row == 0? -1 : 0 + current.column;
			
			if(sg.getGateModel() instanceof CircuitBoard){
				
				return new ExportableCircuitBoard(column, (CircuitBoard) sg.getGateModel(), localDefinitions, localRegisters, localControls);
				
			} else {
				Control[] tempArray = new Control[localControls.size()];
				localControls.toArray(tempArray);
				
				GateModel gm = (GateModel) sg.getGateModel();
				Matrix<Complex>[] matrixes = new Matrix[gm.getDefinitions().size()];
				
				i = 0;
				for(MathObject mo : gm.getDefinitions()) {
					if(!mo.isMatrix())
						throw new RuntimeException("No scalars are allowed to be defined for any gate model");
					
					if(mo.hasArguments())
						matrixes[i] = (Matrix<Complex>) ((ArgObject) mo).getDefinition().compute(localDefinitions);
					else
						matrixes[i] = (Matrix<Complex>) ((MatrixObject) mo).getMatrix();
					
					i++;
				}
				
				HashMap<String, Complex> argParamMap = new HashMap<>();
				int j = 0;
				for(String argument : gm.getArguments())
					argParamMap.put(argument, parameters[j++]);
				
				return new ExportableGate(column, new ExportedGate(gm, argParamMap, localRegisters, tempArray, matrixes));
			}
		}
		
		
		
		private boolean addToExportGate() {
			
			if(sp instanceof SolderedControl) {
				SolderedControl sc = (SolderedControl) sp;
				
				if(current.registers == null)
					localControls.add(new Control(current.row, sc.getControlStatus()));
				else
					localControls.add(new Control(current.registers[current.row], sc.getControlStatus()));
			} else if (!(sp instanceof SpacerPin)) {
				SolderedRegister sr = (SolderedRegister) sp;
				if(current.registers == null)
					localRegisters[sr.getSolderedGatePinNumber()] = current.row;
				else
					localRegisters[sr.getSolderedGatePinNumber()] = current.registers[current.row];
				curReg ++;
			} else {
				return false;
			}
			return true;
		}
		
		
		public SolderedPin getPin() {
			return current.cb.getSolderPinAt(current.row, current.column);
		}
		
		public SolderedGate getGate() {
			return current.cb.getGateAt(current.row, current.column);
		}
		
		private boolean increment() {
			if(current.row + 1 == current.cb.getRows()) {
				current.row = 0;
				if(current.column + 1 == current.cb.getColumns()) {
					finshedExporting = true;
				} else {
					current.row = 0;
					current.column++;
				}
				return false;
			} else {
				current.row++;
			}
			return true;
		}
	}
	
	
	
	
	private static class ExportEveryGateSupplier implements Supplier<ExportedGate> {

		Stack<ExportStatus> exportState = new Stack<>();
		
		SolderedPin sp;
		SolderedGate sg;
		HashSet<Control> localControls = null;
		int[] localRegisters = null;
		int curReg = 0;
		
		public ExportEveryGateSupplier (CircuitBoard cb) {
			exportState.push(new ExportStatus(cb, MathDefintions.GLOBAL_DEFINITIONS, null, new HashSet<>()));
		}
		
		@Override
		public ExportedGate get() {
			if(exportState.size() == 0)
				return null;
			return getNextGate();
		}
		
		private ExportedGate getNextGate() {
			
			SolderedGate nextGate = getGate();
			
			if(nextGate != sg) {
				sg = nextGate;
				localRegisters = new int[sg.getGateModel().getNumberOfRegisters()];
				localControls = new HashSet<>(current().controls);
				curReg = 0;
			}
			
			sp = getPin();
			
			addToExportGate();
			
			while(increment() && sg == (sg = getGate())) {
				sp = getPin();
				boolean isSpacer = !addToExportGate();
				if(isSpacer)
					return ExportedGate.mkIdentAt(current().row);
			}
			
			// get back the last soldered gate
			sg = sp.getSolderedGate();
			
			if(curReg != localRegisters.length)
				throw new RuntimeException("The gate given does not have all its registers defined");
			
			
			// finally export gate to stream
			
			return exportGateToStream();
		}
		
		
		
		@SuppressWarnings("unchecked")
		private ExportedGate exportGateToStream() {
			MathSet localDefinitions = new MathSet(MathDefintions.GLOBAL_DEFINITIONS);
			
			ImmutableArray<String> params = sg.getGateModel().getArguments();
			GroupDefinition grp = sg.getParameterSet();
			Complex[] parameters = new Complex[params.size()];
			Complex c = null;
			
			int i = 0;
			for(MathObject mo : grp.getMathDefinitions()) {
				if(mo.isMatrix())
					throw new RuntimeException("Can not pass a matrix as an parameter");
				
				if(mo.hasArguments())
					c = (Complex) ((ArgObject) mo).getDefinition().compute(current().mathDefinitions);
				else
					c = (Complex) ((ScalarObject) mo).getScalar();
				
				localDefinitions.addVariable(new ConcreteVariable(params.get(i), c));
				parameters[i] = c;
				
				i++;
			}
			
			
			if(sg.getGateModel() instanceof CircuitBoard){
				exportState.push(new ExportStatus((CircuitBoard) sg.getGateModel(), localDefinitions, localRegisters, localControls));
				return getNextGate();
			} else {
				Control[] tempArray = new Control[localControls.size()];
				localControls.toArray(tempArray);
				
				GateModel gm = (GateModel) sg.getGateModel();
				Matrix<Complex>[] matrixes = new Matrix[gm.getDefinitions().size()];
				
				i = 0;
				for(MathObject mo : gm.getDefinitions()) {
					if(!mo.isMatrix())
						throw new RuntimeException("No scalars are allowed to be defined for any gate model");
					
					if(mo.hasArguments())
						matrixes[i] = (Matrix<Complex>) ((ArgObject) mo).getDefinition().compute(localDefinitions);
					else
						matrixes[i] = (Matrix<Complex>) ((MatrixObject) mo).getMatrix();
					
					i++;
				}
				
				HashMap<String, Complex> argParamMap = new HashMap<>();
				int j = 0;
				for(String argument : gm.getArguments())
					argParamMap.put(argument, parameters[j++]);
				
				return new ExportedGate(gm, argParamMap, localRegisters, tempArray, matrixes);
			}
		}
		
		
		
		private boolean addToExportGate() {
			
			if(sp instanceof SolderedControl) {
				SolderedControl sc = (SolderedControl) sp;
				
				if(current().registers == null)
					localControls.add(new Control(current().row, sc.getControlStatus()));
				else
					localControls.add(new Control(current().registers[current().row], sc.getControlStatus()));
			} else if (!(sp instanceof SpacerPin)) {
				SolderedRegister sr = (SolderedRegister) sp;
				if(current().registers == null)
					localRegisters[sr.getSolderedGatePinNumber()] = current().row;
				else
					localRegisters[sr.getSolderedGatePinNumber()] = current().registers[current().row];
				curReg ++;
			} else {
				return false;
			}
			return true;
		}
		
		private ExportStatus current () {
			return exportState.peek();
		}
		
		public SolderedPin getPin() {
			return current().cb.getSolderPinAt(current().row, current().column);
		}
		
		public SolderedGate getGate() {
			return current().cb.getGateAt(current().row, current().column);
		}
		
		private boolean increment() {
			if(current().row + 1 == current().cb.getRows()) {
				current().row = 0;
				if(current().column + 1 == current().cb.getColumns()) {
					exportState.pop();
				} else {
					current().row = 0;
					current().column++;
				}
				return false;
			} else {
				current().row++;
			}
			return true;
		}
	}
	
}
