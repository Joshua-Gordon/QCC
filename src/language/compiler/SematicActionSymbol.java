package language.compiler;

import java.util.Stack;

public class SematicActionSymbol extends ProductionSymbol {

	@Override
	public SymbolType getType() {
		return SymbolType.SEMATIC_ACTION;
	}
	
	public SematicActionSymbol (SematicAction action) {
		
	}
	
	public static interface SematicAction {
		public void action(Stack<Object> valueStack);
	}
}
