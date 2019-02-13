package framework;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import preferences.AppPreferences;
import utils.ResourceLoader;

public class ExternalExecutor {

    static final String quilTemplate =
            "from pyquil.parser import parse_program\n" +
                    "from pyquil.api import QVMConnection\n" +
                    "qvm = QVMConnection()\n" +
                    "p = parse_program(\"\"\"CODE\"\"\")\n" +
                    "print(qvm.wavefunction(p).amplitudes)\n";

    static final String qasmTemplate =
            "import qiskit\n" +
                    "qp = qiskit.QuantumProgram()\n" +
                    "name = \"test\"\n" +
                    "qp.load_qasm_file(\"{}\",name=name)\n" +
                    "if __name__ == \"__main__\":\n" +
                    "   ret = qp.execute([name])\n" +
                    "   print(ret.get_counts(name))";


    /**
     * This runs the current circuit using a QUIL backend
     * @param code A quil program as a string
     * @return The output of the QUIL simulator
     * @throws IOException if the file cannot be run
     */
    public static String runQuil(String code) throws IOException {
        File src = ResourceLoader.addTempFile("temp.py");
        FileWriter fw = new FileWriter(src);
        fw.write(quilTemplate.replace("CODE",code));
        fw.close();
        String interpreterLocation = AppPreferences.get("PyQuil", "Interpreter Location");
        Process p = Runtime.getRuntime().exec(interpreterLocation + " " + src.getAbsolutePath());
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader isr1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        Main.getWindow().getConsole().println(res);
        String s;
        while((s = isr1.readLine()) != null) {
        	Main.getWindow().getConsole().printlnErr(s);
        }
        isr.close();
        return res;
    }

    /**
     * Runs circuit with QASM backend.
     * @param code
     * @return
     * @throws IOException
     */
    public static String runQASM(String code) throws IOException {
        File qsrc = ResourceLoader.addTempFile("/test.qasm");
        File src = ResourceLoader.addTempFile("/temp.py");

        FileWriter fw = new FileWriter(qsrc);
        fw.write(code);
        fw.close();
        fw = new FileWriter(src);
        String toWrite = qasmTemplate.replace("{}",qsrc.getCanonicalPath().replace("\\","\\\\"));
        System.out.println(toWrite);
        fw.write(toWrite);
        fw.close();

        String interpreterLocation = AppPreferences.get("QASM", "Interpreter Location");
        Process p = Runtime.getRuntime().exec(interpreterLocation + " " + src.getAbsolutePath());
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader isr1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        Main.getWindow().getConsole().println(res);
        String s;
        while((s = isr1.readLine()) != null) {
        	Main.getWindow().getConsole().printlnErr(s);
        }
        isr.close();
        return res;
    }

}
