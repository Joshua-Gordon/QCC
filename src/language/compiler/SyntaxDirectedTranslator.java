package language.compiler;

import java.util.Iterator;
import java.util.stream.Stream;

import language.compiler.ContextFreeGrammer.ProductionTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ProductionSymbol.NonTerminal;
import utils.customCollections.Pair;
import utils.customCollections.Stack;

public class SyntaxDirectedTranslator {

	private ContextFreeGrammer contextFreeGrammer;
	
	public SyntaxDirectedTranslator (ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
	
	public Parser getParser (Stream<Pair<Token, String>> tokenStream) {
		return new Parser(tokenStream);
	}
	
	// Getters and Setters
	
	public ContextFreeGrammer getContextFreeGrammer() {
		return contextFreeGrammer;
	}

	public void setContextFreeGrammer(ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
	
	
	
	
	
	
	
	
	// Parser class
	public class Parser {
		private Iterator<Pair<Token, String>> tokenStream;
		private Token lookAheadSymbol;
		private String lexeme;
		private boolean hasRun;
		private ParseBranch currentNode;
		private ProductionTree currentProductionTree;
		private Stack<ParseBranch> nodeStack;
		private Stack<ProductionTree> productionTrees;
		
		private Parser (Stream<Pair<Token, String>> tokenStream) {
			this.tokenStream = tokenStream.iterator();
			this.hasRun = false;
			this.nodeStack = new Stack<>();
			this.productionTrees = new Stack<>();
			
			NonTerminal start = contextFreeGrammer.getStartingNonTerminal();
			currentNode = new ParseBranch(start);
			currentProductionTree = contextFreeGrammer.getProductionTree(start);
		}
		
		public ParseTree run() {
			if(hasRun)
				throw new ParserRanException();
			hasRun = true;
			
			while(fetchNextToken()) {
//				I am stuck here
			}
			
			return null;
		}
		
		public void pushNodeAndTree () {
			
		}
		
		private boolean fetchNextToken () {
			Pair<Token, String> fetchedToken = tokenStream.next();
			if (fetchedToken == null)
				return false;
			lookAheadSymbol = fetchedToken.first();
			lexeme = fetchedToken.second();
			return true;
		}
	}
	
	@SuppressWarnings("serial")
	private static class ParserRanException extends RuntimeException {
		public ParserRanException () {
			super("This parser has already ran once. Parsers can't run more than once.");
		}
	}
	
	@SuppressWarnings("serial")
	private static class ParserSyntaxException extends RuntimeException {
		public ParserSyntaxException () {
			super("The sequence of tokens does not fit the grammer of this parser");
		}
	}
	
}
