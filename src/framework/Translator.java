package framework;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import framework.AbstractGate.GateType;
import framework.DefaultGate.LangType;
import mathLib.Complex;
import mathLib.Matrix;

public class Translator {

    static String code = "";
    static int offset = Integer.MAX_VALUE;
    static int bottomoffset = Integer.MAX_VALUE;

    public static String exportQUIL() {
        CircuitBoard cb = Main.getWindow().getSelectedBoard();
        bottomoffset = offset = cb.getRows();
        ArrayList<String> customGates = new ArrayList<>();
        ExportedGate.exportGates(cb, new ExportGatesRunnable() {
            @Override
            public void gateExported(ExportedGate eg, int x, int y) {
//                SolderedGate.Control[] controls = eg.getControls();
                AbstractGate ag = eg.getAbstractGate();
                Matrix<Complex> m = ag.getMatrix();
                String name = ag.getName();
//                if(isControlled(controls)) {
//                    m = SolderedGate.makeControlledMatrix(m,controls);
//                    name = controls.hashCode() + name;
//                }
//                for(int i = 0; i < y; ++i) {
//                    System.out.println(controls[i]);
//                }
                GateType gt = ag.getType();
                boolean id = ag.getName().equals("I");

                if(!gt.equals(GateType.OTHER) && !id) {
                    name = DefaultGate.typeToString(gt, DefaultGate.LangType.QUIL);
                    code += name + " " + y;
                    if(gt.equals(GateType.CNOT) || gt.equals(GateType.SWAP)) {
                        code += " " + (eg.getHeight()-1+y);
                    }
                } else if(!id){
                    if(!customGates.contains(name)) {
                        customGates.add(name);
                        code += "DEFGATE " + name + ":\n";
                        for (int my = 0; my < m.getRows(); ++my) {
                            code += "\n    ";
                            for (int mx = 0; mx < m.getRows(); ++mx) { //This copies down the matrix into the code
                                code += m.v(mx, my).toString();
                                if (mx + 1 < m.getRows())
                                    code += ", ";
                            }
                        }
                        code += "\n";
                    }
                    code += name;
                    for(int i = 0; i < eg.getRegisters().length; ++i) {
                        code += " " + (eg.getRegisters()[i]);
                    }
                }
                code += id ? "" : "\n";
            }

            @Override
            public void nextColumnEvent(int column) {
                int i = offset;
                int j = bottomoffset;
                try {
                    for (i = 0; cb.getSolderedRegister(column, i).getSolderedGate().getAbstractGate().getName().equals("I"); ++i);
                } catch(IndexOutOfBoundsException e) {}
                try{
                    for (j = cb.getRows(); cb.getSolderedRegister(column,j).getSolderedGate().getAbstractGate().getName().equals("I"); --j);
                } catch(IndexOutOfBoundsException e) {}
                offset = Math.min(offset,i);
                bottomoffset = Math.min(bottomoffset,j);
            }

			@Override
			public void columnEndEvent(int column) {}
        });
        String temp = code;
        code = "";
        return fixQUIL(temp,offset);
    }

