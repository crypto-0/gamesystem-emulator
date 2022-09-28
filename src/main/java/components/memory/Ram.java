package main.java.components.memory;

public class Ram{
  private short[] ram;
  Ram(int size){
    this.ram = new short[size];
  }

  public short read(int address){
    return ram[address];
  }
  public void write(int address,short data){
    ram[address] = data;
  }
}
