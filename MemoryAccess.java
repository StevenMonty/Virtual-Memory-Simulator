
/**
 * A class to represent a single Memory Access instruction of
 * the .trace files
 * 
 * @author StevenMontalbano
 */

public class MemoryAccess {

	private int addr;
	private char mode;

	public MemoryAccess(int addr, char mode) {
		this.addr = addr;
		this.mode = mode;
	}

	public int getAddr() {
		return addr;
	}

	public char getMode() {
		return mode;
	}

}