    public static String exportQASM() {
        CircuitBoard cb = Main.getWindow().getSelectedBoard();
        bottomoffset = offset = cb.getRows();
        ArrayList<String> customGates = new ArrayList<>();
        code += "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[" + "FORMATHERE" + "];\ncreg c[" + "FORMATHERE" + "];\n";
        ExportedGate.exportGates(cb, new ExportGatesRunnable() {
            @Override
            public void gateExported(ExportedGate eg, int x, int y) {
                String name = eg.getAbstractGate().getName();
                if(!name.equals("I")) {
                    switch(name) {
                        case "MEASURE":
                            code += "measure q[" + y + "] -> c[" + y + "];\n";
                            break;
                        case "CNOT":
                            code += "cx q[" + y + "],q[" + (y+eg.getHeight()-1) + "];\n";
                            break;
                        case "SWAP":
                            if(eg.getHeight() > 0) {
                                code += "cx q[" + y + "],q[" + (y + eg.getHeight()) + "];\n";
                                code += "cx q[" + (y + eg.getHeight()) + "],q[" + y + "];\n";
                                code += "cx q[" + y + "],q[" + (y + eg.getHeight()) + "];\n";
                            } else {
                                code += "cx q[" + (y + eg.getHeight()) + "],q[" + y + "];\n";
                                code += "cx q[" + y + "],q[" + (y + eg.getHeight()) + "];\n";
                                code += "cx q[" + (y + eg.getHeight()) + "],q[" + y + "];\n";
                            }
                            break;
                        default:
                            code += DefaultGate.typeToString(eg.getAbstractGate().getType(),LangType.QASM) + " q[" + y + "];\n";
                    }
                }
            }
            @Override
            public void nextColumnEvent(int column) {
                int i = offset;
                int j = bottomoffset;
                try {
                    for (i = 0; cb.getSolderedRegister(column, i).getSolderedGate().getAbstractGate().getName().equals("I"); ++i);
                } catch(IndexOutOfBoundsException e) {}
                try{
                    //System.out.println(cb.getSolderedRegister(column,cb.getRows()));
                    for (j = 0; cb.getSolderedRegister(column, cb.getRows()-j-1).getSolderedGate().getAbstractGate().getName().equals("I"); ++j);
                } catch(IndexOutOfBoundsException e) {}
                offset = Math.min(offset,i);
                bottomoffset = Math.min(bottomoffset,j);
            }
			@Override
			public void columnEndEvent(int column) {}
        });
        System.out.println("Bottom: " + bottomoffset);
        System.out.println("Top: " + offset);
        String temp = fixQASM(code,offset).replace("FORMATHERE",""+(cb.getRows()-offset - bottomoffset));
        code = "";
        for(int i = 0; i < cb.getRows()-offset-bottomoffset; ++i) {
            temp += "measure q[" + i + "] -> c[" + i + "];\n";
        }
        return temp;
    }

