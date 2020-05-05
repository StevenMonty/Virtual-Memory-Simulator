# Virtual Memory Simulator

## Goal: To simulate three different Page Replacement Algorithms (PRA) in a Virtual Memory (VM) system

The three PRAs simulated are
  1. Optimal
  2. The Clock implementation of Second Chance
  3. Least Recently Used (LRU)

Each PRA is able to simulate how the algorithm proceeds with a given physical memory size.

### Optimal
OPT Simulates what the optimal page replacement algorithm would choose if it had perfect knowledge of all future memory
accesses. This is done by preprocessing the trace file and building a 'future' list that will record each time a
page is needed in the future of the simulation.

### Least Recently Used
Least Recently Used (LRU) Simulates least recently used, whereby you will track when pages were last accessed and evict the least recently used page. This is tracked using a ``last_access`` field that is updated with the current system
time on each memory access.

### Second Chance
Candidate pages are considered for removal in a round robin manner, and a page that has been accessed between consecutive page faults will not be evicted. The page will be replaced if it has not been accessed since its last consideration. That is, each page gets a “second chance” before it is replaced. In the worst case, if the second chance bit is set for all pages, the bit is cleared and second chance algorithm degenerates to FIFO.

### Trace File Structure
Each line of the trace file is a memory access for the page at the address represented by the hex number. All
memory addresses are 32-bits in length.
The first character, l or s, tells the simulator wether that access is a load or store command. One a store command,
the page is marked as dirty and must be written to disk before eviction.

```
l 0x00000000
l 0x00001000
s 0x00002000
```

### Trace File Parsing
Prior knowledge from VM systems and Page Tables (PT) tells us that in a 32-bit system, the first 20 bits are the
index into the PT where that page resides. The hex value is converted into a long and then bit shifted right >> by
12 bits to capture the 20 most significant bits, which is the index.


### Class Structure
All three of the classes to represent the algorithms are descendants of a single abstract class ``PRA.java``.
This provides a single place where data structures and variables used by all of the algorithms can be declared.
A full UML diagram displayed lays out the Java class structures.

![](https://github.com/StevenMonty/Virtual-Memory-Simulator/blob/master/UML.png)

### Analysis
[This](https://github.com/StevenMonty/Virtual-Memory-Simulator/blob/master/PRA%20Analysis.pdf) link will take you to a full algorithm analysis report comparing the performance of each of the algoriths.


### Usage
``chmod u+x vmsim.sh // make the shell script executable ``
``./vmsim.sh -n [PHYSICAL FRAMES] -a [ALGORITHM NAME][traceFile]``
```
./vmsim.sh -n 8 -a LRU swim.trace
./vmsim.sh -n 16 -a OPT swim.trace
./vmsim.sh -n 32 -a SECOND swim.trace
```

### Output:

On completion, the simulator displays the following statistics:
  - Number of frames
  - Total number of memory references
  - Number of page faults
  - Number of dirty pages written to disk on eviction

```
Algorithm: LRU
Number of frames: 3
Total memory accesses: 303193
Total page faults: 43622
Total writes to disk: 15182
```
