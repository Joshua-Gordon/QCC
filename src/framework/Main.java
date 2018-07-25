package framework;

import appUI.Window;

import mathLib.*;
import testLib.*;
import testLib.BaseGraph;
import java.util.*;

public class Main {
	
	private static Window window;
	
    public static void main(String[] args) {
    	/* toggle flags: debug mode or not */
    	boolean normalMode = true;
    	boolean debugMode = false;

    	if ( normalMode ) {
        	DefaultGate.loadGates();
    		window = new Window();
    		window.setVisible(true);
    	}
    	
    	if ( debugMode ) {
    		// can we detect windows vs unix to handle the file path extension?
			ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QUIL,"res\\test.quil");
			for(int x = 0; x < gates.size(); ++x) {
				ArrayList<SolderedRegister> srs = gates.get(x);
				for(int y = 0; y < srs.size(); ++y) {
					SolderedRegister sr = srs.get(y);
					System.out.println("X: " + x + "\nY: " + y + "\nGate: " + sr);
				}
			}
			window.getSelectedBoard().setGates(gates);
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
