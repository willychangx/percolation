/* *****************************************************************************
 * File Name: PercolationStats.java
 * Name: Willy Chang
 * NetID: changw@hawaii.edu
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private static final double CONF95 = 1.96; // 95% confidence constant
    private double[] results;                  // array of means from each trial
    private int numT;                          // number of trials to be done

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("n or trials must > 0");

        numT = trials;
        results = new double[numT + 1];

        for (int i = 0; i < numT; i++) {
            Percolation perc = new Percolation(n);

            // keep opening random sites until it percolates
            while (!perc.percolates()) {
                int j = StdRandom.uniform(n) + 1;
                int k = StdRandom.uniform(n) + 1;

                if (!perc.isOpen(j, k)) {
                    perc.open(j, k);
                }
            }

            // store the mean for the trial in an array
            results[i] = (double) perc.numberOfOpenSites() / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return this.mean() - ((CONF95 * this.stddev()) / Math.sqrt(numT));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return this.mean() + ((CONF95 * this.stddev()) / Math.sqrt(numT));
    }

    // test client
    public static void main(String[] args) {
        Stopwatch timer = new Stopwatch();

        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        if (n <= 0 || trials <= 0)
            throw new IllegalArgumentException("n or trials must > 0");

        PercolationStats myPS = new PercolationStats(n, trials);
        StdOut.println("mean = " + myPS.mean());
        StdOut.println("stddev = " + myPS.stddev());
        String conf = "95% confidence interval = [" + myPS.confidenceLo();
        StdOut.println(conf + ", " + myPS.confidenceHi() + "]");

        StdOut.printf("%.5f seconds\n", timer.elapsedTime());
    }
}
