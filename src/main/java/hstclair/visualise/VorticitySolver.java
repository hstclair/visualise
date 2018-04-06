package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

public interface VorticitySolver {

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
    void solve(DoubleGrid Fvc_x, DoubleGrid Fvc_y, DoubleGrid uArray, DoubleGrid vArray);
}
