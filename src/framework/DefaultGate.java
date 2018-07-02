package framework;
import java.io.Serializable;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import mathLib.Complex;
import mathLib.Matrix;
import utils.AppDialogs;
import utils.GateIcon;

public class DefaultGate extends AbstractGate implements Serializable{
	private static final long serialVersionUID = 6220371128991814182L;
	
    public static final DefaultListModel<AbstractGate> DEFAULT_GATES = new DefaultListModel<>();
	
    public static enum LangType{
        QUIL,QASM,QUIPPER
    }
    
	public static void loadGates(){
		
//		Gates with default icons :
		
		Matrix<Complex> identity = new Matrix<>(2, 2, 
        		Complex.ONE(), Complex.ZERO(),
        		Complex.ZERO(), Complex.ONE());
        DefaultGate gate = new DefaultGate("I", identity,GateType.I);
		DEFAULT_GATES.addElement(gate);
		
		Matrix<Complex> mat = new Matrix<>(2, 2, 
        		Complex.ONE(), Complex.ONE(), 
        		Complex.ONE(), Complex.ONE().negative())
        		.mult(Complex.ISQRT2());
		gate = new DefaultGate("H", mat,GateType.H);
		DEFAULT_GATES.addElement(gate);
		
		mat = new Matrix<>(2, 2, 
    			Complex.ZERO(), Complex.ONE(), 
    			Complex.ONE(), Complex.ZERO());
		gate = new DefaultGate("X", mat,GateType.X);
		DEFAULT_GATES.addElement(gate);
		
	    mat = new Matrix<>(2, 2, 
	    			Complex.ZERO(), Complex.I().negative(), 
	    			Complex.I(), Complex.ZERO());
	    gate = new DefaultGate("Y", mat,GateType.Y);
		DEFAULT_GATES.addElement(gate);
		
    	mat = new Matrix<>(2, 2, 
    			Complex.ONE(), Complex.ZERO(), 
    			Complex.ZERO(), Complex.ONE().negative());
	    gate = new DefaultGate("Z", mat,GateType.Z);
		DEFAULT_GATES.addElement(gate);
		
		for(int i = 0 ; i < DEFAULT_GATES.size(); i++)
			DEFAULT_GATES.getElementAt(i).loadIcon();
		
		
//		Gates with custom icons:
		
		gate = new DefaultGate("M", identity, GateType.MEASURE);
		gate.setIcon(GateIcon.getMeasureIcon());
		DEFAULT_GATES.addElement(gate);
		
		mat = new Matrix<>(4, 4, 
    			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
    			Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(),
    			Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
    			Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE());
	    gate = new DefaultGate("SWAP", mat,GateType.SWAP);
	    gate.setIcon(GateIcon.getSwapIcon());
		DEFAULT_GATES.addElement(gate);
		
		mat = new Matrix<>(4, 4, 
    			Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
    			Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
    			Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
    			Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO());
	    gate = new DefaultGate("CNOT", mat,GateType.CNOT);
	    gate.setIcon(GateIcon.getCNotIcon());
		DEFAULT_GATES.addElement(gate);
	}
	
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
                    return "QMeas";
                case SWAP:
                    return "QGate[\"not\"]"; //Don't worry, this is intentional
            }
        }
        return "ERROR";
    }

    private DefaultGate(String name, Matrix<Complex> mat, GateType gt) {
    	setName(name);
        setMatrix(mat);
        setType(gt);
    }
    
    public static AbstractGate getIdentity() {
    	return DEFAULT_GATES.getElementAt(0);
    }
    
    public static AbstractGate getHadmard() {
    	return DEFAULT_GATES.getElementAt(1);
    }

    public static AbstractGate getX() {
    	return DEFAULT_GATES.getElementAt(2);
    }

    public static AbstractGate getY() {
    	return DEFAULT_GATES.getElementAt(3);
    }

    public static AbstractGate getZ() {
    	return DEFAULT_GATES.getElementAt(4);
    }

    public static AbstractGate getMeasure() {
    	return DEFAULT_GATES.getElementAt(5);
    }
    
    public static AbstractGate getSwap() {
    	return DEFAULT_GATES.getElementAt(6);
    }
    
    public static AbstractGate getCNot() {
    	return DEFAULT_GATES.getElementAt(7);
    }
//    private DefaultGate(GateType gt){
//        setMatrix(identity().getMatrix());
//        setType(gt);
//    }

