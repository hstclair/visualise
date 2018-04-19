package hstclair.visualise.component;

import hstclair.visualise.Boundary;
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

        DivergenceFieldGenerator divergenceFieldGenerator = new DivergenceFieldGenerator(uField, vField);

        divergenceField.eachInnerColRow(divergenceFieldGenerator::generate);

        boundary.apply(divergenceField);
        p.clear();

        linearSolver.solve(0, p, divergenceField, 1, 4);      // looks like we're solving <i>into</i> the pressure grid...
                                                                        // and then transforming pressure into force????

        CompressionCounterForceFieldGenerator compressionCounterForceFieldGenerator = new CompressionCounterForceFieldGenerator(uField, vField);

        p.eachInnerColRow(compressionCounterForceFieldGenerator::generate);

        boundary.apply(uField);
        boundary.apply(vField);


//        int halfN = (uField.edgeLength >> 1);
//        double negOneOverTwoN = -0.5/ uField.edgeLength;
//
//        for (int col = 0; col < uField.edgeLength; col++)
//        {
//            for (int row = 0; row < uField.edgeLength; row++)
//            {
//                int index = uField.index(col,row);
//
//                divergenceField.set(col, row, (uField.get(col+1, row) - uField.get(col - 1, row)
//                        + vField.get(col, row+1) - vField.get(col, row - 1))
//                        * negOneOverTwoN);
//                p.grid[index] = 0;
//
////                div.grid[index] = (x.grid[index+1] - x.grid[index-1]
////                        + y.grid[index+x.rowLength] - y.grid[index-x.rowLength])
////                        * negOneOverTwoN;
////                p.grid[index] = 0;
//            }
//        }

//        boundary.apply(p);

//        setBoundry(0, div);
//        setBoundry(0, p);

//        for (int i = 0; i < uField.edgeLength; i++)
//        {
//            for (int j = 0; j < uField.edgeLength; j++)
//            {
//
//                // computing pressure gradient using central difference method
//                // and using this to construct the resulting force vector
//                // (our fluid is incompressible so we must force the pressure gradient to zero)
//
//                uField.add(i,j, - halfN * (p.get(i+1,j) - p.get(i-1,j)));
//                vField.add(i,j, - halfN * p.get(i,j+1) - p.get(i,j-1));
//
//
////                int index = x.index(i,j);
////
////                x.grid[index] -= halfN * (p.grid[index+1] - p.grid[index-1]);
////                y.grid[index] -= halfN * (p.grid[index+x.rowLength] - p.grid[index-x.rowLength]);
//            }
//        }

//        setBoundry(1, x);
//        setBoundry(2, y);
    }
}
