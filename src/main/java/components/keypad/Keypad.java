package main.java.components.keypad;

public class Keypad {
  private short keys[] = new short[16];
  public short read(){
    for(short a : keys){
      if (a == 1){
        return a;
      }
    }
    return (short) 0;
  }

  public void write(short data){

  }
  
  public void clock(){

  }
}
