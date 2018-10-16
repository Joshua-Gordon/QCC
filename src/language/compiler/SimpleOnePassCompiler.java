package language.compiler;

import java.io.BufferedReader;
import java.util.stream.Stream;

import utils.customCollections.Pair;
import utils.customCollections.Stack;

public class SimpleOnePassCompiler {
	
	private LexicalAnalyzer lexicalAnalyzer;
	private SyntaxDirectedTranslator syntaxDirectedTranslator ;
	
	public SimpleOnePassCompiler (LexicalAnalyzer lexicalAnalyzer, SyntaxDirectedTranslator syntaxDirectedTranslator) {
		this.lexicalAnalyzer = lexicalAnalyzer;
		this.syntaxDirectedTranslator = syntaxDirectedTranslator;
	}
	
	public ParseTree compile (BufferedReader br) {
		Stream<Pair<Token, String>> tokenStream = lexicalAnalyzer.getTokenStream(br);
		
		
		
		return null;
	}

	public Pair<ParseTree, Stack<Object>> compileWithTranslationScheme (BufferedReader br) {
		return new Pair<>(null, null);
	}
	
	
	
	
	
	// Getters and Setters
	
	
	public LexicalAnalyzer getLexicalAnalyzer() {
		return lexicalAnalyzer;
	}

	public void setLexicalAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
		this.lexicalAnalyzer = lexicalAnalyzer;
	}

	public SyntaxDirectedTranslator getSyntaxDirectedTranslator() {
		return syntaxDirectedTranslator;
	}

	public void setSyntaxDirectedTranslator(SyntaxDirectedTranslator syntaxDirectedTranslator) {
		this.syntaxDirectedTranslator = syntaxDirectedTranslator;
	}
}
