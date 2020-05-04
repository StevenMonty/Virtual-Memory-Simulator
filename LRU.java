import java.io.BufferedReader;
import java.io.FileReader;


/**
 * A class to represent the Least Recently Used Page
 * Replacement Algorithm. When selecting a PTE for 
 * eviction, the page that was last accessed the 
 * furthest in the past is chosen. 
 * 
 * @author Steven Montalbano
 *
 */
public class LRU extends PRA {

	public LRU() {
		super();
		this.name = "LRU";
	}

	@Override
	public void sim(String traceFile) {

		String line; 	// Each line read in from the trace file
		char mode; 		// The mode character on each memory instruction; l == load, s == store
		long hex; 		// The long representation of the hex memory address
		int index; 		// The index into the Page Table for the cur memory instruction
		PTE tmp; 		// A tmp PageTableEntry reference used for shuffling PTEs across RAM and the Page Table
		PTE victim; 	// The PTE selected by the algorithm to be evicted

		try {

			BufferedReader buf = new BufferedReader(new FileReader(traceFile));

			while (buf.ready()) {

				line = buf.readLine(); 	// Get the next line of the trace file
				mode = line.charAt(0); 	// Parse the mode char from the first position of the String line
				hex = Long.decode(line.substring(2)); // Parse the hex memory address of the page into a long
				index = (int) (hex >> 12); 	// The 12 left most bits are an offset that does not apply to this simulation.

				debugPrint(String.format("Index read in == %d\n", index));

				if (!pageTable.containsKey(index)) { 	// New PTE seen, not in Page Table or RAM (since all things in RAM
														// will be in the PT)
					tmp = new PTE(index); 		// Create a new PTE

					pageTable.put(index, tmp); 	// Insert the new PTE into the Page Table

					// !!!!!!!!!!!!!!!!!!!!!!!!!!!
					// TODO can i access these ^ protected fields like this or do I need
					// getters/setters?
					// !!!!!!!!!!!!!!!!!!!!!!!!!!!

				} else 		// This memory addr has been seen before, A PTE should exist
					tmp = pageTable.get(index); // Get this PTE from its index in the Page Table

				if(mode == 's')
					tmp.dirty = true;
				
				if (RAM.containsKey(index)) { // Page hit

					debugPrint(String.format("Page hit on index %d\n", index));

					updateAccess(tmp); 	// Update last access time and update PQ position accordingly

				} else { 	// Page Fault, must add the tmp PTE into RAM. Check if eviction is needed before
							// loading into RAM
					pageFaults++;

					debugPrint(String.format("Page fault on index %d\n", index));
					debugPrint("\tPQ size == " + pq.size() + "\n");

					if (RAMhasRoom()) { 		// RAM is not full, can insert PTE without issue
						debugPrint("\tRAM size == " + RAM.size() + "\n");
						debugPrint("\tPF: No eviction needed\n", 1);
						updateAccess(tmp); 		// Update last access time and update PQ position accordingly
						RAM.put(index, tmp); 	// Load the PTE into RAM

					} else {
						debugPrint("\tRAM size == " + RAM.size() + "\n");
						debugPrint("\tRAM is full, must evict a PTE... \n");

						victim = pq.poll(); // Get and remove the highest priority PTE, which was the one Least Recently
											// Used one

						if (victim.dirty) { // If the PTE's dirty bit is set, its value must be updated in the Page
											// Table before eviction
//							pageTable.remove(victim.baseAddr);
//							pageTable.put(victim.baseAddr, victim);

							diskWrites++;
							victim.dirty = false;
						}
						
						evictions.add(victim.baseAddr);

						debugPrint(String.format("\tPF: Evicting page # %d \n", victim.baseAddr), 1);
//						System.out.println("Evicting page #" + victim.baseAddr);

						victim.valid = false; // Victim no longer mapped to physical memory, valid becomes false
						RAM.remove(victim.baseAddr, victim); // Remove the victim from RAM

						// The tmp PTE can now be loaded into RAM
						updateAccess(tmp);
						tmp.valid = true; //
						RAM.put(tmp.baseAddr, tmp); // Load the PTE into RAM
					}
				}

				memAccess++; // Increment total memory accesses on every loop iteration
			} // End while

			buf.close();

			debugPrint("----------------------------\n", 1);
			
			debugPrint(evictions.toString(), 3);

//			debugPrint(String.format("PageTable size == %d\n", pageTable.size()), 1);

		} catch (Exception e) {
			System.out.println("\n\nError running Least Recently Used!\n");
			e.printStackTrace();
		} // End Try-Catch

	} // End sim

	/**
	 * Helper function to update entry's lastAccessed time and make sure it remains
	 * in sorted order of the Priority Queue. First checks that entry already exists
	 * in the PQ, if so it removes it. Entry's lastAccess time is then updated to
	 * the current system time before finally being added into the PQ which sorts it
	 * according to access time. (Each remove and add call cause PQ to heapify())
	 * 
	 * @param entry the PTE to be updated
	 */
	private void updateAccess(PTE entry) {

		if (pq.contains(entry))
			pq.remove(entry);

		entry.lastAccess = System.nanoTime();
		pq.add(entry);
	}

}
