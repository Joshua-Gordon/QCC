package appFX.framework.gateModels;

public class OracleModel extends GateModel {
	private static final long serialVersionUID = -3693956684613494709L;
	
	public static final String ORACLE_MODEL_EXTENSION = "om";
	
	public OracleModel(String name, String symbol, String description, String... arguments) {
		super(name, symbol, description, arguments);
	}

	@Override
	public int getNumberOfRegisters() {
		return 0;
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
	public GateModel shallowCopyToNewName(String name, String symbol, String description, String... arguments) {
		return new OracleModel(name, symbol, description, arguments);
	}
	
}
