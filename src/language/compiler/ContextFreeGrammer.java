package language.compiler;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class ContextFreeGrammer {
	private final NonTerminal startingNonTerminal;	
	private final Hashtable<NonTerminal, ProductionTree> nonTerminalMap;
	
	public ContextFreeGrammer (NonTerminal startingNonTerminal, ArrayList<Production> productions) {
		this.startingNonTerminal = startingNonTerminal;
		this.nonTerminalMap = constructNonTerminalMap(productions);
	}
	
	private Hashtable<NonTerminal, ProductionTree> constructNonTerminalMap (ArrayList<Production> productions) {
		
	}
	
	
	private ProductionTree mkTree (ArrayList<Iterator<ProductionSymbol>> rightSideSymbols) {
		if(rightSideSymbols == null)
			return null;
		
		Hashtable<ProductionSymbol, ArrayList<Iterator<ProductionSymbol>>> tempBuff = new Hashtable<>();
		
		ProductionSymbol ps;
		ArrayList<Iterator<ProductionSymbol>> arrayList;
		for(Iterator<ProductionSymbol> iterator : rightSideSymbols) {
			if(iterator.hasNext()) {
				ps = iterator.next();
				if(tempBuff.containsKey(ps)) {
					arrayList = tempBuff.get(ps);
				} else {
					arrayList = new ArrayList<>();
					tempBuff.put(ps, arrayList);
				}
				arrayList.add(iterator);
			}
		}
		
		ProductionTree productionTree = new ProductionTree();
		tempBuff.forEach((prodSymb, arrayIter) -> {
			productionTree.put(prodSymb, mkTree(arrayIter));
		});
		
		return productionTree;
	}
	
	
	
	
	@SuppressWarnings("serial")
	private static class ProductionTree extends Hashtable<ProductionSymbol, ProductionTree> {}
}
