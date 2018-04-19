package hstclair.visualise.component;

import hstclair.visualise.Boundary;
import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.LinearSolver;

public class GaussSeidelLinearSolver implements LinearSolver {

    int repeats;

    Boundary boundary;

    public GaussSeidelLinearSolver(int repeats, Boundary boundary) {
        this.repeats = repeats;
        this.boundary = boundary;
    }

    /**
     * Iterative linear system solver using the Gauss-Seidel
     * relaxation technique. Room for much improvement here...
     *
     **/

    public void solve(int b, DoubleGrid output, DoubleGrid input, double a, double c) {

        GaussSeidelFieldGenerator fieldGenerator = new GaussSeidelFieldGenerator(input, a, c);

        for (int count = 0; count < repeats; count++) {

            output.eachInnerColRow(fieldGenerator::generate);

            boundary.apply(output);

//            setBoundry(b, x);
        }
    }
}
