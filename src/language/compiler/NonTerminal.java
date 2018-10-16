package language.compiler;

public class NonTerminal extends ProductionSymbol {

	@Override
	public SymbolType getType() {
		return SymbolType.NON_TERMINAL;
	}
	
}
