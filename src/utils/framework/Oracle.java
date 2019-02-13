package framework;

import java.awt.BorderLayout;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import mathLib.Complex;
import mathLib.Matrix;

/**
 * Methods to construct a quantum oracle as a multi-qubit gate
 */
public class Oracle { //I really hope this doesn't conflict with any standard library components


    private static String code;
    private static int width = 300;
    private static int height = 250;

    /**
     *
     * @return A multi-qubit gate that computes the oracle using an ancilla. Asks user to input a boolean function with Nashorn.
     * @throws ScriptException When the user writes code that throws an error
     * @throws NoSuchMethodException If the user changes the name of the provided function
     */
    public static CustomGate createAncillaOracle() throws ScriptException, NoSuchMethodException {
        int numQubits = Integer.parseInt(JOptionPane.showInputDialog("How many qubits?"));
        String javascript = textDialog();

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(javascript);

        Invocable runtime = (Invocable) engine;
        int size = 1<<(1+numQubits);

        Matrix<Complex> mat = new Matrix<Complex>(Complex.ZERO(), size, size);
        for(int x = 0; x < size; ++x){
            int length = (int)Math.ceil(Math.log(size));
            String binary = Integer.toBinaryString(x);
            for(int i = 0; binary.length() < length; ++i) {
                binary = "0" + binary;
            }
            System.out.println("Binary " + binary);
            boolean[] qubits = new boolean[binary.length()];
            for(int i = 0; i < binary.length(); ++i) {
                qubits[i] = binary.charAt(i) == '1';
            }
            boolean out = (boolean)runtime.invokeFunction("oracle",qubits);
            System.out.println("X: " + x +"\nOut: " + out);
            for(int y = 0; y < size; ++y){
                if(!out && x == y){
                    mat.r(Complex.ONE(), x, y);
                } else if(out && (size-1-x) == y){
                	mat.r(Complex.ONE(), x, y);
                } else {
                	mat.r(Complex.ZERO(), x, y);
                }
            }
        }

//        ArrayList<Integer> regs = new ArrayList<>();
//        for(int i = 0; i < numQubits; ++i) {
//            regs.add(Integer.parseInt(JOptionPane.showInputDialog("Register for qubit " + i)));
//        }
//        return new MultiQubitGate(mat, DefaultGate.GateType.CUSTOM,regs);
        return new CustomGate(mat);
    }

    /**
     * Creates a dialog that allows the user to input code
     * @return The javascript code for  the oracle
     */
    private static String textDialog() {
        code = "";
        final JFrame frame = new JFrame("Not an oracle product");
        frame.setSize(width,height);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JDialog d = new JDialog(frame);
        d.setLayout(new BorderLayout());
        JButton button = new JButton("Submit function");
        d.add(button,BorderLayout.SOUTH);
        JTextArea textfield = new JTextArea();
        textfield.setText(getText());
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

    /**
     * Generates the code for the oracle function
     * @return
     */
    private static String getText() {
        String function = "function oracle(qbitarray)"; //Switched to array
        function += "{\n\n}\n//Predicate function on array of bools";
        return function;
    }
}
