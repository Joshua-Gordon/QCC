package framework;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import javax.swing.DefaultListModel;

import appUI.CircuitBoardRenderContext;
import appUI.CircuitBoardSelector;
import preferences.AppPreferences;
import utils.AppDialogs;

public class CircuitBoard implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

	private transient URI fileLocation = null;
	private transient boolean unsaved = false;
	
    private ArrayList<ArrayList<SolderedRegister>> board;
    private ArrayList<Integer> boardWidths = new ArrayList<>();
    private DefaultListModel<AbstractGate> customGates = new DefaultListModel<>();
    private DefaultListModel<AbstractGate> customOracles = new DefaultListModel<>();

//    public static EnumMap<DefaultGate.GateType,Supplier<DefaultGate>> gatemap;

    //initialize gatemap
//	static {
//    	gatemap = new EnumMap<DefaultGate.GateType, Supplier<DefaultGate>>(DefaultGate.GateType.class);
//        gatemap.put(DefaultGate.GateType.I,DefaultGate::identity);
//        gatemap.put(DefaultGate.GateType.H,DefaultGate::hadamard);
//        gatemap.put(DefaultGate.GateType.X,DefaultGate::x);
//        gatemap.put(DefaultGate.GateType.Y,DefaultGate::y);
//        gatemap.put(DefaultGate.GateType.Z,DefaultGate::z);
//        gatemap.put(DefaultGate.GateType.MEASURE,DefaultGate::measure);
//        gatemap.put(DefaultGate.GateType.CNOT,DefaultGate::cnot);
//        gatemap.put(DefaultGate.GateType.SWAP,DefaultGate::swap);
//    }

    //Empty 5x5 board
    
    
    /**
     * @return
     * an empty 4 by 5 {@link CircuitBoard}
     */
    public static CircuitBoard getDefaultCircuitBoard() {
    	CircuitBoard board = new CircuitBoard();
    	for(int i = 0; i < 5; ++i){
    		board.addRow();
    		board.addColumn();
        }
    	board.setSaved();
    	return board;
    }
    
    private CircuitBoard() {
        board = new ArrayList<>();
    }

    public void addRow() {
    	setUnsaved();
        for(ArrayList<SolderedRegister> a : board)
            a.add(new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
    }
    
    public void addRow(int r) {
    	if(r == getRows()) {
    		addRow();
    	}else{
    		for(ArrayList<SolderedRegister> a : board)
    			a.add(r, new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
    	}
    }
    
    public void removeRow() {
    	if(board.get(0).size() > 1) {
	    	setUnsaved();
	        for(ArrayList<SolderedRegister> a : board)
	            a.remove(a.size() - 1);
    	}else {
    		AppDialogs.couldNotRemoveRow(Main.getWindow().getFrame());
    	}
    }
    
    public void addColumn(){
    	setUnsaved();
        board.add(new ArrayList<>());
        boardWidths.add(1);
        for(int i = 0; i < board.get(0).size(); ++i)
            board.get(board.size()-1).add(new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
    }
    
    public void addColumn(int c){
    	if(c == getColumns()) {
    		addColumn();
    	}else {
            boardWidths.add(1);
    		ArrayList<SolderedRegister> sr = new ArrayList<>();
        	for(int i = 0; i < board.get(0).size(); ++i)
        		sr.add(new SolderedRegister(new SolderedGate(DefaultGate.getIdentity()), 0));
        	board.add(c, sr);
    	}
    }
    
    public void removeColumn(){
       	if(board.size() > 1) {
	    	setUnsaved();
	    	boardWidths.remove(board.size() - 1);
	        board.remove(board.size() - 1);
       	}else {
       		AppDialogs.couldNotRemoveColumn(Main.getWindow().getFrame());
       	}
    }

	//Takes all selected gates and sets them to type g
//    public void edit(AbstractGate.GateType g) {
//    	mutate();
//        for(int x = 0; x < board.size(); ++x) {
//            for(int y = 0; y < board.get(0).size(); ++y) {
//            	AbstractGate gate = board.get(x).get(y);
//                if(gate.isSelected()) {
//                	AbstractGate newGate = gatemap.get(g).get();
//                    if(newGate != null) {
//                    	board.get(x).set(y,newGate);
//                    }
//                }
//            }
//        }
//        Main.render();
//    }

    
    
    
    
	public URI getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(URI fileLocation) {
		this.fileLocation = fileLocation;
	}
    
	public void setUnsaved() {
		unsaved = true;
	}
	
	public void setSaved() {
		unsaved = false;
	}
    
	public boolean hasBeenEdited() {
		return unsaved;
	}
    
    public void saveFileLocationToPreferences() {
    	if(fileLocation != null)
    		AppPreferences.put("File IO", "Previous File Location", new File(fileLocation).getAbsolutePath());
    	else
    		AppPreferences.put("File IO", "Previous File Location", null);
    }

	public DefaultListModel<AbstractGate> getCustomGates() {
		return customGates;
	}

	public DefaultListModel<AbstractGate> getCustomOracles() {
		return customOracles;
	}

	public String getName() {
    	if(fileLocation == null) {
    		return CircuitBoardSelector.UNSAVED_FILE_NAME;
    	}
    	File file = new File(fileLocation);
    	return file.getName();
    }
	
	public void SolderRegister(int row, int column, SolderedRegister sr) {
		board.get(column).set(row, sr);
	}
	
	public int getRows() {
		return board.get(0).size();
	}
	
	public int getColumns() {
		return board.size();
	}
	
	public SolderedRegister getSolderedRegister(int x, int y) {
		return board.get(x).get(y);
	}
	
	public void setSolderedRegister(int x, int y, SolderedRegister sr) {
		board.get(x).set(y, sr);
	}
	
	public SolderedGate getSolderedGate(int x, int y) {
		return board.get(x).get(y).getSolderedGate();
	}
	
	public int getColumnWidth(int column) {
		return boardWidths.get(column);
	}
	
	public void setColumnWidth(int column, int value) {
		boardWidths.set(column, value);
	}
	
}
