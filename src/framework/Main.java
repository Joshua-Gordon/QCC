package framework;
import java.io.File;

import appUI.CircuitFileSelector;
import appUI.Window;
import preferences.AppPreferences;

public class Main {


    public static CircuitBoard cb;
    public static Window w;

    public static void main(String[] args) {

        w = new Window();
        w.init();
        
        
        cb = loadPreviousCircuitBoard();
        w.setTitle(cb.getName());
        System.out.println(cb.gatemap == null);
        
        w.display(cb.render());
        while(true);

    }

    public static void render(){
        w.display(cb.render());
    }

    
    private static CircuitBoard loadPreviousCircuitBoard() {
    	CircuitBoard board = null;
    	String url = AppPreferences.get("File IO", "Previous File Location");
        File file = new File(url);
        if(url != "" && file.exists()) {
        	board = CircuitFileSelector.openFile(file);
        	if(board == null)
        		board = CircuitBoard.getDefaultCircuitBoard();
        }else {
        	board = CircuitBoard.getDefaultCircuitBoard();
        }
        return board;
    }
    
    
    /**
     * Note, the following code is needed to run the output program
     * from pyquil.parser import parse_program
     * from pyquil.api import QVMConnection
     * qvm = QVMConnection()
     * p = parse_program("whatever this java code outputs")
     * qvm.wavefunction(p).amplitudes
     */

    /**
     * Alternatively, to use the QASM output, this code will work:
     * import qiskit
     * qp = qiskit.QuantumProgram()
     * name = "test"
     * qp.load_qasm_file("test.qasm",name=name)
     * ret = qp.execute([name])
     * print(ret.get_counts(name))
     */

}
