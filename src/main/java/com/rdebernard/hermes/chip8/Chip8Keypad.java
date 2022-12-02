package com.rdebernard.hermes.chip8;
import java.awt.event.KeyEvent;

import com.rdebernard.hermes.shared.Bus;
import com.rdebernard.hermes.shared.Controller;
import com.rdebernard.hermes.shared.Observer;

public class Chip8Keypad implements Controller, Observer{
  private short data;
  private short[] keys ;
  private final int baseAddress;

  public Chip8Keypad(int baseAddress){
    data = 0;
    keys = new short[16];
    this.baseAddress = baseAddress;
  }
	@Override
	public short read(int ioAddress) {
    if(ioAddress - baseAddress == 0){
      if(data > 0 && data < keys.length){
        return keys[data];
      }
    }
    return 0;
	}

	@Override
	public void write(int ioAddress, short data) {
    if(ioAddress - baseAddress == 0)this.data = data;
	}

	@Override
	public void clock() {
		
	}

	@Override
	public int getBaseAddress() {
    return baseAddress;
	}

	@Override
	public void connectBus(Bus bus) {
		
	}
	@Override
	public void Update(KeyEvent keyEvent) {
    if(keyEvent.getID() == KeyEvent.KEY_PRESSED){
    switch(keyEvent.getKeyCode()){
        case KeyEvent.VK_1:
          keys[1] =1;
          break;
        case KeyEvent.VK_2:
          keys[2] =1;
          break;
        case KeyEvent.VK_3:
          keys[3] =1;
          break;
        case KeyEvent.VK_4:
          keys[0xc] =1;
          break;
        case KeyEvent.VK_Q:
          keys[4] =1;
          break;
        case KeyEvent.VK_W:
          keys[5] =1;
          break;
        case KeyEvent.VK_E:
          keys[6] =1;
          break;
        case KeyEvent.VK_R:
          keys[0xd] =1;
          break;
        case KeyEvent.VK_A:
          keys[7] =1;
          break;
        case KeyEvent.VK_S:
          keys[8] =1;
          break;
        case KeyEvent.VK_D:
          keys[9] =1;
          break;
        case KeyEvent.VK_F:
          keys[0xe] =1;
          break;
        case KeyEvent.VK_Z:
          keys[0xa] =1;
          break;
        case KeyEvent.VK_X:
          keys[0] =1;
          break;
        case KeyEvent.VK_C:
          keys[0xb] =1;
          break;
        case KeyEvent.VK_V:
          keys[0xf] =1;
          break;

    }

    }
    else{
      switch(keyEvent.getKeyCode()){
          case KeyEvent.VK_1:
            keys[1] =0;
            break;
          case KeyEvent.VK_2:
            keys[2] =0;
            break;
          case KeyEvent.VK_3:
            keys[3] =0;
            break;
          case KeyEvent.VK_4:
            keys[0xc] =0;
            break;
          case KeyEvent.VK_Q:
            keys[4] =0;
            break;
          case KeyEvent.VK_W:
            keys[5] =0;
            break;
          case KeyEvent.VK_E:
            keys[6] =0;
            break;
          case KeyEvent.VK_R:
            keys[0xd] =0;
            break;
          case KeyEvent.VK_A:
            keys[7] =0;
            break;
          case KeyEvent.VK_S:
            keys[8] =0;
            break;
          case KeyEvent.VK_D:
            keys[9] =0;
            break;
          case KeyEvent.VK_F:
            keys[0xe] =0;
            break;
          case KeyEvent.VK_Z:
            keys[0xa] =0;
            break;
          case KeyEvent.VK_X:
            keys[0] =0;
            break;
          case KeyEvent.VK_C:
            keys[0xb] =0;
            break;
          case KeyEvent.VK_V:
            keys[0xf] =0;
            break;
      }
    }
	}
	@Override
	public void reset() {
    for(short key: keys){
      key =0;
    }
    data =0;
	}
}
