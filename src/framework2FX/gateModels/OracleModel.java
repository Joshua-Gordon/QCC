package framework2FX.gateModels;

import utils.customCollections.ImmutableArray;

public class OracleModel extends GateModel {
	private static final long serialVersionUID = -3693956684613494709L;
	
	public static final String ORACLE_MODEL_EXTENSION = "om";
	
	public OracleModel(String name, String symbol, String description) {
		super(name, symbol, description);
	}

	@Override
	public int getNumberOfRegisters() {
		return 0;
	}

	@Override
	public ImmutableArray<String> getArguments() {
		return null;
	}

	@Override
	public String getExtString() {
		return ORACLE_MODEL_EXTENSION;
	}

	@Override
	public boolean isPreset() {
		return false;
	}

	@Override
	public GateModel getAsNewModel(String name, String symbol, String description) {
		return new OracleModel(name, symbol, description);
	}
	
}
