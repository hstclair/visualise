package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

public interface Advector {

    void advect(int b, DoubleGrid d, DoubleGrid d0, DoubleGrid du, DoubleGrid dv, double dt);

    int getEdgeLength();
}
