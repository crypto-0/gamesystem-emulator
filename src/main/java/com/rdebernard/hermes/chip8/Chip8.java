package com.rdebernard.hermes.chip8;
import com.rdebernard.hermes.shared.*;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

public class Chip8 extends GameSystem implements Observer{
  private Chip8Cpu cpu;
  private Chip8Keypad keypad;
  private Ram ram;
  private Chip8PPU PPU;
  private Timer delayTimer;
  private Timer soundTimer;
  private Chip8Bus bus;
  private boolean step = false;
  private final int MAXCYCLES =9;
  private int elapsecycles = 0;
  public Chip8(boolean debug){
    this.debug = debug;
    cpu = new Chip8Cpu();
    ram = new Ram(4000);
    PPU = new Chip8PPU(12);
    delayTimer = new Timer(4);
    soundTimer = new Timer(8);
    keypad = new Chip8Keypad(16);
    bus = new Chip8Bus(cpu, ram, delayTimer, soundTimer, PPU,keypad);
    cpu.loadFonts();
    cpu.reset();
    GridBagConstraints gbc = new GridBagConstraints();
    setLayout(new GridBagLayout());
    gbc.gridx = 1;
    gbc.gridy = 1;
    if(debug){
      gbc.gridx = 1;
      gbc.gridy = 0;
      add(cpu,gbc);
      gbc.gridx = 1;
      gbc.gridy = 1;
      add(ram,gbc);
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.gridheight = 2;
      gbc.insets = new Insets( 2, 2, 2, 2 );
      add(PPU,gbc);
    }
    else add(PPU);
    addObserver(this);
    addObserver(keypad);
    addKeyListener(this);
  }

	@Override
	public void update() {
    if(!paused){
      for(int a =0; a < MAXCYCLES; a++) cpu.clock();
      PPU.clock();
      delayTimer.clock();
      soundTimer.clock();
    }
    else{
      if(step){
        step = false;
        cpu.clock();
        elapsecycles++;
        if(elapsecycles == MAXCYCLES){
          PPU.clock();
          delayTimer.clock();
          soundTimer.clock();
          elapsecycles = 0;
        }
      }
    }
		
	}

	@Override
	public void render() {
    PPU.render();
    if(debug){
      cpu.render();
      ram.render();
    }
	}
  public void reset(){
    cpu.reset();
    PPU.reset();
    keypad.reset();
    delayTimer.reset();
    soundTimer.reset();
  }

	@Override
	public void loadRom(InputStream inputStream) {
    try{
      InputStream fileInputStream = inputStream;
      int singleByte;
      int currentByteIndex = 0;
      //short rom[] = new short[(int)file.length()];
      short rom[] = new short[(int)fileInputStream.available()];
      while((singleByte = fileInputStream.read()) != -1){
        rom[currentByteIndex++] = (short)singleByte;
      }
      fileInputStream.close();
      cpu.loadRom(rom);
    }
    catch(IOException e){
      System.out.println("failed to load rom");
    }
	}

	@Override
	public void Update(KeyEvent keyEvent) {
    if(keyEvent.getID() == KeyEvent.KEY_PRESSED){
      switch(keyEvent.getKeyCode()){
        case 80:
          paused = !paused;
          break;
        case 78:
          step = true;
          break;
        case KeyEvent.VK_R:
          if(paused){
            reset();
            paused = false;
            elapsecycles =0;
          }
          break;
      }
    }
	}
}
