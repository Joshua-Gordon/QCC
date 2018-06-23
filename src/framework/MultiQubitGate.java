package framework;

import javax.swing.*;
import java.util.ArrayList;

public class MultiQubitGate extends Gate {

    ArrayList<Integer> registers;

    public MultiQubitGate(Complex[][] mat, GateType gt, ArrayList<Integer> registers) {
        super(mat, gt);
        this.registers = registers;
    }


}
