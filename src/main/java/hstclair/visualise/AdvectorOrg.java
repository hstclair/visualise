package hstclair.visualise;

public class AdvectorOrg  {

    FluidSolver solver;
    int edgeLength;
    int size;
    int rowSize;

    public AdvectorOrg(FluidSolver solver, int edgeLength, int size) {

        this.solver = solver;
        this.edgeLength = edgeLength;
        this.size = size;
        this.rowSize = edgeLength + 2;
    }

    public void advect(int b, double[] d, double[] d0, double[] du, double[] dv, double dt) {
        double dt0;

        dt0 = dt * edgeLength;

        for (int col = 1; col <= edgeLength; col++) {
            int rowNum = 1;

            for (int rowIndex = rowSize; rowIndex < size - rowSize; rowIndex += rowSize) {

                if (rowNum == 257 || rowNum == 256)
                    rowNum = rowNum + 0;

                int index = rowIndex + col;

                double dx = dt0 * du[index];
                double dy = dt0 * dv[index];

                double x = col - dx;
                double y = rowNum - dy;

                if (x > edgeLength + 0.5) x = edgeLength + 0.5f;
                if (x < 0.5)     x = 0.5f;

                int i0 = (int) x;
                int i1 = i0 + 1;

                if (i1 > edgeLength + 1) i1 = edgeLength + 1;
                if (i1 <= 0) i1 = 0;

                if (y >= edgeLength - 1) y = ((y-1) % edgeLength) + 1;
                if ((y < 0)) y = ((y-1) % edgeLength) + edgeLength +1;

                int j0 = (int) y;
                int j1 = j0 + 1;

                if (j1 > edgeLength) j1 -= edgeLength;
                if (j1 < 0) j1 += edgeLength;

                double s1 = x - i0;
                double s0 = 1 - s1;
                double t1 = y - j0;
                double t0 = 1 - t1;

//                int index00 = solver.I(i0, j0);
//                int index01 = solver.I(i0, j1);
//                int index10 = solver.I(i1, j0);
//                int index11 = solver.I(i1, j1);

//                d[index] = s0 * (t0 * d0[index00] + t1 * d0[index01]) + s1 * (t0 * d0[index10] + t1 * d0[index11]);
//        return new Advector(index, index00, index01, index10, index11, s0, t0, s1, t1);
//        return new int[] { index00, index01, index10, index11};
//        d[index] = s0 * (t0 * d0[I(i0, j0)] + t1 * d0[I(i0, j1)]) +
//                s1 * (t0 * d0[I(i1, j0)] + t1 * d0[I(i1, j1)]);

                rowNum++;
            }
        }
//
//        solver.setBoundry(b, d);

    }
}
