package appFX.framework;

import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class Executor {

    /**
     * Executes quil code by writing to a QVM instance and collecting the output
     * @param quil The quil code to be executed.
     * @return The output of the QVM when given the quil code
     */
    static String execute(String quil) {
        //runs qvm -e
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec("qvm -e");
            BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader isr1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));


            p.getOutputStream().write(quil.getBytes());
            p.getOutputStream().close();


            output = isr.lines().map(l -> l + "\n").reduce(String::concat).get();
            System.out.println("Printing output:");
            System.out.println(output);
            System.out.println("Done printing output");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * Executes the current project without relying on any external dependencies such as the QVM or a python install.
     * @param p The project to execute
     * @return A string of the resulting wavefunction after execution
     */
    static String executeInternal(Project p) {
        Stream<ExportedGate> exps = null;
        try {
            exps = GateManager.exportGates(p);
        } catch (GateManager.ExportException e) {
            e.printStackTrace();
        }
        return "";
    }

}
