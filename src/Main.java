public class Main {


    public static CircuitBoard cb;
    public static Window w;

    public static void main(String[] args) {

        w = new Window();
        w.init();

        cb = new CircuitBoard();


        cb.addRow();
        cb.addRow();
        cb.addRow();
        cb.addRow();
        cb.addRow();
        cb.addRow();cb.addRow();
        cb.addRow();
        cb.addRow();cb.addRow();cb.addRow();cb.addRow();cb.addRow();


        w.display(cb.render());
        while(true);

    }

    public static void render(){
        w.display(cb.render());
    }

    /**
     * Note, the following code is needed to run the output program
     * from pyquil.parser import parse_program
     * from pyquil.api import QVMConnection
     * qvm = QVMConnection()
     * p = parse_program("whatever this java code outputs")
     * qvm.wavefunction(p).amplitudes
     */

}
