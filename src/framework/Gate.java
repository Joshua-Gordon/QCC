package framework;
import javax.swing.*;

import appUI.AppDialogs;

import java.io.Serializable;
import java.util.ArrayList;
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
    public String name;
    private transient boolean selected = false;
    
    public int length = 0;

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
        Gate g = new Gate(GateType.CNOT);
        String s = null;
        while(true) {
	        try {
	        	s = JOptionPane.showInputDialog("Length of CNOT?");
	        	if(s == null)
	        		return null;
	        	g.length = Integer.parseInt(s);
	        	break;
	        }catch(NumberFormatException fne) {
	        	AppDialogs.lengthNotValid(Main.w.getFrame(), s);
	        }
        }
        return g;
    }
    public static Gate swap(){

        Gate g0 = new Gate(GateType.SWAP);
        String s = null;
        while(true) {
	        try {
	        	s = JOptionPane.showInputDialog("Length of SWAP?");
	        	if(s == null)
	        		return null;
	        	g0.length = Integer.parseInt(s);
	        	break;
	        }catch(NumberFormatException fne) {
	        	AppDialogs.lengthNotValid(Main.w.getFrame(), s);
	        }
        }
        return g0;
    }

    public static Gate customGate() {
        String s;
        boolean done = false;
        ArrayList<Integer> regs = new ArrayList<>();
        String name = JOptionPane.showInputDialog("What is the gate called?");

        while(!done) {
            s = JOptionPane.showInputDialog("Which qubits?");
            try{
                regs.add(Integer.parseInt(s));
            } catch (NumberFormatException nfe) {
                done = true;
            }
        }
        if(Main.cb.customGates.containsKey(name)){
            MultiQubitGate gOld = ((MultiQubitGate) Main.cb.customGates.get(name));
            Gate g = new MultiQubitGate(gOld.matrix,GateType.CUSTOM,regs);
            g.name = name;
            g.length = regs.stream().max(Math::max).get() - regs.stream().min(Math::min).get();
            return g;
        }
        int len = 1 << regs.size();
        Complex[][] m = new Complex[len][len];
        for(int y = 0; y < len; ++y) {
            for(int x = 0; x < len; ++x) {
                m[x][y] = Complex.parseComplex(JOptionPane.showInputDialog("Element (" + x + "," + y + ")"));
            }
        }
        Gate g = new MultiQubitGate(m,GateType.CUSTOM,regs);
        g.name = name;
        g.length = regs.stream().max(Math::max).get() - regs.stream().min(Math::min).get();
        Main.cb.customGates.put(name,g);
        return g;
    }

    
    public boolean isSelected() {
    	return selected;
    }
    
    public void setSelected(boolean selected) {
    	this.selected = selected;
    }

    @Override
    public String toString() {
        String out = "Gate type: " + this.type.toString() + "\n";
        if(this.type == GateType.CUSTOM) {
            out += "Matrix:\n";
            for(int x = 0; x < this.matrix.length; ++x) {
                for(int y = 0; y < this.matrix.length; ++y) {
                    out += this.matrix[x][y].toString() + " ";
                }
                out += "\n";
            }
        }
        return out;
    }
}
