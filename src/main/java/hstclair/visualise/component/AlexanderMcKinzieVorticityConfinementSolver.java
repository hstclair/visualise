package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.VorticitySolver;

public class AlexanderMcKinzieVorticityConfinementSolver implements VorticitySolver {


    int edgeLength;
    int rowLength;
    int size;

    DoubleGrid w;       // this is the grid that will hold the scalar "curl" field

    public AlexanderMcKinzieVorticityConfinementSolver(int edgeLength, int bufferCount) {
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + (bufferCount << 1);

        this.size = rowLength * rowLength;

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
        CurlFieldGenerator fieldGenerator = new CurlFieldGenerator(uField, vField);

        w.eachInner(fieldGenerator::generate);

//        int rowIndex = 0;
//        // Calculate magnitude of curl(u,v) for each cell. (|w|)
//        for (int i = 0; i < edgeLength; i++) {
//            rowIndex += rowLength;
//            for (int col = 1; col <= edgeLength; col++) {
//                int index = rowIndex + col;
//
//                curl[index] = Math.abs(curl(index, uField, vField));
//            }
//        }

        VorticityConfinementFieldGenerator vorticityConfinementFieldGenerator = new VorticityConfinementFieldGenerator(w, Fvc_x, Fvc_y);

        w.eachInner(vorticityConfinementFieldGenerator::generate);
//
//        int i = 0, j = 0;
//
//        for (rowIndex = rowLength << 1; rowIndex < size - rowLength; rowIndex += rowLength) {
//            i++;
//            for (int col = 2; col < edgeLength; col++) {
//                j++;
//
//                int index = rowIndex + col;
//                // Find derivative of the magnitude (n = del |w|)
//                dw_dx = (w[index + 1] - w[index-1]) * 0.5f;
//                dw_dy = (w[index + rowLength] - w[index-rowLength]) * 0.5f;
//
//                // Calculate vector length. (|n|)
//                // Add small factor to prevent divide by zeros.
//                length = (float) Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f;
//
//                // N = ( n/|n| )
//                dw_dx /= length;
//                dw_dy /= length;
//
//                v = computeCurl(rowIndex + col, uField, vField);
//
//                // N x w
//                Fvc_x.grid[index] = dw_dy * -v;
//                Fvc_y.grid[index] = dw_dx *  v;
//            }
//        }
    }

}
