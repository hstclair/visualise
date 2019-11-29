package hstclair.visualise.component;

import hstclair.visualise.Advector;
import hstclair.visualise.grid.DoubleGrid;

public class AdvectorNew implements Advector {

    @Override
    public int getEdgeLength() {
        return 0;
    }

    @Override
    public void advect(int b, DoubleGrid d, DoubleGrid d0, DoubleGrid du, DoubleGrid dv, double dt) {

        d.eachInnerRowCol(new AdvectionGenerator(dt, d0, du, dv)::generate);
    }
}