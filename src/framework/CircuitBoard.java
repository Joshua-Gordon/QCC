package framework;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.Supplier;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import appUI.AppDialogs;
import appUI.CircuitFileSelector;
import preferences.AppPreferences;

public class CircuitBoard implements Serializable{
	private static final long serialVersionUID = -6921131331890897905L;

	private transient URI fileLocation = null;
	private transient boolean mutated = false;
	
    ArrayList<ArrayList<Gate>> board;

    public static EnumMap<Gate.GateType,Supplier<Gate>> gatemap;
    
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
    }
    
    public static CircuitBoard getDefaultCircuitBoard() {
    	CircuitBoard board = new CircuitBoard();
    	for(int i = 0; i < 5; ++i){
    		board.addRow();
    		board.addColumn();
        }
    	board.resetMutate();
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

    public BufferedImage render(){
        int unit = Gate.GATE_PIXEL_SIZE;
        BufferedImage image = new BufferedImage(board.size()*unit,board.get(0).size()*unit,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,image.getWidth(),image.getHeight());
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                g.setColor(Color.GREEN);
                g.drawRect(x*unit,y*unit,unit,unit);
                switch(board.get(x).get(y).type){
                    case I:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + 32,(x+1)*unit,y*unit + 32);
                        break;
                    case H:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Hadamard",x*unit,y*unit + (unit>>2));
                        break;
                    case X:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("X",x*unit,y*unit + (unit>>2));
                        break;
                    case Y:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Y",x*unit,y*unit + (unit>>2));
                        break;
                    case Z:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Z",x*unit,y*unit + (unit>>2));
                        break;
                    case EDIT:
                        g.setColor(Color.RED);
                        g.drawRect(x*unit,y*unit,unit,unit);
                        break;
                    case MEASURE:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Measure",x*unit,y*unit + (unit>>2));
                        break;
                    case CNOT:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        int len = board.get(x).get(y).length;
                        g.drawLine(x*unit + (unit>>1),y*unit + (unit>>1),x*unit + (unit>>1),(y+len)*unit + (unit>>1) + (unit>>2));
                        int centerX = x*unit + (unit>>2);
                        int centerY = (y+len)*unit + (unit>>2);
                        g.drawOval(centerX,centerY,unit>>1,unit>>1);
                        break;
                    case SWAP:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        //Diagonal lines
                        g.drawLine(x*unit + (unit>>2),y*unit + (unit>>2),(x+1)*unit - (unit >> 2), (y+1)*unit - (unit>>2));
                        g.drawLine(x*unit + (unit>>2),(y+1)*unit - (unit>>2),(x+1)*unit - (unit >> 2), y*unit + (unit>>2));
                        int swaplen = board.get(x).get(y).length;
                        g.drawLine(x*unit + (unit>>1),y*unit + (unit>>1),x*unit + (unit>>1),(y+swaplen)*unit + (unit>>1));
                        //More diagonal lines
                        g.drawLine(x*unit + (unit>>2),(y+swaplen)*unit + (unit>>2),(x+1)*unit - (unit >> 2), (y+swaplen+1)*unit - (unit>>2));
                        g.drawLine(x*unit + (unit>>2),(y+1+swaplen)*unit - (unit>>2),(x+1)*unit - (unit >> 2), (y+swaplen)*unit + (unit>>2));

                }
            }
        }
        return image;
    }

    public void edit(Gate.GateType g) {
    	mutate();
        int counter = 0;
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                Gate gate = board.get(x).get(y);
                if(gate.type == Gate.GateType.EDIT) {
                    Gate newGate = gatemap.get(g).get();
                    board.get(x).set(y,newGate);
                    ++counter;
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
    		return CircuitFileSelector.UNSAVED_FILE_NAME;
    	}
    	File file = new File(fileLocation);
    	return file.getName();
    }

}
