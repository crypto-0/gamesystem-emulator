package hermes.chip8;
import hermes.shared.*;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

public class Chip8PPU extends JPanel implements Controller {
  private short data;
  private final int baseAddress;
  private final int PWIDTH = 256;
  private final int PHEIGHT = 240;
  private final int DBWIDTH = 192;
  private final int DBHEIGHT = 196;
  private Image dbImage = null;
  private Graphics dbg;
  private Bus bus;

  public Chip8PPU(int baseAddress){
    this.baseAddress = baseAddress;
    setBackground(Color.BLACK);
    setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
    setVisible(true);
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
		
	}

	@Override
	public int getBaseAddress() {
		return 0;
	}
  public void render(){
    if(dbImage == null) dbImage = createImage(DBWIDTH, DBHEIGHT);
    dbg = dbImage.getGraphics();
    dbg.setColor(Color.green);
    dbg.fillRect(0, 0, DBWIDTH, DBHEIGHT);
    Graphics g;
    try{
      g = this.getGraphics();
      if(g != null){
        g.drawImage(dbImage, (int)((PWIDTH - DBWIDTH)/4), (int)((PHEIGHT - DBHEIGHT)/4), null);
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
}
