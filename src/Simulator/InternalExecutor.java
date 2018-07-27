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
    private static int stack;

    public static int simulate(CircuitBoard cb) {
        gates = new ArrayList<>();
        input = Qubit.getInputState(cb.getRows());
        //column = new Matrix<Complex>(Complex.I(),cb.getRows(),cb.getColumns());
        stack = 0;
        ExportedGate.exportGates(cb, new ExportGatesRunnable() {
            @Override
            public void gateExported(ExportedGate eg, int x, int y) {
                stack--;
                if(column == null) {
                    column = eg.getAbstractGate().getMatrix();
                } else {
                    if(stack<=0)
                        column = column.kronecker(eg.getAbstractGate().getMatrix());
                }
                stack+=eg.getHeight();
            }

            @Override
            public void nextColumnEvent(int column) {
                if(column>0)
                    gates.add(InternalExecutor.column);
                InternalExecutor.column = null;
            }

            @Override
            public void columnEndEvent(int column) {

            }
        }); //TODO: gates is size 0 when testing with one hadamard gate
        Matrix<Complex> output = gates.stream().reduce(input,(state,gate)->gate.mult(state));
        return Qubit.measure(output.toVector());
    }
}
