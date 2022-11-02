package hermes.chip8;
import hermes.shared.*;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumnModel;
import java.awt.Color;
import java.awt.Dimension;

public class Chip8Cpu  extends JPanel implements Cpu {
  Register[] registers = new Register[16];
  Bus bus;
  Register pc = new Register();
  Register sp = new Register();
  Register index = new Register();
  int fontsetStartAddress = 0x50;
  int stackStartAddress = 0x06cf;//to 0x6a0 48 bytes
  int vramStartAddress = 0x0700;// to 0x07ff
  int pcStartAddress = 0x200;                                
  int opcode = 0;
  int cycles =0;
  short[] fontset = {
    0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
    0x20, 0x60, 0x20, 0x20, 0x70, // 1
    0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
    0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
    0x90, 0x90, 0xF0, 0x10, 0x10, // 4
    0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
    0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
    0xF0, 0x10, 0x20, 0x40, 0x40, // 7
    0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
    0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
    0xF0, 0x90, 0xF0, 0x90, 0x90, // A
    0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
    0xF0, 0x80, 0x80, 0x80, 0xF0, // C
    0xE0, 0x90, 0x90, 0x90, 0xE0, // D
    0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
    0xF0, 0x80, 0xF0, 0x80, 0x80  // F
  };
  Instruction[] table = new Instruction[16];
  Instruction[] table8 = new Instruction[16];
  Instruction[] tableE = new Instruction[16];
  Instruction[] tableF = new Instruction[0x66];
  Instruction inst00E0 = new Instruction(new Op_00E0(),new Imp(),1);
  Instruction inst00EE = new Instruction(new Op_00EE(),new Imp(),1);

  private final int PWIDTH = 200;
  private final int PHEIGHT = 200;
  private JTable jtable;
  private TableColumnModel tableModel;
  public Chip8Cpu() {
    setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
    //load opcode classes
    table[1] = new Instruction(new Op_1nnn(),new Imp(),1);
    table[2] = new Instruction(new Op_2nnn(),new Imp(),1);
    table[3] = new Instruction(new Op_3xkk(),new Imp(),1);
    table[4] = new Instruction(new Op_4xkk(),new Imp(),1);
    table[5] = new Instruction(new Op_5xy0(),new Imp(),1);
    table[6] = new Instruction(new Op_6xkk(),new Imp(),1);
    table[7] = new Instruction(new Op_7xkk(),new Imp(),1);
    table[9] = new Instruction(new Op_9xy0(),new Imp(),1);
    table[0xa] = new Instruction(new Op_Annn(),new Imp(),1);
    table[0xb] = new Instruction(new Op_Bnnn(),new Imp(),1);
    table[0xc] = new Instruction(new Op_Cxkk(),new Imp(),1);
    table[0xd] = new Instruction(new Op_Dxyn(),new Imp(),1);

    table8[0] = new Instruction(new Op_8xy0(),new Imp(),1);
    table8[1] = new Instruction(new Op_8xy1(),new Imp(),1);
    table8[2] = new Instruction(new Op_8xy2(),new Imp(),1);
    table8[3] = new Instruction(new Op_8xy3(),new Imp(),1);
    table8[4] = new Instruction(new Op_8xy4(),new Imp(),1);
    table8[5] = new Instruction(new Op_8xy5(),new Imp(),1);
    table8[6] = new Instruction(new Op_8xy6(),new Imp(),1);
    table8[7] = new Instruction(new Op_8xy7(),new Imp(),1);
    table8[0xe] = new Instruction(new Op_8xyE(),new Imp(),1);

    tableE[1] = new Instruction(new Op_ExA1(),new Imp(),1);
    tableE[0xe] = new Instruction(new Op_Ex9E(),new Imp(),1);

    tableF[0x07] = new Instruction(new Op_Fx07(),new Imp(),1);
    tableF[0x0a] = new Instruction(new Op_Fx0A(),new Imp(),1);
    tableF[0x15] = new Instruction(new Op_Fx15(),new Imp(),1);
    tableF[0x18] = new Instruction(new Op_Fx18(),new Imp(),1);
    tableF[0x1e] = new Instruction(new Op_Fx1E(),new Imp(),1);
    tableF[0x29] = new Instruction(new Op_Fx29(),new Imp(),1);
    tableF[0x33] = new Instruction(new Op_Fx33(),new Imp(),1);
    tableF[0x55] = new Instruction(new Op_Fx55(),new Imp(),1);
    tableF[0x65] = new Instruction(new Op_Fx65(),new Imp(),1);

    for(int a =0; a < registers.length;a ++){
      registers[a] = new Register();
    }
    
    jtable = new JTable(10,2);
    tableModel = jtable.getColumnModel();
    this.setVisible(true);
    tableModel.getColumn(0).setPreferredWidth(38);
    tableModel.getColumn(1).setPreferredWidth(20);
    jtable.setShowGrid(false);
    jtable.setTableHeader(null);
    jtable.setBackground(Color.BLACK);
    jtable.setForeground(Color.LIGHT_GRAY);
    this.setBackground(Color.BLACK);
    setMaximumSize(getPreferredSize());
    jtable.setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
    add(jtable);
    TitledBorder border = new TitledBorder("Chip8Cpu");
    border.setTitlePosition(TitledBorder.TOP);
    setBorder(border);
    jtable.setFocusable(false);
  }


