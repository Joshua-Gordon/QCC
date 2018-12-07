package framework2FX.gateModels;

import framework2FX.UserDefinitions;
import framework2FX.UserDefinitions.ArgDefinition;
import framework2FX.UserDefinitions.CheckDefinitionRunnable;
import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MatrixDefinition;
import framework2FX.UserDefinitions.ScalarDefinition;
import framework2FX.gateModels.GateModel.GateModelType;
import framework2FX.gateModels.GateModel.InvalidGateModelMatrixException;
import mathLib.Complex;
import mathLib.Matrix;

public class GateModelFactory {
	
	public static GateModel makeGateModel(String name, String symbol, String description, GateModelType type, String ... userDefinitions) {
		return makeGateModel(name, symbol, description, type, null, userDefinitions);
	}
	
	static GateModel makeGateModel(String name, String symbol, String description, GateModelType type, PresetGateType presetModel, String ... userDefinitions) {
		
		switch (type) {
		case HAMILTONIAN:
			RegularGateChecker rgc = new RegularGateChecker();
			
			GroupDefinition definitions = UserDefinitions.evaluateInput(rgc, new String[] {"t"}, userDefinitions);
			
			if(presetModel == null)
				return new GateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type);
			else
				return new PresetGateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type, presetModel);
		case ORACLE:
			break;
		case POVM:
			rgc = new RegularGateChecker();
			definitions = UserDefinitions.evaluateInput(rgc, userDefinitions);
			
			if(presetModel == null)
				return new GateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type);
			else
				return new PresetGateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type, presetModel);
			
		case REGULAR_GATE:
			if(userDefinitions.length != 1)
				throw new RuntimeException("There should be only one matrix to define this gate model");
			
			rgc = new RegularGateChecker();
			definitions = UserDefinitions.evaluateInput(rgc, userDefinitions);
			
			if(presetModel == null)
				return new GateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type);
			else
				return new PresetGateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type, presetModel);
			
		default:
			break;	
		}
		
		
		return null;
	}
	
	
	
	
	static int getNumberOfRegisters (int matrixSize) {
		int i = 0;
		while(((matrixSize >> (++i)) | 1) != 1);
		return i;
	}
	
	
	
	static int getNumberOfRegisters (Matrix<Complex> matrix) {
		return getNumberOfRegisters(matrix.getRows());
	}
	
	
	private static void checkSize(int rows, int columns) {
		if(rows != columns) {
			throw new InvalidGateModelMatrixException("Number of rows must be equal to number of Columns");
		} else {
			int size = rows;
			if((size & (size - 1)) != 0 || size < 2)
				throw new InvalidGateModelMatrixException("The matrix size must greater than 1 and be some power of 2");
		}
	}
	
	
	private static void checkSize(Matrix<Complex> mat) {
		checkSize(mat.getRows(), mat.getColumns());
	}
	
	
	
	public static class PresetGateModel extends GateModel {

		private static final long serialVersionUID = 3655123001545022473L;
		
		private final PresetGateType presetModel;
		
		PresetGateModel(String name, String symbol, String description, int numberOfRegisters,
				GroupDefinition gateDefinition, GateModelType gateType, PresetGateType presetModel) {
			super(name, symbol, description, numberOfRegisters, gateDefinition, gateType);
			this.presetModel = presetModel;
		}
		
		@Override
		public boolean isPreset() {
			return true;
		}
		
		public PresetGateType getPresetGateType() {
			return presetModel;
		}
	}
	
	
	public static class RegularGateChecker implements CheckDefinitionRunnable {
		
		private Integer numRegAll = null;
		private Integer numReg = null;
		
		@Override
		public void checkScalarDefinition(ScalarDefinition definition) {
			throw new RuntimeException("Definition should not be scalar");
		}

		@Override
		public void checkMatrixDefinition(MatrixDefinition definition) {
			checkSize(definition.getMatrix());
			
			numReg = getNumberOfRegisters(definition.getMatrix());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new RuntimeException("Each matrix muxt be the same size");
		}

		@Override
		public void checkArgDefinition(ArgDefinition definition) {
			if(!definition.isMatrix())
				throw new RuntimeException("Definition should not be scalar");
			checkSize(definition.getRows(), definition.getColumns());
			
			numReg = getNumberOfRegisters(definition.getRows());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new RuntimeException("Each matrix muxt be the same size");
		}
		
		public int getNumberRegisters() {
			return numRegAll;
		}
	}
}
