package com.rdebernard.hermes.shared;
import java.awt.event.KeyEvent;

public interface Observable{
  void notifyObservers(KeyEvent keyEvent);
}