  class Imp implements AddressMode{

    @Override
    public int execute() {
      return 0;
    }

  }
  class Op_0nnn implements Opcode {
    String op_name = "SYS addr";

    @Override
    public int execute() {
      return 0;

    }

  }

  class Op_00E0 implements Opcode {
    String op_name = "CLS";

    @Override
    public int execute() {
      short data = 0;
      bus.cpuWrite(12,(short)1,true);
      for (int a =0; a < 64 * 32; a++){
        int address = a + vramStartAddress;
        bus.cpuWrite(address,data,false);
      }
      return 0;

    }

  }

  class Op_00EE implements Opcode {
    String op_name = "RET";

    @Override
    public int execute() {
      int address = sp.read();
      int temp = bus.cpuRead(address,false);
      temp = temp << 8;
      temp |= bus.cpuRead(address + 1,false);
      sp.write(address + 2);
      pc.write(temp);
      return 0;
    }

  }

  class Op_1nnn implements Opcode {
    String op_name = "JP addr";

    @Override
    public int execute() {
      int temp =  opcode & 0xfff;
      pc.write(temp);
      return 0;
    }

  }

  class Op_2nnn implements Opcode {
    String op_name = "CALL addr";

    @Override
    public int execute() {
      int address = sp.read() - 2;
      sp.write(address);
      short low_byte =(short)(pc.read() & 0xff);
      short high_byte =(short)((pc.read() >> 8) & 0xff);
      bus.cpuWrite(address,high_byte,false);
      bus.cpuWrite(address + 1,low_byte,false);
      int temp = opcode & 0xfff;
      pc.write(temp);
      return 0;
    }

  }

