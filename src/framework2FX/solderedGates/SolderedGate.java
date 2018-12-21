package framework2FX.solderedGates;

import java.io.Serializable;

import appUIFX.AppAlerts;
import framework2FX.AppStatus;
import framework2FX.UserDefinitions;
import framework2FX.UserDefinitions.ArgDefinition;
import framework2FX.UserDefinitions.CheckDefinitionRunnable;
import framework2FX.UserDefinitions.DefinitionEvaluatorException;
import framework2FX.UserDefinitions.GroupDefinition;
import framework2FX.UserDefinitions.MatrixDefinition;
import framework2FX.UserDefinitions.ScalarDefinition;
import framework2FX.gateModels.PresetGateType;
import utils.customCollections.Manifest.ManifestObject;



public class SolderedGate implements Serializable, CheckDefinitionRunnable {
	private static final long serialVersionUID = 2595030500395644473L;
	
	@SuppressWarnings("rawtypes")
	private ManifestObject gateModelFormalName;
	private final GroupDefinition parameterSet;
	
	@SuppressWarnings("rawtypes")
	public SolderedGate(ManifestObject gateModelFormalName, String ... parameters) throws DefinitionEvaluatorException {
		this.gateModelFormalName = gateModelFormalName;
		parameterSet = UserDefinitions.evaluateInput(this, parameters); 
	}
	
	public String getGateModelFormalName() {
		return (String) gateModelFormalName.getObject();
	}
	
	public GroupDefinition getParameterSet() {
		return parameterSet;
	}
	
	public boolean isIdentity() {
		return getGateModelFormalName().equals(PresetGateType.IDENTITY.getModel().getFormalName());
	}
	
	@Override
	public void checkScalarDefinition(ScalarDefinition definition, int i) {}

	@Override
	public void checkMatrixDefinition(MatrixDefinition definition, int i) {
		throw new RuntimeException("Definition should not define a matrix");
	}

	@Override
	public void checkArgDefinition(ArgDefinition definition, int i) {
		if(definition.isMatrix())
			throw new RuntimeException("Definition should not define a matrix");
	}
	
}
