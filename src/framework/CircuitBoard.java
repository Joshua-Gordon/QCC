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
	
    public ArrayList<ArrayList<DefaultGate>> board;
    HashMap<String,DefaultGate> customGates;

    public static EnumMap<DefaultGate.GateType,Supplier<DefaultGate>> gatemap;

    //initialize gatemap
	static {
    	gatemap = new EnumMap<DefaultGate.GateType, Supplier<DefaultGate>>(DefaultGate.GateType.class);
        gatemap.put(DefaultGate.GateType.I,DefaultGate::identity);
        gatemap.put(DefaultGate.GateType.H,DefaultGate::hadamard);
        gatemap.put(DefaultGate.GateType.X,DefaultGate::x);
        gatemap.put(DefaultGate.GateType.Y,DefaultGate::y);
        gatemap.put(DefaultGate.GateType.Z,DefaultGate::z);
        gatemap.put(DefaultGate.GateType.MEASURE,DefaultGate::measure);
        gatemap.put(DefaultGate.GateType.CNOT,DefaultGate::cnot);
        gatemap.put(DefaultGate.GateType.SWAP,DefaultGate::swap);
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
        for(ArrayList<DefaultGate> a : board)
            a.add(DefaultGate.identity());
    }

    public void removeRow() {
    	if(board.get(0).size() > 1) {
	    	mutate();
	        for(ArrayList<DefaultGate> a : board)
	            a.remove(a.size() - 1);
    	}else {
    		AppDialogs.couldNotRemoveRow(Main.w.getFrame());
    	}
    }
    
    public void addColumn(){
    	mutate();
        board.add(new ArrayList<>());
        for(int i = 0; i < board.get(0).size(); ++i)
            board.get(board.size()-1).add(DefaultGate.identity());
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
    public void edit(DefaultGate.GateType g) {
    	mutate();
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                DefaultGate gate = board.get(x).get(y);
                if(gate.isSelected()) {
                    DefaultGate newGate = gatemap.get(g).get();
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
