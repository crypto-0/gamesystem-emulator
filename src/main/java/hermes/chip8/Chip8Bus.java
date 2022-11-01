package hermes.chip8;
import hermes.shared.*;

public class Chip8Bus extends Bus{
  private Controller delayTimer;
  private Controller soundTimer;
  private Controller PPU;

  public Chip8Bus(Cpu cpu,Ram ram,Controller delayTimer, Controller soundTimer,Controller PPU){
    super(cpu,ram);
    this.delayTimer = delayTimer;
    this.soundTimer = soundTimer;
    this.PPU = PPU;
    this.PPU.connectBus(this);
  }

@Override
public short cpuRead(int address, boolean ioRead) {
  if(ioRead){
    if(delayTimer.getBaseAddress() == address) return delayTimer.read(address);
    else if(soundTimer.getBaseAddress() == address) return soundTimer.read(address);
    return 0;
  }
  return super.cpuRead(address, ioRead);
}

@Override
public void cpuWrite(int address, short data, boolean ioWrite) {
  if(ioWrite){
    if(delayTimer.getBaseAddress() == address) delayTimer.write(address, data);
    else if(soundTimer.getBaseAddress() == address) soundTimer.write(address, data);
  }
  else super.cpuWrite(address, data, ioWrite);
}

}
