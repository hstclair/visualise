package hstclair.visualise.grid;

public class Indexor {

    DoubleGrid target;

    public int x;

    public int y;

    int edgeLength;

    int rowLength;

    int index;

    int range;

    double leftBoundary;
    double rightBoundary;
    double upperBoundary;
    double lowerBoundary;

    boolean lateralWrap;
    boolean verticalWrap;

    int[] rowOffset;

    public Indexor(DoubleGrid target, int edgeLength, int range, int[] rowOffset, boolean lateralWrap, boolean verticalWrap) {

        this.target = target;
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + 2;
        this.range = range;

        this.upperBoundary = 0;
        this.leftBoundary = .5;
        this.lowerBoundary = edgeLength + 1;
        this.rightBoundary = edgeLength + .5;

        this.lateralWrap = lateralWrap;
        this.verticalWrap = verticalWrap;

        this.rowOffset = rowOffset;

        if (range == edgeLength) {
            x = 0;
            y = 0;
            index = rowLength + 1;
        } else {
            x = -1;
            y = -1;
            index = 0;
        }
    }

    public void setIndex(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }

    boolean advance() {
        index++;
        x++;

        if (range == edgeLength) {
            if (x == edgeLength) {
                index += 2;
                x = 0;
                y++;

                if (y >= edgeLength)
                    return false;
            }
        } else {
            if (x == edgeLength + 1) {
                x = -1;
                y++;

                if (y >= edgeLength + 1)
                    return false;
            }
        }

        return true;
    }


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
        return target.grid[index + 1] + target.grid[index - 1] + target.grid[index + rowLength] + target.grid[index - rowLength];
    }

    public double neighborSum(DoubleGrid source) {
        return source.grid[index + 1] + source.grid[index - 1] + source.grid[index + rowLength] + source.grid[index - rowLength];
    }

    public double get(DoubleGrid source, double xOffset, double yOffset) {

        double xSrc = x - xOffset;
        double ySrc = y - yOffset;

        if (xSrc > rightBoundary)
            xSrc = rightBoundary;
        else if (xSrc < leftBoundary)
            xSrc = leftBoundary;

        if (ySrc > lowerBoundary || ySrc < upperBoundary)
            ySrc = ySrc % edgeLength;

        double x1 = xSrc + 1;
        double y1 = ySrc + 1;

        if (x1 > edgeLength + 1) x1 = edgeLength + 1;
        if (x1 <= 0) x1 = 0;

        if (y1 > edgeLength) y1 -= edgeLength;
        if (y1 < 0) y1 += edgeLength;

        int dx0 = (int) xSrc - x;
        int dy0 = (int) ySrc - y;
        int dx1 = (int) x1 - x;
        int dy1 = (int) y1 - y;

        double s1 = dx0 % 1;
        double s0 = 1 - s1;
        double t1 = dy0 % 1;
        double t0 = 1 - t1;

        if (x == (edgeLength - 1) && y == edgeLength >> 1)
            x = x;

        try {

            int index00 = index + dx0 + rowOffset[edgeLength + dy0];
            int index01 = index + dx0 + rowOffset[edgeLength + dy1];
            int index10 = index + dx1 + rowOffset[edgeLength + dy0];
            int index11 = index + dx1 + rowOffset[edgeLength + dy1];

            return s0 * (t0 * source.grid[index00] + t1 * source.grid[index01]) + s1 * (t0 * source.grid[index10] + t1 * source.grid[index11]);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public double get(double xOffset, double yOffset) {
        return get(target, xOffset, yOffset);
    }
}
