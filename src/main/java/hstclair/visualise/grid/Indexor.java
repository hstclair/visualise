package hstclair.visualise.grid;

public class Indexor {

    public DoubleGrid target;

    public int x;

    public int y;

    int edgeLength;

    int rowLength;

    public int index;

    double leftBoundary;
    double rightBoundary;
    double upperBoundary;
    double lowerBoundary;

    int[] rowOffset;

    TraversalRange traversalRange;

    TraversalStrategy traversalStrategy;

    Interpolator interpolatorAlt;
    Interpolator interpolator;


    public Indexor(DoubleGrid target, int edgeLength, int[] rowOffset, TraversalRange traversalRange, TraversalStrategy traversalStrategy) {

        this.target = target;
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + 2;
        this.traversalRange = traversalRange;
        this.traversalStrategy = traversalStrategy;

        this.upperBoundary = 0;
        this.leftBoundary = .5;
        this.lowerBoundary = edgeLength + 1;
        this.rightBoundary = edgeLength + .5;

        this.rowOffset = rowOffset;

        interpolatorAlt = new InterpolatorAlt(this);
        interpolator = new InterpolatorNew(this);
        traversalStrategy.init(this);
    }

    public void setIndex(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }

    boolean rangeDepleted() {
        return traversalStrategy.done(this);
    }

    void advance() {

        if (traversalStrategy.done(this))
            return;

        traversalStrategy.advance(this);
    }

//    boolean advanceOrg() {
//        index++;
//        x++;
//
//        if (range == edgeLength) {
//            if (x == edgeLength) {
//                index += 2;
//                x = 0;
//                y++;
//
//                if (y >= edgeLength)
//                    return false;
//            }
//        } else {
//            if (x == edgeLength + 1) {
//                x = -1;
//                y++;
//
//                if (y >= edgeLength + 1)
//                    return false;
//            }
//        }
//
//        return true;
//    }
//
//    boolean advanceColRow() {
//        index += rowLength;
//        y++;
//
//        if (range == edgeLength) {
//            if (y == edgeLength) {
//                x++;
//                index = rowLength + 1 + x;
//                y = 0;
//
//                if (x >= edgeLength)
//                    return false;
//            }
//        } else {
//            if (y == edgeLength + 1) {
//                x = -1;
//                y++;
//
//                if (y >= edgeLength + 1)
//                    return false;
//            }
//        }
//
//        return true;
//    }

    public double get() {
        return target.grid[index];
    }

    public double get(DoubleGrid source) {
        return source.grid[index];
    }

    public void set(double value) {
        target.grid[index] = value;
    }

    public void set(DoubleGrid target, double value) {
        target.grid[index] = value;
    }

    public void add(double value) {
        target.grid[index] += value;
    }

    public void add(DoubleGrid target, double value) {
        target.grid[index] += value;
    }

    public void subtract(double value) {
        target.grid[index] -= value;
    }

    public void subtract(DoubleGrid target, double value) {
        target.grid[index] -= value;
    }

    public double above() {
        return target.grid[index - rowLength];
    }

    public double above(DoubleGrid source) {
        return source.grid[index - rowLength];
    }

    public double below() {
        return target.grid[index + rowLength];
    }

    public double below(DoubleGrid source) {
        return source.grid[index + rowLength];
    }

    public double left() {
        return target.grid[index - 1];
    }

    public double left(DoubleGrid source) {
        return source.grid[index - 1];
    }

    public double right() {
        return target.grid[index + 1];
    }

    public double right(DoubleGrid source) {
        return source.grid[index + 1];
    }

    public double lateralGradient() {
        return target.grid[index + 1] - target.grid[index - 1];
    }
    
    public double lateralGradient(DoubleGrid source) {
        return source.grid[index + 1] - source.grid[index - 1];
    }

    public double verticalGradient() {
        return target.grid[index + rowLength] - target.grid[index - rowLength];
    }

    public double verticalGradient(DoubleGrid source) {
        return source.grid[index + rowLength] - source.grid[index - rowLength];
    }

    public double neighborSum() {
        return target.grid[index + 1] + target.grid[index - 1] + target.grid[index - rowLength] + target.grid[index + rowLength];
    }

    public double neighborSum(DoubleGrid source) {
        return source.grid[index + 1] + source.grid[index - 1] + source.grid[index + rowLength] + source.grid[index - rowLength];
    }

    /**
     * get the index of the cell identified by the provided coordinates
     * @param x
     * @param y
     * @return
     */
    public int indexOf(int x, int y) {
        return rowOffset[y + 1] + x + 1;
    }

    public double getInterpolatedAlt(DoubleGrid source, double xOffset, double yOffset) {

        return interpolatorAlt.getInterpolated(source, xOffset, yOffset);

//        double dx = xOffset;
//        double dy = yOffset;
//
//        int dxi = (int) dx;
//        int dyi = (int) dy;
//
//        double x0 = x - dx;
//        double ySrc = y - dy;
//
//        int x0i = (int) x0;
//        int y0i = (int) ySrc;
//
//        double dxf = x0 - x0i;
//        double dyf = ySrc - y0i;
//
//
//        if (x0i > edgeLength || x0i == edgeLength && dxf > .5) {
//
//            x0i = edgeLength;
//
//            dxf = .5;
//        } else if ( x0i < 0 || x0i == 0 && dxf < .5) {
//            x0i = 0;
//
//            dxf = .5;
//        }
//
//        if (y0i >= edgeLength - 1) {
//
//            y0i = ((y0i - 1) % edgeLength) + 1;
//
//        } else if ( y0i < 0) {
//
//            y0i = ((y0i - 1) % edgeLength) + edgeLength + 1;
//        }
//
////                int i0 = (int) x0;
//        int x1 = x0i + 1;
//
//        int y1 = y0i + 1;
//
//        if (x1 > edgeLength + 1) x1 = edgeLength + 1;
//        if (x1 <= 0) x1 = 0;
//
//        if (y1 > edgeLength) y1 -= edgeLength;
//        if (y1 < 0) y1 += edgeLength;
//
//        int dx0 = x0i - x;
//        int dy0 = y0i - y;
//        int dx1 = x1 - x;
//        int dy1 = y1 - y;
//
//        double s1 = dxf % 1;
//        double s0 = 1 - s1;
//        double t1 = dyf % 1;
//        double t0 = 1 - t1;
//
//
//        int dy0Offset = rowOffset[Math.abs(dy0)];
//        int dy1Offset = rowOffset[Math.abs(dy1)];
//
//        if (dy0 < 0)
//            dy0Offset = -dy0Offset;
//
//        if (dy1 < 0)
//            dy1Offset = -dy1Offset;
//
//        int index00 = index + dx0 + dy0Offset;
//        int index01 = index + dx0 + dy1Offset;
//        int index10 = index + dx1 + dy0Offset;
//        int index11 = index + dx1 + dy1Offset;
//
//        if (coords != null) {
//            coords[0] = index00;
//            coords[1] = index01;
//            coords[2] = index10;
//            coords[3] = index11;
//        }
//
//        if (Math.max(index00, index11) < source.grid.length)
//            return s0 * (t0 * source.grid[index00] + t1 * source.grid[index01]) + s1 * (t0 * source.grid[index10] + t1 * source.grid[index11]);
//
//        return 0;
    }

    public double getInterpolated(DoubleGrid source, double xOffset, double yOffset) {

        return interpolator.getInterpolated(source, xOffset, yOffset);
    }

    public double getInterpolated(double xOffset, double yOffset) {
        return getInterpolated(target, xOffset, yOffset);
    }
}
