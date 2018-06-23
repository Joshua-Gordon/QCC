package framework;

import javax.swing.*;
import java.util.ArrayList;

public class MultiQubitGate extends Gate {

    public ArrayList<Integer> registers; //render the gate on the first index

    public MultiQubitGate(Complex[][] mat, GateType gt, ArrayList<Integer> registers) {
        super(mat, gt);
        this.registers = registers;
    }


}
