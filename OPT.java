import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A class to represent the Optimal Page Replacement Algorithm, 
 * or Furthest First Access in the future. When selecting a 
 * PTE to be evicted from RAM, the algorithm chooses the page
 * in RAM whose nect memory access is the furthest in the 
 * future to delay another page fault. In the case where there
 * are more than one PTE that is never accessed again in the
 * future, the algorithm reverts to LRU to choose a victim. 
 * 
 * NOTE: In order for OPT to pick the "Optimal" page for 
 * eviction, the algorithm must have a perfect knowledge 
 * of all future memory accesses from the OS. This makes
 * it not-implementable in a Real System, but works in 
 * this simulation since all memory instructions are
 * defined in the .trace file, and are not received in
 * real time. 
 * 
 * @author StevenMontalbano
 *
 */
public class OPT extends PRA {

	public OPT() {
		super();
		this.name = "OPT";
	}

	@Override
	public void sim(String traceFile) {

		char mode;	// The mode character on each memory instruction; l == load, s == store
		int index; 	// The index into the Page Table (RAM[]) for the cur memory instruction'
		PTE tmp; 	// A tmp PageTableEntry reference used for shuffling PTEs across RAM and the Page Table
		PTE victim; // The PTE selected by the algorithm to be evicted
		MemoryAccess instruction; // Represents a single Memory Access instruction containing the instruction and mode
		ArrayList<MemoryAccess> refString = new ArrayList<MemoryAccess>();	// List of all the memory accesses in the
																			// tracefile, used to
																			// avoid reading over the file twice to
																			// increase performance
		try {

			BufferedReader buf = new BufferedReader(new FileReader(traceFile));
			preProcess(buf, refString); // Read through the file to populate the future HashMap
			buf.close(); 				// Close buf after the first read through of the file

			while (memAccess < refString.size()) {	

//				debugPrint(String.format("Future contents: %s\n", future.toString()));

				instruction = refString.get(memAccess);	// All MemoryAccesses were stored during the first read through of the file

				index = instruction.getAddr();
				mode = instruction.getMode();

				// The pageTable has been populated during the preProccess method, no need to
				// check if tmp exists in pageTable

				tmp = pageTable.get(index); // Get this PTE from its index in the Page Table

				if(mode == 's')
					tmp.dirty = true;
				
				future.get(index).removeFirst(); 	// Remove this loop iteration's line number from the future accesses of
													// this index Note: removing the head should be safe since LinkedLists 
													// maintain insertion order and the integers were inserted ascendingly 
													// as the file was read.

				if (RAM.containsKey(index)) { // Page hit

//					debugPrint(String.format("Page hit on index %d\n", index));

					updateAccess(tmp); 		// Update last access time

				} else { 	// Page Fault, must add the tmp PTE into RAM. Check if eviction is needed before loading into RAM

					pageFaults++;

//					debugPrint(String.format("Page fault on index %d\n", index));

					if (RAMhasRoom()) { 		// RAM is not full, can insert PTE without issue
//						debugPrint("\tPF: No eviction needed\n", 1);

						updateAccess(tmp); 		// Update last access time
						RAM.put(index, tmp);	// Load the PTE into RAM

					} else {
//						debugPrint("\tRAM is full, must evict a PTE... \n");

						int victimAddr = locateVictim(); // Must find and evict the PTE that has the Furthest First
														 // Access in the future.

//						debugPrint(String.format("\tlocateVictim returned %d\n", victimAddr));

						victim = RAM.get(victimAddr);

						// TODO RAM.get returning null when victimAddr == 65527 but RAM contains 65522 (swim.trace?)

						if (victim.dirty) { // If the PTE's dirty bit is set, its value must be updated in the Page
											// Table before eviction
//							pageTable.remove(victim.baseAddr);
//							pageTable.put(victim.baseAddr, victim);

							diskWrites++;
							victim.dirty = false;
						}
						
						evictions.add(victim.baseAddr);

//						debugPrint(String.format("\tPF: Evicting page # %d \n", victim.baseAddr), 1);

						victim.valid = false; // Victim no longer mapped to physical memory, valid becomes false
						RAM.remove(victim.baseAddr, victim); // Remove the victim from RAM

						updateAccess(tmp); // Update last access time

						// The tmp PTE can now be loaded into RAM
						tmp.valid = true;
						RAM.put(tmp.baseAddr, tmp); // Load the PTE into RAM
					}
				}
				memAccess++; // Increment total memory accesses on every loop iteration
			} // End while
			
			debugPrint(evictions.toString(), 3);


		} catch (Exception e) {
			System.out.println("\n\nError running Least Recently Used!\n");
			e.printStackTrace();
		} // End Try-Catch

	} // End sim

	// Helper Methods

