package appFX.framework;

import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.ExportedGate;
import appFX.framework.exportGates.GateManager.Exportable;
import appFX.framework.exportGates.GateManager;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;

public class Executor {

    /**
     * Executes quil code by writing to a QVM instance and collecting the output
     * @param quil The quil code to be executed.
     * @return The output of the QVM when given the quil code
     */
    static String execute(String quil) {
        //runs qvm -e
        String output = "";
        try {
            Process p = Runtime.getRuntime().exec("qvm -e");
            BufferedReader isr = new BufferedReader(new InputStreamReader(p.getInputStream()));
            p.getOutputStream().write(quil.getBytes());
            p.getOutputStream().close();


            output = isr.lines().map(l -> l + "\n").reduce(String::concat).get();
            System.out.println("Printing output:");
            System.out.println(output);
            System.out.println("Done printing output");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }



    /**
     * Executes the current project without relying on any external dependencies such as the QVM or a python install.
     * @param p The project to execute
     * @return A string of the resulting wavefunction after execution
     */
    static String executeInternal(Project p) {
        Stream<ExportedGate> exps = null;
        try {
            exps = GateManager.exportGates(p);
            System.out.println("Gate stream created");
        } catch (GateManager.ExportException e) {
            System.err.println("Could not create gate stream");
            e.printStackTrace();
        } //To compute the number of registers in the circuit, we check the maximum register index in the circuit that is not an identity gate
        CircuitBoardModel cb = (CircuitBoardModel) p.getGateModel(p.getTopLevelCircuitName());
        int colHeight = cb.getRows();
        System.out.println(colHeight);
        ArrayList<Matrix<Complex>> columns = new ArrayList<>();
        for(Iterator<ExportedGate> itr = exps.iterator(); itr.hasNext();) {
            ArrayList<ExportedGate> column = new ArrayList<>();
            for(int i = 0; i < colHeight; ++i) {
                column.add(itr.next());
            }
            columns.add(buildColumnMatrix(column));
        } //Columns built
        Matrix<Complex> in = getInVector(colHeight);
        for(Matrix<Complex> m : columns) {
            in = m.mult(in);
        }
        return in.toString();
    }

    static Matrix<Complex> getInVector(int numregs) {
        Matrix<Complex> input = new Matrix<Complex>(Complex.ZERO(),1<<numregs,1);
        input.r(Complex.ONE(),0,0);
        for(int i = 1; i < numregs; ++i) {
            input.r(Complex.ZERO(),i,0);
        }
        System.out.println("Input vector: ");
        System.out.println(input);
        return input;
    }

    /**
     * Assumes a pure-quantum state and builds the resulting matrix for a column of gates in the circuit
     * @param column An arraylist of exportables representing a column in the circuit
     * @return The matrix of the column
     */
   static Matrix<Complex> buildColumnMatrix(ArrayList<ExportedGate> column) {
       //Assumes no overlapping circuit components
       //Perhaps place swap gates to ensure this automatically?
       Matrix<Complex> mat = null;
       Matrix<Complex> colmat = null;
       for (int i = 0; i < column.size(); ++i) {
           ExportedGate eg = column.get(i);
           colmat = eg.getInputMatrixes()[0];
           if (eg.getControls().length != 0) {

           }
           if (eg.getGateRegister().length != 1) {

           }
           if(mat == null) {
               mat = colmat;
           } else {
               mat = mat.kronecker(colmat);
           }
       }
       System.out.println(mat);
       return mat;
   }

    static int getMaxElement(int[] arr) {
        if(arr.length==0) {
            return -1;
        }
        int max = arr[0];
        for(int i = 1; i < arr.length; ++i) {
            if(max < arr[i]) {
                max = arr[i];
            }
        }
        return max;
    }

    static Matrix<Complex> getSwapMat(int[] regs) {
       int len = regs.length;
       Matrix<Complex> swapMat = Matrix.identity(Complex.ZERO(),4);
       for(int i = 0; i < len; ++i) {
           int ri = regs[i];
           while(ri != i) {

           }
       }
       return swapMat;
    }
}
