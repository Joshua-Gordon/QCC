package Simulator;

import mathLib.Complex;
import mathLib.Matrix;

import java.util.ArrayList;
import java.util.Random;

public class MixedState {

    private Matrix<Complex> density;
    private ArrayList<Qubit> pureStates;
    private ArrayList<Double> probabilites;

    public MixedState(ArrayList<Qubit> pureStates,ArrayList<Double> probabilites) {
        this.pureStates = pureStates;
        this.probabilites = probabilites;
        int size = pureStates.get(0).length(); //size of our square density matrix
        this.density = new Matrix<>(Complex.ZERO(),size,size); //allocate memory for density matrix
        for(int i = 0; i < pureStates.size(); ++i) { //add the weighted average of each pure state to the density matrix
            Matrix<Complex> state = pureStates.get(i);
            //here we get the current pure state in the loop. We make a complex number of the probability and scale the vector by it.
            Matrix<Complex> outer = state.mult(state.transpose()).mult(new Complex(probabilites.get(i),0)); //outer product, scaled
            this.density = this.density.add(outer);
        }
    }

    public Qubit measure() {
        Random r = new Random();
        double prob = r.nextDouble(); //Random double uniform on (0,1)
        for(int i = 0; i < pureStates.size(); ++i) {
            prob -= probabilites.get(i);
            if(prob <= 0) {
                return pureStates.get(i);
            }
        }
        System.err.println("Probabilities in mixed state did not sum up to 1");
        return null;
    }

    public Matrix<Complex> getDensityMatrix() {
        return density;
    }

}
