import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.function.Supplier;

public class CircuitBoard {

    BufferedImage image;

    ArrayList<ArrayList<Gate>> board;

    public static EnumMap<Gate.GateType,Supplier<Gate>> gatemap;

    public CircuitBoard() {
        board = new ArrayList<>();
        int numGatesX = Window.WIDTH/Gate.GATE_PIXEL_SIZE;
        int numGatesY = Window.HEIGHT/Gate.GATE_PIXEL_SIZE;
        for(int x = 0; x < numGatesX; ++x) {
            board.add(new ArrayList<>());
            for(int y = 0; y < numGatesY; ++y) {
                board.get(x).add(Gate.identity());
            }
        }

        gatemap = new EnumMap<Gate.GateType, Supplier<Gate>>(Gate.GateType.class);
        gatemap.put(Gate.GateType.I,Gate::identity);
        gatemap.put(Gate.GateType.H,Gate::hadamard);
        gatemap.put(Gate.GateType.X,Gate::x);
        gatemap.put(Gate.GateType.Y,Gate::y);
        gatemap.put(Gate.GateType.Z,Gate::z);
        gatemap.put(Gate.GateType.Measure,Gate::measure);
        gatemap.put(Gate.GateType.CNOT,Gate::cnot);
        gatemap.put(Gate.GateType.SWAP,Gate::swap);
    }

    public void addRow(){
        board.add(new ArrayList<>());
        for(int i = 0; i < board.get(0).size(); ++i) {
            board.get(board.size()-1).add(Gate.identity());
        }
    }

    public void addColumn() {
        for(ArrayList<Gate> a : board) {
            a.add(Gate.identity());
        }
    }


    public BufferedImage render(){
        int unit = Gate.GATE_PIXEL_SIZE;
        image = new BufferedImage(board.size()*unit,board.get(0).size()*unit,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,image.getWidth(),image.getHeight());
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                g.setColor(Color.GREEN);
                g.drawRect(x*unit,y*unit,unit,unit);
                switch(board.get(x).get(y).type){
                    case I:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + 32,(x+1)*unit,y*unit + 32);
                        break;
                    case H:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Hadamard",x*unit,y*unit + (unit>>2));
                        break;
                    case X:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("X",x*unit,y*unit + (unit>>2));
                        break;
                    case Y:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Y",x*unit,y*unit + (unit>>2));
                        break;
                    case Z:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Z",x*unit,y*unit + (unit>>2));
                        break;
                    case Edit:
                        g.setColor(Color.RED);
                        g.drawRect(x*unit,y*unit,unit,unit);
                        break;
                    case Measure:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        g.setColor(Color.WHITE);
                        g.fillRect(x*unit + (unit>>2),y*unit + (unit>>2),unit>>1,unit>>1);
                        g.setColor(Color.BLACK);
                        g.drawString("Measure",x*unit,y*unit + (unit>>2));
                        break;
                    case CNOT:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        int len = board.get(x).get(y).length;
                        g.drawLine(x*unit + (unit>>1),y*unit + (unit>>1),x*unit + (unit>>1),(y+len)*unit + (unit>>1) + (unit>>2));
                        int centerX = x*unit + (unit>>2);
                        int centerY = (y+len)*unit + (unit>>2);
                        g.drawOval(centerX,centerY,unit>>1,unit>>1);
                        break;
                    case SWAP:
                        g.setColor(Color.BLACK);
                        g.drawLine(x*unit,y*unit + (unit>>1),(x+1)*unit,y*unit + (unit>>1));
                        //Diagonal lines
                        g.drawLine(x*unit + (unit>>2),y*unit + (unit>>2),(x+1)*unit - (unit >> 2), (y+1)*unit - (unit>>2));
                        g.drawLine(x*unit + (unit>>2),(y+1)*unit - (unit>>2),(x+1)*unit - (unit >> 2), y*unit + (unit>>2));
                        int swaplen = board.get(x).get(y).length;
                        g.drawLine(x*unit + (unit>>1),y*unit + (unit>>1),x*unit + (unit>>1),(y+swaplen)*unit + (unit>>1));
                        //More diagonal lines
                        g.drawLine(x*unit + (unit>>2),(y+swaplen)*unit + (unit>>2),(x+1)*unit - (unit >> 2), (y+swaplen+1)*unit - (unit>>2));
                        g.drawLine(x*unit + (unit>>2),(y+1+swaplen)*unit - (unit>>2),(x+1)*unit - (unit >> 2), (y+swaplen)*unit + (unit>>2));

                }
            }
        }
        return image;
    }

    public void edit(Gate.GateType g) {
        int counter = 0;
        for(int x = 0; x < board.size(); ++x) {
            for(int y = 0; y < board.get(0).size(); ++y) {
                Gate gate = board.get(x).get(y);
                if(gate.type == Gate.GateType.Edit) {
                    Gate newGate = gatemap.get(g).get();
                    board.get(x).set(y,newGate);
                    ++counter;
                }
            }
        }
        if(g == Gate.GateType.H && counter == 3) {

                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\Josh\\IdeaProjects\\QuantumCircuits\\hadamard.wav").getAbsoluteFile());
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);
                    clip.start();
                    //Thread.sleep(clip.getMicrosecondLength()/1000);
                } catch(Exception ex) {
                    System.out.println("Error with playing sound.");
                    ex.printStackTrace();
                }

        }
        Main.render();
    }

}
