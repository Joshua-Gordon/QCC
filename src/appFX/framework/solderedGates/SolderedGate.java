package appFX.framework.solderedGates;

import java.io.Serializable;

import appFX.framework.InputDefinitions;
import appFX.framework.InputDefinitions.ArgDefinition;
import appFX.framework.InputDefinitions.CheckDefinitionRunnable;
import appFX.framework.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.InputDefinitions.GroupDefinition;
import appFX.framework.InputDefinitions.MatrixDefinition;
import appFX.framework.InputDefinitions.ScalarDefinition;
import appFX.framework.gateModels.PresetGateType;
import utils.customCollections.Manifest.ManifestObject;



public class SolderedGate implements Serializable, CheckDefinitionRunnable {
	private static final long serialVersionUID = 2595030500395644473L;
	
	@SuppressWarnings("rawtypes")
	private ManifestObject gateModelFormalName;
	private final GroupDefinition parameterSet;
	
	@SuppressWarnings("rawtypes")
	public SolderedGate(ManifestObject gateModelFormalName, String ... parameters) throws DefinitionEvaluatorException {
		this.gateModelFormalName = gateModelFormalName;
		parameterSet = InputDefinitions.evaluateInput(this, parameters); 
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
	public void checkMatrixDefinition(MatrixDefinition definition, int i) throws DefinitionEvaluatorException {
		throw new DefinitionEvaluatorException("Definition should not define a matrix", i);
	}

	@Override
	public void checkArgDefinition(ArgDefinition definition, int i) throws DefinitionEvaluatorException {
		if(definition.isMatrix())
			throw new DefinitionEvaluatorException("Definition should not define a matrix", i);
	}
	
}
