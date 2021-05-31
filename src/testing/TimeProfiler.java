// TimeProfiler.java
// Last edited Lee Lin 3/15/2003

package testing;

/** <b>TimeProfiler</b> objects allow a programmer to profile the running
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

public class TimeProfiler {

	// Abstraction Function:
	//   AF(worst-case-time)  = slow
	//   AF(best-case-time)   = fast
	//   AF(average-time)     = mean
	//   AF(timing)           = Timer is off iff (temp == -1)

	private long temp;
	private long total;
	private double numTrials;
	private long slow;
	private long fast;
	private double mean;
	private String name;

	/* @effects timer begins off and the best-case, worst-case. and
	   average times not defined until after the first trial. */
	public TimeProfiler() {
		this("Nameless Test");
	}

	/* @effects timer begins off and the best-case, worst-case. and
	   average times not defined until after the first trial. */
	public TimeProfiler(String name) {
		this.name = name;
		total = 0;
		numTrials = 0.0;
		slow = Long.MIN_VALUE;
		fast = Long.MAX_VALUE;
		mean = 0;
		temp = -1;
	}

	/** Starts the timer
	 *
	 *  @modifies this
	 *  @effects turns on timing
	 */
	public void start() {
		temp = System.currentTimeMillis();
	}

	/** Stops the timer and records the data.
	 *  @requires (timing == true)
	 *     which means this.start() called since last time stop() called
	 *  @modifies this
	 *  @effects turns off timing, updates worst,best, and average time
	 *           based on this trial
	 */
	public void stop() {
		temp = System.currentTimeMillis() - temp;
		if (temp > slow)
			slow = temp;
		if (temp < fast)
			fast = temp;
		numTrials++;
		total += temp;
		mean = total / numTrials;
		temp = -1;
	}

	/** returns a formatted String with stats on slowest start-stop time,
	 *  fastest start-stop time, average time, total records, and
	 *  aggregate time */
	public String stats() {
		StringBuffer product = new StringBuffer();
		product.append("\n***** Stats for ").append(name);
		product.append("\nAggregate Time: ").append(total / 1000.0).append(
			" seconds");
		product.append("\nAverage Time: ").append(mean / 1000.0).append(
			" seconds");
		product.append("\nSlowest time: ").append(slow / 1000.0).append(
			" seconds");
		product.append("\nFastest time: ").append(fast / 1000.0).append(
			" seconds");
		product.append("\nTotal Trials: ").append(numTrials);
		return product.toString();
	}

	public String toString() {
		return stats();
	}
}
