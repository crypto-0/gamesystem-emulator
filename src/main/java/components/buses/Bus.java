package main.java.components.buses;
import main.java.components.cpu.Cpu;
import main.java.components.memory.Ram;

public class Bus {
  public Ram ram;
  public Cpu cpu;
  
  Bus(Cpu cpu,Ram ram){
    this.cpu = cpu;
    this.ram = ram;
    this.cpu.connectbus(this);
  }

  public void cpuWrite(int address, short data){
    this.ram.write(address,data);
  }
  public short cpuRead(int address){
    return this.ram.read(address);
  }
}


