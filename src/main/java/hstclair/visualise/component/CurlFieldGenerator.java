package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class CurlFieldGenerator {

    DoubleGrid uField;
    DoubleGrid vField;

    public CurlFieldGenerator(DoubleGrid uField, DoubleGrid vField) {
        this.uField = uField;
        this.vField = vField;
    }

    /**
     * Calculate the curl at position (i, j) in the fluid grid.
     * Physically this represents the vortex strength at the
     * cell. Computed as follows: w = (del x U) where U is the
     * velocity vector at (i, j).
     *
     **/

//    public double computeCurl(double uLeft, double uRight, double vDown, double vUp)
//    {
//
//        double uDiff = uRight - uLeft;
//        double vDiff = vDown - vUp;
//
//        return (uDiff - vDiff) * .5;
//    }

    public void generate(Indexor indexor) {
//        indexor.set(computeCurl(indexor.left(uField), indexor.right(uField), indexor.above(vField), indexor.below(vField)));
        indexor.set((indexor.lateralGradient(uField) - indexor.verticalGradient(vField)) * .5);
    }
}
