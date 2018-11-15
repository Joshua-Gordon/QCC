package framework2FX;

import java.util.HashSet;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Stream;

import framework2FX.solderedGates.SolderedGate;
import framework2FX.solderedGates.SolderedPin;
import mathLib.expression.MathSet;

public class GateManager {
	
	
//	public static void exportGates (Project p, ExportGatesRunnable egr) {
//		exportGates(p.getTopLevelBoard(), MathDefintions.GLOBAL_DEFINITIONS, new int[0], new HashSet<>(), egr);
//	}
	
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
	
	
	public static Stream<ExportedGate> exportGates(Project p) {
		return Stream.generate(new Supplier<ExportedGate>() {
			Stack<ExportState> exportState = new Stack<>();
			
			SolderedPin sp;
			SolderedGate sg;
			HashSet<Control> localControls = null;
			int[] localRegisters = null;
			int curReg;
			
			{
				exportState.push(new ExportState(p.getTopLevelBoard(), MathDefintions.GLOBAL_DEFINITIONS, null, new HashSet<>()));
			}
			
			@Override
			public ExportedGate get() {
				if(exportState.size() == 0)
					return null;
				return getNextGate();
			}
			
			private ExportedGate getNextGate() {
				
				sg = current().cb.getGateAt(current().row, current().column);
				localRegisters = new int[sg.getGateModel().getNumberOfRegisters()];
				
				
//				while(increment() && ) {
//					
//				}
				return null;
			}
			
			private ExportState current () {
				return exportState.peek();
			}
			
			private boolean increment() {
				if(current().row - 1 == current().cb.getRows()) {
					current().row = 0;
					if(current().column - 1 == current().cb.getColumns()) {
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
		});
		
		
		
	}
	
	
	
	
//	
//	@SuppressWarnings("unchecked")
//	private static void exportGates(CircuitBoard cb, MathSet mathDefinitions, int[] registers, HashSet<Control> controls, ExportGatesRunnable egr) {
//		int row = 0;
//		int column = 0;
//		
//		SolderedGate prev = null;
//		SolderedGate sg;
//		
//		HashSet<Control> localControls = null;
//		int[] localRegisters = null;
//		int curReg;
//		
//		while (column != cb.getColumns()) {
//			egr.columnEndEvent(column);
//			while (row != cb.getRows()) {
//				sg = cb.getGateAt(row, column);
//				
//				if(sg == prev) {
//					
//					if(sg.) {
//						
//					}
//					
//					
//					
//				} else {
//					if (prev != null) {
//						
//						MathSet localDefinitions = new MathSet(MathDefintions.GLOBAL_DEFINITIONS);
//						
//						ImmutableArray<String> params = sg.getGateModel().getArguments();
//						GroupDefinition grp = sg.getParameterSet();
//						Complex[] parameters = new Complex[params.size()];
//						Complex c = null;
//						
//						int i = 0;
//						for(MathObject mo : grp.getMathDefinitions()) {
//							if(mo.isMatrix())
//								throw new RuntimeException("Can not pass a matrix as an parameter");
//							
//							if(mo.hasArguments())
//								c = (Complex) ((ArgObject) mo).getDefinition().compute(mathDefinitions);
//							else
//								c = (Complex) ((ScalarObject) mo).getScalar();
//							
//							localDefinitions.addVariable(new ConcreteVariable(params.get(i), c));
//							parameters[i] = c;
//							
//							i++;
//						}
//						
//						
//						if(sg.getGateModel() instanceof CircuitBoard){
//							
//							exportGates((CircuitBoard) sg.getGateModel(), localDefinitions, localRegisters, localControls, egr);
//							
//						} else {
//							Control[] tempArray = new Control[localControls.size()];
//							localControls.toArray(tempArray);
//							
//							GateModel gm = (GateModel) sg.getGateModel();
//							Matrix<Complex>[] matrixes = new Matrix[gm.getDefinitions().size()];
//							
//							i = 0;
//							for(MathObject mo : gm.getDefinitions()) {
//								if(!mo.isMatrix())
//									throw new RuntimeException("No scalars are allowed to be defined for any gate model");
//								
//								if(mo.hasArguments())
//									matrixes[i] = (Matrix<Complex>) ((ArgObject) mo).getDefinition().compute(localDefinitions);
//								else
//									matrixes[i] = (Matrix<Complex>) ((MatrixObject) mo).getMatrix();
//								
//								i++;
//							}
//							
//							egr.action(new ExportedGate(gm, parameters, localRegisters, tempArray, matrixes));
//						}
//					}
//					localControls = new HashSet<>();
//					localControls.addAll(controls);
//					localRegisters = new int[sg.getGateModel().getNumberOfRegisters()];
//				}
//				
//				
//				prev = sg;
//				
//				row++;
//			}
//			egr.columnEndEvent(column++);
//		}
//	}
	
	
	
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
	
	
}
