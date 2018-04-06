package hstclair.visualise.component;

import hstclair.visualise.Boundary;
import hstclair.visualise.grid.DoubleGrid;

public class CyclicalYBoundary implements Boundary {

    public void apply(DoubleGrid x) {

        int leftBoundary = 0;
        int rightBoundary = x.edgeLength + 1;
        int topBoundary = 0;
        int topEdge = x.edgeLength + 1;
        int bottomBoundary = (x.edgeLength + 1) * x.edgeLength + 1;
        int bottomEdge = bottomBoundary - x.edgeLength;

        for (int i = 0; i <= x.edgeLength + 1; i++) {
//            topBoundary++;
            topEdge++;
            bottomBoundary++;
//            bottomEdge++;

//            x[topBoundary] = x[I(i, edgeLength)];
            x.grid[bottomBoundary] = x.grid[topEdge];
        }
    }
}
