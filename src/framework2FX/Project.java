package framework2FX;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;

import appUIFX.AppFileIO;
import framework2FX.exportGates.RawExportableGateData;
import framework2FX.gateModels.BasicModel;
import framework2FX.gateModels.CircuitBoardModel;
import framework2FX.gateModels.GateModel;
import framework2FX.gateModels.OracleModel;
import framework2FX.gateModels.PresetGateType;
import utils.customCollections.Pair;
import utils.customCollections.eventTracableCollections.Notifier;


/**
 * All Quantum Circuits designed within this application are done by modifying an instance of {@link Project} <br>
 * Only one {@link Project} can be focused to be edited upon within one session of this Application <br>
 * <p>
 * All {@link CustomGateModel}s, {@link OracleModel}s, and {@link CircuitBoardModel}s used within this project <br>
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
	
	private final ProjectHashtable subCircuits;
    private final ProjectHashtable customGates;
    private final ProjectHashtable customOracles;
	
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
		subCircuits = new ProjectHashtable();
		customGates = new ProjectHashtable();
		customOracles = new ProjectHashtable();
	}
	
	
	
	
	
	/**
	 * <b>ENSURES:</b>  cb is added to project as a untitled sub-circuit<br>
	 * <b>MODIFIES INSTANCE</b>
	 * @return the generated name of this sub-circuit
	 * @throws {@link RuntimeException} if sub-circuit count exceeds Integer.MAX_VALUE
	 */
	public String addUntitledSubCircuit(){
		Pair<String, Integer> nameAttr = makeUnusedUntitledBoardName();
		
		String name = nameAttr.first();
		String symbol = "U" + nameAttr.second();

		notifier.sendChange(this, "addUntitledSubCircuit");
		subCircuits.put(new CircuitBoardModel(name, symbol, "", 5, 5));
		
		return name + "." + CircuitBoardModel.CIRCUIT_BOARD_EXTENSION;
	}
	
	
	public Pair<String, Integer> makeUnusedUntitledBoardName() {
		final String untitledString = "Untitled";
		
		if(!subCircuits.containsGateModel(untitledString + "." + CircuitBoardModel.CIRCUIT_BOARD_EXTENSION))
			return new Pair<>(untitledString, 0);
		
		int i = 1;
		while(subCircuits.containsGateModel(untitledString + "_" + Integer.toString(i) + "." + CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
			if(i == Integer.MAX_VALUE)
				throw new RuntimeException("Could not add any more untitled sub-ciruits");
			i++;
		}
		
		return new Pair<>(untitledString + "_" + Integer.toString(i), i);
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
	public ProjectHashtable getCircuitBoardModels() {
		return subCircuits;
	}
	
	
	
	
	
	/**
	 * @return the list of the custom-gates for this project
	 */
	public ProjectHashtable getCustomGates() {
		return customGates;
	}
	
	
	
	
	
	
	/**
	 * @return the list of the custom-oracles for this project
	 */
	public ProjectHashtable getCustomOracles() {
		return customOracles;
	}

	
	/**
	 * @return the name of the top-level sub-circuit
	 */
	public String getTopLevelCircuitName() {
		return topLevelCircuit;
	}
	
	
	public boolean hasTopLevel() {
		return topLevelCircuit != null;
	}
	
	
	/**
	 * <b>REQUIRES:</b> name is not null <br>
	 * <b>ENSURES:</b> name is set to the top-level sub-circuit && <br>
	 * GUI is notified of the change (if this project is focused) <br>
	 * <b>MODIFIES INSTANCE</b>
	 * @param name
	 */
	public void setTopLevelCircuitName(String formalName) {
		if(formalName == null) {
			notifier.sendChange(this, "setTopLevelCircuitName", formalName);
			topLevelCircuit = null;
		} else {
			String[] parts = formalName.split("\\.");
			if(parts.length == 2 && parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				if(subCircuits.containsGateModel(formalName)) {
					notifier.sendChange(this, "setTopLevelCircuitName", formalName);
					topLevelCircuit = formalName;
				}
			}
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
	
	public GateModel getGateModel(String gateModelFormalName) {
		String[] parts = gateModelFormalName.split("\\.");
		
		if(parts.length == 2) {
			if(parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				return subCircuits.get(gateModelFormalName);
			} else if(parts[1].equals(BasicModel.GATE_MODEL_EXTENSION)) {
				PresetGateType pgt = PresetGateType.getPresetTypeByFormalName(gateModelFormalName);
				if(pgt != null) 
					return pgt.getModel();
				else
					return customGates.get(gateModelFormalName);
			} else if(parts[1].equals(OracleModel.ORACLE_MODEL_EXTENSION)) {
				return customOracles.get(gateModelFormalName);
			}
		}
		return null;
	}
	
	public boolean containsGateModel(String gateModelFormalName) {
		String[] parts = gateModelFormalName.split("\\.");
		
		if(parts.length == 2) {
			if(parts[1].equals(CircuitBoardModel.CIRCUIT_BOARD_EXTENSION)) {
				return subCircuits.containsGateModel(gateModelFormalName);
			} else if(parts[1].equals(BasicModel.GATE_MODEL_EXTENSION)) {
				if(PresetGateType.containsPresetTypeByFormalName(gateModelFormalName))
					return true;
				else
					return customGates.containsGateModel(gateModelFormalName);
			} else if(parts[1].equals(OracleModel.ORACLE_MODEL_EXTENSION)) {
				return customOracles.containsGateModel(gateModelFormalName);
			}
		}
		return false;
	}
	
	
	public class ProjectHashtable implements Serializable {
		private static final long serialVersionUID = -903225963940733391L;
		private Hashtable <String, GateModel> elements;
		
		private ProjectHashtable () {
			elements = new Hashtable<>();
		}
		
		public void put(GateModel newValue) {
			
			if(elements.containsKey(newValue.getFormalName())) {
				GateModel gm = elements.get(newValue.getFormalName());
				
				if(gm == newValue)
					return;
				
				notifier.sendChange(this, "put", newValue);
				
				
				removeCircuitBoardTraits(gm, true);
				removeAllOccurances(gm.getFormalName());
				
			} else {
				notifier.sendChange(this, "put", newValue);
			}

			elements.put(newValue.getFormalName(), newValue);
			

			addCircuitBoardTraits(newValue);
		}
		
		
		
		
		public void replace(String formalNameToReplace, GateModel newValue) {
			if(newValue == null)
				throw new NullPointerException("Gate to replace cannot be null");
			
			GateModel toReplace = elements.get(formalNameToReplace);
			
			
			if(toReplace == null) {
				throw new RuntimeException("Gate \"" + formalNameToReplace + "\" does not exist and cannot be replaced");
			} else {
				if(toReplace == newValue)
					return;
				if(formalNameToReplace.equals(newValue.getFormalName())) {
					notifier.sendChange(this, "replace", formalNameToReplace, newValue);

					removeCircuitBoardTraits(toReplace, false);
					elements.put(newValue.getFormalName(), newValue);
				} else {
					notifier.sendChange(this, "replace", formalNameToReplace, newValue);
					
					for(GateModel circ : subCircuits.getGateModelIterable())
						if(circ != toReplace)
							((CircuitBoardModel)circ).changeAllOccurrences(formalNameToReplace, newValue.getFormalName());
					
					if(removeCircuitBoardTraits(toReplace, true))
						setTopLevelCircuitName(newValue.getFormalName());
					
					elements.remove(formalNameToReplace);
					elements.put(newValue.getFormalName(), newValue);
				}
			}
		}
		
		public GateModel get (String formalName) {
			return elements.get(formalName);
		}
		
		public void remove (String formalName) {
			if(elements.containsKey(formalName)) {
				
				notifier.sendChange(this, "remove", formalName);
				
				GateModel gm = elements.get(formalName);
				if(gm instanceof CircuitBoardModel) {
					CircuitBoardModel cb = (CircuitBoardModel) gm;
					cb.getNotifier().setReceiver(null);
				}
				elements.remove(formalName);
				removeAllOccurances(formalName);
			}
		}
		
		public boolean containsGateModel (GateModel gateModel) {
			return elements.contains(gateModel);
		}
		
		public boolean containsGateModel (String gateModelFormalName) {
			return elements.containsKey(gateModelFormalName);
		}
		
		public Iterable<String> getGateNameIterable() {
			return elements.keySet();
		}
		
		public Iterable<GateModel> getGateModelIterable() {
			return elements.values();
		}
		
		public int size() {
			return elements.size();
		}
		
		private void removeAllOccurances (String formalName) {
			for(GateModel si : subCircuits.getGateModelIterable()) {
				Iterator<RawExportableGateData> gateData = ((CircuitBoardModel)si).iterator();
				while (gateData.hasNext())
					if(gateData.next().getSolderedGate().getGateModelFormalName().equals(formalName))
						gateData.remove();
			}
		}
		
		private void addCircuitBoardTraits (GateModel gm) {
			if(gm instanceof CircuitBoardModel) {
				CircuitBoardModel cb = (CircuitBoardModel) gm;
				cb.getNotifier().setReceiver(notifier);
			}
		}
		
		private boolean removeCircuitBoardTraits (GateModel gm, boolean removeTopLeve) {
			if(gm instanceof CircuitBoardModel) {
				CircuitBoardModel cb = (CircuitBoardModel) gm;
				cb.getNotifier().setReceiver(null);
				if(removeTopLeve && topLevelCircuit != null && cb.getFormalName().equals(topLevelCircuit)) {
					setTopLevelCircuitName(null);
					return true;
				}
			}
			return false;
		}
		
	}
}
