package framework;
import java.util.ArrayList;
import java.util.Arrays;

import framework.Gate.GateType;
import framework.Gate.LangType;

public class Translator {

    public static String translateQUIL(){ //Translates to Quil
        String code = "";
        ArrayList<ArrayList<Gate>> boardTemp = Main.cb.board;
        ArrayList<ArrayList<Gate>> board = new ArrayList<>();
        for(int i = 0; i < boardTemp.size(); ++i) {
            board.add(boardTemp.get(i));
        }
        int offset = 20;
        for(int x = 0; x < board.size(); ++x){
            ArrayList<Gate> instructions = board.get(x);
            for(int i = 0; i < instructions.size(); ++i){ //i represents the column
                Gate g = instructions.get(i);
                if(g.type != Gate.GateType.I) {
                    int idx = i;// - offset;
                    if(idx+g.length < offset) {
                        offset = idx+g.length;
                    }
                    code += Gate.typeToString(g.type, Gate.LangType.QUIL);
                    code += " ";
                    code += idx;
                    if (g.type == Gate.GateType.CNOT || g.type == Gate.GateType.SWAP) {
                        code += " ";
                        code += (idx + g.length);
                    }
                    if (g.type == Gate.GateType.MEASURE) {
                        code += " [";
                        code += idx;
                        code += "]";
                    }
                    code += "\n";
                }
            }
        }
        return fixQUIL(code,offset);
    }

    public static String translateQASM(){ //Translates to QASM
        String code = "OPENQASM 2.0;\ninclude \"qelib1.inc\";\nqreg q[";

        ArrayList<ArrayList<Gate>> boardTemp = Main.cb.board;
        ArrayList<ArrayList<Gate>> board = new ArrayList<>();
        for(int i = 0; i < boardTemp.size(); ++i) {
            board.add(boardTemp.get(i));
        }

        int numQubits = getQubits(board);
        code += numQubits + "];\ncreg c["+numQubits+"];\n";
        int offset = 20;
        for(int x = 0; x < board.size(); ++x){
            ArrayList<Gate> instructions = board.get(x);
            for(int i = 0; i < instructions.size(); ++i){ //i represents the column
                Gate g = instructions.get(i);
                if(g.type != Gate.GateType.I) {
                    int idx = i;// - offset;
                    if(idx < offset) {
                        offset = idx;
                    }
                    code += Gate.typeToString(g.type, Gate.LangType.QASM);
                    code += " q[";
                    code += idx;
                    code += "]";
                    if (g.type == Gate.GateType.CNOT) {
                        code += ",q[" + (idx + g.length) + "]";
                    }
                    if(g.type == Gate.GateType.SWAP){
                        code += ",q[" + (idx + g.length) + "];\n";
                        code += "cx q[" + idx + "],q[" + (idx + g.length) + "];\n";
                        code += "cx q[" + (idx + g.length) + "],q[" + idx + "]";
                        //Three CNOTs do a swap
                    }
                    if (g.type == Gate.GateType.MEASURE) {
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

    private static String fixQUIL(String code, int offset) {
        String[] lines = code.split("\n");
        String output = "";
        for(String line : code.split("\n")){
            //Subtract offset from each number
            String[] components = line.split(" ");
            if(components[0].equals("MEASURE")){
                output += "MEASURE " + (Integer.parseInt(components[1])-offset) + " [" + (Integer.parseInt(components[2].substring(1,2))-offset) + "]";
            } else {
                output += components[0];
                for(int i = 1; i < components.length; ++i) {
                    output += " " + (Integer.parseInt(components[i])-offset);
                }
            }
            output += "\n";

        }
        return output;
    }

    private static String fixQASM(String code, int offset) {
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

    private static int getQubits(ArrayList<ArrayList<Gate>> board) {
        boolean[] hasGate = new boolean[board.size()];
        for(int i = 0; i < hasGate.length; ++i) {
            hasGate[i] = false;
        }
        for(int x = 0; x < board.size(); ++x) {
            ArrayList<Gate> column = board.get(x);
            for(int y = 0; y < board.get(0).size(); ++y) {
                if(column.get(y).type != Gate.GateType.I) {
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

    public static ArrayList<ArrayList<Gate>> loadQuil(String quil) {
        ArrayList<ArrayList<Gate>> board = new ArrayList<>();
        int maxLen = 0;
        for(String line : quil.split("\n")) {
            String gate = line.split(" ")[0];
            Gate g;
            switch(gate) {
                case "H":
                    g = Gate.hadamard();
                    break;
                case "X":
                    g = Gate.x();
                    break;
                case "Y":
                    g = Gate.y();
                    break;
                case "Z":
                    g = Gate.z();
                    break;
                case "CNOT":
                    g = Gate.identity();
                    g.type = Gate.GateType.CNOT;
                    break;
                case "MEASURE":
                    g = Gate.measure();
                    break;
                default:
                    g = Gate.identity();
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
                    board.get(register+g.length).add(Gate.identity());
                    board.get(register+g.length).add(Gate.identity());
                }
            }
            board.get(register).add(g);
            if(board.get(register).size() > maxLen){
                maxLen = board.get(register).size();
            }
        }
        //Fill
        for(ArrayList<Gate> a : board) {
            while(a.size() < maxLen) {
                a.add(Gate.identity());
            }
        }
        //Transpose
        ArrayList<ArrayList<Gate>> transpose = new ArrayList<>();
        for(int y = 0; y < board.get(0).size(); ++y) {
            ArrayList<Gate> col = new ArrayList<>();
            for(ArrayList<Gate> row : board) {
                col.add(row.get(y));
            }
            transpose.add(col);
        }
        return transpose;
    }

    public static String translateQuipper(){
        String code = "Inputs: None\n";
        ArrayList<ArrayList<Gate>> board = Main.cb.board;
        int numQubits = getQubits(board);
        for(int i = 0; i < numQubits; ++i) {
            code += "QInit0(" + i + ")\n";
        }
        int offset = 20;
        for(int x = 0; x < board.size(); ++x) {
            ArrayList<Gate> instructions = board.get(x);
            for(int y = 0; y < instructions.size(); y++) {
                Gate g = instructions.get(y);
                if(g.type != Gate.GateType.I) {
                    int idx = y;
                    if(idx < offset) offset = idx;
                    code += Gate.typeToString(g.type,Gate.LangType.QUIPPER);
                    code += "(" + idx + ")";
                    if(g.type == Gate.GateType.CNOT || g.type == Gate.GateType.SWAP) {
                        code += " with controls=[+" + (idx+g.length) + "]";
                    }
                    if(g.type == Gate.GateType.SWAP){
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
}
