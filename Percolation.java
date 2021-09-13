/* *****************************************************************************
 * File Name: Percolation.java
 * Name: Willy Chang
 * NetID: changw@hawaii.edu
 ******************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int totalOpen = 0;          // number of blocks that are open
    private int myN;                    // length of one side of the nxn grid
    private WeightedQuickUnionUF sites; // union-find data type with a virtual sink & source
    private WeightedQuickUnionUF mimic; // union-find data type with a virtual source
    private boolean[][] grid;           // 2D boolean array to see if a site is open

    /**
     * Initializes a Percolation data structure with {@code n} by {@code n}
     * elements, each element set to false. It also instantiates two union-find
     * data structures, one of size n by n + 2 and one of size n by n + 1.
     *
     * @param n length of one side of the grid
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("n too small");

        myN = n;
        sites = new WeightedQuickUnionUF(myN * myN + 2);
        mimic = new WeightedQuickUnionUF(myN * myN + 1);
        grid = new boolean[myN][myN];

        for (int i = 0; i < myN; i++) {
            for (int j = 0; j < myN; j++) {
                grid[i][j] = false;
            }
        }
    }   // Create NxN grid, blocked

    /**
     * Opens the site indicated by {@code row} and {@code col}.
     *
     * @param row the row index of the block
     * @param col the column index of the block
     * @throws IndexOutOfBoundsException if {@code row} or {@code col} is equal
     *                                   to or below {@code 0}, or greater than {@code myN}
     */
    public void open(int row, int col) {
        if (row <= 0 || row > myN || col <= 0 || col > myN) {
            throw new IndexOutOfBoundsException();
        }
        // open the site
        if (!isOpen(row, col)) {
            grid[row - 1][col - 1] = true;
            totalOpen += 1;

            // connect site to virtual source
            if (row == 1) {
                sites.union(0, col);
                mimic.union(0, col);
            }

            // connect site to virtual sink
            if (row == myN) {
                sites.union((row - 1) * myN + col, myN * myN + 1);
            }

            // check the four cardinal directions (up, down, left, right) if
            // there's a connection to be made
            for (int i = -1; i <= 1; i += 1) {
                for (int j = -1; j <= 1; j += 1) {
                    if (Math.abs(i) != Math.abs(j) &&
                            row + i > 0 && row + i <= myN && col + j > 0 && col + j <= myN &&
                            isOpen(row + i, col + j)) {
                        sites.union((row - 1) * myN + col, (row - 1 + i) * myN + col + j);
                        mimic.union((row - 1) * myN + col, (row - 1 + i) * myN + col + j);
                    }
                }
            }
        }
    }

    /**
     * Checks to see if a block indicated by {@code row} and {@code col} is open.
     *
     * @param row the row index of the block
     * @param col the column index of the block
     * @return {@code true} if the index in {@code grid} is true;
     * {@code false} otherwise
     * @throws IndexOutOfBoundsException if {@code row} or {@code col} is equal
     *                                   to or below {@code 0}, or greater than {@code myN}
     */
    public boolean isOpen(int row, int col) {
        if (row <= 0 || row > myN || col <= 0 || col > myN) {
            throw new IndexOutOfBoundsException();
        }
        return grid[row - 1][col - 1];
    }

    /**
     * Returns true if a block indicated by {@code row} and {@code col} is full
     * with water (i.e. connected to the top).
     *
     * @param row the row index of the block
     * @param col the column index of the block
     * @return {@code true} if {@code 0} and {@code (row - 1) * myN + col}
     * location in {@code mimic} are connected;
     * {@code false} otherwise
     * @throws IndexOutOfBoundsException if {@code row} or {@code col} is equal
     *                                   to or below {@code 0}, or greater than {@code myN}
     */
    public boolean isFull(int row, int col) {
        if (row <= 0 || row > myN || col <= 0 || col > myN) {
            throw new IndexOutOfBoundsException();
        }

        return mimic.connected(0, (row - 1) * myN + col);
    }

    /**
     * Returns the number of open sites.
     *
     * @return number of sites open
     */
    public int numberOfOpenSites() {
        return totalOpen;
    }

    /**
     * Returns true if the system has a connection from the top to the bottom
     * block (percolates).
     *
     * @return {@code true} if {@code 0} and {@code myN * myN + 1} location in
     * {@code sites} are connected;
     * {@code false} otherwise
     */
    public boolean percolates() {
        return sites.connected(0, myN * myN + 1);
    }

    /**
     * Tests the Percolation data structure using 3 testing groups. The first
     * group tests to see if the data structure can properly open random sites
     * until it percolates. The second group of tests check to see if errors are
     * thrown if the input values to the internal methods are beyond the bounds
     * of the grid. The final group tests to see if the constructor throws an
     * error if an invalid argument is passed.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        // Test Group (1): opening random sites with the following n's until it percolates
        int[] nRandom = { 3, 5, 10, 20, 50, 250, 500, 1000, 2000 };
        for (int myi = 0; myi < nRandom.length; myi++) {
            int counter = 0;
            Percolation myPerRandomSites = new Percolation(nRandom[myi]);
            while (!myPerRandomSites.percolates()) {
                // generate a random site (row, col) to open
                int row = (int) (Math.random() * nRandom[myi]) + 1;
                int col = (int) (Math.random() * nRandom[myi]) + 1;
                if (!myPerRandomSites.isOpen(row, col)) {
                    myPerRandomSites.open(row, col);
                    counter++;
                }
            }
            StdOut.println("n = " + nRandom[myi] + " : Percolated after opening " + counter
                                   + " random sites");
        }
        // Test Group (2): catch exceptions
        Percolation myPerException = new Percolation(10);
        int[][] invalidSites = {
                { -1, 5 }, { 11, 5 }, { 0, 5 }, { 5, -1 },
                { -2147483648, -2147483648 }, { 2147483647, 2147483647 }
        };
        for (int myi = 0; myi < invalidSites.length; myi = myi + 3) {
            // test open()
            StdOut.println(
                    "Trying open() on {" + invalidSites[myi][0] + "," + invalidSites[myi][1] + "}");
            try {
                myPerException.open(invalidSites[myi][0], invalidSites[myi][1]);
                StdOut.println("Success!");
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                System.out.println("open() causes an exception: IndexOutOfBoundsException.");
            }
        }
        for (int myi = 1; myi < invalidSites.length; myi = myi + 3) {
            // test isOpen()
            StdOut.println(
                    "Trying isOpen() on {" + invalidSites[myi][0] + "," + invalidSites[myi][1]
                            + "}");
            try {
                myPerException.isOpen(invalidSites[myi][0], invalidSites[myi][1]);
                StdOut.println("Success!");
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                System.out.println("isOpen() causes an exception: IndexOutOfBoundsException.");
            }
        }
        for (int myi = 2; myi < invalidSites.length; myi = myi + 3) {
            // test isFull()
            StdOut.println(
                    "Trying isFull() on {" + invalidSites[myi][0] + "," + invalidSites[myi][1]
                            + "}");
            try {
                myPerException.isFull(invalidSites[myi][0], invalidSites[myi][1]);
                StdOut.println("Success!");
            }
            catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                System.out.println("isFull() causes an exception: IndexOutOfBoundsException.");
            }
        }
        // Test Group (3): invalid argument
        int[] myN = { -10, -1, 0 };
        for (int myi = 0; myi < myN.length; myi++) {
            // test Percolation constructor with invalid arguments
            StdOut.println("Trying to construct Percolation with " + myN[myi]);
            try {
                Percolation test = new Percolation(myN[myi]);
                StdOut.println("Success!");
            }
            catch (IllegalArgumentException illegalargumentexception) {
                System.out.println("constructor causes an exception: IllegalArgumentException.");
            }
        }
    }
}
