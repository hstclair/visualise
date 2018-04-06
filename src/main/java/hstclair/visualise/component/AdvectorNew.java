package hstclair.visualise.component;

import hstclair.visualise.Advector;
import hstclair.visualise.grid.DoubleGrid;

public class AdvectorNew implements Advector {

//    int[] rowOffset;
//
//    public AdvectorNew(int edgeLength) {
//
//        rowOffset = new int[edgeLength << 1];
//
//        for (int i = 0; i < rowOffset.length; i++) {
//            int offset = i - edgeLength;
//
//            rowOffset[i] = offset * (edgeLength + 2);
//        }
//
//    }
//
//
//    void advectCoreNew(double dx, double dy, int col, int rowNum, int index, DoubleGrid d, DoubleGrid d0) {
//
//        double x0 = col - dx;
//        double y0 = rowNum - dy;
//
//        int x0i = (int) x0;
//        int y0i = (int) y0;
//
//        double dxf = x0 - x0i;
//        double dyf = y0 - y0i;
//
//
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
//
//        if (y0i >= d.edgeLength - 1) {
//
//            y0i = ((y0i - 1) % d.edgeLength) + 1;
//
//        } else if ( y0i < 0) {
//
//            y0i = ((y0i - 1) % d.edgeLength) + d.edgeLength + 1;
//        }
//
//        int x1 = x0i + 1;
//        int y1 = y0i + 1;
//
//        if (x1 > d.edgeLength + 1) x1 = d.edgeLength + 1;
//        if (x1 <= 0) x1 = 0;
//
//        if (y1 > d.edgeLength) y1 -= d.edgeLength;
//        if (y1 < 0) y1 += d.edgeLength;
//
//        int dx0 = x0i - col;
//        int dy0 = y0i - rowNum;
//        int dx1 = x1 - col;
//        int dy1 = y1 - rowNum;
//
//        double s1 = dxf % 1;
//        double s0 = 1 - s1;
//        double t1 = dyf % 1;
//        double t0 = 1 - t1;
//
//
//        int index00 = index + dx0 + rowOffset[d.edgeLength + dy0];
//        int index01 = index + dx0 + rowOffset[d.edgeLength + dy1];
//        int index10 = index + dx1 + rowOffset[d.edgeLength + dy0];
//        int index11 = index + dx1 + rowOffset[d.edgeLength + dy1];
//
//        d.grid[index] = s0 * (t0 * d0.grid[index00] + t1 * d0.grid[index01]) + s1 * (t0 * d0.grid[index10] + t1 * d0.grid[index11]);
//    }

    @Override
    public void advect(int b, DoubleGrid d, DoubleGrid d0, DoubleGrid du, DoubleGrid dv, double dt) {

//        double dt0;
//
//        dt0 = dt * d.edgeLength;

        AdvectionGenerator advectionGenerator = new AdvectionGenerator(dt, d0, du, dv);

        d.eachInner(advectionGenerator::generate);

//        for (int col = 1; col <= d.edgeLength; col++) {
//            int rowNum = 1;
//
//            for (int rowIndex = d.rowLength; rowIndex < d.size - d.rowLength; rowIndex += d.rowLength) {
//
//                int index = rowIndex + col;
//
//                double dx = dt0 * du.grid[index];
//                double dy = dt0 * dv.grid[index];
//
//                advectCoreNew(dx, dy, col, rowNum, index, d, d0);
//
//                rowNum++;
//            }
//        }
    }
}