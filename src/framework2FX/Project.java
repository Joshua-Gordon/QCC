package framework2FX;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

import appUIFX.AppFileIO;
import framework2FX.solderedGates.Solderable;
import utils.customCollections.eventTracableCollections.EventHashTable;
import utils.customCollections.eventTracableCollections.Notifier;


/**
 * All Quantum Circuits designed within this application are done by modifying an instance of {@link Project} <br>
 * Only one {@link Project} can be focused to be edited upon within one session of this Application <br>
 * <p>
 * All {@link CustomGateModel}s, {@link OracleModel}s, and {@link CircuitBoard}s used within this project <br>
 * are stored as lists within a instance of this class <br> 
 * 
 * <p>
 * WARNING: Not thread safe when using object methods that modify <br>
 * internal fields (may cause the GUI to not update as expected) <br>
 * 
 * @author Massimiliano Cutugno
 *
 */
public class Project implements Serializable{
	private static final long serialVersionUID = 8906661352790858317L;
	
	private ProjectHashTable subCircuits;
    private ProjectHashTable customGates;
    private ProjectHashTable customOracles;
	
	private transient URI fileLocation = null;
	
	// Notifies User-Interface of changes
	private Notifier notifier;
	private String topLevelCircuit = null;
	
	
	
	
	/**
	 * @return A new untitled default {@link Project} which consists of one untitled top-level default circuit board
	 */
	public static Project createNewTemplateProject() {
		Project project = new Project();
		project.topLevelCircuit = project.addUntitledSubCircuit();
		return project;
	}
	
	
	/**
	 * @param fileLocation project location on the user's drive
	 * @return the file name of the fileLocation without the extension
	 */
	public static String getProjectNameFromURI(URI fileLocation) {
		if(fileLocation == null) {
			return "Untitled_Project";
		} else {
			String fileName = new File(fileLocation).getName();
			return fileName.substring(0, fileName.length() - AppFileIO.QUANTUM_PROJECT_EXTENSION.length());
		}
	}
	
	
	
	
	
