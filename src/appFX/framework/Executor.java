package appFX.framework;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {

    static String execute(String quil) {
        //runs qvm -e
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec("qvm -e");
            BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader isr1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            System.err.println(isr.read()); //blocking

            quil.concat("\0");
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

}
