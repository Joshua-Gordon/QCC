package language.compiler;

import java.util.LinkedList;

import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.ProductionSymbol.Terminal;

public class ParseTree {
	private ParseNode root;
	
	public ParseTree (ParseNode root) {
		this.root = root;
	}
	
	public ParseNode getRoot() {
		return root;
	}
	
	public static abstract class ParseNode {
		private ProductionSymbol symbol;
		
		public ParseNode (ProductionSymbol symbol) {
			this.symbol = symbol;
		}
		
		public abstract boolean isLeaf();
				
		public ProductionSymbol getProductionSymbol() {
			return symbol;
		}
		
	}
	
	public static class ParseBranch extends ParseNode {
		LinkedList<ParseNode> children = new LinkedList<>();
		
		public ParseBranch(NonTerminal nonTerminal) {
			super(nonTerminal);
		}
		
		public void addChild(ParseNode node) {
			children.offerLast(node);
		}
		
		public LinkedList<ParseNode> getChildren () {
			return children;
		}

		@Override
		public boolean isLeaf() {
			return false;
		}
	}
	
	
	public static class ParseLeaf extends ParseNode {
		private String value;
		
		public ParseLeaf(Terminal terminal, String value) {
			super(terminal);
		}

		@Override
		public boolean isLeaf() {
			return true;
		}
	}
	
}
