package hstclair.visualise.grid;

/**
 * interface for an object to generate the boundary conditions
 * for a given field.
 */
public interface BoundaryCondition {

    /**
     * Get the boundary condition value for any cell at or above the upper X boundary
     * @param x the actual X coordinate
     * @param y the actual Y coordinate
     * @param xMinusLimit the actual X coordinate minus the boundary limit
     *                    For a value <i>at</i> the boundary (i.e. X == upperBoundaryX),
     *                    this will be zero.  For a value <i>beyond</i> the boundary
     *                    (e.g. X > upperBoundaryX), this will be a positive integer
     *                    indicating the value in excess of the boundary
     *                    (e.g. xMinusLimit = X - upperBoundaryX)
     * @return the boundary value for the requested cell
     */
    double upperLimitX(int y, int x, int xMinusLimit);

    /**
     * Get the boundary condition value for any cell at or below the lower X boundary
     * @param x the actual X coordinate
     * @param y the actual Y coordinate
     * @param limitMinusX the boundary limit minus the actual X coordinate
     *                    For a value <i>at</i> the boundary (i.e. X == lowerBoundaryX),
     *                    this will be zero.  For a value <i>below</i> the boundary
     *                    (e.g. X < lowerBoundaryX), this will be a positive integer
     *                    indicating the value in excess of the boundary
     *                    (e.g. xMinusLimit = X - upperBoundaryX)
     * @return the boundary value for the requested cell
     */
    double lowerLimitX(int x, int y, int limitMinusX);

    /**
     * Get the boundary condition value for any cell at or above the upper X boundary
     * @param x the actual X coordinate
     * @param y the actual Y coordinate
     * @param yMinusLimit the actual Y coordinate minus the boundary limit
     *                    For a value <i>at</i> the boundary (i.e. Y == upperBoundaryY),
     *                    this will be zero.  For a value <i>beyond</i> the boundary
     *                    (e.g. Y > upperBoundaryY), this will be a positive integer
     *                    indicating the value in excess of the boundary
     *                    (e.g. yMinusLimit = Y - upperBoundaryY)
     * @return the boundary value for the requested cell
     */
    double upperLimitY(int x, int y, int yMinusLimit);

    /**
     * Get the boundary condition value for any cell at or below the lower Y boundary
     * @param x the actual X coordinate
     * @param y the actual Y coordinate
     * @param limitMinusY the boundary limit minus the actual Y coordinate
     *                    For a value <i>at</i> the boundary (i.e. Y == lowerBoundaryY),
     *                    this will be zero.  For a value <i>below</i> the boundary
     *                    (e.g. Y < lowerBoundaryY), this will be a positive integer
     *                    indicating the value in excess of the boundary
     *                    (e.g. yMinusLimit = Y - upperBoundaryY)
     * @return the boundary value for the requested cell
     */
    double lowerLimitY(int x, int y, int limitMinusY);
}
