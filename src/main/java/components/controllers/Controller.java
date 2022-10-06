package main.java.components.controllers;

public class Controller{
  private short data= 0;
  private short command= 0;
  private short status = 0;
  private final int ioBaseAddress;
  Controller(int ioBaseAddress){
    this.ioBaseAddress = ioBaseAddress;
  }
  public short read(int ioAddress){
    if(ioBaseAddress + 0 == ioAddress) return data;
    else if(ioBaseAddress + 1 == ioAddress) return command;
    else if(ioBaseAddress + 2 == ioAddress) return status;
    else return 0;
    }

    public void write(int ioAddress,short data){
    if(ioBaseAddress + 0 == ioAddress) this.data =data;
    else if(ioBaseAddress + 1 == ioAddress) this.command = data;
  }

}
