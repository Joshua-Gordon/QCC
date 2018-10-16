package language.compiler;

import java.util.ArrayList;
import java.util.Hashtable;

public class ContextFreeGrammer {
	private final NonTerminal startingNonTerminal;	
	private final Hashtable<NonTerminal, ProductionTree> nonTerminalMap;
	
	
	public ContextFreeGrammer (NonTerminal startingNonTerminal, ArrayList<Production> productions) {
		this.startingNonTerminal = startingNonTerminal;
		this.nonTerminalMap = constructNonTerminalMap(productions);
	}
	
	public NonTerminal getStartingNonTerminal() {
		return startingNonTerminal;
	}
	
	public ProductionTree getProductionTree(NonTerminal nonTerminal){
		return nonTerminalMap.get(nonTerminal);
	}
	
	private Hashtable<NonTerminal, ProductionTree> constructNonTerminalMap (ArrayList<Production> productions) {		
		NonTerminal nt;
		ArrayList<ProductionSymbol[]> arrayList;
		Hashtable<NonTerminal, ArrayList<ProductionSymbol[]>> tempBuff = new Hashtable<>();
		for(Production p : productions) {
			nt = p.getLeftSide();
			if(tempBuff.contains(nt)) {
				arrayList = tempBuff.get(nt);
			} else {
				arrayList = new ArrayList<>();
				tempBuff.put(nt, arrayList);
			}
			arrayList.add(p.getRightSideSymbols());
		}
		
		Hashtable<NonTerminal, ProductionTree> nonTerminalMap = new Hashtable<>();
		tempBuff.forEach((sym, arrayIter) -> {
			nonTerminalMap.put(sym, mkTree(0, arrayIter));
		});
		
		return nonTerminalMap;
	}
	
	// this method (recursive) is a utility method used solely in constructNonTerminalMap() for constructing the NonTerminal map
	private ProductionTree mkTree (int index, ArrayList<ProductionSymbol[]> rightSideSymbols) {		
		Hashtable<ProductionSymbol, ArrayList<ProductionSymbol[]>> tempBuff = new Hashtable<>();
		
		ProductionSymbol ps;
		ArrayList<ProductionSymbol[]> arrayList;
		for(ProductionSymbol[] list : rightSideSymbols) {
			if(index < list.length) {
				ps = list[index];
				if(tempBuff.containsKey(ps)) {
					arrayList = tempBuff.get(ps);
				} else {
					arrayList = new ArrayList<>();
					tempBuff.put(ps, arrayList);
				}
				arrayList.add(list);
			}
		}
		
		ProductionTree productionTree = new ProductionTree();
		tempBuff.forEach((prodSymb, arrayIter) -> {
			productionTree.put(prodSymb, mkTree(index+1, arrayIter));
		});
		
		return productionTree;
	}
	
	
	
	
	@SuppressWarnings("serial")
	public static class ProductionTree extends Hashtable<ProductionSymbol, ProductionTree> {}
}
