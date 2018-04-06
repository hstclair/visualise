package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

public interface Projector {

    /**
     * Use project() to make the velocity a mass conserving,
     * incompressible field. Achieved through a Hodge
     * decomposition. First we calculate the divergence field
     * of our velocity using the mean finite differnce approach,
     * and apply the linear solver to compute the Poisson
     * equation and obtain a "height" field. Now we subtract
     * the gradient of this field to obtain our mass conserving
     * velocity field.
     *
     * @param x The array in which the x component of our final
     * velocity field is stored.
     * @param y The array in which the y component of our final
     * velocity field is stored.
     * @param p A temporary array we can use in the computation.
     * @param div Another temporary array we use to hold the
     * velocity divergence field.
     *
     **/

    void project(DoubleGrid x, DoubleGrid y, DoubleGrid p, DoubleGrid div);

}