	/**
	 * Creates an empty Project <br>
	 * Project contains no sub-circuits or top-level-components <br>
	 */
	public Project() {
		notifier = new Notifier();
		subCircuits = new ProjectHashTable();
		customGates = new ProjectHashTable();
		customOracles = new ProjectHashTable();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * <b>ENSURES:</b>  cb is added to project as a untitled sub-circuit<br>
	 * <b>MODIFIES INSTANCE</b>
	 * @return the generated name of this sub-circuit
	 * @throws {@link RuntimeException} if sub-circuit count exceeds Integer.MAX_VALUE
	 */
	public String addUntitledSubCircuit(){
		String title = "Untitled";
		
		if(!subCircuits.containsSolderable(title)) {
			subCircuits.put(new CircuitBoard(title, title, "", 5, 5));
			return title;
		}
		
		int i = 1;
		while(subCircuits.containsSolderable(title + " " + Integer.toString(i))) {
			if(i == Integer.MAX_VALUE)
				throw new RuntimeException("Could not add any more untitled sub-ciruits");
			i++;
		}
		
		String name = title + " " + Integer.toString(i);

		notifier.sendChange(this, "addUntitledSubCircuit");
		subCircuits.put(new CircuitBoard(name, name, "", 5, 5));
		
		return title + " " + Integer.toString(i);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @return the name of this project (the given file name without extension)
	 */
	public String getProjectName() {
		return getProjectNameFromURI(fileLocation);
	}
	
	
	
	
	
	/**
	 * @return the list of the sub-circuits for this project
	 */
	public ProjectHashTable getSubCircuits() {
		return subCircuits;
	}
	
	
	
	
	
	/**
	 * @return the list of the custom-gates for this project
	 */
	public ProjectHashTable getCustomGates() {
		return customGates;
	}
	
	
	
	
	
	
	/**
	 * @return the list of the custom-oracles for this project
	 */
	public ProjectHashTable getCustomOracles() {
		return customOracles;
	}

	
	
	
	/**
	 * <b>REQUIRES:</b> list is not null <br>
	 * <b>ENSURES:</b>  GUI is notified of the change <br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param list list of custom gate models
	 */
	public void setCustomGates(ProjectHashTable list) {
		this.notifier.sendChange(this, "setCustomGate", list);
		customGates = list;
	}
	
	
	
	
	/**
	 * <b>REQUIRES:</b> list is not null <br>
	 * <b>ENSURES:</b>  GUI is notified of the change (if this project is focused)<br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param list
	 */
	public void setCustomOracles(ProjectHashTable list) {
		this.notifier.sendChange(this, "setCustomOracles", list);
		customOracles = list;
	}
	
	
	
	
	
	
	
	/**
	 * @return the name of the top-level sub-circuit
	 */
	public String getTopLevelCircuitName() {
		return topLevelCircuit;
	}
	
	
	public CircuitBoard getTopLevelBoard() {
		return (CircuitBoard) subCircuits.get(topLevelCircuit);
	}
	
	
	
	
	/**
	 * <b>REQUIRES:</b> name is not null <br>
	 * <b>ENSURES:</b> name is set to the top-level sub-circuit && <br>
	 * GUI is notified of the change (if this project is focused) <br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param name
	 */
	public void setTopLevelCircuitName(String name) {
		if(subCircuits.containsSolderable(name)) {
			notifier.sendChange(this, "setTopLevelCircuitName", name);
			topLevelCircuit = name;
		}
	}
	
	
	
	/**
	 * @return this project's location on the drive
	 */
	public URI getProjectFileLocation() {
		return fileLocation;
	}
	
	
	
	
	
	/**
	 * <b>REQUIRES:<b> fileLocation is not null
	 * <b>ENSURES:</b> 
	 * <b>MODIFIES INSTANCE</b>
	 * @param fileLocation location of the 
	 */
	public void setProjectFileLocation(URI fileLocation) {
		notifier.sendChange(this, "setProjectFileLocation", fileLocation);
		this.fileLocation = fileLocation;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * <b>ENSURES:</b> that all changes to this project are notified to receiver. <br>
	 * If null, then all changes will not be notified to any receiver <br>
	 * @param receiver
	 */
	public void setReceiver(Notifier receiver) {
		this.notifier.setReceiver(receiver);
	}
	
	
	/**
	 *
	 * WARNING: Not thread safe when using object methods that modify <br>
	 * internal fields (may cause the GUI to not update as expected) <br>
	 * 
	 * @author Massimiliano Cutugno
	 */
	public class ProjectHashTable implements Serializable{
		private static final long serialVersionUID = -2900966641041727003L;
		private EventHashTable<String, Solderable> table;
		
		public ProjectHashTable() {
			table = new EventHashTable<>(notifier);
		}
		
		public void put(Solderable s) {
			table.put(s.getName(), s);
			if(s instanceof CircuitBoard) {
				CircuitBoard cb = (CircuitBoard) s;
				cb.getNotifier().setReceiver(notifier);
			}
		}
		
		public Solderable get(String name) {
			return table.get(name);
		}
		
		public void remove(String key) {
			if(table.containsKey(key)) {
				Solderable s = table.get(key);
				if(s instanceof CircuitBoard) {
					CircuitBoard cb = (CircuitBoard) s;
					cb.getNotifier().setReceiver(null);
				}
			}
			table.remove(key);
		}
		
		public boolean containsSolderable (String name) {
			return table.containsKey(name);
		}
		
		public int size () {
			return table.size();
		}
		
		public Iterable<String> nameIterable () {
			return  table.getKeyIterable();
		}
		
		public Iterable<Solderable> valueIterable () {
			return  table.getValueIterable();
		}
	}
}
