package language.compiler;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.stream.Stream;

import language.compiler.LexicalAnalyzer.LexicalAnaylizerIOException;
import language.compiler.LexicalAnalyzer.LexemeNotRecognizedException;
import language.compiler.SyntaxDirectedTranslator.Parser;
import utils.customCollections.Pair;

public class SimpleOnePassCompiler {
	
	private final LexicalAnalyzer lexicalAnalyzer;
	private final SyntaxDirectedTranslator syntaxDirectedTranslator ;
	
	public SimpleOnePassCompiler (LexicalAnalyzer lexicalAnalyzer, 
			SyntaxDirectedTranslator syntaxDirectedTranslator) {
		
		this.lexicalAnalyzer = lexicalAnalyzer;
		this.syntaxDirectedTranslator = syntaxDirectedTranslator;
	}
	

	public ParseTree compileWithTranslationScheme(String string) 
			throws LexemeNotRecognizedException, LexicalAnaylizerIOException {
		
		BufferedReader br = new BufferedReader(new StringReader(string));
		
		return compileWithTranslationScheme(br);
	}
	
	public ParseTree compileWithTranslationScheme (BufferedReader br) 
			throws LexemeNotRecognizedException, LexicalAnaylizerIOException {
		
		Stream<Pair<Token, String>> tokenStream = lexicalAnalyzer.getTokenStream(br);
		Parser p = syntaxDirectedTranslator.getParser(tokenStream);
		ParseTree parseTree = p.run();
		
		return parseTree;
	}
	
	
	// Getters
	
	public LexicalAnalyzer getLexicalAnalyzer() {
		return lexicalAnalyzer;
	}

	public SyntaxDirectedTranslator getSyntaxDirectedTranslator() {
		return syntaxDirectedTranslator;
	}
}
