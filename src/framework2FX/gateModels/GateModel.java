package framework2FX.gateModels;

import java.io.Serializable;

import utils.customCollections.ImmutableArray;

public abstract class GateModel implements Serializable {
	private static final long serialVersionUID = 3195910933230664750L;
	
	private final String name;
	private final String symbol;
	private final String description;
	
	public GateModel (String name, String symbol, String description) {
		if(name == null) {
			throw new InproperNameSchemeException("Name must be defined");
		} else if(!name.matches("[a-zA-Z][\\w]*")) {
			throw new InproperNameSchemeException("Name must be a letter followed by letters, digits, or underscores");
		}
		
		if(symbol == null) {
			throw new InproperNameSchemeException("Symbol must be defined");
		} else if(!symbol.matches("[a-zA-Z][\\w\\s]*")) {
			throw new InproperNameSchemeException("Symbol must be a letter followed by letters, digits, or underscores");
		}
		
		this.name = name;
		this.symbol = symbol;
		this.description = description;
	}
	
	public abstract int getNumberOfRegisters();
	public abstract ImmutableArray<String> getArguments();
	public abstract String getExtString();
	public abstract boolean isPreset();
	
	public String getFormalName() {
		return name + "." + getExtString();
	}
	
	public String getName() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getDescription() {
		return description;
	}
	
	@SuppressWarnings("serial")
	public class InproperNameSchemeException extends RuntimeException {
		private InproperNameSchemeException (String message) {
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class NameTakenException extends RuntimeException {
		public NameTakenException (String message) {
			super(message);
		}
	}
}
