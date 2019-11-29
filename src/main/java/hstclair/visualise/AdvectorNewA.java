package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

public class AdvectorNewA implements Advector {

    int edgeLength;
    int size;
    int rowSize;
    int[] rowOffset;

    public AdvectorNewA(int edgeLength, int size) {

        this.edgeLength = edgeLength;
        this.size = size;
        this.rowSize = edgeLength + 2;

        rowOffset = new int[edgeLength << 1];

        for (int i = 0; i < rowOffset.length; i++) {
            int offset = i - edgeLength;

            rowOffset[i] = offset * rowSize;
        }
    }

    @Override
    public int getEdgeLength() {
        return edgeLength;
    }

    public void advect1(int b, double[] d, double[] d0, double[] du, double[] dv, double dt) {
        double dt0;

        dt0 = dt * edgeLength;

        for (int col = 1; col <= edgeLength; col++) {
            int rowNum = 1;

            for (int rowIndex = rowSize; rowIndex < size - rowSize; rowIndex += rowSize) {

                int index = rowIndex + col;

                double dx = dt0 * du[index];
                double dy = dt0 * dv[index];

                int dxi = (int) dx;
                int dyi = (int) dy;

                int x0 = col - dxi;
                int y0 = rowNum - dyi;

                double dxf = dx - dxi;
                double dyf = dy - dyi;


                if (x0 >= edgeLength) {

                    x0 = edgeLength;

                    if (dxf > .5)
                        dxf = .5;
                } else if ( x0 <= 1) {
                    x0 = 1;

                    if (dxf < -.5)
                        dxf = -.5;
                }

                if (y0 >= edgeLength - 1) {

                    y0 = ((y0 - 1) % edgeLength) + 1;

                } else if ( y0 < 0) {

                    y0 = ((y0 - 1) % edgeLength) + edgeLength + 1;
                }

//                int i0 = (int) x0;
                int x1 = x0 + 1;

                int y1 = y0 + 1;

                if (x1 > edgeLength + 1) x1 = edgeLength + 1;
                if (x1 <= 0) x1 = 0;

                if (y1 > edgeLength) y1 -= edgeLength;
                if (y1 < 0) y1 += edgeLength;

                int dx0 = x0 - col;
                int dy0 = y0 - rowNum;
                int dx1 = x1 - col;
                int dy1 = y1 - rowNum;

                double s1 = dxf % 1;
                double s0 = 1 - s1;
                double t1 = dyf % 1;
                double t0 = 1 - t1;


                int index00 = index + dx0 + rowOffset[edgeLength + dy0];
                int index01 = index + dx0 + rowOffset[edgeLength + dy1];
                int index10 = index + dx1 + rowOffset[edgeLength + dy0];
                int index11 = index + dx1 + rowOffset[edgeLength + dy1];

                d[index] = s0 * (t0 * d0[index00] + t1 * d0[index01]) + s1 * (t0 * d0[index10] + t1 * d0[index11]);

                rowNum++;
            }
        }
    }


    public static class AdvectFunction {

        Double[] d;
        Double[] d0;

        boolean reference;
        boolean validate;

        int index00;
        int index01;
        int index10;
        int index11;

        double s0;
        double t0;
        double s1;
        double t1;

        public AdvectFunction(Double[] d, Double[] d0) {
            this.d = d;
            this.d0 = d0;
        }

        void reset() {
            validate = false;
            reference = false;
        }

        void setReference() {
            reference = true;
            validate = false;
        }

        void setValidate() {
            validate = true;
            reference = false;
        }


        Double apply(int index, int index00, int index01, int index10, int index11, double s0, double t0, double s1, double t1) {

//            if (reference) {
//
                this.index00 = index00;
                this.index01 = index01;
                this.index10 = index10;
                this.index11 = index11;

                this.s0 = s0;
                this.t0 = t0;
                this.s1 = s1;
                this.t1 = t1;
//            } else if (validate) {
//
//                if (this.index00 != index00 || this.index01 != index01 || this.index10 != index10 || this.index11 != index11
//                    || this.s0 != s0 || this.t0 != t0 || this.s1 != s1 || this.t1 != t1)
//                    throw new RuntimeException();
//            }

//            if (! validate)
                return s0 * (t0 * d0[index00] + t1 * d0[index01]) + s1 * (t0 * d0[index10] + t1 * d0[index11]);

//            return null;
        }
    }


    void advectCoreOrg(double dx, double dy, int col, int rowNum, int index, AdvectFunction advectFunction) {

        double x0 = col - dx;
        double y0 = rowNum - dy;

        if (x0 > edgeLength + 0.5) x0 = edgeLength + 0.5f;
        if (x0 < 0.5)     x0 = .5;

        if (y0 >= edgeLength - 1) y0 = ((y0 - 1) % edgeLength) + 1;
        if (y0 < 0) y0 = ((y0 - 1) % edgeLength) + edgeLength + 1;

        int i0 = (int) x0;
        int i1 = i0 + 1;

        int j0 = (int) y0;
        int j1 = j0 + 1;

        if (i1 > edgeLength + 1) i1 = edgeLength + 1;
        if (i1 <= 0) i1 = 0;

        if (j1 > edgeLength) j1 -= edgeLength;
        if (j1 < 0) j1 += edgeLength;

        int dx0i = i0 - col;
        int dy0i = j0 - rowNum;
        int dx1i = i1 - col;
        int dy1i = j1 - rowNum;

        double s1 = x0 - (int) x0;
        double s0 = 1 - s1;
        double t1 = y0 - (int) y0;
        double t0 = 1 - t1;

        int index00 = index + dx0i + rowOffset[edgeLength + dy0i];
        int index01 = index + dx0i + rowOffset[edgeLength + dy1i];
        int index10 = index + dx1i + rowOffset[edgeLength + dy0i];
        int index11 = index + dx1i + rowOffset[edgeLength + dy1i];

        advectFunction.apply(index, index00, index01, index10, index11, s0, t0, s1, t1);
    }

