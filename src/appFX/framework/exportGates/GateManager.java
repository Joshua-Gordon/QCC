package appFX.framework.exportGates;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import appFX.framework.AppStatus;
import appFX.framework.MathDefintions;
import appFX.framework.Project;
import appFX.framework.UserDefinitions.ArgObject;
import appFX.framework.UserDefinitions.GroupDefinition;
import appFX.framework.UserDefinitions.MathObject;
import appFX.framework.UserDefinitions.MatrixObject;
import appFX.framework.UserDefinitions.ScalarObject;
import appFX.framework.gateModels.BasicModel;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.solderedGates.SolderedGate;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.Expression.EvaluateExpressionException;
import mathLib.expression.MathSet;
import mathLib.expression.Variable.ConcreteVariable;
import utils.customCollections.ImmutableArray;
import utils.customCollections.Queue;
import utils.customCollections.Stack;

public class GateManager {
	
	public static Stream<ExportedGate> exportGates(String circuitboardName) throws ExportException {
		ExportTree et = startScanAndGetExportStream(circuitboardName, MathDefintions.GLOBAL_DEFINITIONS);
		return Stream.generate(new DefaultExportGatesSupplier(et)).takeWhile(x -> x != null);
	}
	
	public static Stream<ExportedGate> exportGates(Project p) throws ExportException {
		return exportGates(p.getTopLevelCircuitName());
	}
	
	public static Stream<Exportable> exportGatesRecursively(Project p) throws ExportException {
		return exportGatesRecursively(p.getTopLevelCircuitName());
	}
	
	public static Stream<Exportable> exportGatesRecursively(String circuitboardName) throws ExportException {
		return exportGatesRecursively(startScanAndGetExportStream(circuitboardName, MathDefintions.GLOBAL_DEFINITIONS));
	}
	
	private static Stream<Exportable> exportGatesRecursively(ExportTree et) {
		return Stream.generate(new RecursiveExportGatesSupplier(et)).takeWhile(x -> x != null);
	}
	
	
	
	
	public static interface Exportable {
		public Stream<Exportable> exportIfCircuitBoard();
		public ExportedGate exportIfNotCircuitBoard();
		public boolean isCircuitBoard();
		public int getColumn();
		public int[] getRegisters();
		public int[] getUnderneathIdentityRegisters();
		public Control[] getControls();
	}
	
	
	
	private static class ExportNotCircuit implements Exportable {
		private final int column;
		private final ExportLeaf n;
		
		private ExportNotCircuit(ExportLeaf n, int column) {
			this.column = column;
			this.n = n;
		}

		@Override
		public Stream<Exportable> exportIfCircuitBoard() {
			return null;
		}

		@Override
		public ExportedGate exportIfNotCircuitBoard() {
			return toExportedGate(n);
		}

		@Override
		public boolean isCircuitBoard() {
			return false;
		}

		@Override
		public int getColumn() {
			return column;
		}

		@Override
		public int[] getRegisters() {
			RawExportableGateData regs = n.rawData;
			Hashtable<Integer, Integer> registers = regs.getRegisters();
			int[] temp = new int[registers.size()];
			
			for(int i = 0; i < registers.size(); i++)
				temp[i] = registers.get(i);
			
			return temp;
		}

		@Override
		public int[] getUnderneathIdentityRegisters() {
			RawExportableGateData regs = n.rawData;
			LinkedList<Integer> registers = regs.getUnderneathIdentityGates();
			int[] temp = new int[registers.size()];
			
			for(int i = 0; i < registers.size(); i++)
				temp[i] = registers.get(i);
			
			return temp;
		}

		@Override
		public Control[] getControls() {
			RawExportableGateData regs = n.rawData;
			LinkedList<Control> controls = regs.getControls();
			Control[] temp = new Control[controls.size()];
			
			for(int i = 0; i < controls.size(); i++)
				temp[i] = controls.get(i);
			
			return temp;
		}
	}
	
	
	private static class ExportCircuit implements Exportable {
		private int column;
		private ExportTree tree;
		
		private ExportCircuit(ExportTree tree, int column) {
			this.column = column;
			this.tree = tree;
		}
		
		@Override
		public Stream<Exportable> exportIfCircuitBoard() {
			return exportGatesRecursively(tree);
		}

		@Override
		public ExportedGate exportIfNotCircuitBoard() {
			return null;
		}

		@Override
		public boolean isCircuitBoard() {
			return true;
		}

		@Override
		public int getColumn() {
			return column;
		}

		@Override
		public int[] getRegisters() {
			RawExportableGateData regs = tree.rawData;
			Hashtable<Integer, Integer> registers = regs.getRegisters();
			int[] temp = new int[registers.size()];
			
			for(int i = 0; i < registers.size(); i++)
				temp[i] = registers.get(i);
			
			return temp;
		}
		
