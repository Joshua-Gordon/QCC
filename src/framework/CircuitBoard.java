package framework;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.function.Supplier;


import appUI.AppDialogs;
import appUI.FileSelector;
import preferences.AppPreferences;

public class CircuitBoard implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

	private transient URI fileLocation = null;
	private transient boolean mutated = false;
	
    public ArrayList<ArrayList<Gate>> board;
    HashMap<String,Gate> customGates;

    public static EnumMap<Gate.GateType,Supplier<Gate>> gatemap;

    //initialize gatemap
	static {
    	gatemap = new EnumMap<Gate.GateType, Supplier<Gate>>(Gate.GateType.class);
        gatemap.put(Gate.GateType.I,Gate::identity);
        gatemap.put(Gate.GateType.H,Gate::hadamard);
        gatemap.put(Gate.GateType.X,Gate::x);
        gatemap.put(Gate.GateType.Y,Gate::y);
        gatemap.put(Gate.GateType.Z,Gate::z);
        gatemap.put(Gate.GateType.MEASURE,Gate::measure);
        gatemap.put(Gate.GateType.CNOT,Gate::cnot);
        gatemap.put(Gate.GateType.SWAP,Gate::swap);
        gatemap.put(Gate.GateType.CUSTOM,Gate::customGate);
    }

    //Empty 5x5 board
    public static CircuitBoard getDefaultCircuitBoard() {
    	CircuitBoard board = new CircuitBoard();
    	for(int i = 0; i < 5; ++i){
    		board.addRow();
    		board.addColumn();
        }
    	board.resetMutate();
		board.customGates = new HashMap<>();
    	return board;
    }
    
    public static CircuitBoard loadPreviousCircuitBoard() {
    	CircuitBoard board = null;
    	String url = AppPreferences.get("File IO", "Previous File Location");
        File file = new File(url);
        if(url != "" && file.exists()) {
        	board = FileSelector.openFile(file);
        	if(board == null)
        		board = CircuitBoard.getDefaultCircuitBoard();
        }else {
        	board = CircuitBoard.getDefaultCircuitBoard();
        }
        return board;
    }
    
    public CircuitBoard() {
        board = new ArrayList<>();
    }

    public void addRow() {
    	mutate();
        for(ArrayList<Gate> a : board)
            a.add(Gate.identity());
    }

    public void removeRow() {
    	if(board.get(0).size() > 1) {
	    	mutate();
	        for(ArrayList<Gate> a : board)
	            a.remove(a.size() - 1);
    	}else {
    		AppDialogs.couldNotRemoveRow(Main.w.getFrame());
    	}
    }
    
    public void addColumn(){
    	mutate();
        board.add(new ArrayList<>());
        for(int i = 0; i < board.get(0).size(); ++i)
            board.get(board.size()-1).add(Gate.identity());
    }
    
    public void removeColumn(){
       	if(board.size() > 1) {
	    	mutate();
	        board.remove(board.size() - 1);
       	}else {
       		AppDialogs.couldNotRemoveColumn(Main.w.getFrame());
       	}
    }

	//Takes all selected gates and sets them to type g
    public void edit(Gate.GateType g) {
    	mutate();
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                Gate gate = board.get(x).get(y);
                if(gate.isSelected()) {
                    Gate newGate = gatemap.get(g).get();
                    if(newGate != null) {
                    	board.get(x).set(y,newGate);
                    }
                }
            }
        }
        Main.render();
    }

    
    
    
    
	public URI getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(URI fileLocation) {
		this.fileLocation = fileLocation;
	}
    
	public void mutate() {
		mutated = true;
	}
	
	public void resetMutate() {
		mutated = false;
	}
    
	public boolean hasBeenEdited() {
		return mutated;
	}
    
    public void saveFileLocationToPreferences() {
    	if(fileLocation != null) {
    		AppPreferences.put("File IO", "Previous File Location", new File(fileLocation).getAbsolutePath());
    	}else {
    		AppPreferences.put("File IO", "Previous File Location", null);
    	}
    }
    
    public String getName() {
    	if(fileLocation == null) {
    		return FileSelector.UNSAVED_FILE_NAME;
    	}
    	File file = new File(fileLocation);
    	return file.getName();
    }

}