//    public static DefaultGate identity() {
//        Matrix<Complex> mat = new Matrix<>(2, 2, 
//        		Complex.ONE(), Complex.ZERO(),
//        		Complex.ZERO(), Complex.ONE());
//        return new DefaultGate(mat,GateType.I);
//    }
//
//    public static DefaultGate hadamard() { //bam
//        Matrix<Complex> mat = new Matrix<>(2, 2, 
//        		Complex.ONE(), Complex.ONE(), 
//        		Complex.ONE(), Complex.ONE().negative())
//        		.mult(Complex.ISQRT2());
//        return new DefaultGate(mat,GateType.H);
//    }
//
//    public static DefaultGate x(){
//    	Matrix<Complex> mat = new Matrix<>(2, 2, 
//    			Complex.ZERO(), Complex.ONE(), 
//    			Complex.ONE(), Complex.ZERO());
//        return new DefaultGate(mat,GateType.X);
//    }
//
//    public static DefaultGate y(){
//    	Matrix<Complex> mat = new Matrix<>(2, 2, 
//    			Complex.ZERO(), Complex.I().negative(), 
//    			Complex.I(), Complex.ZERO());
//        return new DefaultGate(mat,GateType.Y);
//    }
//
//    public static DefaultGate z(){
//    	Matrix<Complex> mat = new Matrix<>(2, 2, 
//    			Complex.ONE(), Complex.ZERO(), 
//    			Complex.ZERO(), Complex.ONE().negative());
//        return new DefaultGate(mat,GateType.Z);
//    }
//
//    public static DefaultGate measure(){
//        return new DefaultGate(GateType.MEASURE);
//    }
    
    
//    public static DefaultGate cnot(){
//        DefaultGate g = new DefaultGate(GateType.CNOT);
//        String s = null;
//        while(true) {
//	        try {
//	        	s = JOptionPane.showInputDialog("Length of CNOT?");
//	        	if(s == null)
//	        		return null;
//	        	g.length = Integer.parseInt(s);
//	        	break;
//	        }catch(NumberFormatException fne) {
//	        	AppDialogs.lengthNotValid(Main.w.getFrame(), s);
//	        }
//        }
//        return g;
//    }
//    
//    
//    public static DefaultGate swap(){
//
//        DefaultGate g0 = new DefaultGate(GateType.SWAP);
//        String s = null;
//        while(true) {
//	        try {
//	        	s = JOptionPane.showInputDialog("Length of SWAP?");
//	        	if(s == null)
//	        		return null;
//	        	g0.length = Integer.parseInt(s);
//	        	break;
//	        }catch(NumberFormatException fne) {
//	        	AppDialogs.lengthNotValid(Main.w.getFrame(), s);
//	        }
//        }
//        return g0;
//    }
    
    
    
//    public static Gate customGate() {
//        String s;
//        boolean done = false;
//        ArrayList<Integer> regs = new ArrayList<>();
//        String name = JOptionPane.showInputDialog("What is the gate called?");
//
//        while(!done) {
//            s = JOptionPane.showInputDialog("Which qubits?");
//            try{
//                regs.add(Integer.parseInt(s));
//            } catch (NumberFormatException nfe) {
//                done = true;
//            }
//        }
//        if(Main.cb.customGates.containsKey(name)){
//            MultiQubitGate gOld = ((MultiQubitGate) Main.cb.customGates.get(name));
//            Gate g = new MultiQubitGate(gOld.matrix,GateType.CUSTOM,regs);
//            g.name = name;
//            g.length = regs.stream().max(Math::max).get() - regs.stream().min(Math::min).get();
//            return g;
//        }
//        int len = 1 << regs.size();
//        Complex[][] m = new Complex[len][len];
//        for(int y = 0; y < len; ++y) {
//            for(int x = 0; x < len; ++x) {
//                m[x][y] = Complex.parseComplex(JOptionPane.showInputDialog("Element (" + x + "," + y + ")"));
//            }
//        }
//        Gate g = new MultiQubitGate(m,GateType.CUSTOM,regs);
//        g.name = name;
//        g.length = regs.stream().max(Math::max).get() - regs.stream().min(Math::min).get();
//        Main.cb.customGates.put(name,g);
//        return g;    	
//    	return null;
//    }

    @Override
    public String toString() {
        String out = "Gate type: " + getType().toString() + "\n";
        if(getType() == GateType.CUSTOM) {
            out += "Matrix:\n" + getMatrix();
        }
        return out;
    }
    
    
}
