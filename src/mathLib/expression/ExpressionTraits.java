package mathLib.expression;

import java.util.HashSet;

public class ExpressionTraits {
	private HashSet<String> undefinedVariables = new HashSet<>();
	private boolean isMatrix = false;
	private int rows = -1;
	private int columns = -1;
	private String latexString = "";
	
	public void addUndefinedVariable(String variableName) {
		undefinedVariables.add(variableName);
	}
	
	public HashSet<String> getUndefinedVariables() {
		return undefinedVariables;
	}
	
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public int getRows() {
		return rows;
	}
	
	public boolean isMatrix() {
		return isMatrix;
	}
	
	public void setMatrix() {
		setMatrix(true);
	}
	
	public void setMatrix(boolean isMatrix) {
		this.isMatrix = isMatrix;
	}
	
	public void addToLatexString(String string) {
		this.latexString += string;
	}
	
	public void setLatexString (String latexString) {
		this.latexString = latexString;
	}
	
	public String getLatexString() {
		return latexString;
	}
}
