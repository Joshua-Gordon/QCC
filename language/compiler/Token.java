package language.compiler;

import language.compiler.ProductionSymbol.Terminal;

/**
 * This represents a single token within a context free grammer
 * @author Massimiliano Cutugno
 *
 */
public class Token extends Terminal{
	public static final Token NONE = new Token();
}
