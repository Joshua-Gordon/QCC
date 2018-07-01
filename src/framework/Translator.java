package framework;
import java.util.ArrayList;
import java.util.Arrays;

import framework.AbstractGate.GateType;
import mathLib.Complex;
import mathLib.Matrix;

public class Translator {

    /**
     * Takes the main circuitboard and outputs QUIL code
     * @return QUIL code
     */
    public static String translateQUIL(){ //Translates to Quil
        String code = "";
        ArrayList<ArrayList<DefaultGate>> boardTemp = Main.cb.board;
        ArrayList<ArrayList<DefaultGate>> board = new ArrayList<>();
        ArrayList<String> customGates = new ArrayList<>();
        for(int i = 0; i < boardTemp.size(); ++i) {
            board.add(boardTemp.get(i)); //Copy to other circuitboard because safety
        }
        int offset = 20; //Hardcoded right now, should be height of circuit board. This is the offset from the top of the circuitboard
                         //and is computed as the board is parsed
        for(int x = 0; x < board.size(); ++x){
            ArrayList<DefaultGate> instructions = board.get(x); //Current column of instructions
            for(int i = 0; i < instructions.size(); ++i){
                DefaultGate g = instructions.get(i);
                GateType type = g.getType();
                if(type != GateType.I && type != GateType.CUSTOM) {
                    int idx = i;
                    if(idx+g.length < offset) { //Don't cut off a long gate at the bottom of the circuit
                        offset = idx+g.length;
                    }
                    code += DefaultGate.typeToString(type, DefaultGate.LangType.QUIL);
                    code += " ";
                    code += idx;
                    if (type == GateType.CNOT || type == GateType.SWAP) {
                        code += " ";
                        code += (idx + g.length); //Second index
                    }
                    if (type == GateType.MEASURE) {
                        code += " [";
                        code += idx;
                        code += "]";
                    }
                    code += "\n";
                }
//                } else if(type == GateType.CUSTOM) { //All bets are off. Special code to handle these
//                    MultiQubitGate mqg = (MultiQubitGate) g;
//                    String name = mqg.getName();
//                    int idx = i;
//                    if(idx + g.length < offset) {
//                        offset = idx + mqg.length;
//                    }
//                    if(!customGates.contains(name)) { //If this is a new gate
//                        customGates.add(name);        //Then add it to the known gates
//                        String dec = "DEFGATE " + name + ":"; //And define it in the code
//                        Matrix<Complex> m = mqg.getMatrix();
//                        for(int my = 0; my < m.getRows(); ++my) {
//                            dec += "\n    ";
//                            for(int mx = 0; mx < m.getRows(); ++mx) { //This copies down the matrix into the code
//                                dec += m.v(mx,my).toString();
//                                if(mx+1 < m.getRows())
//                                    dec += ", ";
//                            }
//                        }
//                        code += dec; //Declaration of gate
//                        code += "\n";
//                    }
//                    code += name + " ";
//                    for (int r : mqg.registers) {
//                        code += r + " "; //Apply the gate to all the registers it's on
//                    }
//                    code += "\n";
//                }
            }
        }
        return fixQUIL(code,offset); //Fix offset
    }

