package language.compiler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.ProductionSymbol.SematicActionSymbol;
import language.compiler.ProductionSymbol.Terminal;

public class ContextFreeGrammer {
	private final NonTerminal startingNonTerminal;	
	private final Hashtable<NonTerminal, ProductionTree> nonTerminalMap;
	
	
	
	
	public ContextFreeGrammer (NonTerminal startingNonTerminal, Production ... productions) {
		this.startingNonTerminal = startingNonTerminal;
		this.nonTerminalMap = constructNonTerminalMap(productions);
	}
	
	public NonTerminal getStartingNonTerminal() {
		return startingNonTerminal;
	}
	
	public ProductionTree getProductionTree(NonTerminal nonTerminal){
		return nonTerminalMap.get(nonTerminal);
	}
	
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
		
	}
}
