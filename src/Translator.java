import java.util.ArrayList;
import java.util.Arrays;

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
                    if (g.type == Gate.GateType.Measure) {
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
                    if (g.type == Gate.GateType.Measure) {
                        code += " -> c[" + idx + "]";
                    }
                    code += ";\n";
                }
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
}
