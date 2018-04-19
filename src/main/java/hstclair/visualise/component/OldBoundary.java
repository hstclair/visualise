package hstclair.visualise.component;

import hstclair.visualise.Boundary;
import hstclair.visualise.grid.DoubleGrid;

public class OldBoundary implements Boundary {


    @Override
    public void apply(DoubleGrid x) {

        int edgeLength = x.edgeLength;

        for (int i = 0; i <= edgeLength + 1; i++)
        {
            x.grid[I(  i, 0  , edgeLength)] = x.grid[I(i, edgeLength, edgeLength)];
            x.grid[I( i, edgeLength +1, edgeLength)] = + x.grid[I(i,1, edgeLength)];
        }
    }

    // util method for indexing 1d arrays
    int I(int i, int j, int edgeLength){ return i + (edgeLength + 2) * j; }
}
