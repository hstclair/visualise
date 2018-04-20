package hstclair.visualise.component;

import hstclair.visualise.grid.ColumnRowTraversalStrategy;
import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.VorticitySolver;
import hstclair.visualise.grid.TraversalRange;

public class AlexanderMcKinzieVorticityConfinementSolver implements VorticitySolver {



    DoubleGrid w;       // this is the grid that will hold the scalar "curl" field

    public AlexanderMcKinzieVorticityConfinementSolver(int edgeLength, int bufferCount) {
        this.w = new DoubleGrid(edgeLength);
    }



    /**
     * Calculate the vorticity confinement force for each cell
     * in the fluid grid. At a point (i,j), Fvc = N x w where
     * w is the curl at (i,j) and N = del |w| / |del |w||.
     * N is the vector pointing to the vortex center, hence we
     * add force perpendicular to N.
     *
     * @param Fvc_x The array to store the x component of the
     *        vorticity confinement force for each cell.
     * @param Fvc_y The array to store the y component of the
     *        vorticity confinement force for each cell.
     **/
    public void solve(DoubleGrid Fvc_x, DoubleGrid Fvc_y, DoubleGrid uField, DoubleGrid vField)
    {
        FieldGenerator fieldGenerator = new CurlFieldGenerator(uField, vField);

        w.eachInnerColRow(fieldGenerator::generate);

        FieldGenerator vorticityConfinementFieldGenerator = new VorticityConfinementFieldGenerator(w, Fvc_x, Fvc_y, uField, vField);

//        w.each(vorticityConfinementFieldGenerator::generate, TraversalRange.customTraversal(1, 1, w.edgeLength - 1, w.edgeLength - 1), new ColumnRowTraversalStrategy());

        w.eachInnerColRow(vorticityConfinementFieldGenerator::generate);
    }

}
