package language.compiler;

public abstract class ProductionSymbol {
	
	public abstract SymbolType getType();
	
	
	public static enum SymbolType {
		TERMINAL, NON_TERMINAL, SEMATIC_ACTION;
	}
	
	
	
	
	public static class NonTerminal extends ProductionSymbol {
		private final String name;
		
		public NonTerminal(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		
		@Override
		public SymbolType getType() {
			return SymbolType.NON_TERMINAL;
		}
	}
	
	
	
	
	
	public static abstract class Terminal extends ProductionSymbol {
		@Override
		public SymbolType getType() {
			return SymbolType.TERMINAL;
		}
	}

	
	
	
	
	
	public static class SematicActionSymbol extends Terminal {
		private SematicAction sematicAction;
		
		@Override
		public SymbolType getType() {
			return SymbolType.SEMATIC_ACTION;
		}
		
		public SematicActionSymbol (SematicAction sematicAction) {
			this.sematicAction = sematicAction;
		}
		
		public SematicAction getAction() {
			return sematicAction;
		}
		
		public static interface SematicAction {
			public void action();
		}
	}
	
	
}
