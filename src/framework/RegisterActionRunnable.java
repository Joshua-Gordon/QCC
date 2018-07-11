package framework;

/**
 * This interface is used in conjunction with methods from the {@link CircuitBoard} class.
 * Allows to create an action to be applied to a {@link SolderedRegister}
 * 
 * 
 * @author quantumresearch
 *
 */
public interface RegisterActionRunnable {
	
	/**
	 * The action that it run when applies to the {@link SolderedRegister}
	 * @param row
	 * @param column
	 * @param sr
	 */
	public void registerScanned(int row, int column, SolderedRegister sr);
	
	
}
