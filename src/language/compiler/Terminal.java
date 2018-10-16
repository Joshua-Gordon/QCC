package language.compiler;

public class Terminal extends ProductionSymbol {

	@Override
	public SymbolType getType() {
		return SymbolType.TERMINAL;
	}
	
}
