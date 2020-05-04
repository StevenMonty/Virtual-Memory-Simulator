import java.io.FileNotFoundException;

/**
 * A Virtual Memory Algorithm simulation to run the Optimal (OPT), Least 
 * Recently Used (LRU), and the Second Chance Page Replacement Algorithms (PRA).
 * 
 * CS 1550 - Intro to Operating Systems
 * 
 * @author Steven Montalbano smm285@pitt.edu
 */
public class VMSim {

	/*
	 * TODO
	 * 
	 * !!!!!!!!Turn Spell Check back on before submitting --> these comments do be a
	 * mess
	 * 
	 * Run eclipse clean up before turning in
	 * 
	 */
	private static boolean DEBUG_MODE = true; // Boolean flag to turn on/off debugging print statements inside debugPrint(String s)
	private static int DEBUG_LEVEL = 2; // Controls the granularity of debugPrint() messages
	private static String traceFile; // String representation of the trace file name
	private static String algName; // String representation of the algorithm choice from the cmd line
	protected static PRA alg; // The Page Replacement Algorithm
	private static int numFrames; // The amount of Frames in Physical Memory (RAM)
	private static final int numPages = (int) Math.pow(2, 20); 	// The size of the Page Table = 2^y where y == the bit
																// length of the page address. In this simulation, all
																// addresses are 32 bits meaning the first 22 bits are
																// the memory address and the last 12 bits are the
																// physical memory offset

	/**
	 * Parse cmd line args in any order
	 * 
	 * @param args the array of arguments
	 * @throws NumberFormatException in the case of a String in place where an int
	 *                               is expected, or vice versa
	 * @throws FileNotFoundException is the user enters a trace file that is not
	 *                               found in this directory
	 */
	private static void parseArgs(String[] args) {
		try {
			for (int i = 0; i <= args.length - 1; i++) {
				switch (args[i]) {
				case "-n":
					numFrames = Integer.parseInt(args[++i]);
					break;
				case "-a":
					algName = args[i + 1].toUpperCase();
					if (!(algName.equals("OPT") || algName.equals("LRU") || algName.equals("SECOND"))) {
						System.err.printf("Algorithm %s Not Supported:\n", algName);
						showUsage();
					}
					i++;
					break;
				default:
					traceFile = args[i];
					break;
				}
			}
		} catch (NumberFormatException e) {
			System.out.println("Error parsing arguments, types may be used incorrectly.");
			showUsage();
		}
	}

	/**
	 * Prints the statistics of the algorithm simulation
	 */
	private static void printResults() {
		System.out.printf(
				"Algorithm: %s\n" + "Number of frames: %d\n" + "Total memory accesses: %d\n"
						+ "Total page faults: %d\n" + "Total writes to disk: %d\n",
				alg, numFrames, alg.getMemAccess(), alg.getPageFaults(), alg.getDiskWrites());
	}

	/**
	 * Prints the proper cmd line arguments usage and exits with error code 1
	 * signalling abnormal termination
	 */
	private static void showUsage() {
		System.out.println("Usage:\tjava vmsim –n <numFrames> -a <OPT|LRU|Second> <traceFile>");
		System.exit(1);
	}

	/**
	 * A utility method to aid in debugging. debugPrint(s) only prints the String s
	 * when the global variable DEBUG_MODE is set to true AND the current
	 * DEBUG_LEVEL is >= the level passed in. Allows me to set DEBUG_MODE to false
	 * to instantly clean up my code output without having to comment out every
	 * print statement written while debugging the programs logic. Also allows
	 * DEBUG_LEVEL to be changed to increase/decrease the granularity of print
	 * statements for trickier debugging.
	 * 
	 * Setting access to protected allows the PRA class and its decendants to access
	 * this without having to declare their own version of debugPrint(s) and
	 * DEBUG_MODE
	 * 
	 * Note: this method calls print() not println(). Must use the new line char \n
	 * at the end of each call.
	 * 
	 * @param s     the String to be printed while in debugging mode
	 * @param level the debug level this print statement should be displayed at
	 */
	protected static void debugPrint(String s, int level) {
		if (DEBUG_MODE && level >= DEBUG_LEVEL)
			System.out.print(s);
	}

	/**
	 * Default debugPrint, assumes level 0 debug mode for highest granularity of
	 * print statements
	 * 
	 * @param s
	 */
	protected static void debugPrint(String s) {
		debugPrint(s, 0);
	}

	public static void main(String[] args) {

		// Init global vars to null value
		traceFile = null;
		numFrames = 0;

		if (args.length != 5)
			showUsage();
		else
			parseArgs(args);

		PRA.setRAMframes(numFrames);	// Set the size of the Physical Memory
		PRA.setPTsize(numPages); 		// Set the size of the Page Table

		switch (algName) { // Initalize the PRA to be the algorithm passed in from cmd line
		case "OPT":
			alg = new OPT();
			break;
		case "LRU":
			alg = new LRU();
			break;
		case "SECOND":
			alg = new SecondChance();
			break;
		}

		long start = System.nanoTime(); // TODO delete for performance testing

		alg.sim(traceFile); // Run the simulation with the initialized BufferedReader

		long end = System.nanoTime(); // TODO delete for performance testing

		printResults();

		// TODO delete for performance testing
//		System.out.println(String.format("\nTracefile = %s \nRuntime = %d ms\n", traceFile, TimeUnit.NANOSECONDS.toMillis(end - start)));
//		System.out.println(String.format("%d\t%d\n", numFrames, alg.getPageFaults()));
		
		
	} // End main
} // End VMSim class
