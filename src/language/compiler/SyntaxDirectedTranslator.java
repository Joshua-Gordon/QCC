package language.compiler;

public class SyntaxDirectedTranslator {

	private ContextFreeGrammer contextFreeGrammer;
	
	public SyntaxDirectedTranslator (ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}

	
	
	
	
	// Getters and Setters
	
	
	public ContextFreeGrammer getContextFreeGrammer() {
		return contextFreeGrammer;
	}

	public void setContextFreeGrammer(ContextFreeGrammer contextFreeGrammer) {
		this.contextFreeGrammer = contextFreeGrammer;
	}
}
