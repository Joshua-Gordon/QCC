import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Keyboard implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()){
        case "Hadamard":
            Main.cb.edit(Gate.GateType.H);
            break;
        case "I":
            Main.cb.edit(Gate.GateType.I);
            break;
        case "X":
            Main.cb.edit(Gate.GateType.X);
            break;
        case "Y":
            Main.cb.edit(Gate.GateType.Y);
            break;
        case "Z":
            Main.cb.edit(Gate.GateType.Z);
            break;
        case "Measure":
            Main.cb.edit(Gate.GateType.Measure);
            break;
        case "CNot":
            Main.cb.edit(Gate.GateType.CNOT);
            break;
        case "Swap":
            Main.cb.edit(Gate.GateType.SWAP);
            break;
        case "QUIL":
            System.out.println(Translator.translateQUIL());
            break;
        case "QASM":
            System.out.println(Translator.translateQASM());
            break;
        case "Quipper":
            System.out.println(Translator.translateQuipper());
            break;
		}
	}
}
