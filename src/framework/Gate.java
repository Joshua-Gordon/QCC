package framework;
import javax.swing.*;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Gate implements Serializable{
	private static final long serialVersionUID = 6220371128991814182L;
	
	
	public enum GateType{
        I,X,Y,Z,H,CUSTOM, MEASURE, CNOT, SWAP
    }
    public enum LangType{
        QUIL,QASM,QUIPPER
    }
    
    public static int GATE_PIXEL_SIZE = 64;

    public Complex[][] matrix;
    public GateType type;
    private boolean selected = false;
    
    int length = 0;

    public static String typeToString(GateType gt, LangType lt){
        if(lt == LangType.QUIL) {
            switch (gt) {
                case I:
                    return "I";
                case X:
                    return "X";
                case Y:
                    return "Y";
                case Z:
                    return "Z";
                case H:
                    return "H";
                case MEASURE:
                    return "MEASURE";
                case CNOT:
                    return "CNOT";
                case SWAP:
                    return "SWAP";
            }
        } else if(lt == LangType.QASM) {
            switch (gt) {
                case I:
                    return "Well, I guess this string isn't really needed, but here it is";
                case H:
                    return "h";
                case X:
                    return "x";
                case Y:
                    return "y";
                case Z:
                    return "z";
                case CNOT:
                    return "cx";
                case MEASURE:
                    return "measure";
                case SWAP:
                    return "cx"; //Don't worry, this is intentional
            }
        } else if(lt == LangType.QUIPPER) {
            switch (gt) {
                case I:
                    return "Well, I guess this string isn't really needed, but here it is";
                case H:
                    return "QGate[\"H\"]";
                case X:
                    return "QGate[\"not\"]";
                case Y:
                    return "QGate[\"Y\"]";
                case Z:
                    return "QGate[\"Z\"]";
                case CNOT:
                    return "QGate[\"not\"]";
                case MEASURE:
                    return "measure";
                case SWAP:
                    return "QGate[\"not\"]"; //Don't worry, this is intentional
            }
        }
        return "ERROR";
    }

    public Gate(Complex[][] mat, GateType gt) {
        this.matrix = mat;
        this.type = gt;
    }

    public Gate(GateType gt){
        matrix = identity().matrix;
        this.type = gt;
    }

    public static Gate identity() {
        Complex[][] mat = new Complex[2][2];
        mat[0][0] = Complex.ONE();
        mat[0][1] = Complex.ZERO();
        mat[1][0] = Complex.ZERO();
        mat[1][1] = Complex.ONE();
        return new Gate(mat,GateType.I);
    }

    public static Gate hadamard() { //bam
        Complex[][] mat = new Complex[2][2];
        mat[0][0] = Complex.ONE().multiply(Complex.ISQRT2());
        mat[0][1] = Complex.ONE().multiply(Complex.ISQRT2());
        mat[1][0] = Complex.ONE().multiply(Complex.ISQRT2());
        mat[1][1] = Complex.ONE().multiply(Complex.ISQRT2()).negative();
        return new Gate(mat,GateType.H);
    }

    public static Gate x(){
        Complex[][] mat = new Complex[2][2];
        mat[0][0] = Complex.ZERO();
        mat[0][1] = Complex.ONE();
        mat[1][0] = Complex.ONE();
        mat[1][1] = Complex.ZERO();
        return new Gate(mat,GateType.X);
    }

    public static Gate y(){
        Complex[][] mat = new Complex[2][2];
        mat[0][0] = Complex.ZERO();
        mat[0][1] = Complex.I().negative();
        mat[1][0] = Complex.I();
        mat[1][1] = Complex.ZERO();
        return new Gate(mat,GateType.Y);
    }

    public static Gate z(){
        Complex[][] mat = new Complex[2][2];
        mat[0][0] = Complex.ONE();
        mat[0][1] = Complex.ZERO();
        mat[1][0] = Complex.ZERO();
        mat[1][1] = Complex.ONE().negative();
        return new Gate(mat,GateType.Z);
    }

    public static Gate measure(){
        return new Gate(GateType.MEASURE);
    }
    public static Gate cnot(){
        /*Complex[][] mat = new Complex[4][4];
        for(int x = 0; x < 4; ++x){
            for(int y = 0; y < 4; ++y){
                mat[x][y] = x == y ? Complex.ONE() : Complex.ZERO();
            }
        }
        mat[2][2] = mat[3][3] = Complex.ZERO();
        mat[2][3] = mat[3][2] = Complex.ONE();*/
        Gate g = new Gate(GateType.CNOT);

        g.length = Integer.parseInt(JOptionPane.showInputDialog("Length of CNOT?"));
        return g;
    }
    public static Gate swap(){

        Gate g0 = new Gate(GateType.SWAP);
        g0.length = Integer.parseInt(JOptionPane.showInputDialog("Length of SWAP?"));
        return g0;
    }
    
    public boolean isSelected() {
    	return selected;
    }
    
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }
}
