package framework2FX.solderedGates;

import utils.customCollections.ImmutableArray;

public abstract class Solderable {
	private final String name;
	private final String symbol;
	private final String description;
	
	public Solderable (String name, String symbol, String description) {
		this.name = name;
		this.symbol = symbol;
		this.description = description;
	}
	
	public abstract int getNumberOfRegisters();
	public abstract ImmutableArray<String> getArguments();
	
	public String getName() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getDescription() {
		return description;
	}
}