    private static String fixQUIL(String code, int offset) { //Subtract offset from all registers
        String[] lines = code.split("\n");
        String output = "";
        for(String line : code.split("\n")){
            //Subtract offset from each number
            String[] components = line.split(" ");
            if(components[0].equals("MEASURE")){
                try {
                    output += "MEASURE " + (Integer.parseInt(components[1]) - offset) + " [" + (Integer.parseInt(components[2].substring(1, 2)) - offset) + "]";
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            } else if(components[0].startsWith("DEFGATE")){
                output += line;
            } else if(isNumber(line.trim())){
                output += line;
            } else {
                output += components[0];
                for(int i = 1; i < components.length; ++i) {
                    if(!components[i].equals(""))
                        output += " " + (Integer.parseInt(components[i])-offset);
                }
            }
            output += "\n";

        }
        return output;
    }

    private static String fixQASM(String code, int offset) { //Subtract offset from all registers
        String[] lines = code.split("\n");
        String output = "";
        for(int i = 0; i < 4; ++i) {
            output += lines[i] + "\n";
        }
        for(String line : Arrays.copyOfRange(lines,4,lines.length)) {
            String[] components = line.split("\\[");
            int idx;
            if(components.length >= 2) { //single qubit gates
                output += components[0] + "[";
                idx = Integer.parseInt(components[1].substring(0,components[1].indexOf("]")));
                output += idx-offset;
                output += components[1].substring(components[1].indexOf("]"));
            }
            if(components.length == 3) { //cnot
                idx = Integer.parseInt(components[2].substring(0,components[2].indexOf("]")));
                output += "[" + (idx-offset);
                output += components[2].substring(components[2].indexOf("]"));
            }
            output+="\n";
        }
        return output;
    }

    public static ArrayList<ArrayList<SolderedRegister>> importQuil(String quil) {
        ArrayList<CustomGate> customGates = new ArrayList<>();
        ArrayList<ArrayList<SolderedRegister>> rows = new ArrayList<>();
        int maxDepth = 1;
        int maxWidth = 0;
        rows.add(new ArrayList<>());
        ArrayList<String> code = new ArrayList<>();
        Collections.addAll(code,quil.split("\n"));
        for(int instruction = 0; instruction < code.size(); ++instruction) {
            String line = code.get(instruction);
            if (line.equals("")) continue;
            if (line.startsWith("DEFGATE")) {
                String name = line.substring(8,line.length()-1); //Drop the : at the end
                String nextLine;
                while((nextLine = code.get(++instruction)).equals(""));
                int size = nextLine.split(",").length; //the N in NxN matrix
                Matrix<Complex> mat = new Matrix<>(Complex.ONE(),size,size);
                for(int y = 0; y < size; ++y) {
                    String[] row = nextLine.split(","); //grab the next row of complex numbers in the quil file
                    for(int x = 0; x < size; ++x) {
                        Complex idx = Complex.parseComplex(row[x]); //parse the xth complex number
                        mat.r(idx,y,x); //y x because Matrix::r requires row,column.
                    }
                    nextLine = code.get(++instruction); //Increment instruction to advance through the file
                }
                CustomGate cg = new CustomGate(mat);
                cg.setName(name);
                Main.getWindow().getSelectedBoard().addCustomGate(cg);
                --instruction;
                continue;
            }
            if(line.startsWith("MEASURE")) {
                int idx = Integer.parseInt(line.substring(8,line.lastIndexOf(" ")));
                rows.get(idx).add(new SolderedRegister(new SolderedGate(DefaultGate.DEFAULT_GATES.get("MEASURE"),0,0),0));
                continue;
            }
            String[] pieces = line.split(" ");
            AbstractGate ag = null;
            ag = DefaultGate.DEFAULT_GATES.get(pieces[0]);
            if(ag == null) {
                for(int j = 0; j < Main.getWindow().getSelectedBoard().getCustomGates().size(); ++j) {
                    AbstractGate cg = Main.getWindow().getSelectedBoard().getCustomGates().get(j);
                    if(cg.getName().equals(pieces[0])) {
                        ag = cg;
                    }
                }
            }
            int location = Integer.parseInt(pieces[1]);
            if (location >= maxDepth) {
                for (int i = maxDepth; i <= location + 1; ++i) {
                    rows.add(new ArrayList<>());
                }
                maxDepth = location + 1;
            }
            int maxReg = 0;
            int gateDepth = 0;
            for (int i = 1; i < pieces.length; ++i) {
                int reg = Integer.parseInt(pieces[i]);
                try {
                    gateDepth = Math.max(rows.get(reg).size(), gateDepth);
                } catch (IndexOutOfBoundsException ioobe) {
                    rows.add(new ArrayList<>());
                }
                maxReg = Math.max(maxReg, reg);
            }
            maxDepth = Math.max(maxDepth, maxReg + 1);
            SolderedGate sg = new SolderedGate(ag, 0, pieces.length - 2);
            for (int i = 0; i < sg.getExpectedNumberOfRegisters(); ++i) {
                SolderedRegister sr = new SolderedRegister(sg, i);
                int reg = Integer.parseInt(pieces[i + 1]);
                if (reg >= rows.size()) {
                    rows.add(new ArrayList<>());
                }
                for (int j = rows.get(reg).size(); j < gateDepth; ++j) {
                    rows.get(reg).add(SolderedRegister.identity());
                }
                rows.get(reg).add(sr);
            }
            for (int i = 1; i < pieces.length; ++i)
                maxWidth = Math.max(maxWidth, rows.get(Integer.parseInt(pieces[i])).size());

        }
        ArrayList<ArrayList<SolderedRegister>> transpose = new ArrayList<>();
        //<editor-fold desc="Transpose array">
        for(int x = 0; x < maxWidth; ++x) {
            transpose.add(new ArrayList<>());
            for(int y = 0; y < maxDepth; ++y) {
                try{
                    SolderedRegister sr = rows.get(y).get(x);
                    transpose.get(x).add(sr);
                } catch(IndexOutOfBoundsException ioobe) {
                    transpose.get(x).add(SolderedRegister.identity());
                }
            }
        }
        //</editor-fold>
        for(CustomGate cg : customGates)
            Main.getWindow().getSelectedBoard().addCustomGate(cg);
        return transpose;
    }

    /**
     *
     * @param quil A string containing quil code
     * @return A double arraylist of gates representing a circuit
     */
    public static ArrayList<ArrayList<SolderedRegister>> parseQuil(String quil) { //Parses quil into a circuit diagram
        ArrayList<ArrayList<SolderedRegister>> board = new ArrayList<>();
        int maxLen = 0;
        for(String line : quil.split("\n")) {
            if(line.equals("")) continue;
            String gate = line.split(" ")[0];
            AbstractGate g = DefaultGate.DEFAULT_GATES.get(gate);
            int register = Integer.parseInt(line.split(" ")[1]);
            while(board.size()-1 < register) {
                board.add(new ArrayList<>());
            }
            if(gate.equals("CNOT") || gate.equals("MEASURE")){
                String otherBit = line.split(" ")[2];
                if(!otherBit.contains("[")){
                    int target = Integer.parseInt(otherBit);
                    while(board.size()-1 < target) {
                        board.add(new ArrayList<>());
                    }
                    SolderedGate sg = new SolderedGate(g, 0, 1);    // from Max to Josh: I added two values to SolderedGate 
                    												// (look in the documentation to see what the extra parameters 
                    												// mean in SolderedRegister and SolderedGate)
                    board.get(register).add(new SolderedRegister(sg,0));
                    board.get(target).add(new SolderedRegister(sg,1));
                }
            }
            board.get(register).add(new SolderedRegister(new SolderedGate(g, 0, 0),0));  // from Max to Josh: added extra parameters here as well
            if(board.get(register).size() > maxLen){
                maxLen = board.get(register).size();
            }
        }
        //Fill
        for(ArrayList<SolderedRegister> a : board) {
            while(a.size() < maxLen) {
                a.add(SolderedRegister.identity());
            }
        }
        //Transpose
        ArrayList<ArrayList<SolderedRegister>> transpose = new ArrayList<>();
        for(int y = 0; y < board.get(0).size(); ++y) {
            ArrayList<SolderedRegister> col = new ArrayList<>();
            for(ArrayList<SolderedRegister> row : board) {
                col.add(row.get(y));
            }
            transpose.add(col);
        }
        return transpose;
    }

    public static ArrayList<ArrayList<SolderedRegister>> loadProgram(LangType lt, String filepath) {
        ArrayList<ArrayList<SolderedRegister>> gates = null;
        String code = "";
        try {
            FileReader fr = new FileReader(new File(filepath));
            BufferedReader br = new BufferedReader(fr);
            code = br.lines().reduce("",(a,c) -> a+"\n"+c);
        } catch (IOException e) {
            System.err.println("ERROR LOADING FILE");
            e.printStackTrace();
        }
        switch(lt){
            case QUIL:
                gates = importQuil(code);
                break;
            case QASM:
                String unqasm = translateQASMToQuil(code);
                gates = importQuil(unqasm);
                break;
            case QUIPPER:
                String unquipper = translateQuipperToQuil(code);
                gates = importQuil(unquipper);
                break;
        }
        return gates;
    }

    /**
     * Outputs quipper ASCII
     * @return Quipper ASCII representing the main circuit
     */
/*
    public static String translateQuipper(){
        String code = "Inputs: None\n";
        ArrayList<ArrayList<DefaultGate>> board = Main.cb.board;
        int numQubits = getQubits(board);
        for(int i = 0; i < numQubits; ++i) {
            code += "QInit0(" + i + ")\n";
        }
        int offset = 20;
        for(int x = 0; x < board.size(); ++x) {
            ArrayList<DefaultGate> instructions = board.get(x);
            for(int y = 0; y < instructions.size(); y++) {
                DefaultGate g = instructions.get(y);
                GateType type = g.getType();
                if(type != GateType.I) {
                    int idx = y;
                    if(idx < offset) offset = idx;
                    code += DefaultGate.typeToString(type,DefaultGate.LangType.QUIPPER);
                    code += "(" + idx + ")";
                    if(type == GateType.CNOT || type == GateType.SWAP) {
                        code += " with controls=[+" + (idx+g.length) + "]";
                    }
                    if(type == GateType.SWAP){
                        code += "\nQGate[\"not\"](" + (idx + g.length) + ") with controls=[+" + idx + "]\n";
                        code += "QGate[\"not\"](" + idx + ") with controls=[+" + (idx + g.length) + "]";
                        //Three CNOTs do a swap
                    }
                    code += "\n";
                }
            }
        }
        for(int i = 0; i < numQubits; ++i) {
            code += "QMeas(" + i + ")\n";
        }
        code += "Outputs: ";
        for(int i = 0; i < numQubits; ++i) {
            code += i + ":Cbit, ";
        }
        code = code.substring(0,code.length()-2);
        return fixQuipper(code,offset);
    }

    private static String fixQuipper(String code, int offset) {
        String newCode = "";
        for(String line : code.split("\n")) {
            if(line.startsWith("QGate")) {
                newCode += line.substring(0,line.indexOf("("));
                int num = Integer.parseInt(line.substring(line.indexOf("(")+1,line.indexOf(")")));
                newCode += "(" + (num-offset) + ")";
                if(line.contains("with controls")) {
                    newCode += " with controls=[+";
                    num = Integer.parseInt(line.substring(line.indexOf("+")+1,line.substring(16).indexOf("]")+16));
                    newCode += (num-offset) + "]";
                }
            } else {
                newCode += line;
            }
            newCode += "\n";
        }
        return newCode;
    }
*/
    /**
     *
     * @param s String possibly being a complex number
     * @return true if it is a complex number
     */
    private static boolean isNumber(String s){
        String[] ss = s.split(",");
        try{
            Complex.parseComplex(ss[0]);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String translateQuipperToQuil(String quipper) {
        String quil = "";
        String[] tempLines = quipper.split("\n");
        ArrayList<String> quipperLines = new ArrayList<>();
        Collections.addAll(quipperLines,tempLines); //Make list of  lines into arraylist
        quipperLines.remove(0); //Pop off "Inputs: None"
        String line = quipperLines.remove(0); //Grab first line, should be a QInit
        while(line.startsWith("QInit")) {
            line = quipperLines.remove(0); //This is Quil, we do not need to compute the number of qubits
        }
        //At this point, line contains the first actual line of code
        while(!line.startsWith("Outputs:")) {
            if(line.startsWith("QMeas")){
                String num = line.substring(6,line.length()-1);
                quil += "MEASURE " + num + " [" + num +"]\n";
            } else {
                String gateName = line.substring(7); //Cut off QGate["
                gateName = gateName.substring(0, gateName.indexOf("\"")); //Now it is the actual gate name
                String idx = line.substring(line.indexOf("(")+1, line.indexOf(")")); //Target register
                switch (gateName) {
                    case "not":
                        if (line.contains("with controls")) {
                            String controlIdx = line.substring(line.indexOf("+")+1, line.length() - 1); //Control register
                            quil += "CNOT " + idx + " " + controlIdx + "\n";
                        } else {
                            quil += "X " + idx + "\n";
                        }
                        break;
                    default:
                        quil += gateName + " " + idx + "\n"; //hits H,Z,Y gates
                }
            }
            line = quipperLines.remove(0);
        }
        return quil;
    }

    public static String translateQASMToQuil(String qasm) {
        String quil = "";
        String[] tempLines = qasm.split("\n");
        ArrayList<String> qasmLines = new ArrayList<>();
        Collections.addAll(qasmLines,tempLines); //Make list of lines into arraylist
        while (qasmLines.get(0).equals("") || qasmLines.get(0).equals("\n")){
            qasmLines.remove(0);
        }
        qasmLines.remove(0);
        qasmLines.remove(0);
        qasmLines.remove(0);
        qasmLines.remove(0); //Clear out header lines
        while(qasmLines.size() > 0) {
            String line = qasmLines.remove(0);
            String idx = line.substring(line.indexOf("[")+1,line.indexOf("]"));
            String gateName = line.substring(0,line.indexOf(" "));
            switch(gateName) {
                case "cx":
                    String target = line.substring(line.lastIndexOf("[")+1,line.length()-2);
                    quil += "CNOT " + idx + " " + target + "\n";
                    break;
                case "measure":
                    quil += "MEASURE " + idx + " [" + idx + "]\n";
                    break;
                default:
                    quil += gateName.toUpperCase() + " " + idx + "\n";
            }
        }
        return quil;
    }

//    public static boolean isControlled(SolderedGate.Control[] controls) {
//        for(SolderedGate.Control c : controls) {
//            if(c != null) {
//                return true;
//            }
//        }
//        return false;
//    }
}
