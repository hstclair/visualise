package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class AlexanderMcKinzieVorticityConfinementFieldGenerator implements FieldGenerator {

    DoubleGrid w;
    DoubleGrid fvcX;
    DoubleGrid fvcY;
    DoubleGrid uField;
    DoubleGrid vField;

    public AlexanderMcKinzieVorticityConfinementFieldGenerator(DoubleGrid w, DoubleGrid fvcX, DoubleGrid fvcY, DoubleGrid uField, DoubleGrid vField) {
        this.w = w;
        this.fvcX = fvcX;
        this.fvcY = fvcY;
        this.uField = uField;
        this.vField = vField;
    }

    /**
     * Calculate the curl at position (i, j) in the fluid grid.
     * Physically this represents the vortex strength at the
     * cell. Computed as follows: w = (del x U) where U is the
     * velocity vector at (i, j).
     *
     * @param index the index of the cell we're working with
     **/

    public double curl(int index, DoubleGrid u, DoubleGrid v)
    {

        int row = u.rowLength;

        double uDiff = u.grid[index + row] - u.grid[index - row];
        double vDiff = v.grid[index + 1] - v.grid[index - 1];

        return (uDiff-vDiff) * .5;
    }


    public void generate(Indexor indexor) {


        int edgeLength = w.edgeLength;
        int index = indexor.index;
        int row = w.rowLength;

        if (indexor.x < 1 || indexor.x >= edgeLength || indexor.y < 1 || indexor.y >= edgeLength)
            return;

        double dw_dx = (w.grid[index + 1] - w.grid[index-1]) * 0.5f;
        double dw_dy = (w.grid[index + row] - w.grid[index-row]) * 0.5f;

        // Calculate vector length. (|n|)
        // Add small factor to prevent divide by zeros.
        double length = Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f;

        // N = ( n/|n| )
        dw_dx /= length;
        dw_dy /= length;

        double dblV = curl(index, uField, vField);

        // N x w
        fvcX.grid[index] = dw_dy * -dblV;
        fvcY.grid[index] = dw_dx *  dblV;
    }
}
