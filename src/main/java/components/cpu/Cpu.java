package main.java.components.cpu;
import main.java.components.buses.Bus;
public interface Cpu {
  public void reset();
  public void irq();
  public void clock();
  public void connectbus(Bus bus);
  public Boolean completed();

  interface Opcode{
    public void execute();
  }

  interface AddressMode{
    public long execute();
  }

  class Register{
    private long data;
    public long read(){
      return data;
    };
    public void write(long value ){
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

    public long read(){
      long temp = r1.read();
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
    public String name;
  }
}