    public Double advectCoreNew(Double[] du, Double[] dv, double dt, int col, int rowNum, int index, AdvectFunction advectFunction) {

        double dx = dt * du[index];
        double dy = dt * dv[index];

        int dxi = (int) dx;
        int dyi = (int) dy;

        double x0 = col - dx;
        double y0 = rowNum - dy;

        int x0i = (int) x0;
        int y0i = (int) y0;

        double dxf = x0 - x0i;
        double dyf = y0 - y0i;


        if (x0i > edgeLength || x0i == edgeLength && dxf > .5) {

            x0i = edgeLength;

            dxf = .5;
        } else if ( x0i < 0 || x0i == 0 && dxf < .5) {
            x0i = 0;

            dxf = .5;
        }

        if (y0i >= edgeLength - 1) {

            y0i = ((y0i - 1) % edgeLength) + 1;

        } else if ( y0i < 0) {

            y0i = ((y0i - 1) % edgeLength) + edgeLength + 1;
        }

//                int i0 = (int) x0;
        int x1 = x0i + 1;

        int y1 = y0i + 1;

        if (x1 > edgeLength + 1) x1 = edgeLength + 1;
        if (x1 <= 0) x1 = 0;

        if (y1 > edgeLength) y1 -= edgeLength;
        if (y1 < 0) y1 += edgeLength;

        int dx0 = x0i - col;
        int dy0 = y0i - rowNum;
        int dx1 = x1 - col;
        int dy1 = y1 - rowNum;

        double s1 = dxf % 1;
        double s0 = 1 - s1;
        double t1 = dyf % 1;
        double t0 = 1 - t1;


        int index00 = index + dx0 + rowOffset[edgeLength + dy0];
        int index01 = index + dx0 + rowOffset[edgeLength + dy1];
        int index10 = index + dx1 + rowOffset[edgeLength + dy0];
        int index11 = index + dx1 + rowOffset[edgeLength + dy1];


        try {
            return advectFunction.apply(index, index00, index01, index10, index11, s0, t0, s1, t1);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public void advect(int b, DoubleGrid d, DoubleGrid d0, DoubleGrid du, DoubleGrid dv, double dt) {
        advect(b, d.grid, d0.grid, du.grid, dv.grid, dt);
    }

    public void advect(int b, Double[] d, Double[] d0, Double[] du, Double[] dv, Double dt) {
        double dt0;

        AdvectFunction advectFunction = new AdvectFunction(d, d0);

        dt0 = dt * edgeLength;

        for (int col = 1; col <= edgeLength; col++) {
            int rowNum = 1;

            for (int rowIndex = rowSize; rowIndex < size - rowSize; rowIndex += rowSize) {

                int index = rowIndex + col;


//                advectFunction.setReference();
//
//                advectCoreOrg(dx, dy, col, rowNum, index, advectFunction);
//
//                advectFunction.setValidate();

                Double result = advectCoreNew(du, dv, dt0, col, rowNum, index, advectFunction);

                if (result != null)
                    d[index] = result;

//                double x0 = col - dx;
//                double y0 = rowNum - dy;
//
//                if (x0 > edgeLength + 0.5) x0 = edgeLength + 0.5f;
//                if (x0 < 0.5)     x0 = .5;
//
//                if (y0 >= edgeLength - 1) y0 = ((y0 - 1) % edgeLength) + 1;
//                if (y0 < 0) y0 = ((y0 - 1) % edgeLength) + edgeLength + 1;
//
//                int i0 = (int) x0;
//                int i1 = i0 + 1;
//
//                int j0 = (int) y0;
//                int j1 = j0 + 1;
//
//                if (i1 > edgeLength + 1) i1 = edgeLength + 1;
//                if (i1 <= 0) i1 = 0;
//
//                if (j1 > edgeLength) j1 -= edgeLength;
//                if (j1 < 0) j1 += edgeLength;
//
//                int dx0i = i0 - col;
//                int dy0i = j0 - rowNum;
//                int dx1i = i1 - col;
//                int dy1i = j1 - rowNum;
//
//                double s1 = x0 - (int) x0;
//                double s0 = 1 - s1;
//                double t1 = y0 - (int) y0;
//                double t0 = 1 - t1;
//
//                int index00 = index + dx0i + rowOffset[edgeLength + dy0i];
//                int index01 = index + dx0i + rowOffset[edgeLength + dy1i];
//                int index10 = index + dx1i + rowOffset[edgeLength + dy0i];
//                int index11 = index + dx1i + rowOffset[edgeLength + dy1i];
//
//                d[index] = s0 * (t0 * d0[index00] + t1 * d0[index01]) + s1 * (t0 * d0[index10] + t1 * d0[index11]);

                rowNum++;
            }
        }
    }
}