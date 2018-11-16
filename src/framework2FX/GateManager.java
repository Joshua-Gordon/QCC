package framework2FX;

import java.util.HashSet;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Stream;

import framework2FX.UserDefinitions.ArgObject;
import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MathObject;
import framework2FX.UserDefinitions.MatrixObject;
import framework2FX.UserDefinitions.ScalarObject;
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
import utils.customCollections.ImmutableArray;

public class GateManager {
	
	public static Stream<ExportedGate> exportGates(Project p) {
		return Stream.generate(new Supplier<ExportedGate>() {
			Stack<ExportState> exportState = new Stack<>();
			
			SolderedPin sp;
			SolderedGate sg;
			HashSet<Control> localControls = null;
			int[] localRegisters = null;
			int curReg = 0;
			
			{
				exportState.push(new ExportState(p.getTopLevelBoard(), MathDefintions.GLOBAL_DEFINITIONS, null, new HashSet<>()));
			}
			
			@Override
			public ExportedGate get() {
				if(exportState.size() == 0)
					return null;
				return getNextGate();
			}
			
			@SuppressWarnings("unchecked")
			private ExportedGate getNextGate() {
				
				sg = getGate();
				localRegisters = new int[sg.getGateModel().getNumberOfRegisters()];
				localControls = new HashSet<>();
				curReg = 0;
				
				sp = getPin();
				
				addToExportGate();
				
				while(increment() && sg == (sg = getGate())) {
					sp = getPin();	
					addToExportGate();
				}
				
				if(curReg != localRegisters.length)
					throw new RuntimeException("The gate given does not have all its registers defined");
				
				
				sg = sp.getSolderedGate();
				
				// finally export gate to stream
				
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
					exportState.push(new ExportState((CircuitBoard) sg.getGateModel(), localDefinitions, localRegisters, localControls));
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
					
					return new ExportedGate(gm, parameters, localRegisters, tempArray, matrixes);
				}
			}
			
			private void addToExportGate() {
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
				}
			}
			
			private ExportState current () {
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
		}).takeWhile(x -> x != null);
		
		
		
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
	
	private static class ExportState {
		CircuitBoard cb;
		MathSet mathDefinitions;
		int[] registers;
		HashSet<Control> controls;
		
		int row = 0;
		int column = 0;
		
		public ExportState(CircuitBoard cb, MathSet mathDefinitions, int[] registers, HashSet<Control> controls) {
			this.cb = cb;
			this.mathDefinitions = mathDefinitions;
			this.registers = registers;
			this.controls = controls;
		}
	}
	
	
}