    /**
     * Takes the main circuit board and translates it to QASM
     * @return
     */
    public static String translateQASM(){ //Translates to QASM. Same idea as the quil one
        String code = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[";

        ArrayList<ArrayList<DefaultGate>> boardTemp = Main.cb.board;
        ArrayList<ArrayList<DefaultGate>> board = new ArrayList<>();
        for(int i = 0; i < boardTemp.size(); ++i) {
            board.add(boardTemp.get(i));
        }

        int numQubits = getQubits(board);
        code += numQubits + "];\ncreg c["+numQubits+"];\n";
        int offset = 20;
        for(int x = 0; x < board.size(); ++x){
            ArrayList<DefaultGate> instructions = board.get(x);
            for(int i = 0; i < instructions.size(); ++i){ //i represents the column
                DefaultGate g = instructions.get(i);
                GateType type = g.getType();
                if(type != GateType.I) {
                    int idx = i;// - offset;
                    if(idx < offset) {
                        offset = idx;
                    }
                    code += DefaultGate.typeToString(type, DefaultGate.LangType.QASM);
                    code += " q[";
                    code += idx;
                    code += "]";
                    if (type == GateType.CNOT) {
                        code += ",q[" + (idx + g.length) + "]";
                    }
                    if(type == GateType.SWAP){
                        code += ",q[" + (idx + g.length) + "];\n";
                        code += "cx q[" + idx + "],q[" + (idx + g.length) + "];\n";
                        code += "cx q[" + (idx + g.length) + "],q[" + idx + "]";
                        //Three CNOTs do a swap
                    }
                    if (type == GateType.MEASURE) {
                        code += " -> c[" + idx + "]";
                    }
                    code += ";\n";
                }
            }
            for(int i = 0; i < numQubits; ++i) {
                code += "measure q[" + i + "] -> c[" + i + "];\n";
            }
        }

        return fixQASM(code,offset);
    }

    private static String fixQUIL(String code, int offset) { //Subtract offset from all registers
        String[] lines = code.split("\n");
        String output = "";
        for(String line : code.split("\n")){
            //Subtract offset from each number
            String[] components = line.split(" ");
            if(components[0].equals("MEASURE")){
                output += "MEASURE " + (Integer.parseInt(components[1])-offset) + " [" + (Integer.parseInt(components[2].substring(1,2))-offset) + "]";
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
            if(components.length >= 2) {
                output += components[0] + "[";
                idx = Integer.parseInt(components[1].substring(0,components[1].indexOf("]")));
                output += idx-offset;
                output += components[1].substring(components[1].indexOf("]"));
            }
            if(components.length == 3) {
                idx = Integer.parseInt(components[2].substring(0,components[2].indexOf("]")));
                output += "[" + (idx-offset);
                output += components[2].substring(components[2].indexOf("]"));
            }
            output+="\n";
        }
        return output;
    }

    private static int getQubits(ArrayList<ArrayList<DefaultGate>> board) { //Counts number of qubits used in circuit
        boolean[] hasGate = new boolean[board.size()];
        for(int i = 0; i < hasGate.length; ++i) {
            hasGate[i] = false;
        }
        for(int x = 0; x < board.size(); ++x) {
            ArrayList<DefaultGate> column = board.get(x);
            for(int y = 0; y < board.get(0).size(); ++y) {
                if(column.get(y).getType() != GateType.I) {
                    hasGate[y] = true;
                    hasGate[y+column.get(y).length] = true;
                }
            }
        }
        int sum = 0;
        for(int i = 0; i < hasGate.length; ++i) {
            if(hasGate[i]) sum++;
        }
        //System.out.println("Num qubits: " + sum);
        return sum+1;
    }

    /**
     *
     * @param quil A string containing quil code
     * @return A double arraylist of gates representing a circuit
     */
    public static ArrayList<ArrayList<DefaultGate>> loadQuil(String quil) { //Parses quil into a circuit diagram
        ArrayList<ArrayList<DefaultGate>> board = new ArrayList<>();
        int maxLen = 0;
        for(String line : quil.split("\n")) {
            String gate = line.split(" ")[0];
            DefaultGate g;
            switch(gate) {
                case "H":
                    g = DefaultGate.hadamard();
                    break;
                case "X":
                    g = DefaultGate.x();
                    break;
                case "Y":
                    g = DefaultGate.y();
                    break;
                case "Z":
                    g = DefaultGate.z();
                    break;
                case "CNOT":
                    g = DefaultGate.identity();
                    g.setType(GateType.CNOT);
                    break;
                case "MEASURE":
                    g = DefaultGate.measure();
                    break;
                default:
                    g = DefaultGate.identity();
            }
            int register = Integer.parseInt(line.split(" ")[1]);
            while(board.size()-1 < register) {
                board.add(new ArrayList<>());
            }
            if(gate.equals("CNOT") || gate.equals("MEASURE")){
                String otherBit = line.split(" ")[2];
                if(!otherBit.contains("[")){
                    g.length = Integer.parseInt(otherBit);
                    while(board.size()-1 < register+g.length) {
                        board.add(new ArrayList<>());
                    }
                    board.get(register+g.length).add(DefaultGate.identity());
                    board.get(register+g.length).add(DefaultGate.identity());
                }
            }
            board.get(register).add(g);
            if(board.get(register).size() > maxLen){
                maxLen = board.get(register).size();
            }
        }
        //Fill
        for(ArrayList<DefaultGate> a : board) {
            while(a.size() < maxLen) {
                a.add(DefaultGate.identity());
            }
        }
        //Transpose
        ArrayList<ArrayList<DefaultGate>> transpose = new ArrayList<>();
        for(int y = 0; y < board.get(0).size(); ++y) {
            ArrayList<DefaultGate> col = new ArrayList<>();
            for(ArrayList<DefaultGate> row : board) {
                col.add(row.get(y));
            }
            transpose.add(col);
        }
        return transpose;
    }

    /**
     * Outputs quipper ASCII
     * @return Quipper ASCII representing the main circuit
     */
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
}
