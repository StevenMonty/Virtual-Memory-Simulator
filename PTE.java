/**
 * A class to represent a Page Table Entry. Implements the 
 * Comparable<PTE> interface to allow for MinHeap sorting
 * according to lastAccess time when inserted in a 
 * PriorityQueue. 
 * 
 * @author Steven Montalbano
 */
public class PTE implements Comparable<PTE> {

	protected int baseAddr; 	// The page number == first 22 bits of the logical memory address
	protected int offset; 		// The offset == the last 12 bits of the logical memory address
	protected Long lastAccess; 	// The system time this PTE was last accessed in RAM for LRU eviction
	protected Long loaded;		// The system time this PTE was loaded into RAM for Second Chance eviction
	protected boolean ref; 		// Has this PTE been accessed since being loaded into RAM?
	protected boolean valid; 	// Is this PTE currently mapped to a frame in Physical Memory (RAM)?
	protected boolean dirty; 	// Has this PTE been altered by its proccess while in RAM? If so, it 
								// musted be written to disk before eviction.
	
	Long hex;
	
	public PTE() {
		this.ref = this.valid = this.dirty = false; // Init state information bits to false
		this.lastAccess = 0L;
	}

	public PTE(int baseAddr) {
		this.baseAddr = baseAddr;
		this.ref = this.valid = this.dirty = false; // Init state information bits to false
		this.lastAccess = 0L;
	}

	/**
	 * Overwritten Comparable method used to store the PTE's in ascending order of
	 * lastAccess inside of a Priority Queue which acts as a MinHeap by defult
	 * 
	 * @param o the PTE this will be compared to
	 * @return a negative integer, zero, or a positive integer as this PTE's
	 *         lastAccess time is less than, equal to, or greater than the specified
	 *         PTE, o, passed in.
	 */
	@Override
	public int compareTo(PTE o) {
			return this.lastAccess.compareTo(o.lastAccess);		
	}

	@Override
	public String toString() {
//		return String.format("Index %d, lastAccess %d, ref %b, valid %b, dirty %b\n", baseAddr, lastAccess, ref, valid, dirty);
		
		return String.format("addr %x, ref %b\n", baseAddr, ref);

	}

}
