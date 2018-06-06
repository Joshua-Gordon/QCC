import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyChar());
        switch(e.getKeyCode()){
            case KeyEvent.VK_H:
                Main.cb.edit(Gate.GateType.H);
                break;
            case KeyEvent.VK_X:
                Main.cb.edit(Gate.GateType.X);
                break;
            case KeyEvent.VK_Y:
                Main.cb.edit(Gate.GateType.Y);
                break;
            case KeyEvent.VK_Z:
                Main.cb.edit(Gate.GateType.Z);
                break;
            case KeyEvent.VK_M:
                Main.cb.edit(Gate.GateType.Measure);
                break;
            case KeyEvent.VK_C:
                Main.cb.edit(Gate.GateType.CNOT);
                break;
            case KeyEvent.VK_S:
                Main.cb.edit(Gate.GateType.SWAP);
                break;
            case KeyEvent.VK_ENTER:
                System.out.println(Translator.translateQUIL());
                break;
            case KeyEvent.VK_Q:
                System.out.println(Translator.translateQASM());
                break;
            case KeyEvent.VK_P:
                System.out.println(Translator.translateQuipper());
                break;
            default:
                Main.cb.edit(Gate.GateType.I);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
