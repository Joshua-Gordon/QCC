package language.compiler;

import java.util.Iterator;
import java.util.stream.Stream;

import language.compiler.ContextFreeGrammer.ProductionTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ProductionSymbol.NonTerminal;
import utils.customCollections.Pair;
import utils.customCollections.Queue;



/**
 * <b> NOT FINISHED </b> <br>
 * TODO: Sometime finish this <br>
 * 
 * 
 * @author Massimiliano Cutugno
 *
 */
public class SyntaxDirectedTranslator {

	private ContextFreeGrammer contextFreeGrammer;
	
	/**
	 * Creates a {@link SyntaxDirectedTranslator}
	 * @param contextFreeGrammer
	 */
	public SyntaxDirectedTranslator (ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
	
	/**
	 * Returns a parsers object to parse token stream
	 * @param tokenStream
	 * @return
	 */
	public Parser getParser (Stream<Pair<Token, String>> tokenStream) {
		return new Parser(tokenStream);
	}
	
	// Getters and Setters
	
	/**
	 * @return the {@link ContextFreeGrammer} associated with this translator
	 */
	public ContextFreeGrammer getContextFreeGrammer() {
		return contextFreeGrammer;
	}

	/**
	 * Sets this {@link ContextFreeGrammer} of this translator
	 * @param contextFreeGrammer
	 */
	public void setContextFreeGrammer(ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
	
	
	
	
	
	
	
	
	// Parser class
	/**
	 * TODO: NOT FINISHED
	 * 
	 * This object allow for parsing a token stream
	 * @author Massimiliano Cutugno
	 *
	 */
	public class Parser {
		private Iterator<Pair<Token, String>> tokenStream;
		private Token lookAheadSymbol;
		private String lexeme;
		private boolean hasRun;
		private Queue<Pair<Token, String>> tokenBuffer;
		
		private Parser (Stream<Pair<Token, String>> tokenStream) {
			this.tokenStream = tokenStream.iterator();
			this.hasRun = false;
		}
		
		@SuppressWarnings("unused")
		public ParseTree run() {
			if(true)
				throw new RuntimeException("Have not finished implementation");
			
			
			if(hasRun)
				throw new ParserRanException();
			hasRun = true;
			
			ParseBranch branch = matchNonTerminal(
					contextFreeGrammer.getStartingNonTerminal(), new Queue<>());
			
			if(branch == null)
				throw new ParserSyntaxException();
			
			return new ParseTree(branch);
		}
		
		private ParseBranch matchNonTerminal (NonTerminal nonTerminal, Queue<Pair<Token, String>> localTokenBuffer) {
			ProductionTree prodTree = contextFreeGrammer.getProductionTree(nonTerminal);
			if(prodTree == null)
				throw new UndefinedNonTerminalException(nonTerminal);
			
			while(!prodTree.isEmpty()) {
				// TODO: finished this
				// Forfeit this until much later
			}
			return null;
		}
		
		
		
		public void putBack(Queue<Pair<Token, String>> localTokenBuffer) {
			this.tokenBuffer.prepend(localTokenBuffer);
		}
		
		private void fetchNextToken (Queue<Pair<Token, String>> localTokenBuffer) {
			Pair<Token, String> fetchedToken;
			if(this.tokenBuffer.isEmpty())
				fetchedToken = tokenStream.next();
			else 
				fetchedToken = this.tokenBuffer.dequeue();
			
			localTokenBuffer.enqueue(fetchedToken);
			lookAheadSymbol = fetchedToken.first();
			lexeme = fetchedToken.second();
		}
	}
	
	@SuppressWarnings("serial")
	private static class ParserRanException extends RuntimeException {
		public ParserRanException () {
			super("This parser has already ran once. Parsers can't run more than once.");
		}
	}
	
	@SuppressWarnings("serial")
	private static class UndefinedNonTerminalException extends RuntimeException {
		public UndefinedNonTerminalException (NonTerminal nonTerminal) {
			super("The following nonterminal, \"" + nonTerminal.getName() + "\" isn't defined within the context free grammer");
		}
	}
	
	@SuppressWarnings("serial")
	private static class ParserSyntaxException extends RuntimeException {
		public ParserSyntaxException () {
			super("The sequence of tokens does not fit the grammer of this parser");
		}
	}
	
}
