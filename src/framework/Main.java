package framework;

import Simulator.InternalExecutor;
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
    	boolean debugMode = true;

    	if ( normalMode ) {
        	DefaultGate.loadGates();
    		window = new Window();
    		window.setVisible(true);
    	}
    	
    	if ( debugMode ) {
    		/*
    		// can we detect windows vs unix to handle the file path extension?
			// yeah, use System.getProperty("os.name"), it'll either return "Windows" or "Unix". What do you mean by file path extensions?
			ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QUIL,"res\\test.quil");
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
			*/
    		Translator.loadProgram(DefaultGate.LangType.QASM,"res//test.qasm");
    		String qasm = "OPENQASM 2.0;\n" +
					"include \"qelib1.inc\";\n" +
					"qreg q[3];\n" +
					"creg c[3];\n" +
					"z q[0];\n" +
					"y q[1];\n" +
					"h q[2];\n" +
					"cx q[1],q[2];\n" +
					"x q[1];\n" +
					"measure q[0] -> c[0];\n" +
					"measure q[1] -> c[1];\n" +
					"measure q[2] -> c[2];\n";
    		System.out.println(qasm);
    		String  unqasm = Translator.translateQASMToQuil(qasm);
    		System.out.println(unqasm);
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
