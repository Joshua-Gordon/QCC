package framework;

import appUI.Window;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.HermitianDecomposition;
import java.util.*;

public class Main {
	
	private static Window window;
	
    public static void main(String[] args) {
    	/* toggle flags: debug mode or not */
    	boolean normalMode = false;
    	boolean debugMode = true;

    	if ( debugMode ) {
    		/* TESTING: matrix operators */
    		Matrix<Complex> mat = new Matrix<>(Complex.ONE(), 3, 3,
                Complex.ZERO(), Complex.ONE(), Complex.ZERO(),
                Complex.ONE(), Complex.ZERO(), Complex.ONE(),
                Complex.ZERO(), Complex.ONE(), Complex.ZERO());
    		System.out.println(mat.toString());


    		/* TESTING: matrix map */
    		//Matrix<Complex> m = Matrix.map(Complex.ONE(), mat, c -> c.mult(Complex.ONE()));
    		//System.out.println(m.toString());
    	
    		/* TESTING: spectral decomposition */
    		HermitianDecomposition boo = new HermitianDecomposition();
    		List<Matrix<Complex>> foo = new ArrayList<Matrix<Complex>>();
    		foo = boo.decompose(mat);
    		Matrix<Complex> d = foo.get(0);
    		Matrix<Complex> v = foo.get(1);
    	
    		System.out.println(d.toString());
    		System.out.println(v.toString());
    	
    		/* TESTING: user input matrix
    		 * Some unresolved issues: spurious zeros
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
