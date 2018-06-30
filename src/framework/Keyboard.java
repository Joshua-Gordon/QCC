package framework;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import appUI.CustomGateConstructorUI;
import appUI.FileSelector;
import preferences.AppPreferencesWindow;

public class Keyboard implements ActionListener, Runnable {
	
	private Thread thread;
	private String actionCommand;

	@Override
	public void run() {
		switch(actionCommand){


//		Gates
        case "Hadamard":
            Main.cb.edit(DefaultGate.GateType.H);
            break;
        case "I":
            Main.cb.edit(DefaultGate.GateType.I);
            break;
        case "X":
            Main.cb.edit(DefaultGate.GateType.X);
            break;
        case "Y":
            Main.cb.edit(DefaultGate.GateType.Y);
            break;
        case "Z":
            Main.cb.edit(DefaultGate.GateType.Z);
            break;
        case "Measure":
            Main.cb.edit(DefaultGate.GateType.MEASURE);
            break;
        case "CNot":
            Main.cb.edit(DefaultGate.GateType.CNOT);
            break;
        case "Swap":
            Main.cb.edit(DefaultGate.GateType.SWAP);
            break;
        case "Custom":
        	DefaultGate.makeCustom();
            break;

//		Export Types
            
        case "PNG Image":
        	FileSelector.exportPNG(Main.cb, null);
        	break;
        case "QUIL":
            System.out.println(Translator.translateQUIL());
            Main.w.getConsole().println(Translator.translateQUIL());
            break;
        case "QASM":
            System.out.println(Translator.translateQASM());
            Main.w.getConsole().println(Translator.translateQASM());
            break;
        case "Quipper":
            System.out.println(Translator.translateQuipper());
            Main.w.getConsole().println(Translator.translateQuipper());
            break;

//      File Selections
        case "New Circuit":
        	FileSelector.createNewBoard();
        	break;
        case "Open Circuit":
        	FileSelector.selectBoardFromFileSystem();
        	break;
        case "Save Circuit as":
        	FileSelector.saveBoardToFileSystem();
        	break;
        case "Save":
        	FileSelector.saveBoard();
        	break;

//      Preferences
        case "Preferences":
        	AppPreferencesWindow apui = new AppPreferencesWindow(Main.w.getFrame());
        	apui.setVisible(true);
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
            Main.w.getConsole().println("Running QUIL");
            String quil = Translator.translateQUIL();
            quil.trim();
            try {
                Executor.runQuil(quil);
            } catch (IOException e1) {
                System.err.println("Could not create file!");
                e1.printStackTrace();
            }
            break;
        case "Run QASM":
	        System.out.println("Running QASM");
	        Main.w.getConsole().println("Running QASM");
	        String qasm = Translator.translateQASM();
	        qasm.trim();
	        try {
	            Executor.runQASM(qasm);
	        } catch (IOException e1) {
	            System.err.println("Could not create file!");
	            e1.printStackTrace();
	        }
	        break;
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		actionCommand = e.getActionCommand();
		thread = new Thread(this);
		thread.start();
	}
}
