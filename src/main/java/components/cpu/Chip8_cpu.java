package main.java.components.cpu;
import main.java.components.buses.Bus;
import main.java.components.cpu.Cpu;

class Chip8Cpu implements Cpu{
  short[] registers = new short[17];
  short[] memory = new short[4097];
  int[] stack = new int[16];
  int[] video = new int[64 * 32];
  short[] keypad = new short[16];
  int index = 0;
  int pc=0x200;
  short sp=-1;
  short delayTimer=0;
  short soundTimer=0;
  int opcode = 0;
  int fontsetStartAddress = 0x50;
  int scale = 5;
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
  Opcode[] table = new Opcode[16];
  Opcode[] table8 = new Opcode[16];
  Opcode[] tableE = new Opcode[16];
  Opcode[] tableF = new Opcode[0x66];
  Op_00E0 op_00e0 = new Op_00E0();
  Op_00EE op_00EE = new Op_00EE();

  

  public Chip8Cpu() {
    //load fontset in memory
    for (int a = 0; a < 80; a++) {
      memory[fontsetStartAddress + a] = fontset[a];
    }
    //load opcode classes
    table[1] = new Op_1nnn();
    table[2] = new Op_2nnn();
    table[3] = new Op_3xkk();
    table[4] = new Op_4xkk();
    table[5] = new Op_5xy0();
    table[6] = new Op_6xkk();
    table[7] = new Op_7xkk();
    table[9] = new Op_9xy0();
    table[0xa] = new Op_Annn();
    table[0xb] = new Op_Bnnn();
    table[0xc] = new Op_Cxkk();
    table[0xd] = new Op_Dxyn();

    table8[0] = new Op_8xy0();
    table8[1] = new Op_8xy1();
    table8[2] = new Op_8xy2();
    table8[3] = new Op_8xy3();
    table8[4] = new Op_8xy4();
    table8[5] = new Op_8xy5();
    table8[6] = new Op_8xy6();
    table8[7] = new Op_8xy7();
    table8[0xe] = new Op_8xyE();

    tableE[1] = new Op_ExA1();
    tableE[0xe] = new Op_Ex9E();

    tableF[0x07] = new Op_Fx07();
    tableF[0x0a] = new Op_Fx0A();
    tableF[0x15] = new Op_Fx15();
    tableF[0x18] = new Op_Fx18();
    tableF[0x1e] = new Op_Fx1E();
    tableF[0x29] = new Op_Fx29();
    tableF[0x33] = new Op_Fx33();
    tableF[0x55] = new Op_Fx55();
    tableF[0x65] = new Op_Fx65();

  }

  public void cycle() {
    opcode = memory[pc];
    opcode = (opcode << 8) | memory[pc + 1];
    opcode &= 0xffff;
  //  System.out.println(pc + ": "  + opcode);
    if ((opcode >> 4) == 0x00e) {
      if ((opcode & 0xf) == 0) {
        op_00e0.execute();
      } else {
        op_00EE.op();
      }
    }
    else if (opcode >> 12 == 8) {
      table8[opcode & 0xf].execute();
    }
    else if (opcode >> 12 == 0xe) {
      tableE[opcode & 0xf].execute();
    }
    else if (opcode >> 12 == 0xf) {
      tableF[opcode & 0xff].execute();
    }
    else {
      table[opcode >> 12].execute();
    }
    pc += 2;

  }

  public void printmemory() {
    for (int a = 0x200; a < 600; a++) {
      System.out.println(memory[a]);
    }
  }

  public void updateTimers() {
    if (delayTimer > 0) {
      delayTimer++;
    }
    if (soundTimer > 0)
      soundTimer++;
    
  }
  

  class Op_0nnn implements Opcode {
    String op_name = "SYS addr";

