package hermes.shared;


public class Bus {
  private Ram ram;
  private Cpu cpu;
  
  public Bus(Cpu cpu,Ram ram){
    this.cpu = cpu;
    this.ram = ram;
    this.cpu.connectBus(this);
  }

  public void cpuWrite(int address, short data,boolean ioWrite){
    this.ram.write(address,data);
  }
  public short cpuRead(int address,boolean ioRead){
    return this.ram.read(address);
  }
}


