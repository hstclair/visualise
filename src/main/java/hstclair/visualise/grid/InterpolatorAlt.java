package hstclair.visualise.grid;

public class InterpolatorAlt implements Interpolator {


    int edgeLength;

    int[] rowOffset;

    Indexor indexor;


    int x;
    int y;

    int index;

    double dx;
    double dy;

    double xSrc;
    double ySrc;

    int xSrc0;
    int ySrc0;

    int xSrc1;
    int ySrc1;

    int dx0;
    int dy0;
    int dx1;
    int dy1;

    double s0;
    double s1;
    double t0;
    double t1;

    int dy0Offset;
    int dy1Offset;

    int index00;
    int index01;
    int index10;
    int index11;

    double dxf;
    double dyf;

    public InterpolatorAlt(Indexor indexor) {

        this.indexor = indexor;
        edgeLength = indexor.edgeLength;
        rowOffset = indexor.rowOffset;
    }

    @Override
    public double getInterpolated(DoubleGrid source, double xOffset, double yOffset) {

        x = indexor.x;
        y = indexor.y;
        index = indexor.index;

        dx = xOffset;
        dy = yOffset;

        xSrc = x - dx;
        ySrc = y - dy;

        xSrc0 = (int) xSrc;
        ySrc0 = (int) ySrc;

        dxf = xSrc - xSrc0;
        dyf = ySrc - ySrc0;


        if (xSrc0 > edgeLength || xSrc0 == edgeLength && dxf > .5) {

            xSrc0 = edgeLength;

            dxf = .5;
        } else if ( xSrc0 < 0 || xSrc0 == 0 && dxf < .5) {
            xSrc0 = 0;

            dxf = .5;
        }

        if (ySrc0 >= edgeLength - 1) {

            ySrc0 = ((ySrc0 - 1) % edgeLength) + 1;

        } else if ( ySrc0 < 0) {

            ySrc0 = ((ySrc0 - 1) % edgeLength) + edgeLength + 1;
        }

        xSrc1 = xSrc0 + 1;
        ySrc1 = ySrc0 + 1;

        if (xSrc1 > edgeLength + 1) xSrc1 = edgeLength + 1;
        if (xSrc1 <= 0) xSrc1 = 0;

        if (ySrc1 > edgeLength) ySrc1 -= edgeLength;
        if (ySrc1 < 0) ySrc1 += edgeLength;

        dx0 = xSrc0 - x;
        dy0 = ySrc0 - y;
        dx1 = xSrc1 - x;
        dy1 = ySrc1 - y;

        s1 = dxf % 1;
        s0 = 1 - s1;
        t1 = dyf % 1;
        t0 = 1 - t1;


        dy0Offset = rowOffset[Math.abs(dy0)];
        dy1Offset = rowOffset[Math.abs(dy1)];

        if (dy0 < 0)
            dy0Offset = -dy0Offset;

        if (dy1 < 0)
            dy1Offset = -dy1Offset;

        index00 = index + dx0 + dy0Offset;
        index01 = index + dx0 + dy1Offset;
        index10 = index + dx1 + dy0Offset;
        index11 = index + dx1 + dy1Offset;

        if (Math.max(index00, index11) < source.grid.length)
            return s0 * (t0 * source.grid[index00] + t1 * source.grid[index01]) + s1 * (t0 * source.grid[index10] + t1 * source.grid[index11]);

        return 0;
    }
}
