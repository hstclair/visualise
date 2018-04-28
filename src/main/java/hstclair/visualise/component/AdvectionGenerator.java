package hstclair.visualise.component;

import hstclair.visualise.AdvectorNewA;
import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class AdvectionGenerator {

    double dt;
    double dt0;

    DoubleGrid d0;
    DoubleGrid du;
    DoubleGrid dv;

    double leftBoundary;
    double rightBoundary;

    double upperBoundary;
    double lowerBoundary;

    AdvectorNewA advectorNewA;


    public AdvectionGenerator(double dt, DoubleGrid d0, DoubleGrid du, DoubleGrid dv) {

        this.dt = dt;
        this.dt0 = dt * d0.edgeLength;

        leftBoundary = .5;
        rightBoundary = d0.edgeLength + .5;
        upperBoundary = 0;
        lowerBoundary = d0.edgeLength;

        this.d0 = d0;
        this.du = du;
        this.dv = dv;

        advectorNewA = new AdvectorNewA(d0.edgeLength, d0.size);
    }



    public void generate(Indexor indexor) {

        double dx = dt0 * indexor.getValue(du);
        double dy = dt0 * indexor.getValue(dv);

        indexor.setValue(indexor.getInterpolated(d0, dx, dy));
    }
}
