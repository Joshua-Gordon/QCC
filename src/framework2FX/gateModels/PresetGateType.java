package framework2FX.gateModels;

import appUIFX.AppAlerts;
import framework2FX.gateModels.BasicModel.DefaultModelType;
import framework2FX.gateModels.GateModel.NameTakenException;

public enum PresetGateType {
	
	IDENTITY ("Identity", "I", DefaultModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, 1] "),
	
	HADAMARD ("Hadamard", "H", DefaultModelType.UNIVERSAL ,
			"1/sqrt(2) * [1,  1; "
			+ 			" 1, -1] "),
	
	PAULI_X ("Pauli_x", "X", DefaultModelType.UNIVERSAL ,
			  "[0, 1; "
			+ " 1, 0] "),
	
	PAULI_Y ("Pauli_y", "Y", DefaultModelType.UNIVERSAL ,
			" [0, -i; "
			+ "i,  0] "),
	
	PAULI_Z ("Pauli_z", "Z", DefaultModelType.UNIVERSAL ,
			  "[1,  0; "
			+ " 0, -1] "),
	
	PHASE ("Phase", "S", DefaultModelType.UNIVERSAL ,
			  "[1, 0; "
			+ " 0, i] "),
	
	PI_ON_8 ("Pi_over_8", "T", DefaultModelType.UNIVERSAL ,
			  "[1, 0; "
			+ " 0, (1+i) / sqrt(2)] "),
	
	SWAP ("Swap", "Swap", DefaultModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 0, 1, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1] "),
	
	CNOT ("Cnot", "Cnot", DefaultModelType.UNIVERSAL ,
			 "[1, 0, 0, 0; "
			+ "0, 1, 0, 0; "
			+ "0, 0, 0, 1; "
			+ "0, 0, 1, 0] "),
	
	TOFFOLI ("Toffoli", "Toffoli", DefaultModelType.UNIVERSAL ,
			 "[1, 0, 0, 0, 0, 0, 0, 0; "
			+ "0, 1, 0, 0, 0, 0, 0, 0; "
			+ "0, 0, 1, 0, 0, 0, 0, 0; "
			+ "0, 0, 0, 1, 0, 0, 0, 0; "
			+ "0, 0, 0, 0, 1, 0, 0, 0; "
			+ "0, 0, 0, 0, 0, 1, 0, 0; "
			+ "0, 0, 0, 0, 0, 0, 0, 1; "
			+ "0, 0, 0, 0, 0, 0, 1, 0] "),
	
	PHASE_SHIFT ("Phase_Shift", "R", DefaultModelType.UNIVERSAL ,
			 "[1, 0; "
			+ "0, exp(i * \\theta)] "),
	
	
	MEASUREMENT ("Measurement", "M", DefaultModelType.POVM , 
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
	
	private PresetGateType(String name, String symbol, String description, DefaultModelType type, String ... expression) {
		BasicModel gm = null;
		try {
			gm = GateModelFactory.makeGateModel(name, symbol, description, type, this, expression);
		} catch (Exception e) {
			AppAlerts.showJavaExceptionMessage(null, "Program Crashed", "Could not make preset " + name + " gate model", e);
			e.printStackTrace();
			System.exit(1);
		} finally {
			this.gateModel = gm;
		}
	}
	
	private PresetGateType(String name, String symbol, DefaultModelType type, String ... expression) {
		this(name, symbol, "", type, expression);
	}
	
	public BasicModel getModel() {
		return gateModel;
	}
}
