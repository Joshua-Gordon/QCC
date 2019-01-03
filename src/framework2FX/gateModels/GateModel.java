package framework2FX.gateModels;

import java.io.Serializable;

import utils.customCollections.ImmutableArray;

public abstract class GateModel implements Serializable {
	private static final long serialVersionUID = 3195910933230664750L;
	
	public static final String NAME_REGEX = "[a-zA-Z][\\w]*";
	public static final String SYMBOL_REGEX = "[a-zA-Z][\\w\\s]*";
	
	public static final String INPROPER_NAME_SCHEME_MSG = "Name must be a letter followed by letters, digits, or underscores";
	public static final String INPROPER_SYMBOL_SCHEME_MSG = "Name must be a letter followed by letters, digits, or underscores";
	
	private final String name;
	private final String symbol;
	private final String description;
	
	public GateModel (String name, String symbol, String description) {
		if(name == null) {
			throw new InproperNameSchemeException("Name must be defined");
		} else if(!name.matches(NAME_REGEX)) {
			throw new InproperNameSchemeException(INPROPER_NAME_SCHEME_MSG);
		}
		
		if(symbol == null) {
			throw new InproperNameSchemeException("Symbol must be defined");
		} else if(!symbol.matches(SYMBOL_REGEX)) {
			throw new InproperNameSchemeException(INPROPER_SYMBOL_SCHEME_MSG);
		}
		
		this.name = name.trim();
		this.symbol = symbol.trim();
		this.description = description.trim();
	}
	
	public abstract int getNumberOfRegisters();
	public abstract ImmutableArray<String> getArguments();
	public abstract String getExtString();
	public abstract boolean isPreset();
	public abstract GateModel getAsNewModel(String name, String symbol, String description);
	
	
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
