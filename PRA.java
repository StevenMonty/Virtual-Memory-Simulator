import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * An Abstract Page Replacement Algorithm Class that will be the ancestor of the
 * actual PRA implementations. This serves as a template with the fields and
 * accessors that all PRA implementations will need.
 * 
 * @see SecondChance.java, LRU.java, OPT.java
 * @author Steven Montalbano
 *
 */
public abstract class PRA {

	protected String name; // The Algorithms name
	protected static int RAMframes; // The size of physical memory
	protected static int PTpages; // The size of the whole Page Table
	protected int memAccess, pageFaults, diskWrites; // Statistics tracking
	protected HashMap<Integer, PTE> pageTable; // The entire Page Table in virtual memory; key = Page Base Address,
												// value = PageTabelEntry object
	protected HashMap<Integer, PTE> RAM; // Physical Memory
	protected HashMap<Integer, LinkedList<Integer>> future; // The future memory accesses of the PTE whos addr == (key);
															// used in OPT
	protected PriorityQueue<PTE> pq; // Priority Queue used as MinHeap for removal of the LRU accessed PTE in RAM;
										// used in LRU and OPT
	LinkedList<Integer> evictions;
	/**
	 * Protected Constructor; called via super() by the child classes to initialize
	 * the HashMaps to store virtual and physical memory
	 */
	protected PRA() {
		
		 evictions = new LinkedList<>();

//		debugPrint("PRA const called, RAMframes = " + RAMframes+ "\n");

		this.memAccess = this.pageFaults = this.diskWrites = 0; // Init vars to 0
		this.pageTable = new HashMap<>(PTpages); // Initialize the PT to be the proper size based on the memory address
													// length
		this.RAM = new HashMap<>(RAMframes); // Initialize the Physical Memory to have the number of frames specified
												// from cmd line
		// Note: Setting 1.1F as the Load Factor stops the HashTable from dynamically
		// upsizing until its (size == capacity*1.1) which should never happen
		// By default, Java uses 75% as the load factor which would cause RAM to never
		// become full causing page evictions

		if (this instanceof LRU) // LRU needs a MinHeap to select the PTE with the oldest lastAccess time for eviction
			this.pq = new PriorityQueue<PTE>(RAMframes);

		else if (this instanceof OPT) { // The OPT algorithm requires a perfect knowledge of the future memory accesses
										// for a given PTE.
			this.future = new HashMap<>(); // The key will = the PTE's page number, the val will = a LinkedList of
											// Integers where each element of the
											// LinkedList will be a line number of the tracefile where the PTE whose
											// addr = key is accessed

			this.pq = new PriorityQueue<PTE>(RAMframes); // OPT uses a MinHeap in the event where multiple PTEs are not
														 // accessed again in the future; follow LRU to select victim for
														 // eviction
		}
	}

	/**
	 * The actual implementation of the PRA that extends this class
	 * 
	 * @param buf the BufferedReader created from within VMSim tied to the trace
	 *            file with the memory accesses
	 */
	public abstract void sim(String traceFile);

	/**
	 * Utility method that acts as wrapper for the debugPrint(s) method declared in
	 * VMSim. Simply calls VMSim's version of debugPrint() to keep all DEBUG_MODE
	 * logic centralized inside of the main simulation driver class.
	 * 
	 * The protected access qualifier allows the decents of PRA to access this
	 * without issue.
	 * 
	 * @param s the string to be printed while in DEBUG_MODE
	 */
	protected static void debugPrint(String s) {
		VMSim.debugPrint(s);
	}

	protected static void debugPrint(String s, int level) {
		VMSim.debugPrint(s, level);
	}

	// Accessors and Mutators

	/**
	 * Sets the amount of Physical Memory frames in RAM.
	 * 
	 * @param frames the number of Physical Memory frames in RAM
	 */
	public static void setRAMframes(int frames) {
		RAMframes = frames;
	}

	/**
	 * Sets the size of the whole Page Table that contains the memory pages that are
	 * in and out of RAM at all times.
	 * 
	 * @param pages the number of pages
	 */
	public static void setPTsize(int pages) {
		PTpages = pages;
	}

	/**
	 * Used to determine if RAM can accept another PageTableEntry(PTE), or if a PTE
	 * must be evicted before loading a new PTE into RAM
	 * 
	 * @return true if RAM can accept another PTE without eviction
	 * @return false if RAM is full and must evict a PTE first
	 */
	protected boolean RAMhasRoom() {
		return RAM.size() < RAMframes;
	}

	// Used for the final print out of the algorithm simulation

	public int getMemAccess() {
		return memAccess;
	}

	public int getPageFaults() {
		return pageFaults;
	}

	public int getDiskWrites() {
		return diskWrites;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
