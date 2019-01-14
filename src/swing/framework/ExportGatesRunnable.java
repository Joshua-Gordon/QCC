package swing.framework;

/**
 * Used in conjunction with the {@link ExportedGate} class.
 * This interface has methods that are called for the {@link ExportedGate} static method: exportGates()
 * 
 * 
 * @author quantumresearch
 *
 */
public interface ExportGatesRunnable {
	
	/**
	 * This is called when an ExportedGate is constructed at the specified column and row.
	 * @param eg
	 * @param x
	 * @param y
	 */
	public void gateExported(ExportedGate eg, int x, int y);
	
	/**
	 * This is called when a column increments
	 * @param column
	 */
	public void nextColumnEvent(int column);
	
	/**
	 * This is called when a whole column is scanned
	 * @param column
	 */
	public void columnEndEvent(int column);
}
