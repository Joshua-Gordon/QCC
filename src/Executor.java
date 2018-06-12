import java.io.*;

public class Executor {

    static final String quilTemplate =
            "from pyquil.parser import parse_program\n" +
            "from pyquil.api import QVMConnection\n" +
            "qvm = QVMConnection()\n" +
            "p = parse_program(\"\"\"CODE\"\"\")\n" +
            "print(qvm.wavefunction(p).amplitudes)\n";

    public static String runQuil(String code) throws IOException {
        File src = new File("temp.py");
        if(!src.exists()) {
            src.createNewFile();
        } else {
            src.delete();
            src.createNewFile();
        }
        FileWriter fw = new FileWriter(src);
        fw.write(quilTemplate.replace("CODE",code));
        fw.close();
        Process p = Runtime.getRuntime().exec("python temp.py");
        BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String res = isr.lines().reduce("",(x,y)-> x+"\n"+y);
        System.out.println(res);
        return res;
    }

}
