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

        double normalizationFactor = 1 / sum;

        for (int index = 0; index < array.length; index++)
            array[index] *= normalizationFactor;

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

        return result;
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

            indexors[index++] = new Indexor(grid, edgeLength, rowOffset, range, strategy);
            indexors[index++] = new Indexor(grid, edgeLength, rowOffset, range, reflected);
        }

        return indexors;
    }


    void initialize(DoubleGrid grid, int radius, int center) {

        double[] densityHistogram = buildHistogram(radius);

        Indexor[] indexors = buildIndexors(grid);

        indexors[0].setValue(densityHistogram[0] * densityHistogram[0]);

        if (indexors[0].rangeDepleted()) {

            normalize(grid.grid);

            return;
        }

        for (Indexor indexor : indexors)
            indexor.advance();


        while (! indexors[0].rangeDepleted()) {

            int x = Math.abs(indexors[0].x - center);
            int y = Math.abs(indexors[0].y - center);

            double density = densityHistogram[x] * densityHistogram[y];

            for (Indexor indexor : indexors) {
                indexor.setValue(density);
                indexor.advance();
            }
        }

        normalize(grid.grid);
    }
}
