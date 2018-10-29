package language.compiler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.ProductionSymbol.SematicActionSymbol;
import language.compiler.ProductionSymbol.Terminal;


/**
 * 
 * This class allows clients to specify context free grammers for any compiler
 * 
 * @author Massimiliano Cutugno
 *
 */
public class ContextFreeGrammer {
	private final NonTerminal startingNonTerminal;	
	private final Hashtable<NonTerminal, ProductionTree> nonTerminalMap;
	
	
	
	/**
	 * Creates a Context Free Grammer
	 * @param startingNonTerminal
	 * @param productions
	 */
	public ContextFreeGrammer (NonTerminal startingNonTerminal, Production ... productions) {
		this.startingNonTerminal = startingNonTerminal;
		this.nonTerminalMap = constructNonTerminalMap(productions);
	}
	
	/**
	 * @return the starting NonTerminal
	 */
	public NonTerminal getStartingNonTerminal() {
		return startingNonTerminal;
	}
	
	/**
	 * This returns the mapped {@link ProductionTree} for the specified {@link NonTerminal}
	 * 
	 * @param nonTerminal
	 * @return
	 */
	public ProductionTree getProductionTree(NonTerminal nonTerminal){
		return nonTerminalMap.get(nonTerminal);
	}
	
	// creates a map that makes the parser very easily traverse through a productions specified by this grammer
	private Hashtable<NonTerminal, ProductionTree> constructNonTerminalMap (Production[] productions) {		
		NonTerminal nt;
		ArrayList<ProductionSymbol[]> arrayList;
		Hashtable<NonTerminal, ArrayList<ProductionSymbol[]>> tempTable = new Hashtable<>();
		for(Production p : productions) {
			nt = p.getLeftSide();
			if(tempTable.contains(nt)) {
				arrayList = tempTable.get(nt);
			} else {
				arrayList = new ArrayList<>();
				tempTable.put(nt, arrayList);
			}
			arrayList.add(p.getRightSideSymbols());
		}
		
		Hashtable<NonTerminal, ProductionTree> nonTerminalMap = new Hashtable<>();
		tempTable.forEach((sym, arrayIter) -> {
			nonTerminalMap.put(sym, mkTree(0, arrayIter));
		});
		
		return nonTerminalMap;
	}
	
	// this method (recursive) is a utility method used solely in constructNonTerminalMap() for constructing the NonTerminal map
	private ProductionTree mkTree (int index, ArrayList<ProductionSymbol[]> rightSideSymbols) {		
		Hashtable<ProductionSymbol, ArrayList<ProductionSymbol[]>> tempTable = new Hashtable<>();
		
		ProductionSymbol ps;
		ArrayList<ProductionSymbol[]> arrayList;
		for(ProductionSymbol[] list : rightSideSymbols) {
			if(index < list.length) {
				ps = list[index];
				if(tempTable.containsKey(ps)) {
					arrayList = tempTable.get(ps);
				} else {
					arrayList = new ArrayList<>();
					tempTable.put(ps, arrayList);
				}
				arrayList.add(list);
			}
		}
		
		ProductionTree productionTree = new ProductionTree();
		tempTable.forEach((prodSymb, arrayIter) -> {
			switch(prodSymb.getType()) {
			case NON_TERMINAL:
				productionTree.put((NonTerminal) prodSymb, mkTree(index+1, arrayIter));
				break;
			case TERMINAL:
				productionTree.put((Terminal) prodSymb, mkTree(index+1, arrayIter));
				break;
			case SEMATIC_ACTION:
				productionTree.put((SematicActionSymbol) prodSymb, mkTree(index+1, arrayIter));
				break;
			}
		});
		
		return productionTree;
	}
	
	
	
	/**
	 * This class allows a compiler to traverse effiecntly through specified productions within the grammer when parsing
	 * 
	 * @author Massimiliano Cutugno
	 *
	 */
	public static class ProductionTree {
		private final Hashtable<NonTerminal, ProductionTree> nonTerminals = new Hashtable<>();
		private final Hashtable<Terminal, ProductionTree> terminals = new Hashtable<>();
		private final Hashtable<SematicActionSymbol, ProductionTree> actions = new Hashtable<>();
		
		public void put(NonTerminal nonTerminal, ProductionTree productionTree) {
			nonTerminals.put(nonTerminal, productionTree);
		}
		
		public ProductionTree get (NonTerminal nonTerminal) {
			return nonTerminals.get(nonTerminal);
		}
		
		public boolean containsSymbol(NonTerminal nonTerminal) {
			return nonTerminals.containsKey(nonTerminal);
		}
		
		public Set<NonTerminal> getNonTerminalSet() {
			return nonTerminals.keySet();
		}
		
		public void put(Terminal terminal, ProductionTree productionTree) {
			terminals.put(terminal, productionTree);
		}
		
		public ProductionTree get (Terminal nonTerminal) {
			return terminals.get(nonTerminal);
		}
		
		public boolean containsSymbol(Terminal terminal) {
			return terminals.containsKey(terminal);
		}
		
		public Set<Terminal> getTerminalSet () {
			return terminals.keySet();
		}
		
		public void put(SematicActionSymbol sematicActionSymbol, ProductionTree productionTree) {
			actions.put(sematicActionSymbol, productionTree);
		}
		
		public ProductionTree get (SematicActionSymbol sematicActionSymbol) {
			return actions.get(sematicActionSymbol);
		}
		
		public boolean containsSymbol(SematicActionSymbol sematicActionSymbol) {
			return actions.containsKey(sematicActionSymbol);
		}
		
		public Set<SematicActionSymbol> getSematicActions() {
			return actions.keySet();
		}
		
		public boolean isEmpty() {
			return nonTerminals.isEmpty() && terminals.isEmpty() && actions.isEmpty();
		}
		
	}
}
