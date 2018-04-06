package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

public interface LinearSolver {

    /**
     * Linear system solver
     **/

    void solve(int b, DoubleGrid x, DoubleGrid x0, double a, double c);
}
