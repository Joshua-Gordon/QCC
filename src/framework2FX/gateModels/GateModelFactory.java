package framework2FX.gateModels;

import framework2FX.UserDefinitions;
import framework2FX.UserDefinitions.ArgDefinition;
import framework2FX.UserDefinitions.CheckDefinitionRunnable;
import framework2FX.UserDefinitions.DefinitionEvaluatorException;
import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MatrixDefinition;
import framework2FX.UserDefinitions.ScalarDefinition;
import framework2FX.gateModels.BasicModel.DefaultModelType;
import mathLib.Complex;
import mathLib.Matrix;

public class GateModelFactory {
	
	public static BasicModel makeGateModel(String name, String symbol, String description, DefaultModelType type, String ... userDefinitions) throws DefinitionEvaluatorException {
		return makeGateModel(name, symbol, description, type, null, userDefinitions);
	}
	
	static BasicModel makeGateModel(String name, String symbol, String description, DefaultModelType type, PresetGateType presetModel, String ... userDefinitions) throws DefinitionEvaluatorException {
		
		switch (type) {
		case HAMILTONIAN:
			RegularGateChecker rgc = new RegularGateChecker();
			
			GroupDefinition definitions = UserDefinitions.evaluateInput(rgc, new String[] {"t"}, userDefinitions);
			
			boolean containsArgH = false;
			for(String arg : definitions.getArguments()) {
				if(arg.equals("H")) {
					containsArgH = true;
					break;
				}
			}
			
			if(containsArgH)
				throw new RuntimeException("Variable \"H\" cannot bed used as a parameter");
			
			
			if(presetModel == null)
				return new BasicModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type);
			else
				return new PresetGateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type, presetModel);
		case POVM:
			rgc = new RegularGateChecker();
			definitions = UserDefinitions.evaluateInput(rgc, userDefinitions);
			
			
			String variable = null;
			for(String arg : definitions.getArguments()) {
				if(arg.matches("k_\\d")) {
					variable = arg;
					break;
				}
			}
			
			if(variable != null)
				throw new RuntimeException("Variable " + variable + " cannot bed used as a parameter");
			
			
			if(presetModel == null)
				return new BasicModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type);
			else
				return new PresetGateModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type, presetModel);
			
		case UNIVERSAL:
			if(userDefinitions.length != 1)
				throw new RuntimeException("There should be only one matrix to define this gate model");
			
			rgc = new RegularGateChecker();
			definitions = UserDefinitions.evaluateInput(rgc, userDefinitions);
			
			
			containsArgH = false;
			for(String arg : definitions.getArguments()) {
				if(arg.equals("U")) {
					containsArgH = true;
					break;
				}
			}
			
			if(containsArgH)
				throw new RuntimeException("Variable \"H\" cannot bed used as a parameter");
			
			
			if(presetModel == null)
				return new BasicModel(name, symbol, description, rgc.getNumberRegisters(), definitions, type);
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
	
	
	private static void checkSize(int rows, int columns, int definitionIndex) throws DefinitionEvaluatorException {
		if(rows != columns) {
			throw new DefinitionEvaluatorException("Number of rows must be equal to number of Columns", definitionIndex);
		} else {
			int size = rows;
			if((size & (size - 1)) != 0 || size < 2)
				throw new DefinitionEvaluatorException("The matrix size must greater than 1 and be some power of 2", definitionIndex);
		}
	}
	
	
	private static void checkSize(Matrix<Complex> mat , int definitionIndex) throws DefinitionEvaluatorException{
		checkSize(mat.getRows(), mat.getColumns(), definitionIndex);
	}
	
	
	
	public static class PresetGateModel extends BasicModel {
		private static final long serialVersionUID = 3655123001545022473L;
		
		private final PresetGateType presetModel;
		
		PresetGateModel(String name, String symbol, String description, int numberOfRegisters,
				GroupDefinition gateDefinition, DefaultModelType gateType, PresetGateType presetModel) {
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
		public void checkScalarDefinition(ScalarDefinition definition, int definitionIndex)  throws DefinitionEvaluatorException {
			throw new DefinitionEvaluatorException("Definition should not be scalar", definitionIndex);
		}

		@Override
		public void checkMatrixDefinition(MatrixDefinition definition, int definitionIndex)   throws DefinitionEvaluatorException{
			checkSize(definition.getMatrix(), definitionIndex);
			
			numReg = getNumberOfRegisters(definition.getMatrix());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new DefinitionEvaluatorException("Each matrix must be the same size", definitionIndex);
		}

		@Override
		public void checkArgDefinition(ArgDefinition definition, int definitionIndex)  throws DefinitionEvaluatorException {
			if(!definition.isMatrix())
				throw new DefinitionEvaluatorException("Definition should not be scalar", definitionIndex);
			
			checkSize(definition.getRows(), definition.getColumns(), definitionIndex);
			
			
			numReg = getNumberOfRegisters(definition.getRows());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new DefinitionEvaluatorException("Each matrix must be the same size", definitionIndex);
		}
		
		public int getNumberRegisters() {
			return numRegAll;
		}
	}
	
}
