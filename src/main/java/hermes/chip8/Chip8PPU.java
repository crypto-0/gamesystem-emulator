package hermes.chip8;
import hermes.shared.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

public class Chip8PPU extends JPanel implements Controller {
  private short data;
  private final int baseAddress;
  //private final int PWIDTH = 256;
  //private final int PHEIGHT = 240;
  private final int PWIDTH = 420;
  private final int PHEIGHT = 420;
  private final int SCREENWIDTH = 64;
  private final int SCEENHEIGHT = 32;
  private final int DBWIDTH = (PWIDTH/SCREENWIDTH) * SCREENWIDTH;
  private final int DBHEIGHT = (PHEIGHT/SCEENHEIGHT) * SCEENHEIGHT;
  private final int vramStartAddress = 0x0700;// to 0x07ff
  private final Color[] colors = {Color.black,Color.GREEN};
  private BufferedImage dbImage = null;
  private Graphics dbg;
  private Bus bus;

  public Chip8PPU(int baseAddress){
    this.baseAddress = baseAddress;
    setBackground(Color.BLACK);
    setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
    setVisible(true);
    dbImage = new BufferedImage(DBWIDTH ,DBHEIGHT ,BufferedImage.TYPE_INT_RGB);
    setBorder(BorderFactory.createTitledBorder("Disp"));
  }

	@Override
	public short read(int ioAddress) {
    if(ioAddress - baseAddress == 0) return data;
    return 0;
	}

	@Override
	public void write(int ioAddress, short data) {
    if(ioAddress - baseAddress == 0) this.data = data;
		
	}

	@Override
	public void clock() {
    if(data == 1){
      data =0;
      int width = PWIDTH / SCREENWIDTH;
      int height = PHEIGHT / SCEENHEIGHT;
      int finalAdress;
      int yPos;
      dbg = dbImage.createGraphics();
      dbg.setColor(colors[0]);
      dbg.fillRect(0, 0, DBWIDTH, DBHEIGHT);
      for(int a =0; a<SCEENHEIGHT; a++){
        yPos = (a * SCREENWIDTH);
        for(int b =0; b<SCREENWIDTH; b++){
          finalAdress = vramStartAddress + yPos + b;
          int pixel = (int)bus.cpuRead(finalAdress, false);
          dbg.setColor(colors[pixel]);
          dbg.fillRect(b * width, a * height, width, height);
        }
      }
    }
	}

	@Override
	public int getBaseAddress() {
		return baseAddress;
	}
  public void render(){
    Graphics g;
    try{
      g = this.getGraphics();
      if(g != null){
        g.drawImage(dbImage, (int)((PWIDTH - DBWIDTH)), (int)((PHEIGHT - DBHEIGHT)/4), null);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
      }
    }
    catch(Exception e){
      System.out.println("Graphics context error: " + e);
    }

  }

@Override
public void connectBus(Bus bus) {
  this.bus = bus;
}

@Override
public void reset() {
  data = 0;
	
}
}
