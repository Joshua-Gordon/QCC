package language.compiler;

public class Token extends Terminal{
	private final String name;
	
	public Token (String name) {
		this.name = name;
	}
	
	public String getName () {
		return name;
	}
}
