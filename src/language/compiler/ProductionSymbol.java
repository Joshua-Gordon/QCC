package language.compiler;


/**
 * This class represents all the Generic symbols that may be found in a Context Free Grammer
 * @author Massimiliano Cutugno
 *
 */
public abstract class ProductionSymbol {
	
	/** 
	 * @return the type of this symbol
	 */
	public abstract SymbolType getType();
	
	/**
	 * This reprsents the types of symbols in a context free grammer
	 * @author Massimiliano Cutugno
	 *
	 */
	public static enum SymbolType {
		TERMINAL, NON_TERMINAL, SEMATIC_ACTION;
	}
	
	
	/**
	 * This class represents a {@link NonTerminal} symbol in a context free grammer
	 * 
	 * @author Massimiliano Cutugno
	 */
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
	
	
	
	/**
	 * This class represents a {@link Terminal} Symbol in a context free grammer
	 * @author Massimiliano Cutugno
	 *
	 */
	public static abstract class Terminal extends ProductionSymbol {
		
		@Override
		public SymbolType getType() {
			return SymbolType.TERMINAL;
		}
	}

	
	
	/**
	 * This class represents a sematic action symbol in a contect free grammer
	 * @author Massimiliano Cutugno
	 *
	 */
	public static class SematicActionSymbol extends Terminal {
		private SematicAction sematicAction;
		
		@Override
		public SymbolType getType() {
			return SymbolType.SEMATIC_ACTION;
		}
		
		/**
		 * Sets the action of this sematic action and is called when parsing through a
		 * syntax driected translator
		 * 
		 * @param sematicAction
		 */
		public SematicActionSymbol (SematicAction sematicAction) {
			this.sematicAction = sematicAction;
		}
		
		/**
		 * @return the action assocaited with this sematic action symbol
		 */
		public SematicAction getAction() {
			return sematicAction;
		}
		
		/**
		 * This represents the sematic action and is used closely 
		 * and defined with a sematic action symbol
		 * 
		 * @author Massimiliano Cutugno
		 */
		public static interface SematicAction {
			public void action();
		}
	}
	
	
}
