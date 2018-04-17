package hstclair.visualise.component;

import hstclair.visualise.Boundary;
import hstclair.visualise.grid.DoubleGrid;

public class CyclicalYBoundary implements Boundary {

    int leftBoundaryInitial;
    int leftEdgeInitial;
    int rightBoundaryInitial;
    int rightEdgeInitial;
    int topBoundaryInitial;
    int topEdgeInitial;
    int bottomBoundaryInitial;
    int bottomEdgeInitial;


    public CyclicalYBoundary(DoubleGrid x) {

        leftBoundaryInitial = 0;
        leftEdgeInitial = 1;
        rightBoundaryInitial = x.edgeLength + 1;
        rightEdgeInitial = x.edgeLength;
        topBoundaryInitial = 1;
        topEdgeInitial = x.rowLength + 1;
        bottomBoundaryInitial = x.rowLength * x.edgeLength + 1;
        bottomEdgeInitial = bottomBoundaryInitial - x.edgeLength;
    }

    public void apply(DoubleGrid x) {

        int topBoundary = topBoundaryInitial;
//        int topEdge = topEdgeInitial;
        int bottomBoundary = bottomBoundaryInitial;
//        int bottomEdge = bottomEdgeInitial;

        int leftBoundary = leftBoundaryInitial;
        int rightBoundary = rightBoundaryInitial;

        for (int i = 0; i < x.edgeLength; i++) {
            topBoundary++;
//            topEdge++;
            bottomBoundary++;
//            bottomEdge++;
            rightBoundary++;
            leftBoundary++;

//            x.grid[bottomBoundary] = 0;
//            x.grid[topBoundary] = 0;
//            x.grid[leftBoundary] = 0;
//            x.grid[rightBoundary] = 0;

//            x.grid[bottomBoundary] = x.grid[topEdge];
//            x.grid[topBoundary] = x.grid[bottomEdge];
        }
    }
}
