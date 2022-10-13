package hermes.Chip8;
import hermes.shared.*;

public class Chip8Bus extends Bus{
  private Controller delayTimer;
  private Controller soundTimer;

  public Chip8Bus(Controller delayTimer,Controller soundTimer,Cpu cpu,Ram ram){
    super(cpu,ram);
    this.delayTimer = delayTimer;
    this.soundTimer = soundTimer;
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
