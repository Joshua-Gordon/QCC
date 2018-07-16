package Simulator;

import framework.CircuitBoard;
import framework.ExportGatesRunnable;
import framework.ExportedGate;
import mathLib.Complex;
import mathLib.Matrix;

import java.util.ArrayList;

public class InternalExecutor {

    static ArrayList<Matrix<Complex>> gates;
    static Matrix<Complex> column;
    static Qubit input;

    public static int simulate(CircuitBoard cb) {
        gates = new ArrayList<>();
        input = Qubit.getInputState(cb.getRows());
        ExportedGate.exportGates(cb, new ExportGatesRunnable() {
            @Override
            public void gateExported(ExportedGate eg, int x, int y) {
                if(column == null) {
                    column = eg.getAbstractGate().getMatrix();
                } else {
                    column = column.kronecker(eg.getAbstractGate().getMatrix());
                }
            }

            @Override
            public void nextColumnEvent(int column) {
                gates.add(InternalExecutor.column);
                InternalExecutor.column = null;
            }

            @Override
            public void columnEndEvent(int column) {

            }
        });
        Qubit output = (Qubit) gates.stream().reduce(input,(state,gate)->gate.mult(state));
        return Qubit.measure(output);
    }
}
