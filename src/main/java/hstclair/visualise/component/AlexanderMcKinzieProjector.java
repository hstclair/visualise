package hstclair.visualise.component;

import hstclair.visualise.Boundary;
import hstclair.visualise.FluidSolverA;
import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.LinearSolver;
import hstclair.visualise.Projector;

public class AlexanderMcKinzieProjector implements Projector {

    Boundary boundary;

    LinearSolver linearSolver;

    public AlexanderMcKinzieProjector(LinearSolver linearSolver, Boundary boundary) {
        this.linearSolver = linearSolver;
        this.boundary = boundary;
    }

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
     * @param uField The array in which the x component of our final
     * velocity field is stored.
     * @param vField The array in which the y component of our final
     * velocity field is stored.
     * @param p A temporary array we can use in the computation.
     * @param divergenceField Another temporary array we use to hold the
     * velocity divergence field.
     *
     **/

    public void project(DoubleGrid uField, DoubleGrid vField, DoubleGrid p, DoubleGrid divergenceField) {

        FieldGenerator divergenceFieldGenerator = new DivergenceFieldGenerator(uField, vField);

        divergenceField.eachInnerColRow(divergenceFieldGenerator::generate);

        boundary.apply(divergenceField);
        p.clear();

        linearSolver.solve(0, p, divergenceField, 1, 4);      // looks like we're solving <i>into</i> the pressure grid...
                                                                        // and then transforming pressure into force????

        CompressionCounterForceFieldGenerator compressionCounterForceFieldGenerator = new CompressionCounterForceFieldGenerator(uField, vField);

        p.eachInnerColRow(compressionCounterForceFieldGenerator::generate);

        boundary.apply(uField);
        boundary.apply(vField);
    }
}
