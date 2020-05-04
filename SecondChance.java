import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;

/**
 * A class to represent the Clock implementation of the
 * Second Chance Page Replacement Algorithm. When selecting
 * a PTE for eviction, pages that have been accessed since
 * being swapped into RAM are given a second chance. In the
 * event that all PTEs in RAM have been referenced since 
 * being loaded, the algorithm reverts to FIFO. 
 * 
 * NOTE: The Second Chance algor
 * 
 * @author Steven Montalbano
 */
public class SecondChance extends PRA {

	public SecondChance() {
		super();
		this.name = "SECOND";
	}

	@Override
	public void sim(String traceFile) {

		String line; 		// Each line read in from the trace file
		char mode; 			// The mode character on each memory instruction; l == load, s == store
		long hex; 			// The long representation of the hex memory address
		int index; 			// The index into the Page Table for the cur memory instruction
		int clockPos = 0;	// The stored clock hand position
		PTE tmp; 			// A tmp PageTableEntry reference used for shuffling PTEs across RAM and the Page Table
		PTE victim = null;	// The victim to be evicted on Page Faults
		LinkedList<PTE> RAMlist = new LinkedList<PTE>();	// RAM will be represented using a LinkedList to allow the 
															// incoming page to stay in order maintaining the clock hand logic
		try {

			BufferedReader buf = new BufferedReader(new FileReader(traceFile));

			while (buf.ready()) {

				line = buf.readLine(); 			// Get the next line of the trace file
				mode = line.charAt(0); 			// Parse the mode char from the first position of the String line
				hex = Long.decode(line.substring(2)); // Parse the hex memory address of the page into a long
				index = (int) (hex >> 12); 	// The 12 left most bits are an offset that does not apply to this simulation.

				debugPrint(String.format("Index read in == %d\n", index));

				if (!pageTable.containsKey(index)) { 	// New PTE seen, not in Page Table or RAM (since all things in RAM
														// will be in the PT)
					tmp = new PTE(index); 				// Create a new PTE
					pageTable.put(index, tmp); 
				} else 								// This memory addr has been seen before, A PTE should exist
					tmp = pageTable.get(index); 	// Get this PTE from its index in the Page Table

				if(mode == 's')
					tmp.dirty = true;
								
				if(RAMlist.contains(tmp)) {		// Page hit

					tmp.ref = true; 		  	// Page accessed after being loaded into RAM, ref becomes true
					debugPrint(String.format("Page hit on index %d\n", index));

				} else { 	// Page Fault, must add the tmp PTE into RAM. Check if eviction is needed before loading into RAM

					pageFaults++;

					debugPrint(String.format("Page fault on index %d\n", index));

					if(RAMlist.size() < RAMframes) {
						RAMlist.add(tmp);		// Load the PTE into RAM
						tmp.valid = true; 		// Page is now in RAM, set valid bit to true
						debugPrint("PF:\tNo eviction needed\n", 3);

					} else { 	// RAM is full, must evict a PTE and write it back to disk if dirty

						debugPrint("RAM is full, must evict a PTE... \n");
									
						// Start iterating over RAM from the previously stored clock hand position
		  				int i = 0, j = 0;
						for(i = clockPos, j = 0; j < RAMlist.size()+1; j++, i = (i+1) % RAMframes) {
							
							PTE p = RAMlist.get(i);

							if(p.ref)			// If referenced, set to false to give this PTE a second chance
								p.ref = false;
							else {
								victim = p;		// Victim found, break out of the loop
								break;
							}	
						}
	
						clockPos = (i+1) % RAMframes;	// Update the stored clockPos to be the next PTE after victim
						
						// At this point, victim will point towards the PTE that will be evicted
						// from RAM and written back to disk

						if (victim.dirty) { // If the PTE's dirty bit is set, its value must be updated in the Page Table before eviction
							victim.dirty = false;
							diskWrites++;
						}

						debugPrint(String.format("PF:\tEvicting page # %x \n", victim.baseAddr), 4);

						victim.valid = false; 		// This PTE is no longer mapped to physical memory in RAM
						RAMlist.set(i, tmp);		// Replace the victim PTE with the incoming tmp PTE, maintaining order
						tmp.valid = true; 						
					}
				}

				memAccess++; // Increment total memory accesses on every loop iteration
			} // End while

			buf.close();

			debugPrint("----------------------------\n", 1); // TODO delete
			
//			debugPrint(String.format("PageTable size == %d\n", pageTable.size()), 1);

		} catch (Exception e) {
			System.out.println("Error running Second Chance!\n");
			e.printStackTrace();
		}
	} // End sim	
} // End SecondChance class
