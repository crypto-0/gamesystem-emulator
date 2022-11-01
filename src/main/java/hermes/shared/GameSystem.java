package hermes.shared;
import javax.swing.JPanel;
import java.awt.*;
public abstract class GameSystem extends JPanel implements Runnable
{
  private final int PWIDTH =256;
  private final int PHEIGHT=240;
  private Thread animator;
  private volatile boolean running = false;
  private long period = 1000 / 60;
  private int NO_DELAY_PER_YIELD = 10;
  private boolean debug = true;

  public GameSystem() {
    setBackground(Color.black);
    setFocusable(true);
    if(debug) setPreferredSize(new Dimension(PWIDTH + 200, PHEIGHT + 200));
    else setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

  }

  public void  addNotify(){
    super.addNotify();
    startSystem();
  }

  public void startSystem() {
    if (animator == null || !running) {
      animator = new Thread(this);
      animator.start();
    }
  }

  @Override
  public void run() {
    long beforeTime, timeDiff, sleepTime, afterTime;
    long overSleepTime = 0l;
    int noDelays = 0;
    beforeTime = System.currentTimeMillis();
    running = true;;
    while (running) {
      update();
      render();
      afterTime = System.currentTimeMillis();
      timeDiff = afterTime - beforeTime;
      sleepTime = period - timeDiff - overSleepTime;
      if (sleepTime > 0) {
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
        }
        overSleepTime = (System.currentTimeMillis() - afterTime) - sleepTime;
      }
      else {
        overSleepTime = 0;
        if (++noDelays >= NO_DELAY_PER_YIELD) {
          Thread.yield();
          noDelays = 0;
        }
      }
      beforeTime = System.currentTimeMillis();
    }
  }

  public abstract void update();
  public abstract void render();
  public abstract void loadRom(String filename);

}