		@Override
		public int[] getUnderneathIdentityRegisters() {
			RawExportableGateData regs = tree.rawData;
			LinkedList<Integer> gates = regs.getUnderneathIdentityGates();
			int[] temp = new int[gates.size()];
			
			for(int i = 0; i < gates.size(); i++)
				temp[i] = gates.get(i);
			
			return temp;
		}

		@Override
		public Control[] getControls() {
			RawExportableGateData regs = tree.rawData;
			LinkedList<Control> controls = regs.getControls();
			Control[] temp = new Control[controls.size()];
			
			for(int i = 0; i < controls.size(); i++)
				temp[i] = controls.get(i);
			
			return temp;
		}
		
	}
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private static ExportedGate toExportedGate(ExportLeaf leaf) {
		LinkedList<Control> tempControls = leaf.rawData.getControls();
		Control[] controls = new Control[tempControls.size()];
		controls = tempControls.toArray(controls);
		
		Hashtable<Integer, Integer> tempRegs = leaf.rawData.getRegisters();
		int[] registers = new int[tempRegs.size()];
		for(int i = 0; i < tempRegs.size(); i++)
			registers[i] = tempRegs.get(i);
		
		GateModel gm = leaf.gm;
		Hashtable<String, Complex> argParamTable = leaf.parameters;
		BasicModel dg = (BasicModel) gm;
		ImmutableArray<MathObject> definitions = dg.getDefinitions();
		Matrix<Complex>[] matrixes = new Matrix[definitions.size()];
		
		for (int i = 0 ; i < matrixes.length; i++) {
			try {
				MathObject mo = definitions.get(i);
				if(mo.hasArguments())
					matrixes[i] = (Matrix<Complex>) ((ArgObject) mo).getDefinition().compute(leaf.runtimeVariables);
				else
					matrixes[i] = (Matrix<Complex>) ((MatrixObject) mo).getMatrix();
			} catch (EvaluateExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		
		return new ExportedGate(dg, argParamTable, registers, controls, matrixes);
	}
	
	
	
	
	private static class DefaultExportGatesSupplier extends AbstractExportGatesSupplier {
		private DefaultExportGatesSupplier(ExportTree et) {
			super(et);
		}

		@Override
		public void doActionToNode(ExportTree previous, ExportNode next) {
			if(previous.rawData != null) {
				Hashtable<Integer, Integer> registers = previous.rawData.getRegisters();
				Hashtable<Integer, Integer> nextRegisters = next.rawData.getRegisters();
				
				for(int i = 0; i < nextRegisters.size(); i++)
					nextRegisters.put(i, registers.get(nextRegisters.get(i)));
				
				LinkedList<Control> controls = next.rawData.getControls();
				ListIterator<Control> li = controls.listIterator();
				
				while(li.hasNext()){
					Control previousControl = li.next();
					int previousReg = previousControl.getRegister();
					boolean previousStat = previousControl.getControlStatus();
					li.set(new Control(registers.get(previousReg), previousStat));
				}
				controls.addAll(previous.rawData.getControls());
			}
		}
		
		@Override
		public ExportedGate export(ExportLeaf leaf) {
			return toExportedGate(leaf);
		}
	}
	
	
	
	
	
	
	private static class RecursiveExportGatesSupplier implements Supplier<Exportable> {
		private ExportTree et;
		
		private RecursiveExportGatesSupplier(ExportTree et) {
			this.et = et;
		}
		
		@Override
		public Exportable get() {
			Queue<ExportNode> nodes = et.exportNodes;
			if(nodes.size() == 0) return null;
			
			ExportNode node = et.exportNodes.dequeue();
			if(node instanceof ExportTree)
				return new ExportCircuit((ExportTree) node, et.rawData.getColumn());
			else
				return new ExportNotCircuit((ExportLeaf) node, et.rawData.getColumn());
		}
		
	}
	
	
	
	
	
	
	
	
	
	private abstract static class AbstractExportGatesSupplier  implements Supplier<ExportedGate> {
		Stack<ExportTree> currentState = new Stack<>();
		
		private AbstractExportGatesSupplier(ExportTree et) {
			currentState.push(et);
		}
		
		@Override
		public ExportedGate get() {
			while(!currentState.isEmpty()) {
				ExportTree tree = currentState.peak();
				Queue<ExportNode> nodes = tree.exportNodes;
				
				if(nodes.isEmpty()) {
					currentState.pop();
					continue;
				}
				
				ExportNode n = nodes.dequeue();
				
				doActionToNode(tree, n);
				
				if(n instanceof ExportTree) {
					currentState.push((ExportTree) n);
					continue;
				} else {
					return export((ExportLeaf) n);
				}
			}
			return null;
		}
		public abstract void doActionToNode(ExportTree previous, ExportNode next);
		public abstract ExportedGate export(ExportLeaf leaf);
		
	}
	
	
	
	
		
	private static ExportTree startScanAndGetExportStream (String circuitboardName, MathSet runtimeVariables) throws ExportException {
		
		Project p = AppStatus.get().getFocusedProject();
		CircuitBoardModel cb = (CircuitBoardModel) p.getGateModel(circuitboardName);
		
		if(cb == null) throw new ExportException("Gate \"" + circuitboardName + "\" is not valid or does not exist");
		
		Hashtable<Integer, Integer> registers = new Hashtable<>();
		for(int i = 0; i < cb.getRows(); i++)
			registers.put(i, i);
		
		return scanCB(p, cb, runtimeVariables, null);
	}
	
	
	
	
	
	
	
	
	private static ExportTree scanCB (Project p, CircuitBoardModel cb, MathSet runtimeVariables, RawExportableGateData data) throws ExportException {
		
		Queue<ExportNode> nodes = new Queue<>();
		
		for(RawExportableGateData rawData : cb) {
			SolderedGate sg = rawData.getSolderedGate();
			GateModel gm = p.getGateModel(sg.getGateModelFormalName());
			
			if(gm == null) throw new ExportException("Gate \"" + sg.getGateModelFormalName() + 
					"\" is not valid or does not exist in \"" + cb.getFormalName() + "\"");
			

			GroupDefinition        parameters = sg.getParameterSet();
			ImmutableArray<String> arguments = gm.getArguments();
			
			
			if(arguments.size() != parameters.getSize()) {
				throw new ExportException("Gate \"" + sg.getGateModelFormalName() + "\" in \"" 
											+ cb.getFormalName() + "\" are missing necessary arguments");
			}
			
			if(rawData.getRegisters().size() != gm.getNumberOfRegisters()) {
				throw new ExportException("Gate \"" + sg.getGateModelFormalName() + "\" in \"" 
						+ cb.getFormalName() + "\" is not the appropriate size");
			}
			
			int i = 0;
			Complex c;
			
			ExportNode n = null;
			MathSet ms = new MathSet(MathDefintions.GLOBAL_DEFINITIONS);
			
			try {
				if(gm instanceof CircuitBoardModel) {
					for(MathObject mo : parameters.getMathDefinitions()) {
						if(mo.isMatrix())
							throw new ExportException("Gate \"" + sg.getGateModelFormalName() + "\" in \"" 
									+ cb.getFormalName() + "\" cannot not pass a matrix in parameter " + i);
						
						if(mo.hasArguments())
							c = (Complex) ((ArgObject) mo).getDefinition().compute(runtimeVariables);
						else
							c = (Complex) ((ScalarObject) mo).getScalar();
						
						ms.addVariable(new ConcreteVariable(arguments.get(i++), c));
					}
					n = scanCB(p, (CircuitBoardModel) gm, ms, rawData);
				} else {
					Hashtable<String, Complex> argParamTable = new Hashtable<>();
					for(MathObject mo : parameters.getMathDefinitions()) {
						if(mo.isMatrix())
							throw new ExportException("Gate \"" + sg.getGateModelFormalName() + "\" in \"" 
									+ cb.getFormalName() + "\" cannot not pass a matrix in parameter " + i);
						
						if(mo.hasArguments())
							c = (Complex) ((ArgObject) mo).getDefinition().compute(runtimeVariables);
						else
							c = (Complex) ((ScalarObject) mo).getScalar();
						
						argParamTable.put(arguments.get(i), c);
						ms.addVariable(new ConcreteVariable(arguments.get(i++), c));
					}
					n = new ExportLeaf(argParamTable, gm, ms, rawData);
				}
				
			} catch (EvaluateExpressionException e) {
				throw new ExportException("Gate \"" + sg.getGateModelFormalName() + "\" in \"" 
						+ cb.getFormalName() + "\" could not evaluate parameter " + i + " due to: " + e.getMessage());
			}
			
			
			nodes.enqueue(n);
		}
		
		return new ExportTree(nodes, data);
	}
	
	
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class ExportException extends Exception {
		private ExportException (String message) {
			super(message);
		}
	}
	
	
	
	
	
	
	
	private static class ExportTree extends ExportNode {
		
		final Queue<ExportNode> exportNodes;
		
		public ExportTree(Queue<ExportNode> exportStates, RawExportableGateData rawData) {
			super(rawData);
			this.exportNodes = exportStates;
		}
		
		
	}
	
	
	
	
	
	
	
	private abstract static class ExportNode {
		final RawExportableGateData rawData;
		
		public ExportNode (RawExportableGateData rawData) {
			this.rawData = rawData;
		}
	}
	
	
	
	
	
	
	private static class ExportLeaf extends ExportNode {
		final Hashtable<String, Complex> parameters;
		final GateModel gm;
		final MathSet runtimeVariables;
		
		private ExportLeaf(Hashtable<String, Complex> parameters, GateModel gm, MathSet runtimeVariables, RawExportableGateData rawData) {
			super(rawData);
			this.runtimeVariables = runtimeVariables;
			this.parameters = parameters;
			this.gm = gm;
		}
	}
	
	
}
