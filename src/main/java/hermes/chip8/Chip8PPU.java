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
  private final int PWIDTH = 192;
  private final int PHEIGHT = 96;
  private Image dbImage = null;
  private Graphics dbg;

  public Chip8PPU(int baseAddress){
    this.baseAddress = baseAddress;
    setBackground(Color.BLACK);
    setPreferredSize(new Dimension(PWIDTH,PHEIGHT));
    dbImage = createImage(PWIDTH, PHEIGHT);
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
    dbg = dbImage.getGraphics();
    dbg.setColor(Color.black);
    dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
    Graphics g;
    try{
      g = this.getGraphics();
      if(g != null){
        g.drawImage(dbImage, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
      }
    }
    catch(Exception e){
      System.out.println("Graphics context error: " + e);
    }

  }
}