	/**
	 * Sets up the future and pageTable HashMaps that store every access of all
	 * the PTEs for Optimal removal of PTEs that will be needed furthest in the
	 * future.
	 * 
	 * @param buf       the BufferedReader used to read the trace file
	 * @param refString the ArrayList of type MemoryAccess used to store all the
	 *                  memory instructions of the tracefile to avoid reading large
	 *                  the file twice
	 */
	private void preProcess(BufferedReader buf, ArrayList<MemoryAccess> refString) {

		String line; 	// Each line read in from the trace file
		long hex; 		// The long representation of the hex memory address
		int index; 		// The index into the Page Table (RAM[]) for the cur memory instruction
		int lineNum; 	// The current line number of the instruction being read from the tracefile
		char mode; 		// The mode character on each memory instruction; l == load, s == store
		PTE tmp;

//		debugPrint("Beginning OPT preprocessing:\n", 0);

		try {
			lineNum = 0;
			while (buf.ready()) {

				line = buf.readLine(); 	// Get the next line of the trace file
				mode = line.charAt(0); 	// Parse the mode char from the first position of the String  line
				hex = Long.decode(line.substring(2)); // Parse the hex memory address of the page into an int
				index = (int) (hex >> 12); // The 12 left most bits are an offset that does not apply to this simulation.

				refString.add(new MemoryAccess(index, mode));

				// Initalize the PageTable and future at the same time

				if (!pageTable.containsKey(index)) { // New PTE seen, not in Page Table or RAM (since all things in RAM
														// will be in the PT)
					tmp = new PTE(index); // Create a new PTE

					pageTable.put(index, tmp); // Insert the new PTE into the Page Table
				}

				if (!future.containsKey(index)) // Preprocessor has not seen this PTE number before:
					future.put(index, new LinkedList<Integer>()); // Create a new LinkedList and insert it at this index

				future.get(index).add(lineNum); // Append the current line number onto its liked list

				lineNum++; // Increment the current line number
			}

			buf.close();
		} catch (IOException e) {
			System.out.println("Error during OPT PreProcessing!");
			e.printStackTrace();
		}

	}

	/**
	 * Helper function to easily select the optimal
	 * PTE to be evicted from RAM by locating the
	 * page whose next access is the furthest in
	 * the future. Uses the HashMap.entrySet() to
	 * create an iterator for easier traversal. In
	 * the event that multiple PTEs are not accessed
	 * again in the future, this method uses a 
	 * Priority Queue to follow the LRU heuristic. 
	 * 
	 * @return the address of the PTE to be evicted
	 */
	private int locateVictim() {

		Iterator<Entry<Integer, PTE>> it; 	// Iterator used to iterate through RAM entries
		Map.Entry<Integer, PTE> entry; 		// A tmp Map Entry ref that is used to store the return of the Iterator
		PTE tmp; 							// Tmp PTE reference used for comparisons

		it = RAM.entrySet().iterator(); // Initalize the iterator
		entry = it.next(); 				// Get the first Map Entry
		tmp = entry.getValue(); 		// Get the PTE stored in the value field of the Map Entry

		int max = 0; 		 // The max future access
		int maxAddr = 0; 	 // The address of the page accessed furtherst in the future
		int emptyFuture = 0; // The number of PTEs not accessed again in the future, used to break tie corner case

		while (it.hasNext()) {	// while there are unseen Entries in the iterator

			if (future.get(tmp.baseAddr).isEmpty()) {	// If a future is empty, this PTE is a good candidate for eviction
				pq.add(tmp);							// Add to the PQ incase of a tie to follow LRU
				emptyFuture++;
			} else {

				if (future.get(tmp.baseAddr).getFirst() > max) {  // If tmp's next access is > that the previous max, 
					max = future.get(tmp.baseAddr).getFirst();	  // tmp's next access becomes the new max. 
					maxAddr = tmp.baseAddr;
				}
			}
			entry = it.next();				// Advance the iterator
			tmp = entry.getValue();
		}

		if (!it.hasNext()) { // Corner Case: Iterator reached last element without returning, must check last element

			tmp = entry.getValue();

			if (future.get(tmp.baseAddr).isEmpty()) {	// Logic is the same as the while loop above
				pq.add(tmp);
				emptyFuture++;
			} else if (future.get(tmp.baseAddr).getFirst() > max) {
				max = future.get(tmp.baseAddr).getFirst();
				maxAddr = tmp.baseAddr;
			}
		}

		if (emptyFuture > 0) { 		// If there are 1 or more PTEs that are not accessed again in the future,
			tmp = pq.poll();		// follow LRU and evict the once used farthest in the future. PQ is a
			maxAddr = tmp.baseAddr; // MinHeap ordered by smallest lastAccess time
		}

		while (!pq.isEmpty()) // Clear the pq after each method call, fixed NullPointerException
			pq.poll();

		return maxAddr;		// Return the address of the victim selected
	}

	/**
	 * Simplified updateAccess similar to LRU. Does
	 * not maintain the PriorityQueue, only updates 
	 * the lastAccess time of entry. 
	 * 
	 * @param entry the PTE to be updated
	 */
	private void updateAccess(PTE entry) {

		entry.lastAccess = System.nanoTime();
	}

}