    @Override
    public void execute() {

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_00E0 implements Opcode {
    String op_name = "CLS";

    @Override
    public void execute() {
      for (int a = 0; a < 64 * 32; a++) {
        video[a] = 0;
      }
      registers[0xf] = 1;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_00EE implements Opcode {
    String op_name = "RET";

    @Override
    public void execute() {
      pc = stack[sp];
      sp--;
      pc -= 2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_1nnn implements Opcode {
    String op_name = "JP addr";

    @Override
    public void execute() {
      pc = opcode & 0xfff;
      pc -= 2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_2nnn implements Opcode {
    String op_name = "CALL addr";

    @Override
    public void execute() {
      sp++;
      stack[sp] = pc;
      pc = opcode & 0xfff;
      pc -= 2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_3xkk implements Opcode {
    String op_name = "SE Vx, byte";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      if (registers[Vx] == (opcode & 0xff))
        pc+=2; 

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_4xkk implements Opcode {
    String op_name = "SNE Vx, byte";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      if (registers[Vx] != (opcode & 0xff))
        pc+=2; 

    }

    @Override
    public String getName() {
      return op_name;

    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
  }

  class Op_5xy0 implements Opcode {
    String op_name = "SE Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      if (registers[Vx] == registers[Vy])
        pc+=2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_6xkk implements Opcode {
    String op_name = "LD Vx, byte";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[Vx] = (short)(opcode & 0xff);

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_7xkk implements Opcode {
    String op_name = "ADD Vx, byte";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      int result = registers[Vx] + (opcode & 0xff);
      registers[Vx] = (short) (result & 0xff);

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy0 implements Opcode {
    String op_name = "LD Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx] = registers[Vy];

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy1 implements Opcode {
    String op_name = "OR Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx] |= registers[Vy];

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy2 implements Opcode {
    String op_name = "AND Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx] &= registers[Vy];

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy3 implements Opcode {
    String op_name = "XOR Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      registers[Vx] ^= registers[Vy];

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy4 implements Opcode {
    String op_name = "ADD Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int result = registers[Vx] + registers[Vy];
      registers[0xf] = 0;
      if (result > 255)
        registers[0xf] = 1;
      registers[Vx] = (short) (result & 0xff);
    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy5 implements Opcode {
    String op_name = "SUB Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int result = registers[Vx] - registers[Vy];
      registers[0xf] = 0;
      if (registers[Vx] > registers[Vy])
        registers[0xf] = 1;
      registers[Vx] = (short) (result & 0xff);

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy6 implements Opcode {
    String op_name = "SHR Vx {, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[0xf] = 0;
      int result = registers[Vx] >> 1;
      if ((registers[Vx] & 0x1) == 1)
        registers[0xf] = 1;
      registers[Vx] = (short) (result  & 0xff);

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xy7 implements Opcode {
    String op_name = "SUBN Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int result = registers[Vy] - registers[Vx];
      registers[0xf] = 0;
      if (registers[Vy] > registers[Vx])
        registers[0xf] = 1;
      registers[Vx] = (short) (result & 0xff);

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_8xyE implements Opcode {
    String op_name = "SHL Vx {, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[0xf] = 0;
      if (((registers[Vx] >> 7) & 0x1) == 0x1)
        registers[0xf] = 1;
      int result = registers[Vx] << 1;
      registers[Vx] = (short) (result & 0xff);
    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_9xy0 implements Opcode {
    String op_name = "SNE Vx, Vy";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      if (registers[Vx] != registers[Vy])
        pc+=2;
    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Annn implements Opcode {
    String op_name = "LD I, addr";
@Override public void execute() { index = opcode & 0xfff;
    }

    @Override
    public String getName() {
      return op_name;

    }

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}
  }

  class Op_Bnnn implements Opcode {
    String op_name = "JP V0, addr";

    @Override
    public void execute() {
      pc = registers[0] + (opcode & 0xfff);
      pc-=2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Cxkk implements Opcode {
    String op_name = "RND Vx, byte";

    @Override
    public void execute() {
      final Random rand = new Random();
      final int result = rand.nextInt(256);
      final int Vx = (opcode >> 8) & 0xf;
      registers[Vx] = (short) ((opcode & 0xff) & result);

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Dxyn implements Opcode {
    String op_name = "DRW Vx, Vy, nibble";

    @Override
    public void execute() {
      //display is 64 *32 monochrome
      final int Vx = (opcode >> 8) & 0xf;
      final int Vy = (opcode >> 4) & 0xf;
      final int x_pos = registers[Vx];
      final int y_pos = registers[Vy];
      registers[0xf] = 0;
      for (int a = 0; a < (opcode & 0xf); a++) {
        final short sprite_byte = memory[index + a];
        final int pos = (((y_pos + a) % 32) * 64);
        int afterPixel;
        int beforePixel = video[pos + ((x_pos + 0) % 64)];
        video[pos + ((x_pos + 0) % 64)] ^= (short) ((sprite_byte >> 7) & 0x1);
        afterPixel = video[pos + ((x_pos + 0) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 1) % 64)];
        video[pos + ((x_pos + 1) % 64)] ^= (short) ((sprite_byte >> 6) & 0x1);
        afterPixel = video[pos + ((x_pos + 1) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 2) % 64)];
        video[pos + ((x_pos + 2) % 64)] ^= (short) ((sprite_byte >> 5) & 0x1);
        afterPixel = video[pos + ((x_pos + 2) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 3) % 64)];
        video[pos + ((x_pos + 3) % 64)] ^= (short) ((sprite_byte >> 4) & 0x1);
        afterPixel = video[pos + ((x_pos + 3) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 4) % 64)];
        video[pos + ((x_pos + 4) % 64)] ^= (short) ((sprite_byte >> 3) & 0x1);
        afterPixel = video[pos + ((x_pos + 4) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 5) % 64)];
        video[pos + ((x_pos + 5) % 64)] ^= (short) ((sprite_byte >> 2) & 0x1);
        afterPixel = video[pos + ((x_pos + 5) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 6) % 64)];
        video[pos + ((x_pos + 6) % 64)] ^= (short) ((sprite_byte >> 1) & 0x1);
        afterPixel = video[pos + ((x_pos + 6) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;
        beforePixel = video[pos + ((x_pos + 7) % 64)];
        video[pos + ((x_pos + 7) % 64)] ^= (short) ((sprite_byte >> 0) & 0x1);
        afterPixel = video[pos + ((x_pos + 7) % 64)];
        if ((beforePixel == 1) && afterPixel == 0)
          registers[0xf] = 1;

      }

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Ex9E implements Opcode {
    String op_name = "SKP Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int key = registers[Vx];
      if (keypad[key] == 1)
        pc+=2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_ExA1 implements Opcode {
    String op_name = "SKNP Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      final int key = registers[Vx];
      if (keypad[key] == 0)
        pc+=2;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx07 implements Opcode {
    String op_name = "LD Vx, DT";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[Vx] = delayTimer;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx0A implements Opcode {
    String op_name = "LD Vx, k";

    @Override
    public void execute() {
      short key = 0;
      for (int a = 0; a < 16; a++) {
        if (keypad[a] == 1) {
          key = (short)a;
          break;
        }
      }
      if (key == 0)
        pc-=2;
      else
        keypad[key] = 1;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx15 implements Opcode {
    String op_name = "LD DT, Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      delayTimer = registers[Vx];

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx18 implements Opcode {
    String op_name = "LD ST, Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      soundTimer = registers[Vx];

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx1E implements Opcode {
    String op_name = "ADD I, Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      index += registers[Vx];
      index &= 0xfff;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx29 implements Opcode {
    String op_name = "LD F, Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      index = (registers[Vx] * 5) + fontsetStartAddress;
      index = index & 0xfff;
    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx33 implements Opcode {
    String op_name = "LD B, Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      int result = registers[Vx] ;
      memory[index + 2] = (short) (result % 10);
      result = registers[Vx] / 10;
      memory[index + 1] =(short) (result % 10);
      memory[index] = (short) (result / 10);
      index += 3;
      index &= 0xfff;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx55 implements Opcode {
    String op_name = "LD [i], Vx";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      memory[index + Vx] = registers[Vx];
      for (int a = 0; a < Vx; a++) {
        memory[a + index] = registers[a];
      }
      //index += Vx + 1;
      //index &= 0xfff;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

  class Op_Fx65 implements Opcode {
    String op_name = "LD Vx, [I]";

    @Override
    public void execute() {
      final int Vx = (opcode >> 8) & 0xf;
      registers[Vx] = memory[index + Vx];
      for (int a = 0; a < Vx; a++) {
        registers[a] = memory[a + index];
      }
      //index += Vx + 1;
      //index &= 0xfff;

    }

    @Override
    public String getName() {
      return op_name;

    }
  }

@Override
public void reset() {
	// TODO Auto-generated method stub
	
}

@Override
public void irq() {
	// TODO Auto-generated method stub
	
}

@Override
public void clock() {
	// TODO Auto-generated method stub
	
}

@Override
public void connectbus(Bus bus) {
	// TODO Auto-generated method stub
	
}

@Override
public Boolean completed() {
	// TODO Auto-generated method stub
	return null;
}


}
