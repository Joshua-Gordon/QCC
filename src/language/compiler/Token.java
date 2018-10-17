package language.compiler;

import language.compiler.ProductionSymbol.Terminal;

public class Token extends Terminal{
	private final String name;
	
	public Token (String name) {
		this.name = name;
	}
	
	public String getName () {
		return name;
	}
}
