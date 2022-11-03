package hermes.shared;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.awt.*;
public abstract class GameSystem extends JPanel implements Runnable, Observable, KeyListener
{
  private Thread animator;
  private volatile boolean running = false;
  private long period = 1000 / 60;
  private int NO_DELAY_PER_YIELD = 10;
  protected boolean debug = true;
  protected boolean paused = false;
  private ArrayList<Observer> obsList = new ArrayList<>();

  public GameSystem() {
    setBackground(Color.black);
    setFocusable(true);

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
  public abstract void loadRom(InputStream  inputStream);
  public void notifyObservers(KeyEvent keyEvent){
    for(Observer obs: obsList){
      obs.Update(keyEvent);
    }
  }

  public void addObserver(Observer obs){
    if(obs !=null)obsList.add(obs);
  }

	@Override
	public void keyPressed(KeyEvent keyEvent) {
    notifyObservers(keyEvent);
	}

	@Override
	public void keyReleased(KeyEvent keyEvent) {
    notifyObservers(keyEvent);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

}
