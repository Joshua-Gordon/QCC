package language.compiler;

import language.compiler.ProductionSymbol.NonTerminal;

public class Production {
	private final NonTerminal leftSide;
	private final ProductionSymbol[] rightSideSymbols;
	
	public Production (NonTerminal leftSide, ProductionSymbol ... rightSideSymbols) {
		this.leftSide = leftSide;
		this.rightSideSymbols = rightSideSymbols;
	}
	
	public NonTerminal getLeftSide () {
		return leftSide;
	}
	
	public ProductionSymbol[] getRightSideSymbols () {
		return rightSideSymbols;
	}
}
