package javafx.framework.gateModels;

import java.io.Serializable;

import utils.customCollections.ImmutableArray;

public abstract class GateModel implements Serializable {
	private static final long serialVersionUID = 3195910933230664750L;
	
	public static final String NAME_REGEX = "[a-zA-Z][\\w]*";
	public static final String SYMBOL_REGEX = "[a-zA-Z][\\w\\s]*";
	public static final String PARAMETER_REGEX = "\\\\?[a-zA-Z][\\w]*";
	
	public static final String IMPROPER_NAME_SCHEME_MSG = "Name must be a letter followed by letters, digits, or underscores";
	public static final String IMPROPER_SYMBOL_SCHEME_MSG = "Symbol name must be a letter followed by letters, digits, or underscores";
	public static final String IMPROPER_PARAMETER_SCHEME_MSG = "Parameter name must be a letter followed "
			+ "by letters, digits, or underscores. For special mathematical symbols, the \"\\\" character"
			+ "can be used to escape the name to use the proper mathematical symbol";
	
	private final String name;
	private final String symbol;
	private final String description;
	private final String[] arguments;
	
	public GateModel (String name, String symbol, String description, String ... arguments) {
		if(name == null) {
			throw new ImproperNameSchemeException("Name must be defined");
		} else if(!name.matches(NAME_REGEX)) {
			throw new ImproperNameSchemeException(IMPROPER_NAME_SCHEME_MSG);
		}
		
		if(symbol == null) {
			throw new ImproperNameSchemeException("Symbol must be defined");
		} else if(!symbol.matches(SYMBOL_REGEX)) {
			throw new ImproperNameSchemeException(IMPROPER_SYMBOL_SCHEME_MSG);
		}
		
		this.name = name.trim();
		this.symbol = symbol.trim();
		this.description = description.trim();
		this.arguments = arguments;
		
		int i = 0;
		for(String arg : arguments) {
			if(arg == null) {
				throw new ImproperNameSchemeException("Symbol must be defined");
			} else if(!symbol.matches(PARAMETER_REGEX)) {
				throw new ImproperNameSchemeException(IMPROPER_PARAMETER_SCHEME_MSG);
			}
			for(int j = 0; j < i; j++)
				if(arg.equals(arguments[j]))
					throw new IllegalArgumentException("There are two parameters with the same name");
			i++;
		}
	}
	
	public abstract int getNumberOfRegisters();
	public abstract String getExtString();
	public abstract boolean isPreset();
	public abstract GateModel shallowCopyToNewName(String name, String symbol, String description, String ... parameters);
	
	public GateModel shallowCopyToNewName(String name, String symbol, String description) {
		ImmutableArray<String> array = getArguments();
		return shallowCopyToNewName(name, symbol, description, array.toArray(new String[array.size()]));
	}

	public ImmutableArray<String> getArguments() {
		return new ImmutableArray<>(arguments);
	}
	
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
	public class ImproperNameSchemeException extends RuntimeException {
		private ImproperNameSchemeException (String message) {
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
