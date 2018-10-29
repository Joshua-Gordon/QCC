package language.compiler;

import java.util.Iterator;
import java.util.LinkedList;

import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.ProductionSymbol.Terminal;


/**
 * This class creates structures for representing parse trees for parsing
 * @author Massimiliano Cutugno
 *
 */
public class ParseTree {
	private ParseNode root;
	
	
	/**
	 * Creates a parse tree with a given node
	 * @param root
	 */
	public ParseTree (ParseNode root) {
		this.root = root;
	}
	
	/**
	 * @return the root of this tree
	 */
	public ParseNode getRoot() {
		return root;
	}
	
	/**
	 * Represents a Node within a Parse Tree
	 * @author Massimiliano Cutugno
	 *
	 */
	public static abstract class ParseNode {
		private ProductionSymbol symbol;
		
		public ParseNode (ProductionSymbol symbol) {
			this.symbol = symbol;
		}
		
		/**
		 * @return whether this Node is a leaf or a branch
		 */
		public abstract boolean isLeaf();
				
		public ProductionSymbol getProductionSymbol() {
			return symbol;
		}
		
	}
	
	/**
	 * Represents a {@link ParseNode} that has branches
	 * @author Massimiliano Cutugno
	 *
	 */
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
	
	/**
	 * Represents a {@link ParseNode} that is a lead with the parse tree
	 * @author Massimiliano Cutugno
	 *
	 */
	public static class ParseLeaf extends ParseNode {
		private String value;
		
		public ParseLeaf(Terminal terminal, String value) {
			super(terminal);
			this.value = value;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	@Override
	public String toString() {
		if(root == null)
			return "null";
		
		String format = "";
		
		if(root.isLeaf())
			format += leafToString((ParseLeaf) root);
		else
			format += branchToString((ParseBranch) root, "");
		
		return format;
	}
	
	private String leafToString(ParseLeaf node) {
		return node.value;
	}

	private String branchToString (ParseBranch node, String formatOffset) {
		String format = ((NonTerminal)node.getProductionSymbol()).getName();
		String formatOffsetNew = formatOffset  + "|       "; // branches only;
		String formatOffsetLast = formatOffset + "        "; // branches only
		
		Iterator<ParseNode> nodes = node.children.iterator();
		
		ParseNode next;
		while (nodes.hasNext()) {
			next = nodes.next();
			if(next.isLeaf()) {
				format += "\n" + formatOffset + "|_______" + leafToString((ParseLeaf) next);
			} else {
				if(nodes.hasNext())
					format += "\n" + formatOffset + "|_______" + branchToString((ParseBranch) next, formatOffsetNew);
				else 
					format += "\n" + formatOffset + "|_______" + branchToString((ParseBranch) next, formatOffsetLast);
			}
		}
		return format;
	}
	
}
