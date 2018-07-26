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
    	boolean debugMatrixMode = false;

    	if ( debugMatrixMode ) {
    		/* TESTING: matrix operators */
    		Matrix<Complex> mat = BaseGraph.completeGraph(2);
    		double mixTime = Math.PI / 4.0;
    		System.out.println("Test matrix = \n" + mat);
    	
    		/* TESTING: spectral decomposition for hermitian matrices */
    		MatrixDecomposition obj = new MatrixDecomposition();
    		List<Eigenspace> eigspaces = obj.eigh(mat);
    		if ( obj.checkDecomposition( mat, eigspaces, obj.testEpsilon) ) {
    			System.err.println("Hermitian spectral decomposition: ok");
    		}
    		else {
    			System.err.println("Hermitian spectral decomposition: fail");
    		}

    		/* TESTING: matrix exponential */
    		Matrix<Complex> mixMatrix = HamiltonianSimulation.quantumWalk(mat, mixTime);
    		System.out.println("Matrix = \n" + mat.toString());
    		System.out.println("Func(Matrix) = \n" + mixMatrix.toString());
    		
    		/* TESTING: user input matrix
    		CustomGateConstructorUI g = new CustomGateConstructorUI(null);
    		g.show();
    		Matrix<Complex> mat = g.getCustomMatrix().stream().reduce((a, m) -> a.kronecker(m)).get();
    		System.out.println(mat);
    		*/
    	}
    	
    	if ( normalMode ) {
        	DefaultGate.loadGates();
    		window = new Window();
    		window.setVisible(true);
    	}
    	
    	if ( debugMode ) {
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
