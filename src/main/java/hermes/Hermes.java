package hermes;
import javax.swing.JFrame;
import hermes.chip8.Chip8;

public class Hermes{
  public static void main(String args[]){
    final JFrame frame = new JFrame("Hermes");
    final Chip8 chip8 = new Chip8();
    //chip8.loadRom("/home/tae/projects/java/hermes/chip8-test-rom.ch8");
    //chip8.loadRom("/home/tae/projects/java/hermes/test_opcode.ch8");
    chip8.loadRom("/home/tae/projects/java/hermes/Brick.ch8");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //frame.setSize(1000,1000);
    frame.setVisible(true);
    frame.add(chip8);
    frame.pack();
    chip8.render();
    //frame.getContentPane().add(ram);
  }
}
