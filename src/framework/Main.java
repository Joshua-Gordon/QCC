package framework;

import appUI.Window;

public class Main {
	
	private static Window window;
	
    public static void main(String[] args) {
    	
    	
    	DefaultGate.loadGates();

    	window = new Window();
    	window.setVisible(true);
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
