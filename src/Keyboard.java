import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Keyboard implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
		
		
//		Gates
        case "Hadamard":
            Main.cb.edit(Gate.GateType.H);
            break;
        case "I":
            Main.cb.edit(Gate.GateType.I);
            break;
        case "X":
            Main.cb.edit(Gate.GateType.X);
            break;
        case "Y":
            Main.cb.edit(Gate.GateType.Y);
            break;
        case "Z":
            Main.cb.edit(Gate.GateType.Z);
            break;
        case "Measure":
            Main.cb.edit(Gate.GateType.Measure);
            break;
        case "CNot":
            Main.cb.edit(Gate.GateType.CNOT);
            break;
        case "Swap":
            Main.cb.edit(Gate.GateType.SWAP);
            break;
            
            
//		Export Types
        case "QUIL":
            System.out.println(Translator.translateQUIL());
            break;
        case "QASM":
            System.out.println(Translator.translateQASM());
            break;
        case "Quipper":
            System.out.println(Translator.translateQuipper());
            break;
            
//      File Selections
            
        case "Open Circuit":
        	CircuitFileSelector.selectBoardFromFileSystem();
        	break;            
        case "Save Circuit as":
        	CircuitFileSelector.saveBoardToFileSystem();
        	break;            
        case "Save":
        	CircuitFileSelector.saveBoard();
        	break;
		
		
//    	Grid Selections
        
        case "Add Row":
        	Main.cb.addRow();
        	Main.render();
        	break;            
        case "Add Column":
        	Main.cb.addColumn();
        	Main.render();
        	break;            
        case "Remove Last Row":
        	Main.cb.removeRow();
        	Main.render();
        	break;	
        case "Remove Last Column":
        	Main.cb.removeColumn();
        	Main.render();
        	break;		
        case "Run QUIL":
            System.out.println("Running QUIL");
            String quil = Translator.translateQUIL();
            quil.trim();
            try {
                Executor.runQuil(quil);
            } catch (IOException e1) {
                System.err.println("Could not create file!");
            }
        }

	}
}
