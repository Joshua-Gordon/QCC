package framework2FX.gateModels;

import framework2FX.gateModels.GateModel.GateModelType;

public enum PresetGateType {
	
	IDENTITY ("Identity", "I", GateModelType.REGULAR_GATE ,
			 "[1, 0; "
			+ "0, 1]"),
	
	HADAMARD ("Hadamard", "H", GateModelType.REGULAR_GATE ,
			"1/sqrt(2) * [1,  1;"
			+ 			" 1, -1]"),
	
	PAULI_X ("Pauli x", "X", GateModelType.REGULAR_GATE ,
			  "[0, 1;"
			+ " 1, 0]"),
	
	PAULI_Y ("Pauli y", "Y", GateModelType.REGULAR_GATE ,
			" [0, -i;"
			+ "i,  0]"),
	
	PAULI_Z ("Pauli z", "Z", GateModelType.REGULAR_GATE ,
			  "[1,  0;"
			+ " 0, -1]"),
	
	PHASE ("Phase", "S", GateModelType.REGULAR_GATE ,
			  "[1, 0;"
			+ " 0, i]"),
	
	PI_ON_8 ("Pi/8", "T", GateModelType.REGULAR_GATE ,
			  "[1,              0;"
			+ " 0, (1+i) / sqrt(2)]"),
	
	SWAP ("Swap", "", GateModelType.REGULAR_GATE ,
			 "[1, 0, 0, 0;"
			+ "0, 0, 1, 0;"
			+ "0, 1, 0, 0;"
			+ "0, 0, 0, 1]"),
	
	CNOT ("Cnot", "", GateModelType.REGULAR_GATE ,
			 "[1, 0, 0, 0;"
			+ "0, 1, 0, 0;"
			+ "0, 0, 0, 1;"
			+ "0, 0, 1, 0]"),
	
	TOFFOLI ("Toffoli", "", GateModelType.REGULAR_GATE ,
			 "[1, 0, 0, 0, 0, 0, 0, 0;"
			+ "0, 1, 0, 0, 0, 0, 0, 0;"
			+ "0, 0, 1, 0, 0, 0, 0, 0;"
			+ "0, 0, 0, 1, 0, 0, 0, 0;"
			+ "0, 0, 0, 0, 1, 0, 0, 0;"
			+ "0, 0, 0, 0, 0, 1, 0, 0;"
			+ "0, 0, 0, 0, 0, 0, 0, 1;"
			+ "0, 0, 0, 0, 0, 0, 1, 0]"),
	
	PHASE_SHIFT ("Phase Shift", "R", GateModelType.REGULAR_GATE ,
			 "[1,             0;"
			+ "0, exp(i * theta)]"),
	
	
	MEASUREMENT ("Measurement", "M", GateModelType.POVM , 
			 "[1, 0; "
			+ "0, 0]",
			
			  "[0, 0;"
			+ " 0, 1]"), 
	
	
	
	
	;
	
	
	
	private final GateModel gateModel;
	
	private PresetGateType(String name, String symbol, String description, GateModelType type, String ... expression) {
		this.gateModel = GateModelFactory.makeGateModel(name, symbol, description, type, this, expression);
	}
	
	private PresetGateType(String name, String symbol, GateModelType type, String ... expression) {
		this(name, symbol, "", type, expression);
	}
	
	public GateModel getModel() {
		return gateModel;
	}
}
