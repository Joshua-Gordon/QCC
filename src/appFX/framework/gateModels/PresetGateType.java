package appFX.framework.gateModels;

import appFX.appUI.AppAlerts;
import appFX.framework.UserDefinitions.DefinitionEvaluatorException;
import appFX.framework.gateModels.BasicModel.BasicModelType;
import appFX.framework.gateModels.GateModel.NameTakenException;

public enum PresetGateType {
	
	IDENTITY ("Identity", "I", BasicModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, 1] "),
	
	HADAMARD ("Hadamard", "H", BasicModelType.UNIVERSAL ,
			"1/sqrt(2) * [1,  1; "
			+ 			" 1, -1] "),
	
	PAULI_X ("Pauli_x", "X", BasicModelType.UNIVERSAL ,
			  "[0, 1; "
			+ " 1, 0] "),
	
	PAULI_Y ("Pauli_y", "Y", BasicModelType.UNIVERSAL ,
			" [0, -i; "
			+ "i,  0] "),
	
	PAULI_Z ("Pauli_z", "Z", BasicModelType.UNIVERSAL ,
			  "[1,  0; "
			+ " 0, -1] "),
	
	PHASE ("Phase", "S", BasicModelType.UNIVERSAL ,
			  "[1, 0; "
			+ " 0, i] "),
	
	PI_ON_8 ("Pi_over_8", "T", BasicModelType.UNIVERSAL ,
			  "[1, 0; "
			+ " 0, (1+i) / sqrt(2)] "),
	
	SWAP ("Swap", "Swap", BasicModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 0, 1, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1] "),
	
	CNOT ("Cnot", "Cnot", BasicModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1; "
			+ "0, 0, 1, 0] "),
	
	TOFFOLI ("Toffoli", "Toffoli", BasicModelType.UNIVERSAL ,
			 "[1, 0, 0, 0, 0, 0, 0, 0; "
			+ "0, 1, 0, 0, 0, 0, 0, 0; "
			+ "0, 0, 1, 0, 0, 0, 0, 0; "
			+ "0, 0, 0, 1, 0, 0, 0, 0; "
			+ "0, 0, 0, 0, 1, 0, 0, 0; "
			+ "0, 0, 0, 0, 0, 1, 0, 0; "
			+ "0, 0, 0, 0, 0, 0, 0, 1; "
			+ "0, 0, 0, 0, 0, 0, 1, 0] "),
	
	PHASE_SHIFT ("Phase_Shift", "R", new String[]{"\\theta"} , BasicModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, exp(i * \\theta)] "),
	
	
	MEASUREMENT ("Measurement", "M", BasicModelType.POVM , 
			 "[1, 0; "
			+ "0, 0] ",
			
			  "[0, 0; "
			+ " 0, 1] "), 
	
	
	
	
	;
	
	
	public static void checkName(String name) {
		for(PresetGateType pgt : values())
			if(pgt.getModel().getName().equals(name)) 
				throw new NameTakenException("The name \"" + name + "\" is already a preset gate name and cannot be used");
	}
	
	
	public static PresetGateType getPresetTypeByFormalName (String name) {
		for(PresetGateType pgt : PresetGateType.values())
			if(pgt.gateModel.getFormalName().equals(name))
				return pgt;
		return null;
	}
	
	public static boolean containsPresetTypeByFormalName (String name) {
		for(PresetGateType pgt : PresetGateType.values())
			if(pgt.gateModel.getFormalName().equals(name))
				return true;
		return false;
	}
	
	
	private final BasicModel gateModel;
	
	private PresetGateType(String name, String symbol, String description, BasicModelType type, String ... expression) {
		this(name, symbol, description, new String[0], type, expression);
	}
	
	private PresetGateType(String name, String symbol, String description, String[] parameters, BasicModelType type, String ... expression) {
		BasicModel gm = null;
		try {
			gm = new PresetGateModel(name, symbol, description, parameters, type, this, expression);
		} catch (Exception e) {
			AppAlerts.showJavaExceptionMessage(null, "Program Crashed", "Could not make preset " + name + " gate model", e);
			e.printStackTrace();
			System.exit(1);
		} finally {
			this.gateModel = gm;
		}
	}
	
	private PresetGateType(String name, String symbol, BasicModelType type, String ... expression) {
		this(name, symbol, "", type, expression);
	}
	
	private PresetGateType(String name, String symbol, String[] parameters, BasicModelType type, String ... expression) {
		this(name, symbol, "", parameters, type, expression);
	}
	
	public BasicModel getModel() {
		return gateModel;
	}
	
	
	
	public static class PresetGateModel extends BasicModel {
		private static final long serialVersionUID = 3655123001545022473L;
		
		private final PresetGateType presetModel;
		
		PresetGateModel(String name, String symbol, String description, String[] parameters, BasicModelType gateType, PresetGateType presetModel, String ... userDefinitions) 
				throws DefinitionEvaluatorException {
			super(name, symbol, description, parameters, gateType, userDefinitions);
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
}
