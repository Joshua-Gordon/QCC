package framework;

import Simulator.InternalExecutor;
import Simulator.MixedState;
import Simulator.Qubit;
import appUI.Window;

import mathLib.*;
import mathLib.Vector;
import testLib.*;
import testLib.BaseGraph;
import java.util.*;
import java.io.*;

public class Main {
	
	private static Window window;
	
    public static void main(String[] args) {
    	/* toggle flags: debug mode or not */
    	boolean normalMode = true;
    	boolean debugMode = true;
    	boolean debugSimulatorMode = false;

    	if ( normalMode ) {
        	DefaultGate.loadGates();
    		window = new Window();
    		window.setVisible(true);
    	}
    	
    	if ( debugMode ) {   		
    		ArrayList<ArrayList<SolderedRegister>> gates = Translator.loadProgram(DefaultGate.LangType.QUIL,"res/test.quil");
    		window.getSelectedBoard().setGates(gates);
    		CircuitBoard cb = window.getSelectedBoard();
    		System.out.println(cb.getRows());
    		MixedState ms = InternalExecutor.createMixedState(cb);
    		System.out.println(ms.getDensityMatrix());
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
