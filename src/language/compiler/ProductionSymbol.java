package language.compiler;

public abstract class ProductionSymbol {
	
	public abstract SymbolType getType();
	
	public static enum SymbolType {
		TERMINAL, NON_TERMINAL, SEMATIC_ACTION;
	}
}
