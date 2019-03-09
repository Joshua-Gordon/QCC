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
        addCols: for(Iterator<ExportedGate> itr = exps.iterator(); itr.hasNext();) {
            ArrayList<ExportedGate> column = new ArrayList<>();
            for(int i = 0; i < colHeight;) {
                if(!itr.hasNext()) {
                  break addCols;
                }
                ExportedGate eg = itr.next();
                i += eg.getGateRegister().length;
                column.add(eg);
            }
            columns.add(buildColumnMatrix(column,colHeight));
        } //Columns built
        Matrix<Complex> in = getInVector(colHeight);
        System.out.println("Beginning input vector multiplication");
        for(Matrix<Complex> m : columns) {
            System.out.println(m);
            System.out.println(m.getColumns());
            System.out.println(in);
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
   static Matrix<Complex> buildColumnMatrix(ArrayList<ExportedGate> column, int colheight) {
       //Assumes no overlapping circuit components
       //Perhaps place swap gates to ensure this automatically?
       Matrix<Complex> mat = null;
       Matrix<Complex> colmat = null;
       Matrix<Complex> swapBuffer = Matrix.identity(Complex.ZERO(),1<<colheight);
       int itr = 0; //itr is the gate we are processing, i is the register we are processing
       for (int i = 0; i < colheight; itr++) {
           ExportedGate eg = column.get(itr);
           colmat = eg.getInputMatrixes()[0];
           int span = 1+getMaxElement(eg.getGateRegister())-getMinElement(eg.getGateRegister());
           if (eg.getControls().length != 0) {

           }
           //Shuffle eg to be contiguous and in order, then pad with identity
           /*
           So if there is a gate with registers
           3
           1
           -
           2
           It becomes
           1
           2
           3
           -
           With a swap buffer
            */
           if (eg.getGateRegister().length != 1) {
               swapBuffer = swapBuffer.mult(getSwapMat(eg.getGateRegister(),colheight));
               System.out.println("buildColumnMatrix: swapBuffer is of size " + swapBuffer.getRows());
               System.out.println(swapBuffer);
               Matrix<Complex> adjustedColmat = colmat.kronecker(Matrix.identity(Complex.ZERO(),1<<(span-eg.getGateRegister().length)));
               System.out.println("buildColumnMatrix: Adjusted colmat is of size " + adjustedColmat.getRows());
               System.out.println(adjustedColmat);
               colmat = adjustedColmat;
           }
           if(mat == null) {
               mat = colmat;
           } else {
               mat = mat.kronecker(colmat);
           }
           i += span;
           itr += span-eg.getGateRegister().length;
       }
       System.out.println("Column matrix size: " + mat.getRows());
       System.out.println("Swap buffer size: " + swapBuffer.getRows());
       return swapBuffer.mult(mat).mult(swapBuffer.transpose());
   }

    private static int getMaxElement(int[] arr) {
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
    private static int getMinElement(int[] arr) {
       if(arr.length==0){
           return -1;
       }
       int min = arr[0];
       for(int i = 1; i < arr.length; ++i) {
           if(min > arr[i]) {
               min = arr[i];
           }
       }
       return min;
    }


    private static Matrix<Complex> getSwapMat(int[] regs, int columnHeight) {
        int len = regs.length;
        Matrix<Complex> swapMat = Matrix.identity(Complex.ZERO(),4);
        swapMat.r(Complex.ZERO(),1,1);
        swapMat.r(Complex.ZERO(),2,2);
        swapMat.r(Complex.ONE(),2,1);
        swapMat.r(Complex.ONE(),1,2);
        //System.out.println("Swap Matrix: " + swapMat.toString());
        Matrix<Complex> buffer = Matrix.identity(Complex.ZERO(),1<<columnHeight);
        /*
        Algorithm: Build swap buffer by bubble-sort like process
        Bring 1st register to top, then second register to second place, etc.
         */
        for(int i = 0; i < len; ++i) {
           int ri = regs[i];
           Matrix<Complex> sc = farSwap(i,ri,columnHeight);
           buffer = buffer.mult(sc);
        }
        return buffer;
    }




    private static Matrix<Complex> farSwap(int p1, int p2, int columnHeight) {
        Matrix<Complex> swapMat = Matrix.identity(Complex.ZERO(),4);
        swapMat.r(Complex.ZERO(),1,1);
        swapMat.r(Complex.ZERO(),2,2);
        swapMat.r(Complex.ONE(),2,1);
        swapMat.r(Complex.ONE(),1,2);

        Matrix<Complex> farSwap = Matrix.identity(Complex.ZERO(),1<<columnHeight);
        if(p1 == p2) {
            return farSwap;
        }
        if(p1 < p2) {
            for(int i = 0; i < p2-p1; ++i) {
                Matrix<Complex> nextLink = identityPad(swapMat,p1+i,columnHeight);
                farSwap = farSwap.mult(nextLink);
            }
            for(int i = 1; i < p2-p1; ++i) {
                Matrix<Complex> nextLink = identityPad(swapMat,p2-i,columnHeight);
                farSwap = farSwap.mult(nextLink);
            }
            return farSwap;
        } else {
             return farSwap(p2,p1,columnHeight);
        }
    }

    /**
     * Takes a gate and returns the matrix for a column consisting of only that gate
     * A call to identityPad with SWAP at 1 and a size of 4 should return
     * ID
     * SWAP1
     * SWAP2
     * ID
     * @param gate The matrix of the gate to be used in the column
     * @param position Where the gate should be in the column; zero indexed
     * @param columnHeight The size of the column
     * @return A matrix representing a column of gates containing identity and the one gate given to the function
     */
    private static Matrix<Complex> identityPad(Matrix<Complex> gate, int position, int columnHeight) {
        int numberOfQubits = 0;
        while(gate.getRows()>>++numberOfQubits > 1);
        Matrix<Complex> column = Matrix.identity(Complex.ZERO(),1);
        for(int i = 0; i < position; ++i) {
            column = column.kronecker(Matrix.identity(Complex.ZERO(),2));
        }
        column = column.kronecker(gate);
        for(int i = position+numberOfQubits; i < columnHeight; ++i) {
            column = column.kronecker(Matrix.identity(Complex.ZERO(),2));
        }
        return column;
    }
}
