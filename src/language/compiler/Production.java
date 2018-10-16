package language.compiler;

import java.util.LinkedList;
import java.util.ListIterator;

public class Production {
	private Terminal leftSide;
	private LinkedList<ProductionSymbol> rightSideSymbols;
	
	public Production (Terminal leftSide, ProductionSymbol ... rightSideSymbols) {
		this.rightSideSymbols = new LinkedList<>();
		ListIterator<ProductionSymbol> iterator = this.rightSideSymbols.listIterator();
		
		while(iterator.nextIndex() != rightSideSymbols.length)
			iterator.add(rightSideSymbols[iterator.nextIndex() - 1]);
	}
}
