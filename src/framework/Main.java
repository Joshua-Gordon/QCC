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
    	boolean normalMode = false;
    	boolean debugMode = true;
    	boolean debugSimulatorMode = false;

    	if ( normalMode ) {
        	DefaultGate.loadGates();
    		window = new Window();
    		window.setVisible(true);
    	}
    	
    	if ( debugMode ) {   		
    		Qubit purestate0 = new Qubit(new Vector<Complex>(Complex.ONE(),Complex.ZERO()));
    		Qubit purestate1 = new Qubit(new Vector<Complex>(Complex.ZERO(),Complex.ONE()));
    		ArrayList<Qubit> states = new ArrayList<>();
    		states.add(purestate0);
    		states.add(purestate1);
    		ArrayList<Double> probs = new ArrayList<>();
    		probs.add(.7);
    		probs.add(.3);
			MixedState ms = new MixedState(states,probs);
			System.out.println(ms.getDensityMatrix());
			Qubit out = ms.measure();
			System.out.println("Measured: \n" + out);
			int res = Qubit.measure(out);
			System.out.println("Result: " + res);
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
