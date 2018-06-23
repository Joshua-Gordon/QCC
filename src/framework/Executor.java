package framework;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import preferences.AppPreferences;
import utils.ResourceLoader;

public class Executor {

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
                    "qp.load_qasm_file(\"test.qasm\",name=name)\n" +
                    "if __name__ == \"__main__\":\n" +
                    "   ret = qp.execute([name])\n" +
                    "   print(ret.get_counts(name))";


    public static String runQuil(String code) throws IOException {
        File src = ResourceLoader.addTempFile("temp.py");
        FileWriter fw = new FileWriter(src);
        fw.write(quilTemplate.replace("CODE",code));
        fw.close();
        String interpretorLocation = AppPreferences.get("PyQuil", "Interpreter Location");
        Process p = Runtime.getRuntime().exec(interpretorLocation + " " + src.getAbsolutePath());
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader isr1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        Main.w.getConsole().println(res);
        String s;
        while((s = isr1.readLine()) != null) {
        	Main.w.getConsole().printlnErr(s);
        }
        isr.close();
        return res;
    }

    public static String runQASM(String code) throws IOException {
        File qsrc = ResourceLoader.addTempFile("test.qasm");
        File src = ResourceLoader.addTempFile("temp.py");
        FileWriter fw = new FileWriter(qsrc);
        fw.write(code);
        fw.close();
        fw = new FileWriter(src);
        fw.write(qasmTemplate);
        fw.close();
        String interpretorLocation = AppPreferences.get("QASM", "Interpreter Location");
        Process p = Runtime.getRuntime().exec(interpretorLocation + " " + src.getAbsolutePath());
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader isr1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        Main.w.getConsole().println(res);
        String s;
        while((s = isr1.readLine()) != null) {
        	Main.w.getConsole().printlnErr(s);
        }
        isr.close();
        return res;
    }

}
