package Simulator;

import java.util.ArrayList;
import java.util.Random;

import mathLib.Complex;
import mathLib.Vector;

public class Qubit extends Vector<Complex> {

    public Qubit(Complex a, Complex b) {
        super(a,b);
    }

    public Qubit(Vector<Complex> v) {
        super(v);
    }

    public static Qubit ONE() {
        return new Qubit(Complex.ZERO(),Complex.ONE());
    }

    public static Qubit ZERO() {
        return new Qubit(Complex.ONE(),Complex.ZERO());
    }

    public static Qubit PLUS() {
        return new Qubit(Complex.ISQRT2(),Complex.ISQRT2());
    }

    public static Qubit MINUS() {
        return new Qubit(Complex.ISQRT2(),Complex.ISQRT2().negative());
    }

    public static int measure(Vector<Complex> qubits){
        //Check that qubit vector is properly normalized
        ArrayList<Complex> amps = qubits.toArrayList();
        ArrayList<Double> probs = new ArrayList<>();
        double sum = 0;
        for(Complex c : amps) {
            double mag = c.abs();
            double prob = mag*mag;
            sum += prob;
            probs.add(prob);
        }
        if(sum-1 > 0.05) { //error tolerance
            return -1;
        }
        int measured = 0;
        double rand = (new Random()).nextDouble();
        for(int i = 0; i < probs.size(); ++i) {
            rand -= probs.get(i);
            if(rand <= 0) {
                measured = i;
                break;
            }
        }
        //for(int i = probs.size()-1; i >= 0; --i) {
        //    measured += (new Random()).nextDouble() < probs.get(i) ? 1 << i : 0;
        //}
        return measured; //possible bug, may need to bitshift down 1, as 0001 should be zero.
    }

    public static Qubit getInputState(int registers) {
        Qubit start = Qubit.ZERO();
        for(int i = 1; i < registers; ++i) {
            start = new Qubit(start.kronecker(Qubit.ZERO()).toVector());
        }
        return start;
    }

    /**
     * Mutates state of qubit to set index to either one or zero
     * @param idx Index to mutate
     * @param value True if one, False if zero
     */
    public Qubit setIndex(int idx, boolean value) {
        return new Qubit(setSlice(idx,idx,0,0,value? ONE() : ZERO()).toVector());
    }

    /**
     * Returns the amplitude at a specific index of this qubit
     * @param idx The index to get
     * @return the amplitude at that index
     */
    public Complex getIndex(int idx) {
        return this.getSlice(idx,idx,0,0).v(0,0);
    }

}
