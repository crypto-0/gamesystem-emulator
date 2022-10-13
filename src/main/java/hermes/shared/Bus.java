package hermes.gamesystem.component.bus;
import hermes.gamesystem.component.memory.Ram;
import hermes.gamesystem.component.cpu.Cpu;


public class Bus {
  private Ram ram;
  private Cpu cpu;
  
  public Bus(Cpu cpu,Ram ram){
    this.cpu = cpu;
    this.ram = ram;
    this.cpu.connectbus(this);
  }

  public void cpuWrite(int address, short data,boolean ioWrite){
    this.ram.write(address,data);
  }
  public short cpuRead(int address,boolean ioRead){
    return this.ram.read(address);
  }
}


