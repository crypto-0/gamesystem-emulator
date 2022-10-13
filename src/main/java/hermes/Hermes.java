package hermes;
import java.awt.BorderLayout;
import javax.swing.JFrame;

import hermes.gamesystem.component.memory.Ram;
import hermes.gamesystem.component.cpu.Chip8Cpu;
import hermes.gamesystem.component.bus.Bus;

public class Hermes{
  public static void main(String args[]){
    final JFrame frame = new JFrame("Hermes");
    Ram ram = new Ram(2000);
    Chip8Cpu chip8Cpu = new Chip8Cpu();
    Bus bus = new Bus(chip8Cpu, ram);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.add(ram,BorderLayout.SOUTH);
    frame.add(chip8Cpu,BorderLayout.NORTH);
    frame.pack();
    ram.render();
    chip8Cpu.render();
    //frame.getContentPane().add(ram);
  }
}