  class Op_3xkk implements Opcode {
    String op_name = "SE Vx, byte";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      if (registers[Vx].read() == (opcode & 0xff))
        pc.write(pc.read() + 2);
      return 0;
    }

  }

  class Op_4xkk implements Opcode {
    String op_name = "SNE Vx, byte";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      if (registers[Vx].read() != (opcode & 0xff))
        pc.write(pc.read() + 2);
      return 0;
    }

  }

  class Op_5xy0 implements Opcode {
    String op_name = "SE Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      if (registers[Vx].read() == registers[Vy].read())
        pc.write(pc.read() + 2);
      return 0;
    }

  }

  class Op_6xkk implements Opcode {
    String op_name = "LD Vx, byte";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[Vx].write(opcode & 0xff);
      return 0;
    }

  }

  class Op_7xkk implements Opcode {
    String op_name = "ADD Vx, byte";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      int result = registers[Vx].read() + (opcode & 0xff);
      registers[Vx].write(result & 0xff);
      return 0;

    }

  }

  class Op_8xy0 implements Opcode {
    String op_name = "LD Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx].write(registers[Vy].read());
      return 0;

    }

  }

  class Op_8xy1 implements Opcode {
    String op_name = "OR Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx].write(registers[Vx].read() | registers[Vy].read());
      return 0;

    }

  }

  class Op_8xy2 implements Opcode {
    String op_name = "AND Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx].write(registers[Vx].read() & registers[Vy].read());
      return 0;
    }

  }

  class Op_8xy3 implements Opcode {
    String op_name = "XOR Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx].write(registers[Vx].read() ^ registers[Vy].read());
      return 0;
    }

  }

  class Op_8xy4 implements Opcode {
    String op_name = "ADD Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int result = registers[Vx].read() + registers[Vy].read();
      registers[0xf].write(0);
      if (result > 255)
        registers[0xf].write(1);
      registers[Vx].write(result & 0xff);
      return 0;
    }

  }

  class Op_8xy5 implements Opcode {
    String op_name = "SUB Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int result = registers[Vx].read() - registers[Vy].read();
      registers[0xf].write(0);
      if (registers[Vx].read() > registers[Vy].read())
        registers[0xf].write(1);
      registers[Vx].write(result & 0xff);
      return 0;
    }

  }

  class Op_8xy6 implements Opcode {
    String op_name = "SHR Vx {, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[0xf].write(0);
      int result = registers[Vx].read() >> 1;
      if ((registers[Vx].read() & 0x1) == 1)
        registers[0xf].write(1);
      registers[Vx].write(result  & 0xff);
      return 0;

    }

  }

  class Op_8xy7 implements Opcode {
    String op_name = "SUBN Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int result = registers[Vy].read() - registers[Vx].read();
      registers[0xf].write(0);
      if (registers[Vy].read() > registers[Vx].read())
        registers[0xf].write(0);
      registers[Vx].write(result & 0xff);
      return 0;
    }

  }

  class Op_8xyE implements Opcode {
    String op_name = "SHL Vx {, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[0xf].write(0);
      if (((registers[Vx].read() >> 7) & 0x1) == 0x1)
        registers[0xf].write(1);
      int result = registers[Vx].read() << 1;
      registers[Vx].write(result & 0xff);
      return 0;
    }

  }

  class Op_9xy0 implements Opcode {
    String op_name = "SNE Vx, Vy";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      if (registers[Vx].read() != registers[Vy].read())
        pc.write(pc.read() + 2);
      return 0;
    }

  }

  class Op_Annn implements Opcode {
    String op_name = "LD I, addr";
    @Override 
    public int execute() {
      index.write(opcode & 0xfff);
      return 0;
    }

  }

  class Op_Bnnn implements Opcode {
    String op_name = "JP V0, addr";

    @Override
    public int execute() {
      pc.write(registers[0].read() + (opcode & 0xfff));
      return 0;
    }

  }

  class Op_Cxkk implements Opcode {
    String op_name = "RND Vx, byte";

    @Override
    public int execute() {
      final Random rand = new Random();
      final int result = rand.nextInt(256);
      final int Vx = (opcode >> 8) & 0xf;
      registers[Vx].write((opcode & 0xff) & result);
      return 0;
    }

  }

  class Op_Dxyn implements Opcode {
    String op_name = "DRW Vx, Vy, nibble";

    @Override
    public int execute() {
      //display is 64 *32 monochrome
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int x_pos = registers[Vx].read();
      final int y_pos = registers[Vy].read();
      registers[0xf].write(0);
      bus.cpuWrite(12,(short)1,true);
      short originalPixel;
      short finalPixel;
      int finalAddress;
      short spriteByte;
      for (int a = 0; a < (opcode & 0xf); a++) {
        spriteByte = bus.cpuRead(index.read() + a, false);
        for(int b=0; b< 8; b++){
          finalAddress = vramStartAddress + ((64 * ((y_pos + a)%32)) + ((b + x_pos) % 64));
          originalPixel = bus.cpuRead(finalAddress , false);
          finalPixel = (short)(originalPixel ^ (spriteByte>>(7-b)) & 0x1);
          if(originalPixel == 1 && originalPixel != finalPixel){
            registers[0xf].write(1);
          }
          bus.cpuWrite(finalAddress,finalPixel,false);
        }
      }
      return 0;
    }

  }

  class Op_Ex9E implements Opcode {
    String op_name = "SKP Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int keyInReg = registers[Vx].read();
      final short status = bus.cpuRead(6,true);
      if (status == 1){
        final short key = bus.cpuRead(4,true);
        if (key == keyInReg) pc.write(pc.read() + 2);
      }
      return 0;
    }
  }

  class Op_ExA1 implements Opcode {
    String op_name = "SKNP Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int keyInReg = registers[Vx].read();
      final short status = bus.cpuRead(6,true);
      if (status == 1){
        final short key = bus.cpuRead(4,true);
        if (key != keyInReg) pc.write(pc.read() + 2);
      }
      else{
        pc.write(pc.read() + 2);
      }

      return 0;

    }

  }

  class Op_Fx07 implements Opcode {
    String op_name = "LD Vx, DT";

    @Override
    public int execute() {
      final int Vx = (opcode >> 12) & 0xf;
      registers[Vx].write(bus.cpuRead(4,true));
      return 0;
    }

  }

  class Op_Fx0A implements Opcode {
    String op_name = "LD Vx, k";

    @Override
    public int execute() {
      short key = 0;
      short status = bus.cpuRead(3,true);
      if (status ==1){
        final int Vx = (opcode >> 12) & 0xf;
        key = bus.cpuRead(4,true);
        registers[Vx].write(key);
      }
      else pc.write(pc.read() -2);
      return 0;
    }
  }

  class Op_Fx15 implements Opcode {
    String op_name = "LD DT, Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      bus.cpuWrite(12,(short)registers[Vx].read(),true);
      return 0;

    }

  }

  class Op_Fx18 implements Opcode {
    String op_name = "LD ST, Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      bus.cpuWrite(8,(short)registers[Vx].read(),true);
      return 0;

    }

  }

  class Op_Fx1E implements Opcode {
    String op_name = "ADD I, Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      int temp = index.read() + registers[Vx].read();
      temp &=0xfff;
      index.write(temp);
      return 0;

    }

  }

  class Op_Fx29 implements Opcode {
    String op_name = "LD F, Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      int temp = (registers[Vx].read() * 5) + fontsetStartAddress;
      temp = temp & 0xffff;
      index.write(temp);
      return 0;
    }

  }

  class Op_Fx33 implements Opcode {
    String op_name = "LD B, Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      int result = registers[Vx].read() ;
      bus.cpuWrite(index.read() + 2,(short)(result % 10),false);
      result = result/ 10;
      bus.cpuWrite(index.read() + 1,(short)(result % 10),false);
      result = result/10;
      bus.cpuWrite(index.read() + 0,(short)(result),false);
      return 0;

    }

  }

  class Op_Fx55 implements Opcode {
    String op_name = "LD [i], Vx";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      for (int a = 0; a <=Vx; a++) {
        bus.cpuWrite(index.read() + a,(short)registers[a].read(),false);
      }
      return 0;
    }

  }

  class Op_Fx65 implements Opcode {
    String op_name = "LD Vx, [I]";

    @Override
    public int execute() {
      final int Vx = (opcode >> 8) & 0xf;
      for (int a = 0; a <=Vx; a++) {
        registers[a].write(bus.cpuRead(index.read() + a,false));
      }

      return 0;

    }
  }
  public void loadFonts(){
    for (int a = 0; a < 80; a++) {
      bus.cpuWrite(fontsetStartAddress + a,fontset[a],false);
    }
  }
  public void loadRom(short[] rom){
    for(int a =0; a< rom.length;a++){
      bus.cpuWrite(pcStartAddress + a,rom[a],false);
    }

  }

  @Override
  public void reset() {
    for(Register reg: this.registers){
      reg.write(0);
    }
    this.pc.write(0x200);
    this.index.write(0);
    this.sp.write(this.stackStartAddress);
    //this.inst00E0.op.execute();
  }

  @Override
  public void irq() {

  }

  @Override
  public void clock() {
    if(cycles ==0){
      opcode = bus.cpuRead(pc.read(),false);
      opcode = opcode << 8;
      opcode = opcode | bus.cpuRead(pc.read() + 1, false);
      pc.write(pc.read() + 2);
      switch (opcode){
        case 0x00E0:
          cycles = inst00E0.cycles;
          cycles +=inst00E0.addressMode.execute();
          inst00E0.op.execute();
          break;
        case 0x00EE:
          cycles = inst00EE.cycles;
          cycles +=inst00EE.addressMode.execute();
          cycles +=inst00EE.op.execute();
          break;
        default:
          int firstdigit = opcode >>12;
          Instruction instruction;
          switch(firstdigit){
            case 0x8:
              instruction = table8[(opcode & 0xf)];
              if(instruction !=null){
                cycles = instruction.cycles;
                cycles += instruction.addressMode.execute();
                cycles += instruction.op.execute();
              }
              else{
                cycles =1;
              }
              break;
            case 0xE:
              instruction = tableE[(opcode & 0xf)];
              if(instruction !=null){
                cycles = instruction.cycles;
                cycles += instruction.addressMode.execute();
                cycles += instruction.op.execute();
              }
              else{
                cycles =1;
              }
              break;
            case 0xf:
              instruction = tableF[(opcode & 0xff)];
              if(instruction !=null){
                cycles = instruction.cycles;
                cycles += instruction.addressMode.execute();
                cycles += instruction.op.execute();
              }
              else{
                cycles =1;
              }
              break;
            default:
              instruction = table[firstdigit];
              if(instruction !=null){
                cycles = instruction.cycles;
                cycles += instruction.addressMode.execute();
                cycles += instruction.op.execute();
              }
              else{
                cycles =1;
              }
          }
      }
    }
    cycles--;
    render();
  }

  @Override
  public Boolean completed() {
    return null;
  }
  public void render(){
    String memoryPc = String.format("PC: %04X:", pc.read());
    String memorySp = String.format("SP: %04X:", sp.read());
    String memoryIndex = String.format("ID: %04X:", index.read());
    jtable.setValueAt(memoryPc, 0, 0);
    jtable.setValueAt(memorySp, 0, 1);
    jtable.setValueAt(memoryIndex, 1, 0);
    for(int a=0; a< registers.length/2 ; a++){
      String memoryVx = String.format("V%d: %04X:",a * 2 + 1, registers[a * 2].read());
      String memoryVx2 = String.format("V%d: %04X:",a * 2 + 2, registers[a *2 +1].read());
      jtable.setValueAt(memoryVx, 2 + a, 0);
      jtable.setValueAt(memoryVx2, 2 + a, 1);
    }
    repaint();
  }

@Override
public void connectBus(Bus bus) {
  this.bus = bus;
}

}
