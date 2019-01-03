package utils;

import javafx.scene.paint.Color;

public interface PrintStream {
	public void clearConsole() ;
	
	public void print(String text, Color color) ;
	
	public default void println(String text, Color color) {
		print(text + "\n", color);
	}
	
	public void print(String text) ;
	
	public default void println(String text) {
		print(text + "\n");
	}
	
	public default void printErrln(String text) {
		printErr(text + "\n");
	}
	
	public void printErr(String text) ;
	
	public void printLatex(String latex) ;
	
	public void printLatex(String latex, float font) ;
	
	public void printLatex(String latex, float font, String backgroundColor) ;
	
	public void printLatex(String latex, float font, String backgroundColor, String textColor) ;
	
	
	public default void printLatexln(String latex) {
		printLatex(latex);
		println("");
	}
	
	public default void printLatexln(String latex, float font) {
		printLatex(latex, font);
		println("");
	}
	
	public default void printLatexln(String latex, float font, String backgroundColor) {
		printLatex(latex, font, backgroundColor);
		println("");
	}
	
	public default void printLatexln(String latex, float font, String backgroundColor, String textColor) {
		printLatex(latex, font, backgroundColor, textColor);
		println("");
	}
	
	
	public static class SystemPrintStream implements PrintStream {
		public static SystemPrintStream instance = new SystemPrintStream();
		
		public static SystemPrintStream get() {
			return instance;
		}
		
		private SystemPrintStream () {}
		
		@Override
		public void clearConsole() {}

		@Override
		public void print(String text, Color color) {
			System.out.print(text);
		}

		@Override
		public void print(String text) {
			System.out.print(text);			
		}

		@Override
		public void printErr(String text) {
			System.err.print(text);				
		}

		@Override
		public void printLatex(String latex) {
			System.out.print(latex);
		}

		@Override
		public void printLatex(String latex, float font) {
			printLatex(latex);			
		}

		@Override
		public void printLatex(String latex, float font, String backgroundColor) {
			printLatex(latex);
		}

		@Override
		public void printLatex(String latex, float font, String backgroundColor, String textColor) {
			printLatex(latex);
		}
		
	}
	
}
