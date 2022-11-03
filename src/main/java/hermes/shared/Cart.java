package hermes.shared;

public class Cart implements Controller{
  private short rom[];
  private int baseAddress;
  private short size;

  public Cart(int baseAddress,int size){
    this.baseAddress = baseAddress;
    this.rom = new short[size];
  }

	@Override
	public short read(int ioAddress) {
		return 0;
	}

	@Override
	public void write(int ioAddress, short data) {
		
	}

	@Override
	public void clock() {
		
	}

	@Override
	public int getBaseAddress() {
		return 0;
	}

	@Override
	public void connectBus(Bus bus) {
		
	}

	@Override
	public void reset() {
    for(short data: rom){
      data = 0;
    }
		
	}
}
