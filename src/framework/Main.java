package framework;

import Simulator.InternalExecutor;
import appUI.Window;

import mathLib.*;
import testLib.*;
import testLib.BaseGraph;
import java.util.*;
import java.io.*;

public class Main {
	
	private static Window window;
	
    public static void main(String[] args) {
    	/* toggle flags: debug mode or not */
    	boolean normalMode = true;
    	boolean debugMode = false;
    	boolean debugSimulatorMode = false;

    	if ( normalMode ) {
        	DefaultGate.loadGates();
    		window = new Window();
    		window.setVisible(true);
    	}
    	
    	if ( debugMode ) {   		
    		// can we detect windows vs unix to handle the file path extension?
			// yeah, use System.getProperty("os.name"), it'll either return "Windows" or "Unix". What do you mean by file path extensions?
			ArrayList<ArrayList<SolderedRegister>> gates = new ArrayList<ArrayList<SolderedRegister>>();
			String os = System.getProperty("os.name");
			System.err.println("OS = " + os);
    		if ( os.contains("Windows") ) {
				gates = Translator.loadProgram(DefaultGate.LangType.QUIL,"res\\test.quil");
			}
			else if ( os.equalsIgnoreCase("Linux") ) {
				File testFile = new File("res/test.quil");
				if ( !testFile.exists() ) {
					throw new RuntimeException("File does not exist.");
				}
				gates = Translator.loadProgram(DefaultGate.LangType.QUIL,"res/test.quil");
			}
			else {
				throw new RuntimeException("OS " + os + " not supported");
			}
			for(int x = 0; x < gates.size(); ++x) {
				ArrayList<SolderedRegister> srs = gates.get(x);
				for(int y = 0; y < srs.size(); ++y) {
					SolderedRegister sr = srs.get(y);
					System.out.println("X: " + x + "\nY: " + y + "\nGate: " + sr);
				}
			}
			window.getSelectedBoard().setGates(gates);
			int output = InternalExecutor.simulate(window.getSelectedBoard());
			System.out.println("OUTPUT: " + output);
    	}
    	
    	if ( debugSimulatorMode ) {
    		ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QASM,"res//test2.qasm");
			Main.getWindow().getSelectedBoard().setGates(gates);
			for(int i = 0; i < 10; ++i) {
				int result = InternalExecutor.simulate(window.getSelectedBoard());
				System.out.println(result);
			}
		}
    }

    public static Window getWindow() {
    	return window;
    }



    /*
     * Note, the following code is needed to run the output program
     * from pyquil.parser import parse_program
     * from pyquil.api import QVMConnection
     * qvm = QVMConnection()
     * p = parse_program("whatever this java code outputs")
     * qvm.wavefunction(p).amplitudes
     */

    /*
     * Alternatively, to use the QASM output, this code will work:
     * import qiskit
     * qp = qiskit.QuantumProgram()
     * name = "test"
     * qp.load_qasm_file("test.qasm",name=name)
     * ret = qp.execute([name])
     * print(ret.get_counts(name))
     */

}
