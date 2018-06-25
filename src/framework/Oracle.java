package framework;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Oracle { //I really hope this doesn't conflict with any standard library components

    private static String code;
    private static int width = 300;
    private static int height = 250;

    public static MultiQubitGate createPhaseOracle() throws ScriptException, NoSuchMethodException {
        int numQubits = Integer.parseInt(JOptionPane.showInputDialog("How many qubits?"));
        String javascript = textDialog(numQubits);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(javascript);

        Invocable runtime = (Invocable) engine;

        Complex[][] mat = new Complex[numQubits << 1][numQubits << 1];
        for(int x = 0; x < numQubits; ++x) {
            for(int y = 0; y < numQubits; ++y) {
                //Placeholder. Have to figure out how this works
                mat[x][y] = Complex.parseComplex(runtime.invokeFunction("oracle",x,y).toString());
                
            }
        }


        boolean done = false;
        ArrayList<Integer> regs = new ArrayList<>();
        while(!done) {
            String s = JOptionPane.showInputDialog("Which qubits?");
            try{
                regs.add(Integer.parseInt(s));
            } catch (NumberFormatException nfe) {
                done = true;
            }
        }

        return new MultiQubitGate(mat, Gate.GateType.CUSTOM,regs);
    }

    private static String textDialog(int numQubits) {
        code = "";
        final JFrame frame = new JFrame("Not an oracle product");
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JDialog d = new JDialog(frame);
        d.setLayout(new BorderLayout());
        JButton button = new JButton("Submit function");
        d.add(button,BorderLayout.SOUTH);
        JTextArea textfield = new JTextArea();
        textfield.setText(getText(numQubits));
        d.add(textfield,BorderLayout.CENTER);
        button.addActionListener(e -> {
            code = textfield.getText();
            d.dispose();
        });
        d.setSize(width,height);
        d.setVisible(true);
        while(code.equals("")){
            //Spin
        }
        return code;
    }

    private static String getText(int numQubits) {
        String function = "function oracle(q0";
        for(int i = 1; i < numQubits; ++i){
            function += ", q" + i;
        }
        function += "){\n\n}";
        return function;
    }
}
