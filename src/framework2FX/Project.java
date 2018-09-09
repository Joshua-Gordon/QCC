package framework2FX;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;

import appUIFX.AppFileIO;
import utils.EventArrayList;
import utils.EventHashTable;
import utils.Notifier;

public class Project implements Serializable{
	private static final long serialVersionUID = 8906661352790858317L;
	
	private SubCircuitList subCircuits;
    private CustomList <CustomGateModel> customGates;
    private CustomList <AbstractGateModel> customOracles;
	
	private transient URI fileLocation = null;
	
	private Notifier notifier;
	private String topLevelCircuit = null;
	
	
	
	public static Project createNewTemplateProject() {
		Project project = new Project();
		project.topLevelCircuit = project.addUntitledSubCircuit(
				CircuitBoard.getDefaultCircuitBoard());
		return project;
	}
	
	public static String getProjectName(URI fileLocation) {
		if(fileLocation == null) {
			return "Untitled_Project";
		} else {
			String fileName = new File(fileLocation).getName();
			return fileName.substring(0, fileName.length() - AppFileIO.QUANTUM_PROJECT_EXTENSION.length());
		}
	}
	
	
	
	
	
	
	public Project() {
		notifier = new Notifier();
		subCircuits = new SubCircuitList();
		customGates = new CustomList<>();
		customOracles = new CustomList<>();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String addUntitledSubCircuit(CircuitBoard cb){
		String title = "Untitled";
		
		if(!subCircuits.containsKey(title)) {
			subCircuits.put(title, cb);
			return title;
		}
		
		int i = 1;
		while(subCircuits.containsKey(title + " " + Integer.toString(i))) {
			if(i == Integer.MAX_VALUE)
				throw new RuntimeException("Could not add any more untitled sub-ciruits");
			i++;
		}
		

		notifier.sendChange(this, "addUntitledSubCircuit", cb);
		subCircuits.put(title + " " + Integer.toString(i), cb);
		
		return title + " " + Integer.toString(i);
	}
	
	
	
	
	
	
	
	
	public String getProjectName() {
		return getProjectName(fileLocation);
	}
	
	public SubCircuitList getSubCircuits() {
		return subCircuits;
	}
	
	public CustomList<CustomGateModel> getCustomGates() {
		return customGates;
	}
	
	public CustomList<AbstractGateModel> getCustomOracles() {
		return customOracles;
	}
	

	
	
	
	
	
	
	
	
	public String getTopLevelCircuitName() {
		return topLevelCircuit;
	}
	
	public void setTopLevelCircuitName(String name) {
		if(subCircuits.containsKey(name)) {
			notifier.sendChange(this, "setTopLevelCircuitName", name);
			topLevelCircuit = name;
		}
	}
	
	public URI getProjectFileLocation() {
		return fileLocation;
	}
	
	public void setProjectFileLocation(URI fileLocation) {
		notifier.sendChange(this, "setProjectFileLocation", fileLocation);
		this.fileLocation = fileLocation;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Update on the fly code
	
	public void setReciever(Notifier reciever) {
		this.notifier.setReceiver(reciever);
	}
	
	public class SubCircuitList extends EventHashTable<String, CircuitBoard> {
		private static final long serialVersionUID = -2900966641041727003L;
		public SubCircuitList() {
			super(notifier);
		}
		
		@Override
		public void put(String key, CircuitBoard value) {
			super.put(key, value);
			value.getNotifier().setReceiver(notifier);
		}
		
		@Override
		public void remove(String key) {
			if(containsKey(key))
				get(key).getNotifier().setReceiver(notifier);
			super.remove(key);
		}
	}
	
	public class CustomList <T> extends EventArrayList<T> {
		private static final long serialVersionUID = -3016434516784502217L;

		public CustomList() {
			super(notifier);
		}
	}
}
