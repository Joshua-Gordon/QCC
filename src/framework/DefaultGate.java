package framework;
import java.io.Serializable;

import appUI.GateChooserUI;
import mathLib.Complex;
import mathLib.Matrix;
import utils.GateIcon;
import utils.HashListModel;


/**
 * This class is an extension of {@link AbstractGate} in which all Default Gates should be constructed and declared here.
 * This class comprises of a static list of {@link DefaultGate}s and static methods to access them.
 * 
 * @author quantumresearch
 *
 */
public class DefaultGate extends AbstractGate implements Serializable{
	private static final long serialVersionUID = 6220371128991814182L;
	
    public static final HashListModel<String, AbstractGate> DEFAULT_GATES = new HashListModel<>();
	
    /**
     * The enum of supported language types
     * @author quantumresearch
     *
     */
    public static enum LangType{
        QUIL,QASM,QUIPPER
    }
    
    /**
     * This method loads all the {@link DefaultGates} before the application window opens in the main thread.
     * <b> ALL </b> "default gates" must be added to <code>DEFAULT_GATES</code> before this method
     * ends.
     * <p>
     * Attempting to add a {@link DefaultGate} after this method is called may cause the gate to not show in the {@link GateChooserUI} panel
     */
    public static void loadGates(){

//		Gates with default icons :

        Matrix<Complex> identity = new Matrix<>(2, 2,
                Complex.ONE(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE());
        DefaultGate gate = new DefaultGate("I", identity, AbstractGate.GateType.I);
        DEFAULT_GATES.put("I",gate);

        Matrix<Complex> mat = new Matrix<>(2, 2,
                Complex.ONE(), Complex.ONE(),
                Complex.ONE(), Complex.ONE().negative())
                .mult(Complex.ISQRT2());
        gate = new DefaultGate("H", mat, AbstractGate.GateType.H);
        DEFAULT_GATES.put("H",gate);

        mat = new Matrix<>(2, 2,
                Complex.ZERO(), Complex.ONE(),
                Complex.ONE(), Complex.ZERO());
        gate = new DefaultGate("X", mat, AbstractGate.GateType.X);
        DEFAULT_GATES.put("X",gate);

        mat = new Matrix<>(2, 2,
                Complex.ZERO(), Complex.I().negative(),
                Complex.I(), Complex.ZERO());
        gate = new DefaultGate("Y", mat, AbstractGate.GateType.Y);
        DEFAULT_GATES.put("Y",gate);

        mat = new Matrix<>(2, 2,
                Complex.ONE(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE().negative());
        gate = new DefaultGate("Z", mat, AbstractGate.GateType.Z);
        DEFAULT_GATES.put("Z",gate);
        
        DEFAULT_GATES.getValues().forEach(AbstractGate::loadIcon);


//		Gates with custom icons:

        gate = new DefaultGate("MEASURE", identity, AbstractGate.GateType.MEASURE);
        gate.setIcon(GateIcon.getMeasureIcon());
        DEFAULT_GATES.put("MEASURE",gate);

        mat = new Matrix<>(4, 4,
                Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE());
        gate = new DefaultGate("SWAP", mat, AbstractGate.GateType.SWAP);
        gate.setIcon(GateIcon.getSwapIcon());
        DEFAULT_GATES.put("SWAP",gate);

        mat = new Matrix<>(4, 4,
                Complex.ONE(), Complex.ZERO(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ONE(), Complex.ZERO(), Complex.ZERO(),
                Complex.ZERO(), Complex.ZERO(), Complex.ZERO(), Complex.ONE(),
                Complex.ZERO(), Complex.ZERO(), Complex.ONE(), Complex.ZERO());
        gate = new DefaultGate("CNOT", mat, AbstractGate.GateType.CNOT);
        gate.setIcon(GateIcon.getCNotIcon());
        DEFAULT_GATES.put("CNOT",gate);
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

    
    private DefaultGate(String name, Matrix<Complex> mat, GateType gt) { //public for use in gatemap
    	setName(name);
        setMatrix(mat);
        setType(gt);
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
