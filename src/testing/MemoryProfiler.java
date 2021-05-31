// MemoryProfiler.java

package testing;

/** <b>MemoryProfiler</b> objects allow a programmer to profile the running
 *  time of his or her code.  It abstracts away any calls to
 *  System.currentTimeMillis() and also keeps a set of statistics to allow
 *  the programmer to check worst-case time, best-case time, and average time.
 *  TimeProfilers are mutable.
 *
 * @specfield worst-case-time : integer   longest observed time in milliseconds
 * @specfield best-case-time  : integer   shorted observed time in milliseconds
 * @specfield average-time    : integer   average number of milliseconds per trial
 * @specfield timing          : boolean   state of timer (on or off)
*/

public class MemoryProfiler {

	// Abstraction Function:
	//   AF(worst-case-time)  = low
	//   AF(best-case-time)   = high
	//   AF(average-time)     = mean
	//   AF(timing)           = Timer is off iff (temp == -1)

	private long usedB, usedA;
	private long total, temp;
	private double numTrials;
	private long low;
	private long high;
	private double mean;
	private String name;
	private Runtime runTime;

	/* @effects timer begins off and the best-case, worst-case. and
	   average times not defined until after the first trial. */
	public MemoryProfiler() {
		this("Nameless Test");
	}

	/* @effects timer begins off and the best-case, worst-case. and
	   average times not defined until after the first trial. */
	public MemoryProfiler(String name) {
		this.name = name;
		total = 0;
		numTrials = 0.0;
		low = Long.MIN_VALUE;
		high = Long.MAX_VALUE;
		mean = 0;
		usedA = -1;
		usedB = -1;
		runTime = Runtime.getRuntime();
		temp = -1;
	}

	/** Starts the timer
	 *
	 *  @modifies this
	 *  @effects turns on timing
	 */
	public void start() {
		runTime.runFinalization();
		System.gc();
		usedB = runTime.totalMemory() - runTime.freeMemory();
	}

	/** Stops the timer and records the data.
	 *  @requires (timing == true)
	 *     which means this.start() called since last time stop() called
	 *  @modifies this
	 *  @effects turns off timing, updates worst,best, and average time
	 *           based on this trial
	 */
	public void stop() {
		usedA = runTime.totalMemory() - runTime.freeMemory();
		temp = usedA - usedB;
		if (temp > low)
			low = temp;
		if (temp < high)
			high = temp;
		numTrials++;
		total += temp;
		mean = total / numTrials;
		temp = -1;
	}

	/** returns a formatted String with stats on lowest start-stop time,
	 *  highest start-stop time, average time, total records, and
	 *  aggregate time */
	public String stats() {
		StringBuffer product = new StringBuffer();
		product.append("\n***** Stats for ").append(name);
		product.append("\nAverage Memory: ").append(mean / 1000000.0).append(
			" Megs");
		product.append("\nLowest Memory: ").append(high / 1000000.0).append(
			" Megs");
		product.append("\nHighest memory: ").append(low / 1000000.0).append(
			" Megs");
		product.append("\nTotal Trials: ").append(numTrials);
		return product.toString();
	}

	public String toString() {
		return stats();
	}
}
