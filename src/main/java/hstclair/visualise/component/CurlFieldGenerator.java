package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class CurlFieldGenerator implements FieldGenerator {

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
    public void generate(Indexor indexor) {
        indexor.setValue(Math.abs((indexor.verticalGradient(uField) - indexor.lateralGradient(vField)) * .5));
    }
}
