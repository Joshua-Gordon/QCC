package framework;

import mathLib.Complex;
import mathLib.Matrix;
import utils.GateIcon;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;

public class GateMap extends DefaultListModel<AbstractGate> {
    private static HashMap<String,AbstractGate> map = new HashMap<>();

    public static void loadGates(){

//		Gates with default icons :

        Matrix<Complex> identity = new Matrix<>(2, 2,
                Complex.ONE(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE());
        DefaultGate gate = new DefaultGate("I", identity, AbstractGate.GateType.I);
        map.put("I",gate);

        Matrix<Complex> mat = new Matrix<>(2, 2,
                Complex.ONE(), Complex.ONE(),
                Complex.ONE(), Complex.ONE().negative())
                .mult(Complex.ISQRT2());
        gate = new DefaultGate("H", mat, AbstractGate.GateType.H);
        map.put("H",gate);

        mat = new Matrix<>(2, 2,
                Complex.ZERO(), Complex.ONE(),
                Complex.ONE(), Complex.ZERO());
        gate = new DefaultGate("X", mat, AbstractGate.GateType.X);
        map.put("X",gate);

        mat = new Matrix<>(2, 2,
                Complex.ZERO(), Complex.I().negative(),
                Complex.I(), Complex.ZERO());
        gate = new DefaultGate("Y", mat, AbstractGate.GateType.Y);
        map.put("Y",gate);

        mat = new Matrix<>(2, 2,
                Complex.ONE(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE().negative());
        gate = new DefaultGate("Z", mat, AbstractGate.GateType.Z);
        map.put("Z",gate);

        map.values().forEach(AbstractGate::loadIcon);


//		Gates with custom icons:

        gate = new DefaultGate("MEASURE", identity, AbstractGate.GateType.MEASURE);
        gate.setIcon(GateIcon.getMeasureIcon());
        map.put("MEASURE",gate);

        mat = new Matrix<>(4, 4,
                Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE());
        gate = new DefaultGate("SWAP", mat, AbstractGate.GateType.SWAP);
        gate.setIcon(GateIcon.getSwapIcon());
        map.put("SWAP",gate);

        mat = new Matrix<>(4, 4,
                Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
                Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO());
        gate = new DefaultGate("CNOT", mat, AbstractGate.GateType.CNOT);
        gate.setIcon(GateIcon.getCNotIcon());
        map.put("CNOT",gate);
    }

    @Override
    public AbstractGate getElementAt(int index) {
        Iterator<AbstractGate> it = map.values().iterator();
        for(int i = 0; i < index; ++i) {
            it.next();
        }
        return it.next();
    }

    public static AbstractGate lookup(String name) {
        return map.get(name);
    }
    
}
