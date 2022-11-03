package hermes;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JFrame;
import hermes.chip8.Chip8;

public class Hermes{
  static boolean debug;
  static InputStream rom;
  public static void main(String args[]){
    if(args.length <=1){
      help();
      return;
    }
    parseArgs(args);
    final JFrame frame = new JFrame("Hermes");
    final Chip8 chip8 = new Chip8(debug);
    chip8.loadRom(rom);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(chip8);
    frame.pack();
    frame.setVisible(true);
    chip8.render();
  }

  private  static void help(){
    System.out.println("Usage: java -jar Hermes.jar [OPTIONS]");
    System.out.println();
    System.out.println("Options:");
    System.out.println("  --help Show this message and exit");
    System.out.println("  --rom choose a rom to run from available roms");
    System.out.println("  --debug start in debug mode");
    System.out.println("Available roms: ");
    System.out.println("  Tetris");
    System.out.println("  Pong");
    System.out.println("  Brick");
    System.out.println("  Brix");
    System.out.println("  Space-Invaders");
  }

  private static void parseArgs(String args[]){
    Queue<String> queue = new LinkedList<String>(Arrays.asList(args));
    int requiredArgsFound =0;
    while(!queue.isEmpty()){
      String arg = queue.poll();
      if(arg.equals("--help") || arg.equals("-h")){
        help();
        System.exit(1);
      }
      else if(arg.equals("--debug") || arg.equals("-d")){
        debug = true;
      }
      else if(arg.equals("--rom")|| arg.equals("-r")){
        String romName = queue.poll();
        try{
        InputStream inputStream = Hermes.class.getResourceAsStream("/"+ romName +".ch8");
        if(inputStream ==null){
          System.out.println("Could not load " + romName +".ch8 rom");
          System.exit(1);
        }
        rom = inputStream;
        requiredArgsFound++;
        }
        catch(Exception e){
          System.out.println("Could not load " + romName +".ch8 rom");
          System.exit(1);
        }
      }
    }
    if(requiredArgsFound !=1){
        System.out.println("Required arguments not provided! type --help");
        System.exit(1);
    }
  }
}
