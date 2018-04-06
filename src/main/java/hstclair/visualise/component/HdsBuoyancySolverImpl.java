package hstclair.visualise.component;

import hstclair.visualise.BuoyancySolver;

public class HdsBuoyancySolverImpl {

    int edgeLength;
    int rowLength;
    int area;

    public HdsBuoyancySolverImpl(int edgeLength, int rowLength) {
        this.edgeLength = edgeLength;
        this.rowLength = rowLength;
        this.area = edgeLength * edgeLength;
    }

    public void buoyancy(double[] buoyancyForce, double[] density) {

        double sigmaDensity = 0;

        int maxRowIndex = density.length - rowLength;

        // we are indexing into a 1-dimensional array that represents a two-dimensional array
        // with a one-cell buffer at the beginning and end of each row (and a leading and trailing
        // buffer row).  As a result, the indexing is one-relative.
        // For performance, I'm avoiding any additional multiplication operations and computing
        // the index through addition only
        for (int rowIndex = rowLength; rowIndex < maxRowIndex; rowIndex += rowLength) {

            int maxColIndex = rowIndex + 1 + edgeLength;

            for (int colIndex = rowIndex + 1; colIndex < maxColIndex; colIndex++)
                sigmaDensity += density[colIndex];
        }

        double meanDensity = sigmaDensity /= area;


    }
}
