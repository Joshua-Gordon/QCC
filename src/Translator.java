import java.util.ArrayList;
import java.util.stream.Collectors;

public class Translator {

    public static String translate(){ //Translates to Quil
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
                    if(idx < offset) {
                        offset = idx;
                    }
                    code += Gate.typeToString(g.type);
                    code += " ";
                    code += idx;
                    if (g.type == Gate.GateType.CNOT) {
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
        return fix(code,offset);
    }

    private static String fix(String code,int offset) {
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
}
