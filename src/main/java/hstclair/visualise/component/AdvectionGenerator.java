package hstclair.visualise.component;

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
    }



    public void generate(Indexor indexor) {

        double dx = - dt0 * indexor.get(du);
        double dy = - dt0 * indexor.get(dv);

        if (indexor.get(du) != 0 || indexor.get(dv) != 0)
            dx = dx;

        indexor.set(indexor.get(d0, dx, dy));

//        double x0 = indexor.x - dx;
//        double y0 = indexor.y - dy;



//        int x0i = (int) x0;
//        int y0i = (int) y0;
//
//        double dxf = x0 - x0i;
//        double dyf = y0 - y0i;


//        if (x0i > d.edgeLength || x0i == d.edgeLength && dxf > .5) {
//
//            x0i = d.edgeLength;
//
//            dxf = .5;
//        } else if ( x0i < 0 || x0i == 0 && dxf < .5) {
//            x0i = 0;
//
//            dxf = .5;
//        }

//        if (y0i >= d.edgeLength - 1) {
//
//            y0i = ((y0i - 1) % d.edgeLength) + 1;
//
//        } else if ( y0i < 0) {
//
//            y0i = ((y0i - 1) % d.edgeLength) + d.edgeLength + 1;
//        }




//        advectCoreNew(dx, dy, col, rowNum, index, d, d0);
    }
}
