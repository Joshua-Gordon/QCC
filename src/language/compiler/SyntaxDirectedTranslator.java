package language.compiler;

import java.util.stream.Stream;

import utils.customCollections.Pair;

public class SyntaxDirectedTranslator {

	private ContextFreeGrammer contextFreeGrammer;
	
	public SyntaxDirectedTranslator (ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
	
	private Parser getParser () {
		return new Parser();
	}
	
	// Getters and Setters
	
	
	public ContextFreeGrammer getContextFreeGrammer() {
		return contextFreeGrammer;
	}

	public void setContextFreeGrammer(ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
	
	public class Parser {
		
		private Parser () {
			
		}
	}
}
