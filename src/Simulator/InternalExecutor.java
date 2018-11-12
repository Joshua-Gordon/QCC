package Simulator;

import framework.*;
import mathLib.Complex;
import mathLib.Matrix;

import java.util.ArrayList;
import java.util.Random;

public class InternalExecutor {

    /*
    Functions in this class make heavy use of the exportGates functionality,
    which involves putting most logic inside of an inner class. Due to Java,
    this requires making variables static. Static variables cannot be declared
    within methods, so here we are declaring them all at the top of the class.
     */

    //For use in getGates
    private static ArrayList<Matrix<Complex>> gates;
    private static Matrix<Complex> column;
    private static Qubit input;
    private static int stack;

    //For use in createMixedState
    private static ArrayList<Qubit> pureStates;
    private static ArrayList<Double> probabilities;
    private static ArrayList<Integer> toMeasure = new ArrayList<>();
    private static int location;

    /**
     * Executes a purely quantum circuit
     * @param cb The circuit board to be executed
     * @return An integer that, if interpreted binary unsigned, represents the measured state at the end of the circuit
     */
    public static int simulate(CircuitBoard cb) {
        getGates(cb);
        Matrix<Complex> output = gates.stream().reduce(input,(state,gate)->gate.mult(state));
        return Qubit.measure(output.toVector());
    }

    /**
     * Creates a mixed state object out of a circuitboard with measurement gates
     * @param cb The circuit board to extract the state from
     * @return the mixed state object representing this circuit board
     */
    public static MixedState createMixedState(CircuitBoard cb) {

        input = Qubit.getInputState(cb.getRows());
        pureStates = new ArrayList<>();
        probabilities = new ArrayList<>();
        pureStates.add(input);
        probabilities.add(1.0);

        ExportedGate.exportGates(cb, new ExportGatesRunnable() {

            ArrayList<Matrix<Complex>> acc = new ArrayList<>();
            ArrayList<Integer> toMeasure = new ArrayList<>();

            @Override
            public void gateExported(ExportedGate eg, int x, int y) {
                if(eg.getAbstractGate().getName().equals("MEASURE")) {
                    acc.add(Matrix.identity(Complex.I(),2));
                    toMeasure.add(y);
                } else {
                    acc.add(eg.getExportedMatrix());
                }
            }

            @Override
            public void nextColumnEvent(int column) {
                location = 0;
                acc = new ArrayList<>();
                toMeasure = new ArrayList<>();
            }

            @Override
            public void columnEndEvent(int column) {
                int size = pureStates.size(); //purestates size may change during loop
                for(int i = 0; i < size; ++i) {// we do not want to access newly added items
                    Qubit state = pureStates.get(i);
                    Matrix<Complex> fullmat = acc.stream().reduce(Matrix.identity(Complex.I(),1),Matrix::kronecker);
                    if (toMeasure.size() == 0) {
                        Qubit newstate = new Qubit(fullmat.mult(state).toVector());
                        pureStates.set(i,newstate);
                    } else {
                        for(int y = 0; y < toMeasure.size(); ++y) {
                            Qubit newstate = new Qubit(fullmat.mult(state).toVector());
                            Complex amp = state.getIndex(toMeasure.get(y));
                            double prob = amp.mult(amp).abs();
                            Qubit new0 = newstate.setIndex(toMeasure.get(y),false);
                            Qubit new1 = newstate.setIndex(toMeasure.get(y),true);
                            pureStates.set(i,new0);
                            probabilities.set(i,1-prob);
                            pureStates.add(new1);
                            probabilities.add(prob);
                        }
                    }
                }
                for(Matrix<Complex> m : pureStates) {
                    System.out.println(m);
                }
            }

        });
        return new MixedState(pureStates,probabilities);
    }

    private static void getGates(CircuitBoard cb) {
        gates = new ArrayList<>();
        input = Qubit.getInputState(cb.getRows());
        //column = new Matrix<Complex>(Complex.I(),cb.getRows(),cb.getColumns());
        stack = 0;
        ExportedGate.exportGates(cb, new ExportGatesRunnable() {
            @Override
            public void gateExported(ExportedGate eg, int x, int y) {
                stack--;
                if (column == null) {
                    column = eg.getAbstractGate().getMatrix();
                } else {
                    if (stack <= 0)
                        column = column.kronecker(eg.getAbstractGate().getMatrix());
                }
                stack += eg.getHeight();
            }

            @Override
            public void nextColumnEvent(int column) {

            }

            @Override
            public void columnEndEvent(int column) {
                System.err.println("Column: " + column);
                if (column >= 0)
                    gates.add(InternalExecutor.column);
                InternalExecutor.column = null;
            }
        });
    }


}
