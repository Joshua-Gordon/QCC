import java.io.*;

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

    private static void fixFile(File src) throws IOException {
        if(!src.exists()) {
            src.createNewFile();
        } else {
            src.delete();
            src.createNewFile();
        }
    }

    public static String runQuil(String code) throws IOException {
        File src = new File("temp.py");
        fixFile(src);
        FileWriter fw = new FileWriter(src);
        fw.write(quilTemplate.replace("CODE",code));
        fw.close();
        Process p = Runtime.getRuntime().exec("python temp.py");
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        return res;
    }

    public static String runQASM(String code) throws IOException {
        File qsrc = new File("test.qasm");
        fixFile(qsrc);
        File src = new File("temp.py");
        fixFile(src);
        FileWriter fw = new FileWriter(qsrc);
        fw.write(code);
        fw.close();
        fw = new FileWriter(src);
        fw.write(qasmTemplate);
        fw.close();
        Process p = Runtime.getRuntime().exec("python temp.py");
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        return res;
    }

}
