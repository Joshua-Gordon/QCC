package framework2FX.solderedGates;

import utils.customCollections.ImmutableArray;

public abstract class Solderable {
	
	public abstract int getNumberOfRegisters();
	public abstract ImmutableArray<String> getArguments();
	
}
