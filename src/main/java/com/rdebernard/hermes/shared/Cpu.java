package com.rdebernard.hermes.shared;

public interface Cpu {
  public void reset();
  public void irq();
  public void clock();
  public void connectBus(Bus bus);
  public Boolean completed();

  interface Opcode{
    public int execute();
  }

  interface AddressMode{
    public int execute();
  }

  public class Register{
    private int data;
    public Register(){
    }
    public int read(){
      return data;
    };
    public void write(int value ){
      data = value;
    }
  }
  class RegisterUnion{
    private Register r1;
    private Register r2;
    RegisterUnion(Register r1, Register r2){
      this.r1 = r1;
      this.r2 = r2;
    }

    public int read(){
      int temp = r1.read();
      temp = temp << 16;
      temp = temp | r2.read();
      return temp;
    }
    public void write(Register r1, Register r2){
      this.r1 = r1;
      this.r2 = r2;
    }

  }
  class Instruction{
    public Opcode op;
    public AddressMode addressMode;
    public int cycles;
    //public String name;
    public Instruction(Opcode op,AddressMode addressMode,int cycles){
      this.op = op;
      this.addressMode = addressMode;
      this.cycles = cycles;
    }
  }
}

