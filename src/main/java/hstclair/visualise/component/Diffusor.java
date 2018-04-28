package hstclair.visualise.component;

import hstclair.visualise.grid.*;
import hstclair.visualise.statistics.StandardNormalDistribution;

import java.util.Arrays;

public class Diffusor {

    static final StandardNormalDistribution dist = new StandardNormalDistribution();

    DoubleGrid grid;
    int radius;
    int center;

    public Diffusor(int radius) {

        if (radius < 0)
            throw new IllegalArgumentException("radius cannot be negative");

        if (radius == 0)
            radius = 1;

        this.radius = radius;

        center = radius - 1;

        grid = new DoubleGrid(center * 2 + 1);

        initialize(grid, radius, center);
    }


    double[] normalize(double[] array) {

        double sum = Arrays.stream(array).sum();

        double factor = 1 / sum;

        for (int index = 0; index < array.length; index++)
            array[index] /= factor;

        return array;
    }



    double[] buildHistogram(int size) {

        double[] result = new double[size];

        double range = 2.777d;      // ending at the limit of my dist function's accuracy

        double stepSize = range / result.length;

        double z = stepSize;

        double cumulativeDensity = 0;

        for (int index = 0; index < result.length; index++, z += stepSize) {

            double current = dist.normalDistributionFunction(z);

            result[index] = current - cumulativeDensity;

            cumulativeDensity = current;
        }

        return normalize(result);
    }

    Indexor[] buildIndexors(DoubleGrid grid) {

        int index = 0;

        int edgeLength = grid.edgeLength;
        int[] rowOffset = grid.rowOffset;
        TraversalRange range = TraversalRange.innerTraversal(grid.edgeLength);

        Indexor[] indexors = new Indexor[Axis.values().length << 1];

        for (Axis axis : Axis.values()) {
            TraversalStrategy strategy = new EightfoldSymmetryTraversalStrategy(axis, false);
            TraversalStrategy reflected = new EightfoldSymmetryTraversalStrategy(axis, true);

            indexors[index++] = new IndexorImpl(grid, edgeLength, rowOffset, range, strategy);
            indexors[index++] = new IndexorImpl(grid, edgeLength, rowOffset, range, reflected);
        }

        return indexors;
    }


    void initialize(DoubleGrid grid, int radius, int center) {

        double[] densityHistogram = buildHistogram(radius);

        Indexor[] indexors = buildIndexors(grid);

        indexors[0].setValue(densityHistogram[0] * densityHistogram[0]);

        while (! indexors[0].rangeDepleted()) {

            int x = indexors[0].getX() - center;
            int y = indexors[0].getY() - center;

            double density = densityHistogram[x] * densityHistogram[y];

            for (int index = 0; index < indexors.length; index++)
                indexors[index].setValue(density);
        }
    }
}
